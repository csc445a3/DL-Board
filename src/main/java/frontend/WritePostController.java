package frontend;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class WritePostController {
    @FXML public AnchorPane writePostAnchorPane;
    @FXML public JFXTextField nameField;
    @FXML public JFXTextField messageField;
    @FXML public JFXButton postButton;
    public String name, message;
}
