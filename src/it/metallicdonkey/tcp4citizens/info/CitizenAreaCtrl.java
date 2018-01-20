package it.metallicdonkey.tcp4citizens.info;

import java.io.IOException;

import it.metallicdonkey.tcp4citizens.App;
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
		loader.setLocation(App.class.getResource("info/InfoLineScreen.fxml"));
		AnchorPane lineInfo = (AnchorPane) loader.load();
		System.out.println("Resource done!");
		Scene scene = new Scene(lineInfo);
		System.out.println(scene);
		System.out.println(lineInfo);
		Stage stage = mainApp.getPrimaryStage();
		stage.setScene(scene);
		InfoLineCtrl lsvCtrl = loader.getController();
		lsvCtrl.setMainApp(mainApp);
	}

	@FXML
	private void showInfoStop() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(App.class.getResource("info/InfoStopScreen.fxml"));
		AnchorPane stopInfo = (AnchorPane) loader.load();
		System.out.println("Resource done!");
		Scene scene = new Scene(stopInfo);
		System.out.println(scene);
		System.out.println(stopInfo);
		Stage stage = mainApp.getPrimaryStage();
		stage.setScene(scene);
		InfoStopCtrl lsvCtrl = loader.getController();
		lsvCtrl.setMainApp(mainApp);
	}

	@FXML
	private void showInfoPath() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(App.class.getResource("info/InfoPathScreen.fxml"));
		AnchorPane pathInfo = (AnchorPane) loader.load();
		System.out.println("Resource done!");
		Scene scene = new Scene(pathInfo);
		System.out.println(scene);
		System.out.println(pathInfo);
		Stage stage = mainApp.getPrimaryStage();
		stage.setScene(scene);
		InfoPathCtrl lsvCtrl = loader.getController();
		lsvCtrl.setMainApp(mainApp);
	}


	public void setMainApp(App mainApp) {
		this.mainApp = mainApp;
	}
}
