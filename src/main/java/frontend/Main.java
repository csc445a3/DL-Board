package frontend;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    /**
     * Starts an instance of the GUI
     * @param primaryStage stage for the GUI to runn off of
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        HomeController.setStage(primaryStage);
        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(600);
        primaryStage.setTitle("DL-Board");
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("home.fxml"));
        Pane root = loader.load();
        primaryStage.setScene(new Scene(root));
        root.requestFocus();
        primaryStage.show();
    }

    /**
     * Launches GUI
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

}
