package it.metallicdonkey.tcp4citizens.info;

import it.metallicdonkey.tcp.models.Stop;
import javafx.beans.property.SimpleStringProperty;

public class StopDataModel {
	private final SimpleStringProperty address;
	private Stop stop;

	private StopDataModel(String address) {
		this.address = new SimpleStringProperty(address);
	}
	public StopDataModel(Stop s) {
		this.setStop(s);
		this.address = new SimpleStringProperty(s.getAddress());
	}
	public String getAddress() {
		return address.get();
	}
	public Stop getStop() {
		return stop;
	}
	public void setStop(Stop stop) {
		this.stop = stop;
	}
}
