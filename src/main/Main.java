package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import view.Login;

public class Main extends Application {
	
	Scene scene;
	
	public void initialize() {
		Label label = new Label("hello");
		scene = new Scene(label, 500, 250);
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage arg0) throws Exception {
        new Login().start(arg0);

	}

}
