package appli;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	
	private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
    	Main.primaryStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("viewDesignPattern.fxml"));
        Main.primaryStage.setTitle("TEAMS Attendees List Converter");
        Main.primaryStage.setScene(new Scene(root, 450, 500));
        Main.primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    public static Stage getPrimaryStage() {
    	return primaryStage;
    }
}
