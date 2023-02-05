/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package appinformes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Alejandro
 */
public class AppInformes extends Application {

    public static Connection conexion = null;

    @Override
    public void start(Stage primaryStage) {
        //establecemos la conexión con la BD
        conectaBD();
       
        Menu menu = new Menu("Informes");
        Menu menu2 = new Menu("Ayuda");
        Menu menu3 = new Menu("Salir");
        
        MenuItem menuItem1 = new MenuItem("Listado de facturas");
        MenuItem menuItem2 = new MenuItem("Ventas Totales");
        MenuItem menuItem3 = new MenuItem("Facturas por cliente");
        MenuItem menuItem4 = new MenuItem("Subinforme Listado Facturas");
        
        MenuItem menuItemSalir = new MenuItem("Salir..."); 

        menu.getItems().add(menuItem1);
        menu.getItems().add(menuItem2);
        menu.getItems().add(menuItem3);
        menu.getItems().add(menuItem4);
        
        menu3.getItems().add(menuItemSalir);
        
        menuItemSalir.setOnAction((ActionEvent t) -> {
            System.exit(0);
        });
        
        menuItem1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
               generaInforme("facturas.jasper");
            }
        });
        
        menuItem2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
               generaInforme("Ventas_Totales.jasper");
            }
        });
        
        menuItem3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showInputTextDialog();
            }
        });
        
        menuItem4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                generaInformeConSubinforme("listado_Facturas.jasper","subinformeFactura.jasper");
            }
        });
     
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(menu, menu2, menu3);
        VBox vBox = new VBox(menuBar);
        vBox.setId("VB");
        Scene scene = new Scene(vBox, 700, 500);
        scene.getStylesheets().addAll(this.getClass().getResource("/Style/style.css").toExternalForm());
        primaryStage.setTitle("AppInformes");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        try {
            DriverManager.getConnection("jdbc:hsqldb:hsql://localhost;shutdown=true");
        } catch (Exception ex) {
            System.out.println("No se pudo cerrar la conexion a la BD");
        }
    }

    public void conectaBD() {
//Establecemos conexión con la BD
        String baseDatos = "jdbc:hsqldb:hsql://localhost/sampleDB";
        String usuario = "sa";
        String clave = "";
        try {
            Class.forName("org.hsqldb.jdbcDriver").newInstance();
            conexion = DriverManager.getConnection(baseDatos, usuario, clave);
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Fallo al cargar JDBC");
            System.exit(1);
        } catch (SQLException sqle) {
            System.err.println("No se pudo conectar a BD");
            System.exit(1);
        } catch (java.lang.InstantiationException sqlex) {
            System.err.println("Imposible Conectar");
            System.exit(1);
        } catch (Exception ex) {
            System.err.println("Imposible Conectar");
            System.exit(1);
        }
    }

    public void generaInforme(String nombreInforme) {
        try {
            JasperReport jr = (JasperReport) JRLoader.loadObject(AppInformes.class.getResource(nombreInforme));
            //Map de parámetros
            Map parametros = new HashMap();
            JasperPrint jp = (JasperPrint) JasperFillManager.fillReport(jr, parametros, conexion);
            JasperViewer.viewReport(jp, false);
        } catch (JRException ex) {
            System.out.println("Error al recuperar el jasper");
            JOptionPane.showMessageDialog(null, ex);
        }
    } 
    
    public void generaInformeConParametro(String nombreInforme, String parametro) {
   
        try {
            JasperReport jr = (JasperReport) JRLoader.loadObject(AppInformes.class.getResource(nombreInforme));
            System.out.println(Integer.valueOf(parametro));
            //Map de parámetros
            Map parametros = new HashMap();
            System.out.println(Integer.valueOf(parametro));
            int nproducto = Integer.parseInt(parametro);
            parametros.put("id_Cliente", nproducto);
            JasperPrint jp = (JasperPrint) JasperFillManager.fillReport(jr, parametros, conexion);
            JasperViewer.viewReport(jp, false);
        } catch (JRException ex) {
            System.out.println("Error al recuperar el jasper");
            JOptionPane.showMessageDialog(null, ex);
        }
    }
    
    public void generaInformeConSubinforme(String nombreInforme, String nombreSubinforme) {
        try {
            JasperReport jr = (JasperReport) JRLoader.loadObject(AppInformes.class.getResource(nombreInforme));
            JasperReport jsr = (JasperReport) JRLoader.loadObject(AppInformes.class.getResource(nombreSubinforme));
            //Map de parámetros
            Map parametros = new HashMap();
            parametros.put("subReportParameter", jsr);
            //Ya tenemos los datos para instanciar un objeto JasperPrint que permite ver, //imprimir o exportar a otros formatos
            JasperPrint jp = (JasperPrint) JasperFillManager.fillReport(jr, parametros,conexion);
            JasperViewer.viewReport(jp, false);
        } catch (JRException ex) {
            System.out.println("Error al recuperar el jasper");
            JOptionPane.showMessageDialog(null, ex);
        }
    }
    
    public void showInputTextDialog() {
        TextInputDialog cuadro_Dialogo = new TextInputDialog();
        cuadro_Dialogo.setTitle("Inserción parámetro");
        cuadro_Dialogo.setHeaderText("Introducca el id del cliente:");
        cuadro_Dialogo.setContentText("ID:");
        Optional<String> result = cuadro_Dialogo.showAndWait();
        result.ifPresent(parametro -> {
            generaInformeConParametro("factura_por_cliente.jasper", parametro);
        });
    }
   

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
