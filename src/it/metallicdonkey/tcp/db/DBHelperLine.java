package it.metallicdonkey.tcp.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.metallicdonkey.tcp.models.Check;
import it.metallicdonkey.tcp.models.Line;
import it.metallicdonkey.tcp.models.Match;
import it.metallicdonkey.tcp.models.Stop;
import it.metallicdonkey.tcp4citizens.info.LineDataModel;
import it.metallicdonkey.tcp4citizens.info.StopDataModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DBHelperLine {
	private static DBManager dbm = new DBManager("localhost", "root", "root", "tcp");
	private static DBHelperLine instance;

	private DBHelperLine() throws SQLException {
		dbm.connect();
	}

	public static DBHelperLine getInstance() throws SQLException {
		if(instance != null) {
			return instance;
		}
		instance = new DBHelperLine();
		return instance;

	}

	public Line getLineById(String id) throws SQLException {
		return getAllLinesArray("idLine = '" + id + "'").get(0);
	}

	public ArrayList<Line> getAllLinesArray(String clause) throws SQLException{
		ArrayList<Line> lines = new ArrayList<>();

		clause = (clause == null)? "TRUE":clause;

		dbm.executeQuery("SELECT * FROM line WHERE " + clause);
		ResultSet resultSet = dbm.getResultSet();
		while(resultSet.next()) {
			Line line = new Line();
			line.setName(resultSet.getString("idLine"));
			line.setStartTerminal(this.getTerminal(line, true));
			line.setEndTerminal(this.getTerminal(line, false));
			line.setGoingStops(this.getStops(line, true));
			line.setReturnStops(this.getStops(line, false));
			lines.add(line);
		}
		return lines;
	}

//bisogna vedere come identificare un capolinea rispetto alle normali fermate
	public Stop getTerminal(Line line, boolean first) {
		Stop stop = new Stop();
		try {
			if(first == true) {
				dbm.executeQuery("SELECT s.idStop, s.Address FROM stop s, line_has_stop ls, line l " +
						"WHERE s.idStop=ls.Stop_idStop AND l.idLine='" + line.getName() +
						"' AND l.idLine=ls.Line_idLine AND ls.type='FIRST'");
			} else {
				dbm.executeQuery("SELECT s.idStop, s.Address FROM stop s, line_has_stop ls, line l " +
						"WHERE s.idStop=ls.Stop_idStop AND l.idLine='" + line.getName() +
						"' AND l.idLine=ls.Line_idLine AND ls.type='END'");
			}
			ResultSet result = dbm.getResultSet();
			/**************
			 * CONTROLLO DA FARE
			 * ATTENZIONE
			 */
			if(!result.next()) {
				Stop s = new Stop();
				s.setAddress("Via vattelappesca 12");
				return s;
			}
			/**
			 * AGGIUSTARE
			 */
			stop.setAddress(result.getString("s.Address"));
		} catch(SQLException exc) {
			exc.printStackTrace();
		}
		return stop;
	}
	public ArrayList<Stop> getStops(Line line, boolean going) {
		ArrayList<Stop> stops = new ArrayList<>();
		try {
			if(going == true) {
				dbm.executeQuery("SELECT s.idStop, s.Address FROM stop s, line_has_stop ls, line l " +
						"WHERE s.idStop=ls.Stop_idStop AND l.idLine='" + line.getName() +
						"' AND l.idLine=ls.Line_idLine AND ls.type = 'GOING'");
			} else {
				dbm.executeQuery("SELECT s.idStop, s.Address FROM stop s, line_has_stop ls, line l " +
						"WHERE s.idStop=ls.Stop_idStop AND l.idLine='" + line.getName() +
						"' AND l.idLine=ls.Line_idLine AND ls.type='RETURN'");
			}
			ResultSet resultSet = dbm.getResultSet();
			resultSet.beforeFirst();
			while(resultSet.next()) {
				Stop stop = new Stop();
				stop.setAddress(resultSet.getString("s.Address"));
				stops.add(stop);
			}
		} catch (SQLException exc) {
			exc.printStackTrace();
		}
		return stops;
	}
	public ObservableList<StopDataModel> getAllStops() {
		ArrayList<StopDataModel> stops = new ArrayList<>();
		try {
			dbm.executeQuery("SELECT * FROM tcp.stop");
			ResultSet result = dbm.getResultSet();
			while(result.next()) {
				Stop stop = new Stop();
				stop.setAddress(result.getString("Address"));
				stops.add(new StopDataModel(stop));
			}
		} catch (SQLException exc) {
			exc.printStackTrace();
		}
		return FXCollections.observableArrayList(stops);
	}
	public ArrayList<Stop> getAllStopsArray () {
		ArrayList<Stop> stops = new ArrayList<>();
		try {
			dbm.executeQuery("SELECT * FROM tcp.stop");
			ResultSet result = dbm.getResultSet();
			while(result.next()) {
				Stop stop = new Stop();
				stop.setAddress(result.getString("Address"));
				stops.add(stop);
			}
		} catch (SQLException exc) {
			exc.printStackTrace();
		}
		return stops;
	}
	public ObservableList<LineDataModel> getAllLines() {
		ArrayList<LineDataModel> lines = new ArrayList<>();
		try {
			dbm.executeQuery("SELECT * FROM line");
			ResultSet resultSet = dbm.getResultSet();
			resultSet.beforeFirst();
			while(resultSet.next()) {
				System.out.println("Line!");
				Line line = new Line();
				line.setName(resultSet.getString("idLine"));
				line.setStartTerminal(this.getTerminal(line, true));
				line.setEndTerminal(this.getTerminal(line, false));
				line.setGoingStops(this.getStops(line, true));
				line.setReturnStops(this.getStops(line, false));
				lines.add(new LineDataModel(line));
			}
		} catch(SQLException exc) {
			exc.printStackTrace();
		}
		ObservableList<LineDataModel> dataLines = FXCollections.observableArrayList(lines);
		return dataLines;
	}

	public void insertLine(Line l) throws SQLException {
		String query = " INSERT INTO tcp.line () values (?, ?)";

		PreparedStatement preparedStmt = dbm.getConnection().prepareStatement(query);
		preparedStmt.setString(1, l.getName());
		preparedStmt.setInt(2, l.getPriority());
		preparedStmt.execute();

		// Create the stops in case they don't exist.
		insertStop(l.getStartTerminal());
		insertStop(l.getEndTerminal());

		ArrayList<Stop> going = l.getGoingStops();
		ArrayList<Stop> ret = l.getReturnStops();

		for(int i=0; i<going.size(); i++) {
			insertStop(going.get(i));
		}

		for(int i=0; i<ret.size(); i++) {
			insertStop(ret.get(i));
		}

		connectLine(l);

	}

	private void connectLine(Line l) throws SQLException {

		int sequence = -1;

		insertLineHasStop(l.getName(), getIdStop(l.getStartTerminal().getAddress()), "FIRST", ++sequence);
		ArrayList<Stop> going = l.getGoingStops();
		ArrayList<Stop> ret = l.getReturnStops();

		for(int i=0; i<going.size(); i++) {
			insertLineHasStop(l.getName(), getIdStop(going.get(i).getAddress()), "GOING", ++sequence);
		}

		for(int i=0; i<ret.size(); i++) {
			insertLineHasStop(l.getName(), getIdStop(ret.get(i).getAddress()), "RETURN", ++sequence);
		}

		insertLineHasStop(l.getName(), getIdStop(l.getEndTerminal().getAddress()), "END", ++sequence);

	}

	private void insertLineHasStop(String id, int idStop, String type, int sequence) throws SQLException {
		String query = "INSERT INTO tcp.line_has_stop () values (?, ?, ?, ?)";
		PreparedStatement preparedStmt = dbm.getConnection().prepareStatement(query);
		preparedStmt.setString(1, id);
		preparedStmt.setInt(2, idStop);
		preparedStmt.setString(3, type);
		preparedStmt.setInt(4, sequence);
		preparedStmt.execute();
	}

	public int getIdStop(String address) throws SQLException {
		dbm.executeQuery("SELECT idStop from tcp.stop WHERE stop.Address = '"+address+"'");
		ResultSet result = dbm.getResultSet();
		if(!dbm.getResultSet().next()) {
			return -1;
		}
		return result.getInt("idStop");
	}

	private void insertStop(Stop s) throws SQLException {
		int id = this.getIdStop(s.getAddress());
		if(id == -1) {	// If it's a new Stop
			String query = "INSERT INTO tcp.stop (Address) values (?)";
			PreparedStatement preparedStmt = dbm.getConnection().prepareStatement(query);
			preparedStmt.setString(1, s.getAddress());
			preparedStmt.execute();
		}
	}

	public int removeLine(Line l) {
		int removed1 = -1;
		int result = -1;
		removed1 = dbm.executeUpdate("DELETE FROM tcp.line_has_stop WHERE Line_idLine = '"+
				l.getName()+"'");
		if(removed1 < 0)
			return -1;
		result = dbm.executeUpdate("DELETE FROM tcp.line WHERE idLine='"+l.getName()+"'");
		return result;
	}

	public List<Line> getPath(Stop start, Stop end) throws SQLException {
		List<Line> path = new ArrayList<>();
		int id1 = getIdStop(start.getAddress());
		int id2 = getIdStop(end.getAddress());
		String query = "SELECT DISTINCT ls.Stop_idStop, ls.Line_idLine FROM tcp.stop s, tcp.line l, tcp.line_has_stop ls " + 
				"WHERE l.idLine = ls.Line_idLine AND s.idStop = ls.Stop_idStop " + 
				"AND ls.Stop_idStop = " + id1 + " OR ls.Stop_idStop = " + id2 + " "+
				"GROUP BY ls.Stop_idStop;";
			
		ResultSet result = dbm.executeQuery(query);
		while(result.next()) {
			path.add(getLineById(result.getInt("Line_idLine")+"") );
		}
		return path;
	}
}