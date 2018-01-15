package it.metallicdonkey.tcp.citizenArea;

import java.io.IOException;

import it.metallicdonkey.tcp.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class CitizenAreaCtrl {
	private App mainApp;
	
  @FXML
  private void initialize() {
  }
 
  @FXML
  private void showInfoLine() throws IOException {
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(App.class.getResource("citizenArea/InfoLineScreen.fxml"));
    AnchorPane personalInfo = (AnchorPane) loader.load();
    System.out.println("Resource done!");
	    
		Scene scene = new Scene(personalInfo);
		System.out.println(scene);
		System.out.println(personalInfo);
    Stage stage = mainApp.getPrimaryStage();
    stage.setScene(scene);
    InfoLineCtrl lsvCtrl = loader.getController();
    lsvCtrl.setMainApp(mainApp);
  }
  
  @FXML
  private void showInfoStop() {
  	
  }
  
  @FXML
  private void showInfoPath() {
  	
  }
 
  
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }
}
