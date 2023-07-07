/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alertador;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author Usuario
 */
public class Ventana extends javax.swing.JFrame {

    /**
     * Creates new form Ventana
     */
    private PopupMenu popup;
    private final Image image;
    private final TrayIcon trayIcon;
    private Timer timer;
    public JLabel label_leyenda;
    private String url_web = "http://172.22.254.51/solicitud_rutafolio/";
    //private String url_web = "http://localhost/phpmvc/admin";

    public Ventana() {
        initComponents();

        //codigo propio
        popup = new PopupMenu();
        image = new ImageIcon(getClass().getResource("/imagenes/bell_celeste.png")).getImage();
        trayIcon = new TrayIcon(image, "Asignar Ruta y Folio", popup);
        label_leyenda = jLabel_mensaje;

        //inicia minimizado
        setExtendedState(JFrame.ICONIFIED);
        setVisible(false);
        this.repaint();

        //consulta si la funcion de bandeja de sistema está soportada.
        if (SystemTray.isSupported()) {
            //obtiene instancia SystemTray
            SystemTray systemtray = SystemTray.getSystemTray();
            trayIcon.setImageAutoSize(true);

            //acciones del raton sobre el icono en la barra de tareas
            MouseListener mouseListener = new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent evt) {

                    //Si se presiona con el boton izquierdo en el icono
                    //y la aplicacion esta minimizada se muestra una frase
                    if (evt.getButton() == MouseEvent.BUTTON1 && getExtendedState() == JFrame.ICONIFIED) {
                        MensajeTrayIcon("Monitor Nuevas Altas\n \"Notifica cuando hay una solicitud,\n para asignar nueva ruta y folio\"", TrayIcon.MessageType.INFO);

                        setVisible(true);
                        setExtendedState(JFrame.NORMAL);
                        repaint();
                    }
                }

                @Override
                public void mouseEntered(MouseEvent evt) {/*nada x aqui circulen...*/
                }

                @Override
                public void mouseExited(MouseEvent evt) {/*nada x aqui circulen...*/
                }

                @Override
                public void mousePressed(MouseEvent evt) {/*nada x aqui circulen...*/
                }

                @Override
                public void mouseReleased(MouseEvent evt) {/*nada x aqui circulen...*/
                }
            };

            /* ----------------- ACCIONES DEL MENU POPUP : COMIENZO--------------------- */
            //Salir de aplicacion
            ActionListener exitListener = (ActionEvent e) -> {
                System.exit(0);
            };

            //Restaurar aplicacion
            ActionListener restoreListener = (ActionEvent e) -> {
                //si esta minimizado restaura JFrame
                if (getExtendedState() == JFrame.ICONIFIED) {
                    setVisible(true);
                    setExtendedState(JFrame.NORMAL);
                    repaint();
                    //if(timer!=null) timer.cancel();
                    //JOptionPane.showMessageDialog(null, "Notificacion desde bandeja de sistema");
                }
            };

            //Se crean los Items del menu PopUp y se añaden
            MenuItem exitAppItem = new MenuItem("Salir");
            exitAppItem.addActionListener(exitListener);
            popup.add(exitAppItem);

            MenuItem restoreAppItem = new MenuItem("Restaurar");
            restoreAppItem.addActionListener(restoreListener);
            popup.add(restoreAppItem);

            /* ----------------- ACCIONES DEL MENU POPUP : END ---------------- */
            trayIcon.addMouseListener(mouseListener);

            //Enlace para ir directamente sl sistema web
            ActionListener enlace = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Desktop desk = Desktop.getDesktop();
                    try {
                        // now we enter our URL that we want to open in our default browser
                        desk.browse(new URI(url_web));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };

            //con el siguiente listener, se abre el navegador al hacer clic en el icono de la bandeja del sistema.
            trayIcon.addActionListener(enlace);

            //Añade el TrayIcon al SystemTray
            try {
                systemtray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println("Error:" + e.getMessage());
            }
        } else {
            System.err.println("Error: SystemTray no es soportado");
            return;
        }

        //Cuando se minimiza JFrame, se oculta para que no aparesca en la barra de tareas
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowIconified(WindowEvent e) {
                setVisible(false);//Se oculta JFrame

                /*
               //Se inicia una tarea cuando se minimiza           
               if(timer!=null) timer.cancel();
               timer = new Timer();           
               timer.schedule(new MyTimerTask(),2000, 10000 );//Se ejecuta cada 12 segundos
                 */
                iniciarMonitoreo();
            }
        });

        //la primera ves se hace una consulta
        iniciarMonitoreo();
    }

    //Muestra una burbuja con la accion que se realiza
    public void MensajeTrayIcon(String texto, TrayIcon.MessageType tipo) {
        trayIcon.displayMessage("Asignar Ruta y Folios:", texto, tipo);
        //trayIcon.displayMessage(texto, texto, tipo);
    }

    //Muestra una burbuja con la accion que se realiza
    public void MensajeTrayIcon(String titulo, String texto, TrayIcon.MessageType tipo) {
        trayIcon.displayMessage(titulo, texto, tipo);
    }

    //lanza la actividad de monitoreo
    public void iniciarMonitoreo() {
        //Se inicia el temporizador para que consulte por nuevas solicitudes
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new MyTimerTask(), 2000, 10000);//Se ejecuta cada 12 segundos
    }

    /**
     *
     * @author Usuario
     */
    public class MyTimerTask extends TimerTask {

        /**
         * clase interna que manejara una accion en segundo plano
         */
        @Override
        public void run() {
            actionBackground();
        }

        /**
         * accion a realizar cuando la aplicacion a sido minimizada
         */
        public void actionBackground() {
            //establecer un horario para hacer consultas 
            /**
             * ------------------------------------------------ Se establece un
             * horario para hacer consultas (de 8 a 15 de lunes a viernes)
             * ------------------------------------------------
             */
            if (this.comprobar_hora_valida()) {
                //consulta para saber si hay nuevas solicitudes
                int rowCount = 0;
                GestorBD gestor = new GestorBD();
                rowCount = gestor.getNumNuevos();
                switch (rowCount) {
                    case -1:
                        //MensajeTrayIcon("Se perdió la conexión con los datos.", TrayIcon.MessageType.ERROR);
                        JOptionPane.showOptionDialog(null, "Se perdió la conexión con los datos. No se puede conocer si hay nuevas solicitudes.",
                                "Advertencia - Rutas y Folios", JOptionPane.OK_OPTION,
                                JOptionPane.WARNING_MESSAGE, null,// null para icono por defecto.
                                new Object[]{"Aceptar"}, "Aceptar");
                        jLabel_mensaje.setText("Se perdió la conexión con los datos.");
                        jLabel_mensaje.setForeground(Color.MAGENTA);
                        jLabel_mensaje.repaint();
                        break;
                    case 0:
                        jLabel_mensaje.setText("No hay solicitudes nuevas");
                        jLabel_mensaje.setForeground(Color.BLACK);
                        jLabel_mensaje.repaint();
                        break;
                    default:
                        jLabel_mensaje.setText("Hay " + rowCount + " solicitud(es) nueva(s).");
                        jLabel_mensaje.setForeground(Color.RED);
                        jLabel_mensaje.repaint();

                        //MensajeTrayIcon("Asignar Ruta y Folio (" + rowCount + ")", "Hay " + rowCount + " solicitud(es) nueva(s).", TrayIcon.MessageType.INFO);
                        /**
                         * ------------------------------------------------------------------
                         * Mensaje de dialogo que alerta sobre nuevas
                         * solicitudes
                         *------------------------------------------------------------------
                         */
                        int seleccion = JOptionPane.showOptionDialog(null, "Hay nuevas solicitudes de Ruta y Folio sin atender ¿Qué desea hacer?",
                                "Solicitudes de Ruta y Folio...", JOptionPane.YES_NO_OPTION,
                                JOptionPane.INFORMATION_MESSAGE, null,// null para icono por defecto.
                                new Object[]{"Ir al Sistema", "Cancelar"}, "Ir al Sistema");

                        if (seleccion != -1) {
                            if (seleccion == 0) { //primera opcion
                                Desktop desk = Desktop.getDesktop();
                                try {
                                    // URL del sistema de respuesta
                                    desk.browse(new URI(url_web));
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                }
            }else{
                //no es un dia y horario valido, no hago nada, solo muestro por consola
                System.out.println("Dia y horario invalido.");
            }
        }

        /**
         * Comprueba si la hora actual está dentro de una franja horaria valida
         * Se toma el horario del puesto de trabajo.
         * La franja horaria valida es de 8 a 15 hs
         * Los días validos son de lunes a viernes.
         * @return true cuando esta dentro del horario valido
         */
        public boolean comprobar_hora_valida() {
            Calendar now = Calendar.getInstance();
            if (now.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && now.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                //es valido el día, consulto por el horario
                return (now.get(Calendar.HOUR_OF_DAY) >= 8 && now.get(Calendar.HOUR_OF_DAY) < 15);
            } else {
                return false;
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel_mensaje = new javax.swing.JLabel();
        jLabel_titulo = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel_mensaje.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel_mensaje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_mensaje.setText("Para recibir notificaciones sobre solicitudes de nuevas rutas y folios, minimizar esta pantalla y no cerrarla. ");

        jLabel_titulo.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel_titulo.setText("Monitor de Solicitudes para Nueva Ruta y Folio");

        jButton1.setBackground(new java.awt.Color(51, 102, 255));
        jButton1.setFont(new java.awt.Font("Arial", 0, 20)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Ir al sitio Web");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel_titulo)
                .addGap(361, 361, 361))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(456, 456, 456)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel_mensaje, javax.swing.GroupLayout.DEFAULT_SIZE, 1120, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel_titulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
                .addComponent(jLabel_mensaje)
                .addGap(57, 57, 57)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(45, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        //cuando presiona el boton, se navega por la pagina web
        Desktop desk = Desktop.getDesktop();
        try {
            // now we enter our URL that we want to open in our default browser
            desk.browse(new URI(url_web));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Ventana().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel_mensaje;
    private javax.swing.JLabel jLabel_titulo;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
