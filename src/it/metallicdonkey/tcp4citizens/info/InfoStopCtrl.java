package it.metallicdonkey.tcp4citizens.info;

import java.awt.Event;
import java.sql.SQLException;
import java.util.List;

import it.metallicdonkey.tcp.db.DBHelperLine;
import it.metallicdonkey.tcp.models.Stop;
import it.metallicdonkey.tcp4citizens.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class InfoStopCtrl {
	private App mainApp;
	@FXML
	private TableView<StopDataModel> stops;
	@FXML
	private TextField filter;
	@FXML
	private TableColumn<StopDataModel, String> addressColumn;
	@FXML
	private ListView<String> linesList; 

	private ObservableList<StopDataModel> data;
	private Stop stop;

	
	@FXML
	private void initialize() throws SQLException {
		data = DBHelperLine.getInstance().getAllStops();
		// Initialization data
		addressColumn.setCellValueFactory(
				new PropertyValueFactory<StopDataModel, String>("address"));
		System.out.println(data);

		// Initialization filter
		FilteredList<StopDataModel> filteredData = new FilteredList<>(data, p -> true);

		filter.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(stop -> {
				// If filter text is empty, display all persons.
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}

				// Compare first name and last name of every person with filter text.
				String lowerCaseFilter = newValue.toLowerCase();

				return stop.getAddress().toLowerCase().contains(lowerCaseFilter) /*||
		          			 line.getStartTerminal().toLowerCase().contains(lowerCaseFilter) ||
		          			 line.getEndTerminal().toLowerCase().contains(lowerCaseFilter)*/;
			});
		});
		SortedList<StopDataModel> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(stops.comparatorProperty());

		stops.setItems(sortedData);
		
		stops.setRowFactory(tv -> {
			TableRow<StopDataModel> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if(event.getClickCount() == 2 && (! row.isEmpty())) {
					stop = row.getItem().getStop();
					showLines();
				}
			});
			return row;
			
		});
	}
	
	public void showLines() {
		try {
			ObservableList<String> lines = DBHelperLine.getInstance().getLinesPassingBy(stop);
			if(lines.isEmpty()) {
				linesList.setPlaceholder(new Label("Nessuna Linea"));
			}
			linesList.setItems(lines);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setMainApp(App mainApp) {
		this.mainApp = mainApp;
	}
}
