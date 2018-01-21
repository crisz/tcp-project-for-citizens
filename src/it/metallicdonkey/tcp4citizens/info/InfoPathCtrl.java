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
	
	private static final int DURATION = 30;

	@FXML
	private void initialize() {
		timerStart();
	}

	@FXML
	private void onCalcClicked() {
		// Clear ListViews
		linesList.setItems(FXCollections.observableArrayList());
		pathList.setItems(FXCollections.observableArrayList());

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
			    alert.showAndWait();
				e.printStackTrace();
				return;
			}

			if(lines.isEmpty()) {
				pathList.setItems(FXCollections.observableArrayList("Nessun percorso"));
			}

			else {
				try {
					ArrayList<String> path = new ArrayList<>();
					ArrayList<String> linesArrayList = new ArrayList<>();
					for(Line l: lines) {
						linesArrayList.add(l.getName());
						Stop startStop = DBHelperLine.getInstance().getTerminal(l, true);
						ArrayList<Stop> stopsGoing = DBHelperLine.getInstance().getStops(l, true);
						ArrayList<Stop> stopsRet = DBHelperLine.getInstance().getStops(l, false);
						Stop endStop = DBHelperLine.getInstance().getTerminal(l, false);
						String stringa = ": ";
						stringa += startStop.getAddress().toUpperCase() + "; ";
						for(int i=0; i<stopsGoing.size(); i++) {
							stringa += stopsGoing.get(i).getAddress() + "; ";
						}
						stringa += endStop.getAddress().toUpperCase() + "; ";
						for(int i=0; i<stopsRet.size(); i++) {
							stringa += stopsRet.get(i).getAddress() + "; ";
						}
						stringa += startStop.getAddress().toUpperCase() + ".";
						path.add("LINEA " + l.getName() + stringa);
					}
					pathList.setItems(FXCollections.observableArrayList(path));
					linesList.setItems(FXCollections.observableArrayList(linesArrayList));
				} catch (SQLException exc) {
					exc.printStackTrace();
				}

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
