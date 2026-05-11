package com.transportesrbl.services;

import com.transportesrbl.dao.RutaDAO;
import com.transportesrbl.models.RutaSeguimiento;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RutaService {

    private static final double BASE_LAT = 3.451646;
    private static final double BASE_LON = -76.531985;
    private static final String[] COLORES = {
        "#2563eb", "#16a34a", "#dc2626", "#9333ea", "#ea580c", "#0891b2", "#be123c", "#4f46e5"
    };

    private final RutaDAO rutaDAO = new RutaDAO();
    private final Map<String, double[]> destinosConocidos = crearDestinosConocidos();

    public List<RutaSeguimiento> listarRutas() {
        List<RutaSeguimiento> rutas = rutaDAO.listarRutasActivas();
        for (int i = 0; i < rutas.size(); i++) {
            completarSeguimiento(rutas.get(i), i);
        }
        return rutas;
    }

    private void completarSeguimiento(RutaSeguimiento ruta, int indice) {
        double[] destino = resolverDestino(ruta.getDestino());
        double progreso = calcularProgreso(ruta);
        double curva = Math.sin(progreso * Math.PI) * 0.006;

        ruta.setOrigenLat(BASE_LAT);
        ruta.setOrigenLon(BASE_LON);
        ruta.setDestinoLat(destino[0]);
        ruta.setDestinoLon(destino[1]);
        if (ruta.isUbicacionReal() && estaEnMovimiento(ruta)) {
            progreso = calcularProgresoReal(ruta, destino);
        } else {
            ruta.setActualLat(interpolar(BASE_LAT, destino[0], progreso) + curva);
            ruta.setActualLon(interpolar(BASE_LON, destino[1], progreso) - curva / 2);
        }
        ruta.setProgreso(progreso);
        ruta.setEtaMinutos(Math.max(0, (int) Math.round((1.0 - progreso) * 75)));
        ruta.setColor(COLORES[Math.abs(ruta.getIdEntrega() + indice) % COLORES.length]);
    }

    private double calcularProgreso(RutaSeguimiento ruta) {
        String estado = normalizar(ruta.getEstado());
        if (estado.contains("entregado")) {
            return 1.0;
        }
        if (estado.contains("pendiente")) {
            return 0.0;
        }
        if (estado.contains("no entregado") || estado.contains("cancelado")) {
            return 0.82;
        }

        long segundos = System.currentTimeMillis() / 1000L;
        double avance = ((segundos + ruta.getIdEntrega() * 13L) % 75) / 100.0;
        return Math.min(0.95, 0.18 + avance);
    }

    private double[] resolverDestino(String direccion) {
        String clave = normalizar(direccion);
        for (Map.Entry<String, double[]> entry : destinosConocidos.entrySet()) {
            if (clave.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        int hash = Math.abs(clave.hashCode());
        double latOffset = ((hash % 1200) / 10000.0) - 0.0600;
        double lonOffset = (((hash / 1200) % 1200) / 10000.0) - 0.0600;
        return new double[] { BASE_LAT + latOffset, BASE_LON + lonOffset };
    }

    private Map<String, double[]> crearDestinosConocidos() {
        Map<String, double[]> destinos = new HashMap<>();
        destinos.put("cali norte", new double[] { 3.491930, -76.515090 });
        destinos.put("norte", new double[] { 3.491930, -76.515090 });
        destinos.put("cali sur", new double[] { 3.382430, -76.537640 });
        destinos.put("sur", new double[] { 3.382430, -76.537640 });
        destinos.put("cali centro", new double[] { 3.451620, -76.532000 });
        destinos.put("centro", new double[] { 3.451620, -76.532000 });
        destinos.put("yumbo", new double[] { 3.585010, -76.495730 });
        destinos.put("jamundi", new double[] { 3.260740, -76.541820 });
        destinos.put("palmira", new double[] { 3.539440, -76.303610 });
        destinos.put("candelaria", new double[] { 3.406710, -76.348190 });
        destinos.put("sameco", new double[] { 3.501600, -76.505920 });
        destinos.put("alfonso lopez", new double[] { 3.447160, -76.481730 });
        destinos.put("ciudad jardin", new double[] { 3.363250, -76.529500 });
        return destinos;
    }

    private double interpolar(double inicio, double fin, double progreso) {
        return inicio + (fin - inicio) * progreso;
    }

    private double calcularProgresoReal(RutaSeguimiento ruta, double[] destino) {
        double vectorLat = destino[0] - BASE_LAT;
        double vectorLon = destino[1] - BASE_LON;
        double vectorActualLat = ruta.getActualLat() - BASE_LAT;
        double vectorActualLon = ruta.getActualLon() - BASE_LON;
        double denominador = vectorLat * vectorLat + vectorLon * vectorLon;

        if (denominador == 0) {
            return 0;
        }

        double progreso = (vectorActualLat * vectorLat + vectorActualLon * vectorLon) / denominador;
        return Math.max(0, Math.min(1, progreso));
    }

    private boolean estaEnMovimiento(RutaSeguimiento ruta) {
        String estado = normalizar(ruta.getEstado());
        return estado.contains("reparto") || estado.contains("progreso");
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return "";
        }
        String texto = Normalizer.normalize(valor, Normalizer.Form.NFD);
        texto = texto.replaceAll("\\p{M}", "");
        return texto.toLowerCase(Locale.ROOT).trim();
    }
}