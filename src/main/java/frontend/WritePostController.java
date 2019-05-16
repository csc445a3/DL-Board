package frontend;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

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
