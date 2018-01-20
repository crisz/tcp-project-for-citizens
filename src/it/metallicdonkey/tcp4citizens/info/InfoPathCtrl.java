package it.metallicdonkey.tcp4citizens.info;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import com.mysql.fabric.jdbc.ErrorReportingExceptionInterceptor;

import it.metallicdonkey.tcp.db.DBHelperLine;
import it.metallicdonkey.tcp.models.Line;
import it.metallicdonkey.tcp.models.Stop;
import it.metallicdonkey.tcp4citizens.App;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class InfoPathCtrl {
	private App mainApp;
	private Stop start;
	private Stop end;
	
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
			List<Line>lines = null;
			try {
				lines = DBHelperLine.getInstance().getPath(start, end);
			}
			catch (SQLException e) {
				Alert alert = new Alert(AlertType.WARNING);
			    alert.initOwner(mainApp.getPrimaryStage());
			    alert.setTitle("Avviso");
			    alert.setHeaderText("Connessione non disponibile");
			    alert.setContentText("Controlla la connessione e riprova");
				e.printStackTrace();
				return;
			}
			
			if(lines.isEmpty()) {
				pathList.setItems(FXCollections.observableArrayList("Nessun percorso"));
			}
			
			// Check if there's a single Line linking the Stops
			if(lines.get(0).getName().equals(lines.get(1).getName())) {
				linesList.setItems(FXCollections.observableArrayList(lines.get(0)));
				String path = lines.get(0).getName() + " da: " + start.getAddress() + " a: " + end.getAddress();
				pathList.setItems(FXCollections.observableArrayList(path));
			}
			else {	// Multi line path
				ArrayList<String> path = new ArrayList<>(); 
				for(Line l: lines) {
					path.add(l.getName());
				}
				pathList.setItems(FXCollections.observableArrayList(path));
				linesList.setItems(FXCollections.observableArrayList(lines));
			}
			linesList.refresh();
			pathList.refresh();
			
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
		start = new Stop(startField.getText());
		end = new Stop(endField.getText());
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
		boolean s1 = false, s2 = false, retval;
		for(Stop s: allStops) {
			if (s.getAddress().equals(start.getAddress())) {
				s1 = true;
			}
			if(s.getAddress().equals(end.getAddress())) {
				s2 = true;
			}
		}
		retval = s1 && s2;
		System.out.println("VALID STOPS: " + retval);
		return retval;
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
