package it.metallicdonkey.tcp4citizens.info;

import java.sql.SQLException;
import java.util.ArrayList;

import com.mysql.fabric.jdbc.ErrorReportingExceptionInterceptor;

import it.metallicdonkey.tcp.db.DBHelperLine;
import it.metallicdonkey.tcp.models.Line;
import it.metallicdonkey.tcp.models.Stop;
import it.metallicdonkey.tcp4citizens.App;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class InfoPathCtrl {
	private App mainApp;
	
	@FXML
	Button calcButton;
	
	@FXML
	TextField startField;
	@FXML
	TextField endField;
	@FXML
	ListView<Line> linesList;
	@FXML
	ListView<String> pathList;

	@FXML
	private void initialize() {
	 
	}
	
	@FXML
	private void onCalcClicked() {
		Alert error = check();
		
		if(error != null) {
			error.showAndWait();
			return;
		}
		
		if (areStopsOk()) {
			// TODO calculate path
		}
		else {
			Alert alert = new Alert(AlertType.WARNING);
		    alert.initOwner(mainApp.getPrimaryStage());
		    alert.setTitle("Avviso");
		    alert.setHeaderText("Calcolo percorso fallito");
		    alert.setContentText("Le fermate inserite non esistono");
		}
	}
	
	private boolean areStopsOk() {
		Stop start = new Stop(startField.getText());
		Stop end = new Stop(endField.getText());
		ArrayList<Stop> allStops = null;
		try {
			allStops = DBHelperLine.getInstance().getAllStopsArray();
		} catch (SQLException e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.WARNING);
		    alert.initOwner(mainApp.getPrimaryStage());
		    alert.setTitle("Avviso");
		    alert.setHeaderText("Connessione non disponibile");
		    alert.setContentText("Controlla la connessione e riprova");
			return false;
		}
		return (allStops.contains(start) && allStops.contains(end));
	}
	
	private Alert check() {
		Alert alert = new Alert(AlertType.WARNING);
	    alert.initOwner(mainApp.getPrimaryStage());
	    alert.setTitle("Avviso");
	    alert.setHeaderText("Calcolo percorso fallito");
	    
		if(startField.getText().equals("")) {
			alert.setContentText("Inserisci una fermata di partenza");
			return alert;
		}
		
		if(endField.getText().equals("")) {
			alert.setContentText("Inserisci una fermata di arrivo");
			return alert;
		}
		
		// If it's all ok
		return null;
	}



	public void setMainApp(App mainApp) {
	  this.mainApp = mainApp;
	}
}
