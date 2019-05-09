package frontend;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import java.net.URL;
import java.util.ResourceBundle;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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


    }

}
