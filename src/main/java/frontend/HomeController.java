package frontend;

import com.jfoenix.controls.JFXDrawer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.scene.layout.AnchorPane.setTopAnchor;

public class HomeController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private StackPane bodyStackPane;
    @FXML
    private VBox bodyVBox;
    @FXML
    private GridPane topBarGridPane;
    @FXML
    private ColumnConstraints gridPaneLeft;
    @FXML
    private ColumnConstraints gridPaneRight;
    @FXML
    private ColumnConstraints centerColumn;
    @FXML
    private StackPane writePostButton;
    @FXML
    private StackPane refreshButton;
    @FXML
    private JFXDrawer writePostDrawer;

    private DoubleProperty scrollPaneLocation = new SimpleDoubleProperty(this, "scrollPaneLocation");
    private WritePostController writePostController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("writePost.fxml"));
            AnchorPane writePostPane = loader.load();
            writePostController = loader.getController();
            writePostController.writePostAnchorPane.maxWidthProperty().bind(topBarGridPane.widthProperty());
            writePostDrawer.setSidePane(writePostPane);
        } catch (IOException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }


        //JavaFX resizing junk:
        scrollPane.setFitToWidth(true);
        Platform.runLater(() -> scrollPane.requestLayout());
        anchorPane.prefWidthProperty().bind(stackPane.widthProperty());
        anchorPane.prefHeightProperty().bind(stackPane.heightProperty());
        topBarGridPane.prefWidthProperty().bind(anchorPane.widthProperty());
        bodyStackPane.prefWidthProperty().bind(anchorPane.widthProperty());
        bodyVBox.prefHeightProperty().bind(bodyStackPane.heightProperty());
        centerColumn.maxWidthProperty().bind(topBarGridPane.widthProperty());
        gridPaneLeft.maxWidthProperty().bind(topBarGridPane.widthProperty());
        gridPaneRight.maxWidthProperty().bind(topBarGridPane.widthProperty());
        writePostDrawer.prefWidthProperty().bind(anchorPane.widthProperty());

        //Resize bodyStackPane when filters bar is opened
        scrollPaneLocation.addListener(it -> updateScrollPaneAnchors());

        //Set writePostDrawer invisible at first
        writePostDrawer.setVisible(false);

        //Change color of write button when highlighted
        writePostButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
            writePostButton.setStyle("-fx-background-color: #1e9952; -fx-background-radius: 1000;");
        });
        writePostButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
            writePostButton.setStyle("-fx-background-color: #27ae60; -fx-background-radius: 1000;");
        });

        //Change color of refresh button when highlighted
        refreshButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
            refreshButton.setStyle("-fx-background-color: #1e9952; -fx-background-radius: 1000;");
        });
        refreshButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
            refreshButton.setStyle("-fx-background-color: #27ae60; -fx-background-radius: 1000;");
        });

        //Open write post drawer
        writePostButton.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            if (writePostDrawer.isOpened()) {
                writePostDrawer.close();
                changeScrollPaneHeight(0);
                final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
                executor.schedule(new Runnable() {
                    @Override
                    public void run() {
                        Platform.runLater(()-> writePostDrawer.setVisible(false));
                    }
                }, 500, TimeUnit.MILLISECONDS);
                executor.shutdown();
            } else {
                writePostDrawer.setVisible(true);
                writePostDrawer.open();
                changeScrollPaneHeight(75);
            }
        });


    }

    public void changeScrollPaneHeight(double height) {
        KeyValue keyValue = new KeyValue(scrollPaneLocation, height);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(500), keyValue);
        Timeline timeline = new Timeline(keyFrame);
        timeline.play();
    }

    private double getScrollPaneLocation() {
        return scrollPaneLocation.get();
    }

    private void updateScrollPaneAnchors() {
        setTopAnchor(scrollPane, 50 + getScrollPaneLocation());
    }

}
