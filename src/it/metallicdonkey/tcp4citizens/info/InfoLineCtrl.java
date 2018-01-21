package it.metallicdonkey.tcp4citizens.info;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import it.metallicdonkey.tcp.db.DBHelperLine;
import it.metallicdonkey.tcp4citizens.App;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class InfoLineCtrl {
	private App mainApp;
	@FXML
	private TableView<LineDataModel> lines;
	@FXML
	private TextField filter;
	@FXML
	private TableColumn<LineDataModel, String> nameColumn;
	@FXML
	private TableColumn<LineDataModel, String> startTerminalColumn;
	@FXML
	private TableColumn<LineDataModel, String> endTerminalColumn;
	
	ObservableList<LineDataModel> data;
	private TimerTask timerTask;
	private Timer timer;
	
	@FXML
	private void initialize() throws SQLException {
		data = DBHelperLine.getInstance().getAllLines();
		// Initialization data
		nameColumn.setCellValueFactory(
				new PropertyValueFactory<LineDataModel, String>("name"));
		startTerminalColumn.setCellValueFactory(
				new PropertyValueFactory<LineDataModel, String>("startTerminal"));
		endTerminalColumn.setCellValueFactory(
				new PropertyValueFactory<LineDataModel, String>("endTerminal"));
		System.out.println(data);

		// Initialization filter
	    FilteredList<LineDataModel> filteredData = new FilteredList<>(data, p -> true);

		filter.textProperty().addListener((observable, oldValue, newValue) -> {
		      filteredData.setPredicate(line -> {
		          // If filter text is empty, display all persons.
		          if (newValue == null || newValue.isEmpty()) {
		              return true;
		          }

		          // Compare first name and last name of every person with filter text.
		          String lowerCaseFilter = newValue.toLowerCase();

		          return line.getName().toLowerCase().contains(lowerCaseFilter) /*||
		          			 line.getStartTerminal().toLowerCase().contains(lowerCaseFilter) ||
		          			 line.getEndTerminal().toLowerCase().contains(lowerCaseFilter)*/;
		      });
		    });
		SortedList<LineDataModel> sortedData = new SortedList<>(filteredData);
	    sortedData.comparatorProperty().bind(lines.comparatorProperty());

	  	lines.setItems(sortedData);
	  	
	  	timerStart();
	}

	
	private void timerStart() {
		timerTask = new TimerTask() {
	  		@Override
	  		public void run() {
	  			goHome();
	  			System.out.println("TIMER ELAPSED");
	  			
	  		}
	  	};
		timer = new Timer();
		timer.schedule(timerTask, 15 * 1000);
	}
	
	@FXML
	private void timerReset() {
		timerTask.cancel();
		timer.cancel();
		timerStart();
	}

	public void setMainApp(App mainApp) {
		this.mainApp = mainApp;
	}
	
	private void goHome() {
		// TODO implement
	}
}
