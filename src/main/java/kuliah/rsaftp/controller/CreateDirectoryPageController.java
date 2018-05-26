package kuliah.rsaftp.controller;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.action.ActionMethod;
import io.datafx.controller.flow.action.ActionTrigger;
import io.datafx.controller.flow.action.LinkAction;
import io.datafx.controller.flow.context.ActionHandler;
import io.datafx.controller.flow.context.FlowActionHandler;
import io.datafx.controller.util.VetoException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import kuliah.rsaftp.AppContext;

import java.io.IOException;

@FXMLController(value = "/CreateDirectoryPageView.fxml", title = "Create Directory")
public class CreateDirectoryPageController {
    @ActionHandler private FlowActionHandler handler;
    @FXML private TextField folderNameField;
    @FXML @ActionTrigger("createDirectory") private Button createButton;
    @FXML @LinkAction(DirectoryPageController.class) private Button cancelButton;

    @ActionMethod("createDirectory")
    public void createDirectory() throws IOException, VetoException, FlowException {
        AppContext.CLIENT.makeDirectory(folderNameField.getText());
        handler.navigate(DirectoryPageController.class);
    }
}
