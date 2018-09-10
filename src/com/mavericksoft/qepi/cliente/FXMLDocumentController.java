/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mavericksoft.qepi.cliente;

import com.mavericksoft.qepi.recurso.Personaje;
import com.mavericksoft.qepi.recurso.Protocolo;
import com.mavericksoft.qepi.recurso.RecursoConexion;
import com.mavericksoft.qepi.recurso.Universo;
import com.mavericksoft.qepi.recurso.Utilitario;
import com.mavericksoft.qepi.servidor.DatosCliente;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

/**
 *
 * @author lsegovia
 */
public class FXMLDocumentController implements Initializable {
    
    private Utilitario util;
    private DatosCliente datosClienteOrigen;
    private ObservableList<Personaje> personajesObservable;

    @FXML
    private ComboBox aliasComboBox;
    @FXML
    private ListView<?> usuariosListView;
    @FXML
    private Label nombreSistemaLb;
    
    private ToggleGroup group;
    @FXML
    private ToggleButton onToggleButton;
    @FXML
    private ToggleButton offToggleButton;
    //@FXML
    //private AnchorPane AnchorPane;
    private Stage stagePrincipal;

    
    public Stage getStagePrincipal() {
        return stagePrincipal;
    }

    public void setStagePrincipal(Stage stagePrincipal) {
        this.stagePrincipal = stagePrincipal;
    }
    
    

    
    
    @FXML
    private void handleOnToggleButtonAction(ActionEvent event) {
        if(onToggleButton.isSelected()){
            onToggleButton.setSelected(true);
            offToggleButton.setSelected(false);
            System.out.println("You clicked me On!");
            accionConectarPersonaje();
        }else{
            onToggleButton.setSelected(false);
            offToggleButton.setSelected(true);
        }
    }
    
    @FXML
    private void handleOffToggleButtonAction(ActionEvent event) {
        if(offToggleButton.isSelected()){
            System.out.println("You clicked me Off!");
            offToggleButton.setSelected(true);
            onToggleButton.setSelected(false);
        }else{
            offToggleButton.setSelected(false);
            onToggleButton.setSelected(true);
            accionConectarPersonaje();
        }
        
        windowChat();
        
    }
    
    
    private void windowChat() {
        /*
        Stage stageWindowChat = new Stage();
        WindowFxmlNode windowChatRoot = new WindowFxmlNode();
        Scene sceneChat = new Scene(windowChatRoot);
        Image applicationIcon = new Image(getClass().getResourceAsStream("/com/mavericksoft/qepi/recurso/qepi33.png"));
        stageWindowChat.getIcons().add(applicationIcon);      
        stageWindowChat.setTitle("Qëpi");
        stageWindowChat.setScene(sceneChat);
        stageWindowChat.show();
        */
        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("WindowFxmlNode.fxml"));
        AnchorPane AnchorPaneChat = new AnchorPane();
        fxmlLoader.setRoot(AnchorPaneChat);
        //fxmlLoader.setController(AnchorPaneChat);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        Stage stageWindowChat = new Stage();
        Scene sceneChat = new Scene(AnchorPaneChat);
        Image applicationIcon = new Image(getClass().getResourceAsStream("/com/mavericksoft/qepi/recurso/qepi3333.png"));
        stageWindowChat.getIcons().add(applicationIcon);      
        stageWindowChat.setTitle("Qëpi");
        stageWindowChat.setScene(sceneChat);
        stageWindowChat.initOwner(stagePrincipal);
        stageWindowChat.resizableProperty().setValue(Boolean.FALSE);
        //stageWindowChat.initStyle(StageStyle.UTILITY);
        stageWindowChat.show();
        
    }
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargar();
        
    }    
    
    
    private void cargar(){
        util = new Utilitario();
        ToggleGroup group = new ToggleGroup();
        onToggleButton.setToggleGroup(group);
        offToggleButton.setToggleGroup(group);
        offToggleButton.setSelected(true);
        onToggleButton.setSelected(false);
        personajesObservable = FXCollections.observableArrayList();
        personajesObservable.addAll(util.getPersonajes());
        personajesObservable.addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change c) {
                System.out.println(".onChanged()");
            }
        });
        /*
         // Se insta una lista.
        List<String> list = new ArrayList<String>();
        list.add("Clark");
        list.add("Peter");
        list.add("Luke");
        // se agrega la observación usando FXCollections:
        ObservableList<String> observableList = FXCollections.observableList(list);
        observableList.addListener(new ListChangeListener() {

            @Override
            public void onChanged(ListChangeListener.Change change) {
                System.out.println("Ocurrio un cambio! ");
            }
        });

        // Se reportan cambios a observableList.
        // Aquí se va imprimir la alerta 
        observableList.add("Aquaman");

        // cambios directas a la lista escapan la observación
        // no se imprime ninguna alerta 
        list.add("Thor");
        
        System.out.println("Tamano: "+observableList.size());
        
        aliasComboBox.setItems(observableList);
        */
        //aliasComboBox = new ComboBox<>(o);
        aliasComboBox.setItems(personajesObservable);
        aliasComboBox.getSelectionModel().selectFirst();
        //aliasComboBox.show();
        System.out.println("ObservaleList o: "+personajesObservable);
    }
    

    @FXML
    private void handleViewAcercaDeQuepiLabelAction(MouseEvent event) {
        windowAcercaDe();
    }
    
    
    private void windowAcercaDe() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AcercaDeFxmlNode.fxml"));
        AnchorPane AnchorPaneAcercaDe = new AnchorPane();
        fxmlLoader.setRoot(AnchorPaneAcercaDe);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        Stage stageAcercaDe = new Stage();
        Scene sceneAcercaDe = new Scene(AnchorPaneAcercaDe);
        Image applicationIcon = new Image(getClass().getResourceAsStream("/com/mavericksoft/qepi/recurso/qepi3333.png"));
        stageAcercaDe.getIcons().add(applicationIcon);      
        stageAcercaDe.setTitle("Qëpi");
        stageAcercaDe.setScene(sceneAcercaDe);
        stageAcercaDe.initOwner(stagePrincipal);
        //stageAcercaDe.initStyle(StageStyle.UTILITY);
        stageAcercaDe.resizableProperty().setValue(Boolean.FALSE);
        stageAcercaDe.initModality(Modality.APPLICATION_MODAL); 
        stageAcercaDe.showAndWait();
    }
    
    
    private void accionConectarPersonaje(){
        Socket clientSocket;
        DataInputStream in;
        DataOutputStream out;
        String idPersonaje = Utilitario.rellenarEspaciosDerecha(((Personaje)aliasComboBox.getValue()).getId(), 3);
        String idEscenario = "   ";
        String idPersonajeDestino = "   ";
        String mensaje = " ";
        String mensajeSalida = Protocolo.RECIBE_FROM_CLIENT + Protocolo.ACTION_CONNECT
                + idPersonaje + idEscenario + idPersonajeDestino + mensaje;
        Utilitario.imprimir(  "-->"+mensajeSalida+"<--"  );
        try {
            clientSocket = new Socket(util.getRecursoConexion().getServidor(), util.getRecursoConexion().getPuerto());
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            datosClienteOrigen = new DatosCliente(((Personaje)aliasComboBox.getValue()).getId(), "   ", in, out);
            Utilitario.enviarMensaje(out, mensajeSalida);
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
