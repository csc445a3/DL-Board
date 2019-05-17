package frontend;

import backend.Client;
import backend.MessagePacket;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.events.JFXDialogEvent;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.scene.layout.AnchorPane.setTopAnchor;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import java.util.*;
import javafx.animation.Timeline;

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
    public JFXDrawer writePostDrawer;
    @FXML
    private StackPane spinnerStackPane;
    private DoubleProperty scrollPaneLocation = new SimpleDoubleProperty(this, "scrollPaneLocation");
    private WritePostController writePostController;
    private static Stage primaryStage;
    private ResourceLoadingTask refresh = new ResourceLoadingTask();
    private boolean opened = false;

    static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public class ResourceLoadingTask extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            spinnerStackPane.setVisible(true);
            //Do Refresh stuff here:

            return null;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                bodyVBox.getChildren().removeAll();
                Platform.exit();
                System.exit(0);
            }
        });

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
                        Platform.runLater(() -> writePostDrawer.setVisible(false));
                    }
                }, 500, TimeUnit.MILLISECONDS);
                executor.shutdown();
            } else {
                writePostDrawer.setVisible(true);
                writePostDrawer.open();
                changeScrollPaneHeight(65);
            }
        });

        writePostController.postButton.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            writePostController.name = writePostController.nameField.getText();
            writePostController.message = writePostController.messageField.getText();
            if (writePostController.name.length() > 12 && writePostController.message.length() > 100) {
                BoxBlur blur = new BoxBlur(2, 2, 2);
                JFXDialogLayout content = new JFXDialogLayout();
                content.setHeading(new Text("Your Name and Message Too Long!"));
                content.setBody(new Text("The max name length is 12 characters and the max message length is 100 characters, please shorten them and try again."));
                JFXDialog errorMessage = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
                JFXButton button = new JFXButton("Okay");
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        errorMessage.close();
                    }
                });
                content.setActions(button);
                if (!opened) {
                    errorMessage.show();
                    anchorPane.setEffect(blur);
                    opened = true;
                }
                errorMessage.setOnDialogClosed((JFXDialogEvent closedEvent) -> {
                    anchorPane.setEffect(null);
                    opened = false;
                });
            } else if (writePostController.name.length() > 12) {
                BoxBlur blur = new BoxBlur(2, 2, 2);
                JFXDialogLayout content = new JFXDialogLayout();
                content.setHeading(new Text("Your Name is Too Long!"));
                content.setBody(new Text("The max name length is 12 characters, please shorten it and try again."));
                JFXDialog errorMessage = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
                JFXButton button = new JFXButton("Okay");
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        errorMessage.close();
                    }
                });
                content.setActions(button);
                if (!opened) {
                    errorMessage.show();
                    anchorPane.setEffect(blur);
                    opened = true;
                }
                errorMessage.setOnDialogClosed((JFXDialogEvent closedEvent) -> {
                    anchorPane.setEffect(null);
                    opened = false;
                });
            } else if (writePostController.message.length() > 100) {
                BoxBlur blur = new BoxBlur(2, 2, 2);
                JFXDialogLayout content = new JFXDialogLayout();
                content.setHeading(new Text("Your Message is Too Long!"));
                content.setBody(new Text("The max message length is 100 characters, please shorten it and try again."));
                JFXDialog errorMessage = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
                JFXButton button = new JFXButton("Okay");
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        errorMessage.close();
                    }
                });
                content.setActions(button);
                if (!opened) {
                    errorMessage.show();
                    anchorPane.setEffect(blur);
                    opened = true;
                }
                errorMessage.setOnDialogClosed((JFXDialogEvent closedEvent) -> {
                    anchorPane.setEffect(null);
                    opened = false;
                });
            } else {
                writePostDrawer.close();
                changeScrollPaneHeight(0);
                final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
                executor.schedule(new Runnable() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> writePostDrawer.setVisible(false));
                    }
                }, 500, TimeUnit.MILLISECONDS);
                executor.shutdown();

                //Do the client creation and message sending here:
                try {

                    String clientMessage = writePostController.message;
                    Client c = new Client();
                    c.send(clientMessage, writePostController.name);
                    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), new EventHandler<ActionEvent>() {

                        private int i = 1;

                        @Override
                        public void handle(ActionEvent event) {
                            try {
                                MessagePacket messagePacket = c.receive();
                                Platform.runLater(() -> bodyVBox.getChildren().add(createPost(messagePacket.getId(), messagePacket.getMessage())));
                            } catch (Exception err) {
                                err.printStackTrace();
                            }
                        }

                    }));
                    timeline.setCycleCount(Timeline.INDEFINITE);
                    timeline.play();

                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

        });

        refreshButton.addEventHandler(MouseEvent.MOUSE_PRESSED, (e2) -> {
            Thread t = new Thread(refresh);
            refresh.setOnSucceeded(e3 -> {
                spinnerStackPane.setVisible(false);
            });
            t.start();

        });

        //Adding a Test Message
        //bodyVBox.getChildren().add(createPost("Doug", "Hello"));
    }

    public StackPane createPost(String name, String message) {
        StackPane messageBody = new StackPane();
        messageBody.setPrefSize(400, 75);
        messageBody.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        messageBody.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        messageBody.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 100; -fx-border-color: #7bed9f; -fx-border-radius: 100; -fx-border-width: 3;");

        DropShadow messageDropShadow = new DropShadow();
        messageDropShadow.setBlurType(BlurType.THREE_PASS_BOX);
        messageDropShadow.setWidth(100);
        messageDropShadow.setHeight(100);
        messageDropShadow.setHeight(100);
        messageDropShadow.setRadius(50);
        messageDropShadow.setHeight(100);
        messageDropShadow.setSpread(0);
        messageDropShadow.setColor(new Color(0, 0, 0, 0.25));
        messageBody.setEffect(messageDropShadow);

        HBox messageHBox = new HBox();
        messageHBox.setAlignment(Pos.CENTER_LEFT);
        messageHBox.setStyle("-fx-spacing: 20; -fx-padding: 20 20 20 20;");

        Text nameText = new Text(name + " says:");
        nameText.setFont(Font.font("Helvetica Neue Bold", 15));
        nameText.setStyle("-fx-fill: #2d3436;");
        nameText.setWrappingWidth(80);

        Text messageText = new Text(message);
        messageText.setFont(Font.font("Helvetica Neue Medium", 13));
        messageText.setStyle("-fx-fill: #2d3436;");
        messageText.setWrappingWidth(240);

        messageHBox.getChildren().add(nameText);
        messageHBox.getChildren().add(messageText);
        messageBody.getChildren().add(messageHBox);

        return messageBody;

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
        setTopAnchor(scrollPane, 65 + getScrollPaneLocation());
    }

}
