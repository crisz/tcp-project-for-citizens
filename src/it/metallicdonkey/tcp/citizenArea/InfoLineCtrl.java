package it.metallicdonkey.tcp.citizenArea;

import java.io.IOException;

import it.metallicdonkey.tcp.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class InfoLineCtrl {
	private App mainApp;
	
  @FXML
  private void initialize() {
  }
 
 
  
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }
}
