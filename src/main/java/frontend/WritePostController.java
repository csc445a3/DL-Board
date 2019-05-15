package frontend;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WritePostController {
    @FXML
    public AnchorPane writePostAnchorPane;
    @FXML
    public JFXTextField nameField;
    @FXML
    public JFXTextField messageField;
    @FXML
    public JFXButton postButton;

    public String name, message;
}
