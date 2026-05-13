package com.transportesrbl.controllers;

import com.transportesrbl.models.Asignacion;
import com.transportesrbl.models.SesionUsuario;
import com.transportesrbl.models.Usuario;
import com.transportesrbl.services.ConductorService;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class MisRutasController {

    @FXML private Label lblFecha;
    @FXML private Label lblCompletadas;
    @FXML private Label lblEnProgreso;
    @FXML private Label lblPendientes;
    @FXML private VBox vboxRutas;

    private final ConductorService conductorService = new ConductorService();

    @FXML
    public void initialize() {
        Usuario user = SesionUsuario.getInstancia().getUsuarioActivo();
        if (user != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
            lblFecha.setText(LocalDate.now().format(formatter));

            int idConductor = conductorService.obtenerIdConductor(user.getId());
            if (idConductor != -1) {
                cargarRutas(idConductor);
            }
        }
    }

    private void cargarRutas(int idConductor) {
        List<Asignacion> rutas = conductorService.obtenerRutas(idConductor);
        vboxRutas.getChildren().clear();

        int completadas = 0;
        int enProgreso = 0;
        int pendientes = 0;

        for (int i = 0; i < rutas.size(); i++) {
            Asignacion asig = rutas.get(i);
            boolean isLast = (i == rutas.size() - 1);
            
            HBox item = crearItemRuta(asig, isLast);
            vboxRutas.getChildren().add(item);

            String estado = asig.getEstado() != null ? asig.getEstado().toLowerCase() : "";
            if (estado.contains("entregado")) completadas++;
            else if (estado.contains("reparto") || estado.contains("progreso")) enProgreso++;
            else pendientes++;
        }

        lblCompletadas.setText(String.valueOf(completadas));
        lblEnProgreso.setText(String.valueOf(enProgreso));
        lblPendientes.setText(String.valueOf(pendientes));
    }

    private HBox crearItemRuta(Asignacion asig, boolean isLast) {
        HBox container = new HBox();
        String estado = asig.getEstado() != null ? asig.getEstado().toLowerCase() : "";
        
        if (estado.contains("entregado")) {
            container.getStyleClass().add("timeline-item-completed");
        } else if (estado.contains("reparto") || estado.contains("progreso")) {
            container.getStyleClass().add("timeline-item-active");
        } else {
            container.getStyleClass().add("timeline-item-pending");
        }

        VBox timelineCol = new VBox();
        timelineCol.setAlignment(Pos.TOP_CENTER);
        timelineCol.setMinWidth(40);
        timelineCol.setSpacing(5);

        Circle dot = new Circle(8);
        Region line = new Region();
        line.getStyleClass().add("line-connector");
        VBox.setVgrow(line, Priority.ALWAYS);

        if (estado.contains("entregado")) {
            dot.getStyleClass().add("dot-completed");
        } else if (estado.contains("reparto") || estado.contains("progreso")) {
            dot.getStyleClass().add("dot-active");
        } else {
            dot.getStyleClass().add("dot-pending");
        }

        timelineCol.getChildren().add(dot);
        if (!isLast) {
            timelineCol.getChildren().add(line);
        }

        VBox card = new VBox();
        HBox.setHgrow(card, Priority.ALWAYS);
        
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label(asig.getProducto().toUpperCase() + " — " + asig.getRuta().split(",")[0].toUpperCase());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label tag = new Label(asig.getEstado().toUpperCase());

        if (estado.contains("entregado")) {
            card.getStyleClass().add("route-card-completed");
            title.getStyleClass().add("card-title-done");
            tag.getStyleClass().add("tag-completed");
        } else if (estado.contains("reparto") || estado.contains("progreso")) {
            card.getStyleClass().add("route-card-active");
            title.getStyleClass().add("card-title-active");
            tag.getStyleClass().add("tag-active");
        } else {
            card.getStyleClass().add("route-card-pending");
            title.getStyleClass().add("card-title-pending");
            tag.getStyleClass().add("tag-pending");
            tag.setText("PENDIENTE");
        }

        header.getChildren().addAll(title, spacer, tag);
        
        Label subTitle = new Label(asig.getRuta());
        subTitle.getStyleClass().add(estado.contains("reparto") ? "card-subtitle-active" : "card-subtitle");

        card.getChildren().addAll(header, subTitle);

        if (!estado.equals("pendiente")) {
            card.getChildren().add(new Separator());
            GridPane details = new GridPane();
            details.setHgap(30);

            VBox v1 = createDetailBox("CAMIÓN", asig.getCamion());
            VBox v2 = createDetailBox("DESTINO", asig.getRuta().contains(",") ? asig.getRuta().split(",")[1].trim() : "Principal");

            details.add(v1, 0, 0);
            details.add(v2, 1, 0);
            card.getChildren().add(details);
        }

        container.getChildren().addAll(timelineCol, card);
        VBox.setMargin(container, new javafx.geometry.Insets(0, 0, 20, 0));

        return container;
    }

    private VBox createDetailBox(String label, String value) {
        VBox box = new VBox();
        Label lbl = new Label(label);
        lbl.getStyleClass().add("info-label");
        Label val = new Label(value);
        val.getStyleClass().add("info-value");
        box.getChildren().addAll(lbl, val);
        return box;
    }
}