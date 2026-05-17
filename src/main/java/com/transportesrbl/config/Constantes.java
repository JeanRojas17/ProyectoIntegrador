package com.transportesrbl.config;

public final class Constantes {

    private Constantes() {}

    // Roles
    public static final String ROL_ADMIN = "Admin";
    public static final String ROL_CONDUCTOR = "Conductor";
    public static final String ROL_AUXILIAR = "Auxiliar";
    public static final String ROL_CLIENTE = "Cliente";

    public static final int ID_ROL_ADMIN = 1;
    public static final int ID_ROL_CONDUCTOR = 2;
    public static final int ID_ROL_AUXILIAR = 5;
    public static final int ID_ROL_CLIENTE = 4;

    // Estados comunes
    public static final String ESTADO_PENDIENTE = "Pendiente";
    public static final String ESTADO_EN_REPARTO = "En reparto";
    public static final String ESTADO_ENTREGADO = "Entregado";
    public static final String ESTADO_NO_ENTREGADO = "No entregado";
    public static final String ESTADO_CANCELADO = "Cancelado";
    public static final String ESTADO_DISPONIBLE = "Disponible";
    public static final String ESTADO_EN_RUTA = "En ruta";
    public static final String ESTADO_MANTENIMIENTO = "Mantenimiento";
    public static final String ESTADO_ACTIVO = "ACTIVO";
    public static final String ESTADO_INACTIVO = "INACTIVO";
    public static final String ESTADO_CARGADO = "Cargado";

    // SQL - Subquery historial estados (repetido en múltiples DAOs)
    public static final String SQL_HISTORIAL_RECIENTE =
        "SELECT DISTINCT ON (Id_Asig_Paq) Id_Asig_Paq, Estado, Fecha " +
        "FROM HISTORIAL_ESTADOS " +
        "ORDER BY Id_Asig_Paq, Fecha DESC";

    public static final String SQL_HISTORIAL_RECIENTE_SIN_FECHA =
        "SELECT DISTINCT ON (Id_Asig_Paq) Id_Asig_Paq, Estado " +
        "FROM HISTORIAL_ESTADOS " +
        "ORDER BY Id_Asig_Paq, Fecha DESC";

    // Default password for new auxiliares
    public static final String DEFAULT_PASSWORD_AUXILIAR = "123456";

    // Simulación de rutas
    public static final double BASE_LAT = 3.451646;
    public static final double BASE_LON = -76.531985;
}
