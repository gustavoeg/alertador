package alertador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    //Connection conexion;
    private static Connection conexion = null;
    private static Connection conexion241 = null;

    public static Connection getConnection() throws SQLException{
        int error = 0;
        String usuario, pass, bd, host;
        usuario = "user_asignafolios";
        pass = "37H!Z*j3o";
        bd = "asignafolio";
        host = "172.22.254.51";
        try {
            if (conexion == null || conexion.isClosed()) {
                //Cargar driver especifico de base de datos
                Class.forName("com.mysql.jdbc.Driver");
                conexion = DriverManager.getConnection("jdbc:mysql://" + host + "/" + bd + "?useSSL=false", usuario, pass);
            }
        } catch (SQLException e) {
            //e.printStackTrace();
            if (e.getErrorCode() == 1049) {
                //no existe la base de datos
                error = 1;
            } else if (e.getSQLState().equalsIgnoreCase("08S01")) {
                error = 1;
            } else {
                error = 1;
            }
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
            error = 1;
        } finally {
            if (error != 0) {
                throw new SQLException("Perdida de conexión");
                //conexion = null;
            }
        }
        return conexion;
    }

    public static Connection getConnection241() {
        int error = 0;
        String usuario, pass, bd, host;
        usuario = "login_e";
        pass = "1p8nInERS4AJ";
        bd = "sistemas";
        host = "192.168.0.241";
        try {
            if (conexion241 == null || conexion241.isClosed()) {
                //Cargar driver especifico de base de datos
                Class.forName("com.mysql.jdbc.Driver");
                conexion241 = DriverManager.getConnection("jdbc:mysql://" + host + "/" + bd + "?useSSL=false", usuario, pass);
            }
        } catch (SQLException e) {
            //e.printStackTrace();
            if (e.getErrorCode() == 1049) {
                //no existe la base de datos
                error = 1;
            } else if (e.getSQLState().equalsIgnoreCase("08S01")) {
                error = 1;
            } else {
                error = 1;
            }
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
            error = 1;
        } finally {
            if (error != 0) {
                //throw new SQLException(error);
                conexion241 = null;
            }
        }
        return conexion241;
    }

    public void cerrarConection() {
        boolean cerrado = false;
        try {
            conexion.close();
            conexion241.close();
            cerrado = true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            cerrado = false;
        } finally {
            if (!cerrado) {
                //throw new ExcepcionAutogestion(1);
            }
        }
    }

    public static void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                conexion241.close();
            } catch (SQLException e) {
                //como se cierra no habría inconveniente
            }
        }
    }
}
