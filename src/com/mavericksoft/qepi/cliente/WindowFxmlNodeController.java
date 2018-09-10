/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mavericksoft.qepi.cliente;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author lsegovia
 */
public class WindowFxmlNodeController implements Initializable {

    @FXML
    private AnchorPane AnchorPane;
    @FXML
    private Label labelSala;
    @FXML
    private TextArea textAreaOut;
    @FXML
    private TextField textFieldIn;
    @FXML
    private Button buttonAgregarUsuario;

    private Stage stagePrincipal;

    
    public Stage getStagePrincipal() {
        return stagePrincipal;
    }

    public void setStagePrincipal(Stage stagePrincipal) {
        this.stagePrincipal = stagePrincipal;
    }
    
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        textFieldIn.requestFocus();
        textFieldIn.setFocusTraversable(true);
    }    

    @FXML
    private void handletextFieldInSend(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)) {
             textAreaOut.appendText(textFieldIn.getText());
             textFieldIn.clear();
        }
    }

    @FXML
    private void handleButtonAgregarUsuarioSala(ActionEvent event) {
        windowAddUserChat();
    }

    @FXML
    private void handleButtonAddUserChat(MouseEvent event) {
    }
    
    private void windowAddUserChat() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddUserChatFxmlNode.fxml"));
        AnchorPane AnchorPaneAddUserChat = new AnchorPane();
        fxmlLoader.setRoot(AnchorPaneAddUserChat);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        //AddUserChatFxmlNodeController addUserChatController = fxmlLoader.getController();
        //addUserChatController.
        
        Stage stageAddUserChat = new Stage();
        Scene sceneAddUserChat = new Scene(AnchorPaneAddUserChat);
        Image applicationIcon = new Image(getClass().getResourceAsStream("/com/mavericksoft/qepi/recurso/qepi3333.png"));
        stageAddUserChat.getIcons().add(applicationIcon);      
        stageAddUserChat.setTitle("Qëpi");
        stageAddUserChat.setScene(sceneAddUserChat);
        stageAddUserChat.initOwner(stagePrincipal);
        //stageAcercaDe.initStyle(StageStyle.UTILITY);
        stageAddUserChat.resizableProperty().setValue(Boolean.FALSE);
        stageAddUserChat.initModality(Modality.APPLICATION_MODAL); 
        stageAddUserChat.showAndWait();
        
    }
    
}
