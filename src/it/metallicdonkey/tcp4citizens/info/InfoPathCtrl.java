package it.metallicdonkey.tcp4citizens.info;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import com.mysql.fabric.jdbc.ErrorReportingExceptionInterceptor;

import it.metallicdonkey.tcp.db.DBHelperLine;
import it.metallicdonkey.tcp.models.Line;
import it.metallicdonkey.tcp.models.Stop;
import it.metallicdonkey.tcp4citizens.App;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
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
	ListView<String> linesList;
	@FXML
	ListView<String> pathList;

	private Timer timer;
	private TimerTask timerTask;
	private ArrayList<Line> allLines = null;
	private ArrayList<Line> containsStart = new ArrayList<>();
	private ArrayList<Line> containsEnd = new ArrayList<>();
	
	private static final int DURATION = 30;

	@FXML
	private void initialize() {
		timerStart();
		
		
		linesList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
	    @Override
	    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
	    		String[] steps = newValue.split(" -> ");
	    		Line line1 = null;
	    		Line line2 = null;
	    		for(int i=0; i<containsStart.size(); i++) {
	    			if(containsStart.get(i).getName().equals(steps[0])) 
	    				line1 = containsStart.get(i);
	    		}
	    		for(int i=0; i<containsEnd.size(); i++) {
	    			if(containsEnd.get(i).getName().equals(steps[2])) 
	    				line2 = containsEnd.get(i);
	    		}
	    		
	    		ArrayList<String> path = new ArrayList<>();
	    		
	    		ArrayList<Stop> stops1 = line1.getAllStops();
	    		ArrayList<Stop> stops2 = line2.getAllStops();
	    		
	    		
	    		boolean inside = false;
	    		path.add("Dirigiti verso la fermata "+startField.getText());
	    		path.add("Sali sul bus "+steps[0]);
	    		for(int i=0; i<stops1.size(); i++) {
	    			if(stops1.get(i).getAddress().equals(steps[1])) {
	    				break;
	    			}
	    			
	    			if(inside) {
	    				path.add("Fermata "+stops1.get(i).getAddress());	
	    			}
	    			
	    			if(startField.getText().equals(stops1.get(i).getAddress())) {
	    				inside = true;
	    			}
	    		}
	    		
	    		path.add("Scendi alla fermata "+steps[1]);
	    		
	    		path.add("Sali sul "+steps[2]);
	    		
	    		inside = false;
	    		
	    		for(int i=0; i<stops2.size(); i++) {
	    			if(endField.getText().equals(stops2.get(i).getAddress())) {
	    				break;
	    			}
	    			
	    			if(inside) {
	    				path.add("Fermata "+stops2.get(i).getAddress());
	    			}
	    			
	    			if(stops2.get(i).getAddress().equals(steps[1])) {
	    				inside = true;
	    			}
	    		}
	    		
	    		path.add("Scendi alla fermata "+endField.getText());
	    		path.add("Sei arrivato a destinazione");
	    		
	    		pathList.setItems(FXCollections.observableArrayList(path));
	    }
	});
		
	}

	@FXML
	private void onCalcClicked() {
		// Clear ListViews
		linesList.setItems(FXCollections.observableArrayList());
		pathList.setItems(FXCollections.observableArrayList());
		
		/*
		 * 
		 * Cristian test
		 */

		ArrayList<Stop> stops;
		ArrayList<String[]> sequence = new ArrayList<>();
		try {
			allLines = DBHelperLine.getInstance().getAllLinesArray(null);
		} catch (SQLException e1) {
			// TODO caduta di connessione
			e1.printStackTrace();
		}
		
		for(int i=0; i<allLines.size(); i++) {
			stops = allLines.get(i).getAllStops();
			for(int j=0; j<stops.size(); j++) {
				if(stops.get(j).getAddress().equalsIgnoreCase(startField.getText())) 
					containsStart.add(allLines.get(i));
				if(stops.get(j).getAddress().equalsIgnoreCase(endField.getText())) 
					containsEnd.add(allLines.get(i));
			}
		}
		
		System.out.println(containsStart);
		System.out.println(containsEnd);
		
		for(int i=0; i<containsStart.size(); i++) {
			for(int j=0; j<containsEnd.size(); j++) {
				ArrayList<Stop> startStops = containsStart.get(i).getAllStops();
				ArrayList<Stop> endStops = containsEnd.get(j).getAllStops();
				System.out.println("!!" + startStops + " ?? " + endStops);
				for(int k=0; k<endStops.size(); k++) {
					for(int l=0; l<startStops.size(); l++) {
						if(startStops.get(l).getAddress().equals(endStops.get(k).getAddress())) {
							String[] twoLines = new String[3];
							twoLines[0] = containsStart.get(i).getName();
							twoLines[1] = containsEnd.get(j).getName();
							twoLines[2] = endStops.get(k).getAddress();
							sequence.add(twoLines);
							break;
						}
					}
				}
			}
		}
		
		Alert error = check();

		if(error != null) {
			error.showAndWait();
			return;
		}

		if (areStopsOk()) {
			if(sequence.isEmpty()) {
				pathList.setItems(FXCollections.observableArrayList("Nessun percorso"));
			}
			else {
				ArrayList<String> al = new ArrayList<>();
				for(int i=0; i<sequence.size(); i++) {
					al.add(sequence.get(i)[0] + " -> " +sequence.get(i)[2] +" -> " +sequence.get(i)[1]);
				}
				System.out.println("AL"+al);
				
				linesList.setItems(FXCollections.observableArrayList(al));

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
		    alert.showAndWait();
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
		    alert.showAndWait();
			return false;
		}
		boolean s1 = false, s2 = false, retval;
		for(Stop s: allStops) {
			if (s.getAddress().toLowerCase().equals(start.getAddress().toLowerCase())) {
				s1 = true;
			}
			if(s.getAddress().toLowerCase().equals(end.getAddress().toLowerCase())) {
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

	private void timerStart() {
		timerTask = new TimerTask() {
	  		@Override
	  		public void run() {
	  			Platform.runLater(new Runnable() {
					@Override
					public void run() {
						goHome();
					}
				});
	  			System.out.println("TIMER ELAPSED");

	  		}
	  	};
		timer = new Timer();
		timer.schedule(timerTask, DURATION * 1000);
	}
	
	@FXML
	private void onHomeClicked() {
		// Stop timers
		timerTask.cancel();
		timer.cancel();
		
		// Go home
		goHome();
	}


	private void goHome(){
		FXMLLoader loader = new FXMLLoader();
		Scene scene;
		try {
			loader.setLocation(App.class.getResource("info/CitizenAreaScreen.fxml"));
		    AnchorPane anchorPane = (AnchorPane) loader.load();
		  	scene = new Scene(anchorPane);
			mainApp.getPrimaryStage().setScene(scene);
			CitizenAreaCtrl ctrl = loader.getController();
			ctrl.setMainApp(mainApp);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}



	public void setMainApp(App mainApp) {
	  this.mainApp = mainApp;
	}
}