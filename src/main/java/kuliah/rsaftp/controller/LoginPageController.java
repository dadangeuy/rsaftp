package kuliah.rsaftp.controller;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.action.ActionMethod;
import io.datafx.controller.flow.action.ActionTrigger;
import io.datafx.controller.flow.context.ActionHandler;
import io.datafx.controller.flow.context.FlowActionHandler;
import io.datafx.controller.util.VetoException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import kuliah.rsaftp.AppContext;
import org.apache.commons.net.ftp.FTPClient;

import javax.annotation.PostConstruct;
import java.io.IOException;

@FXMLController(value = "/LoginPageView.fxml", title = "Login")
public class LoginPageController {
    @ActionHandler private FlowActionHandler handler;
    @FXML private TextField hostField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML @ActionTrigger("attemptConnect") private Button connectButton;

    private FTPClient client = AppContext.CLIENT;

    @PostConstruct
    public void init() {
        hostField.setText("localhost");
        usernameField.setText("dadangeuy");
        passwordField.setText("namamu");
    }

    @ActionMethod("attemptConnect")
    public void attemptConnect() throws IOException, VetoException, FlowException {
        mustNotEmpty(hostField, usernameField, passwordField);
        client.connect(hostField.getText());
        client.login(usernameField.getText(), passwordField.getText());
        handler.navigate(DirectoryPageController.class);
    }

    private void mustNotEmpty(TextInputControl... controls) throws IOException {
        for (TextInputControl control : controls)
            if (control.getText().isEmpty())
                throw new IOException("@id/" + control.getId() + " is empty");
    }
}
