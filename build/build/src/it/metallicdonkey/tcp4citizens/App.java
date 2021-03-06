package it.metallicdonkey.tcp4citizens;

import java.io.IOException;

import it.metallicdonkey.tcp4citizens.info.CitizenAreaCtrl;

//import com.mysql.jdbc.Driver;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {
	private Stage primaryStage;
	private BorderPane rootLayout;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("TCP for citizens");
		try {
			initRootLayout();
			showLogin();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initRootLayout() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(App.class.getResource("RootLayout.fxml"));
		rootLayout = (BorderPane) loader.load();
		Scene scene = new Scene(rootLayout);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void showLogin() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		Scene scene;
		loader.setLocation(App.class.getResource("info/CitizenAreaScreen.fxml"));
		AnchorPane adminScreen = (AnchorPane) loader.load();
		scene = new Scene(adminScreen);
		getPrimaryStage().setScene(scene);
		CitizenAreaCtrl cCtrl = loader.getController();
		cCtrl.setMainApp(this);
		primaryStage.show();
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		//  	try {
		//			Driver d = (Driver) Class.forName("com.mysql.jdbc.Driver").newInstance();
		//			DriverManager.registerDriver(d);
		//			DriverManager.getConnection("jdbc:mysql://<host>:<porta>/<baseDati>");
		//		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		} catch (SQLException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		launch(args);
	}
}
