package alertador;

//import java.awt.Toolkit;
//import java.awt.TrayIcon;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author gustavo
 */
public class GestorBD {

    /**
     * Funcion que valida las credenciales de acceso, se corresponden a las que
     * tienen en el .241
     *
     * @param usuario
     * @param clave
     * @return int con el codigo de acceso: -2 error general, -1 error BD en la
     * consulta, 0 invalidas, 1 correcto
     */
    public int validarAcceso(String usuario, String clave) {
        String sql;
        int respuesta = -2;
        PreparedStatement ps = null;
        int rowCount = 0;
        ResultSet rs = null;

        try {
            sql = "SELECT\n"
                    + "* \n"
                    + "            FROM sistemas.usuarios \n"
                    + "            INNER JOIN sistemas.funciones_de_usuarios    ON usuarios.id_usuarios = funciones_de_usuarios.id_usuario \n"
                    + "            WHERE usuarios.nombre = ? AND usuarios.clave = PASSWORD(?)\n"
                    + "            AND funciones_de_usuarios.id_funcion = 253";
            ps = Conexion.getConnection241().prepareStatement(sql);
            ps.setString(1, usuario);
            ps.setString(2, clave);

            rs = ps.executeQuery();

            //forma de contar los resultados totales
            while (rs.next()) {
                rowCount++;
            }

            if (rowCount == 0) {
                //no hay resultados con el criterio, no se hace nada
                //Enviar codigo 0
                respuesta = 0;
            } else {
                //hubo credenciales validas
                respuesta = 1;
            }

            //cierre de la conexion
            ps.close();
            Conexion.cerrarConexion();
        } catch (SQLException ex) {
            //no hay conexion, se avisa que se perdió la conexion
            System.out.println(ex.getMessage());
            respuesta = -1;
        }
        return respuesta;
    }

    public int getNumNuevos() {
        String sql;
        int respuesta = -1;
        PreparedStatement ps = null;
        int rowCount = 0;
        ResultSet rs = null;
        try {
            sql = "SELECT COUNT(*) AS nuevos "
                    + "FROM `asignafolio`.`folio_solicitudes` fs "
                    + "LEFT JOIN respuestas resp ON resp.fk_folio_solicitudes = fs.id "
                    + "WHERE resp.respuesta IS null";
            ps = Conexion.getConnection().prepareStatement(sql);
            rs = ps.executeQuery();

            //forma de contar los resultados totales
            //while (rs.next()) {
            if (rs.next()) {
                respuesta = rs.getInt("nuevos");
                //rowCount++;
            }

        } catch (SQLException ex) {
            //no hay conexion, se avisa que se perdió la conexion
            System.out.println(ex.getMessage());
            respuesta = -1;
        }
        return respuesta;
    }

}
