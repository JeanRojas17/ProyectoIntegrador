package com.transportesrbl.controllers;

import com.transportesrbl.models.RutaSeguimiento;
import com.transportesrbl.services.RutaService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class RutasController {

    @FXML private Label lblTotalRutas;
    @FXML private Label lblEnReparto;
    @FXML private Label lblCompletadas;
    @FXML private Label lblActualizacion;
    @FXML private Label lblEstadoMapa;
    @FXML private TextField txtProducto;
    @FXML private ComboBox<String> cbEstado;
    @FXML private ComboBox<String> cbConductor;
    @FXML private TableView<RutaSeguimiento> tblRutas;
    @FXML private TableColumn<RutaSeguimiento, String> colProducto;
    @FXML private TableColumn<RutaSeguimiento, String> colDestino;
    @FXML private TableColumn<RutaSeguimiento, String> colConductor;
    @FXML private TableColumn<RutaSeguimiento, String> colEstado;
    @FXML private TableColumn<RutaSeguimiento, String> colProgreso;
    @FXML private WebView webMapa;

    private final RutaService rutaService = new RutaService();
    private final ObservableList<RutaSeguimiento> rutasFiltradas = FXCollections.observableArrayList();
    private final DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private List<RutaSeguimiento> todasLasRutas = new ArrayList<>();
    private boolean mapaListo;

    @FXML
    public void initialize() {
        configurarTabla();
        configurarFiltros();
        cargarMapaBase();
        cargarDatos();
    }

    private void configurarTabla() {
        colProducto.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProducto()));
        colDestino.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDestino()));
        colConductor.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getConductor()));
        colEstado.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEstado()));
        colProgreso.setCellValueFactory(data -> new SimpleStringProperty(Math.round(data.getValue().getProgreso() * 100) + "%"));
        tblRutas.setItems(rutasFiltradas);
        tblRutas.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccion) -> renderizarMapa());
    }

    private void configurarFiltros() {
        cbEstado.setItems(FXCollections.observableArrayList("Todos", "Pendiente", "En reparto", "Entregado", "No entregado"));
        cbEstado.setValue("Todos");
        txtProducto.textProperty().addListener((obs, anterior, nuevo) -> aplicarFiltros());
        cbEstado.setOnAction(event -> aplicarFiltros());
        cbConductor.setOnAction(event -> aplicarFiltros());
    }

    private void cargarDatos() {
        String conductorSeleccionado = cbConductor.getValue();
        todasLasRutas = rutaService.listarRutas();
        cargarConductores(conductorSeleccionado);
        actualizarMetricas(todasLasRutas);
        aplicarFiltros();
        lblActualizacion.setText("Ultima actualizacion: " + LocalDateTime.now().format(horaFormatter));
    }

    private void cargarConductores(String conductorSeleccionado) {
        Set<String> conductores = new LinkedHashSet<>();
        conductores.add("Todos");
        todasLasRutas.stream()
            .map(RutaSeguimiento::getConductor)
            .filter(nombre -> nombre != null && !nombre.isBlank())
            .sorted()
            .forEach(conductores::add);

        cbConductor.setItems(FXCollections.observableArrayList(conductores));
        if (conductorSeleccionado != null && conductores.contains(conductorSeleccionado)) {
            cbConductor.setValue(conductorSeleccionado);
        } else {
            cbConductor.setValue("Todos");
        }
    }

    private void actualizarMetricas(List<RutaSeguimiento> rutas) {
        long enReparto = rutas.stream().filter(r -> contieneEstado(r, "reparto") || contieneEstado(r, "progreso")).count();
        long completadas = rutas.stream().filter(r -> contieneEstado(r, "entregado")).count();
        lblTotalRutas.setText(String.valueOf(rutas.size()));
        lblEnReparto.setText(String.valueOf(enReparto));
        lblCompletadas.setText(String.valueOf(completadas));
    }

    private void aplicarFiltros() {
        String producto = normalizar(txtProducto.getText());
        String estado = cbEstado.getValue();
        String conductor = cbConductor.getValue();

        List<RutaSeguimiento> filtradas = todasLasRutas.stream()
            .filter(r -> producto.isBlank() || normalizar(r.getProducto()).contains(producto))
            .filter(r -> estado == null || "Todos".equals(estado) || estado.equalsIgnoreCase(r.getEstado()))
            .filter(r -> conductor == null || "Todos".equals(conductor) || conductor.equals(r.getConductor()))
            .collect(Collectors.toList());

        rutasFiltradas.setAll(filtradas);
        conservarSeleccion(filtradas);
        renderizarMapa();
    }

    @FXML
    private void handleRefrescar() {
        cargarDatos();
    }

    private void cargarMapaBase() {
        WebEngine engine = webMapa.getEngine();
        engine.getLoadWorker().stateProperty().addListener((obs, anterior, estado) -> {
            if (estado == Worker.State.SUCCEEDED) {
                mapaListo = true;
                renderizarMapa();
            }
        });
        engine.loadContent(crearHtmlMapa());
    }

    private void renderizarMapa() {
        if (!mapaListo || webMapa == null) {
            return;
        }

        RutaSeguimiento seleccionada = tblRutas.getSelectionModel().getSelectedItem();
        int idSeleccionado = seleccionada != null ? seleccionada.getIdEntrega() : -1;
        String json = seleccionada != null
            ? construirJsonRutas(Collections.singletonList(seleccionada))
            : "[]";

        try {
            webMapa.getEngine().executeScript("window.renderRoutes(" + json + ", " + idSeleccionado + ");");
            if (seleccionada != null) {
                lblEstadoMapa.setText("Calculando ruta vial para #" + seleccionada.getIdEntrega());
            } else {
                lblEstadoMapa.setText("Seleccione una ruta para verla en el mapa");
            }
        } catch (Exception e) {
            lblEstadoMapa.setText("No se pudo actualizar el mapa");
        }
    }

    private String construirJsonRutas(List<RutaSeguimiento> rutas) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < rutas.size(); i++) {
            RutaSeguimiento r = rutas.get(i);
            if (i > 0) {
                json.append(",");
            }
            json.append("{")
                .append("\"id\":").append(r.getIdEntrega()).append(",")
                .append("\"producto\":\"").append(escaparJson(r.getProducto())).append("\",")
                .append("\"cliente\":\"").append(escaparJson(r.getCliente())).append("\",")
                .append("\"destino\":\"").append(escaparJson(r.getDestino())).append("\",")
                .append("\"estado\":\"").append(escaparJson(r.getEstado())).append("\",")
                .append("\"camion\":\"").append(escaparJson(r.getCamion())).append("\",")
                .append("\"conductor\":\"").append(escaparJson(r.getConductor())).append("\",")
                .append("\"fecha\":\"").append(escaparJson(r.getFechaAsignacion())).append("\",")
                .append("\"volumen\":").append(numero(r.getVolumen())).append(",")
                .append("\"origenLat\":").append(numero(r.getOrigenLat())).append(",")
                .append("\"origenLon\":").append(numero(r.getOrigenLon())).append(",")
                .append("\"destinoLat\":").append(numero(r.getDestinoLat())).append(",")
                .append("\"destinoLon\":").append(numero(r.getDestinoLon())).append(",")
                .append("\"actualLat\":").append(numero(r.getActualLat())).append(",")
                .append("\"actualLon\":").append(numero(r.getActualLon())).append(",")
                .append("\"progreso\":").append(numero(r.getProgreso())).append(",")
                .append("\"eta\":").append(r.getEtaMinutos()).append(",")
                .append("\"ubicacionReal\":").append(r.isUbicacionReal()).append(",")
                .append("\"color\":\"").append(escaparJson(r.getColor())).append("\"")
                .append("}");
        }
        json.append("]");
        return json.toString();
    }

    private String crearHtmlMapa() {
        return """
            <!doctype html>
            <html>
            <head>
                <meta charset='utf-8'>
                <meta name='viewport' content='width=device-width, initial-scale=1.0'>
                <link rel='stylesheet' href='https://unpkg.com/leaflet@1.9.4/dist/leaflet.css'>
                <style>
                    html, body, #map { height: 100%; width: 100%; margin: 0; }
                    body { font-family: 'Segoe UI', Arial, sans-serif; background: #eef2f7; }
                    .leaflet-popup-content { margin: 12px 14px; min-width: 210px; }
                    .popup-title { font-weight: 700; color: #1f2937; margin-bottom: 6px; }
                    .popup-line { color: #4b5563; font-size: 12px; margin-top: 3px; }
                    .popup-tag { display: inline-block; padding: 3px 8px; border-radius: 4px; color: #fff; font-size: 11px; margin-top: 8px; }
                    .map-note { position: absolute; top: 12px; left: 50px; z-index: 900; background: #fff; color: #334155; padding: 6px 10px; border-radius: 4px; box-shadow: 0 2px 8px rgba(15,23,42,.18); font-size: 12px; }
                    .fallback { height: 100%; display: grid; place-items: center; color: #334155; text-align: center; padding: 30px; box-sizing: border-box; }
                </style>
            </head>
            <body>
                <div id='map'><div class='fallback'>Cargando mapa de rutas...</div></div>
                <script src='https://unpkg.com/leaflet@1.9.4/dist/leaflet.js'></script>
                <script>
                    let map;
                    let routeLayer;
                    let noteControl;
                    const routeCache = {};

                    function ensureMap() {
                        if (!window.L) {
                            document.getElementById('map').innerHTML = '<div class="fallback">No fue posible cargar OpenStreetMap. Revisa la conexion a internet.</div>';
                            return false;
                        }
                        if (!map) {
                            map = L.map('map', { zoomControl: true }).setView([3.451646, -76.531985], 12);
                            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                                maxZoom: 19,
                                attribution: '&copy; OpenStreetMap'
                            }).addTo(map);
                            routeLayer = L.layerGroup().addTo(map);
                        }
                        return true;
                    }

                    function setNote(text) {
                        if (!noteControl) {
                            noteControl = L.control({ position: 'topleft' });
                            noteControl.onAdd = function() {
                                const div = L.DomUtil.create('div', 'map-note');
                                div.innerHTML = text;
                                return div;
                            };
                            noteControl.addTo(map);
                            return;
                        }
                        noteControl.getContainer().innerHTML = text;
                    }

                    function esc(value) {
                        return String(value || '').replace(/[&<>"']/g, function(char) {
                            return ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[char];
                        });
                    }

                    function popup(route) {
                        const pct = Math.round((route.progreso || 0) * 100);
                        return '<div class="popup-title">' + esc(route.producto) + '</div>' +
                            '<div class="popup-line"><b>Cliente:</b> ' + esc(route.cliente) + '</div>' +
                            '<div class="popup-line"><b>Destino:</b> ' + esc(route.destino) + '</div>' +
                            '<div class="popup-line"><b>Conductor:</b> ' + esc(route.conductor) + '</div>' +
                            '<div class="popup-line"><b>Camion:</b> ' + esc(route.camion) + '</div>' +
                            '<div class="popup-line"><b>Avance:</b> ' + pct + '% | ETA: ' + route.eta + ' min</div>' +
                            '<div class="popup-line"><b>Ubicacion:</b> ' + (route.ubicacionReal ? 'GPS registrado' : 'Sin GPS registrado') + '</div>' +
                            '<span class="popup-tag" style="background:' + route.color + '">' + esc(route.estado) + '</span>';
                    }

                    function osrmKey(route) {
                        return [
                            route.origenLat, route.origenLon,
                            route.destinoLat, route.destinoLon
                        ].map(function(value) { return Number(value).toFixed(5); }).join('|');
                    }

                    function osrmUrl(route) {
                        return 'https://router.project-osrm.org/route/v1/driving/' +
                            route.origenLon + ',' + route.origenLat + ';' +
                            route.destinoLon + ',' + route.destinoLat +
                            '?overview=full&geometries=geojson&steps=false';
                    }

                    function drawFallback(route) {
                        const origin = [route.origenLat, route.origenLon];
                        const current = [route.actualLat, route.actualLon];
                        const destination = [route.destinoLat, route.destinoLon];
                        L.polyline([origin, destination], {
                            color: route.color,
                            weight: 6,
                            opacity: 0.85,
                            dashArray: '8 8'
                        }).addTo(routeLayer).bindPopup(popup(route));
                        drawMarkers(route, origin, current, destination);
                        map.fitBounds(route.ubicacionReal ? [origin, current, destination] : [origin, destination], { padding: [35, 35], maxZoom: 13 });
                    }

                    function drawMarkers(route, origin, current, destination) {
                        L.circleMarker(origin, {
                            radius: 7,
                            color: '#111827',
                            fillColor: '#ffffff',
                            fillOpacity: 1,
                            weight: 2
                        }).addTo(routeLayer).bindPopup('<b>Origen</b><br>Centro de Logistica RBL');

                        L.circleMarker(destination, {
                            radius: 8,
                            color: route.color,
                            fillColor: '#fff',
                            fillOpacity: 1,
                            weight: 3
                        }).addTo(routeLayer).bindPopup(popup(route));

                        if (route.ubicacionReal) {
                            L.circleMarker(current, {
                                radius: 11,
                                color: '#111827',
                                fillColor: route.color,
                                fillOpacity: 0.95,
                                weight: 2
                            }).addTo(routeLayer).bindPopup(popup(route));
                        }
                    }

                    function drawStreetRoute(route, osrmData) {
                        const origin = [route.origenLat, route.origenLon];
                        const current = [route.actualLat, route.actualLon];
                        const destination = [route.destinoLat, route.destinoLon];
                        const geometry = osrmData.routes && osrmData.routes[0] && osrmData.routes[0].geometry;

                        if (!geometry) {
                            setNote('Ruta vial no disponible. Mostrando referencia directa.');
                            drawFallback(route);
                            return;
                        }

                        const mainRoute = L.geoJSON(geometry, {
                            style: {
                                color: route.color,
                                weight: 6,
                                opacity: 0.88
                            }
                        }).addTo(routeLayer);

                        const routeInfo = osrmData.routes[0];
                        const km = (routeInfo.distance / 1000).toFixed(1);
                        const min = Math.round(routeInfo.duration / 60);
                        mainRoute.bindPopup(popup(route) + '<div class="popup-line"><b>Ruta vial:</b> ' + km + ' km | ' + min + ' min aprox.</div>');
                        drawMarkers(route, origin, current, destination);
                        map.fitBounds(mainRoute.getBounds(), { padding: [35, 35], maxZoom: 13 });
                        setNote('Ruta vial calculada por calles');
                    }

                    function loadStreetRoute(route) {
                        const key = osrmKey(route);
                        if (routeCache[key]) {
                            drawStreetRoute(route, routeCache[key]);
                            return;
                        }

                        setNote('Calculando ruta vial...');
                        fetch(osrmUrl(route))
                            .then(function(response) {
                                if (!response.ok) throw new Error('OSRM no disponible');
                                return response.json();
                            })
                            .then(function(data) {
                                routeCache[key] = data;
                                routeLayer.clearLayers();
                                drawStreetRoute(route, data);
                            })
                            .catch(function() {
                                routeLayer.clearLayers();
                                setNote('Ruta vial no disponible. Mostrando referencia directa.');
                                drawFallback(route);
                            });
                    }

                    window.renderRoutes = function(routes, selectedId) {
                        if (!ensureMap()) return;
                        routeLayer.clearLayers();

                        if (!routes || routes.length === 0) {
                            if (routeLayer) routeLayer.clearLayers();
                            setNote('Seleccione una ruta para verla en el mapa');
                            map.setView([3.451646, -76.531985], 12);
                            return;
                        }

                        loadStreetRoute(routes[0]);
                    };

                    ensureMap();
                </script>
            </body>
            </html>
            """;
    }

    private boolean contieneEstado(RutaSeguimiento ruta, String texto) {
        return normalizar(ruta.getEstado()).contains(texto);
    }

    private void conservarSeleccion(List<RutaSeguimiento> filtradas) {
        RutaSeguimiento seleccionada = tblRutas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            return;
        }

        boolean sigueVisible = filtradas.stream()
            .anyMatch(ruta -> ruta.getIdEntrega() == seleccionada.getIdEntrega());

        if (!sigueVisible) {
            tblRutas.getSelectionModel().clearSelection();
        }
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.toLowerCase(Locale.ROOT).trim();
    }

    private String escaparJson(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", " ")
            .replace("\r", " ")
            .replace("\t", " ");
    }

    private String numero(double valor) {
        return String.format(Locale.US, "%.6f", valor);
    }
}
