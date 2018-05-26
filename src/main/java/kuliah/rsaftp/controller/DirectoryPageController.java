package kuliah.rsaftp.controller;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.action.ActionMethod;
import io.datafx.controller.flow.action.ActionTrigger;
import io.datafx.controller.flow.action.LinkAction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import kuliah.rsaftp.AppContext;
import kuliah.rsaftp.helper.BytesTransform;
import kuliah.rsaftp.helper.KeyManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.spec.InvalidKeySpecException;

@FXMLController(value = "/DirectoryPageView.fxml", title = "Directory")
public class DirectoryPageController {
    @FXML private ListView<String> filesView;
    @FXML private ListView<String> typesView;
    @FXML @ActionTrigger("open") private Button openButton;
    @FXML @ActionTrigger("back") private Button backButton;
    @FXML @ActionTrigger("upload") private Button uploadButton;
    @FXML @ActionTrigger("download") private Button downloadButton;
    @FXML @LinkAction(CreateDirectoryPageController.class) private Button createButton;
    @FXML @ActionTrigger("deleteFileOrFolder") private Button deleteButton;
    @FXML private TextArea publicKeyField;
    @FXML @ActionTrigger("loadPublicKey") private Button publicKeyLoadButton;
    @FXML @ActionTrigger("savePublicKey") private Button publicKeySaveButton;
    @FXML private TextArea privateKeyField;
    @FXML @ActionTrigger("loadPrivateKey") private Button privateKeyLoadButton;
    @FXML @ActionTrigger("savePrivateKey") private Button privateKeySaveButton;

    private FTPClient client = AppContext.CLIENT;
    private FileChooser chooser = AppContext.CHOOSER;
    private ObservableList<String> files = FXCollections.observableArrayList();
    private ObservableList<String> types = FXCollections.observableArrayList();

    @PostConstruct
    public void init() throws IOException {
        fetchFilesViewData();
        filesView.setItems(files);
        typesView.setItems(types);
        publicKeyField.setText(KeyManager.encodeKey(AppContext.publicKey));
        privateKeyField.setText(KeyManager.encodeKey(AppContext.privateKey));
    }

    @ActionMethod("open")
    public void openDirectory() throws IOException {
        String selected = filesView.getFocusModel().getFocusedItem();
        client.changeWorkingDirectory(selected);
        fetchFilesViewData();
    }

    @ActionMethod("back")
    public void backDirectory() throws IOException {
        client.changeToParentDirectory();
        fetchFilesViewData();
    }

    @ActionMethod("upload")
    public void uploadFile() throws IOException {
        File file = chooser.showOpenDialog(AppContext.stage);
        byte[] fileByte = FileUtils.readFileToByteArray(file);

        fileByte = BytesTransform.compressAndEncrypt(fileByte, AppContext.publicKey);

        ByteArrayInputStream stream = new ByteArrayInputStream(fileByte);
        client.storeUniqueFile(file.getName(), stream);
        stream.close();

        fetchFilesViewData();
    }

    @ActionMethod("download")
    public void downloadFile() throws IOException {
        String selected = filesView.getFocusModel().getFocusedItem();
        InputStream stream = client.retrieveFileStream(selected);

        byte[] fileByte = IOUtils.toByteArray(stream);
        stream.close();
        client.completePendingCommand();

        fileByte = BytesTransform.decryptAndDecompress(fileByte, AppContext.privateKey);

        chooser.setInitialFileName(selected);
        File file = chooser.showSaveDialog(AppContext.stage);
        FileUtils.writeByteArrayToFile(file, fileByte);
    }

    @ActionMethod("deleteFileOrFolder")
    public void deleteFileOrFolder() throws IOException {
        String selected = filesView.getFocusModel().getFocusedItem();
        boolean deleted = (client.deleteFile(selected) || client.removeDirectory(selected));
        fetchFilesViewData();
    }

    @ActionMethod("loadPublicKey")
    public void loadPublicKey() throws IOException, InvalidKeySpecException {
        File file = chooser.showOpenDialog(AppContext.stage);
        String keyEncoded = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        AppContext.publicKey = KeyManager.decodePublicKey(keyEncoded);
        publicKeyField.setText(keyEncoded);
    }

    @ActionMethod("savePublicKey")
    public void savePublicKey() throws IOException {
        File file = chooser.showSaveDialog(AppContext.stage);
        FileUtils.writeStringToFile(file, publicKeyField.getText(), StandardCharsets.UTF_8);
    }

    @ActionMethod("loadPrivateKey")
    public void loadPrivateKey() throws IOException, InvalidKeySpecException {
        File file = chooser.showOpenDialog(AppContext.stage);
        String keyEncoded = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        AppContext.privateKey = KeyManager.decodePrivateKey(keyEncoded);
        privateKeyField.setText(keyEncoded);
    }

    @ActionMethod("savePrivateKey")
    public void savePrivateKey() throws IOException {
        File file = chooser.showSaveDialog(AppContext.stage);
        FileUtils.writeStringToFile(file, privateKeyField.getText(), StandardCharsets.UTF_8);
    }

    private void fetchFilesViewData() throws IOException {
        files.clear();
        types.clear();
        for (FTPFile file : client.listFiles()) {
            files.add(file.getName());
            types.add(getTypeName(file));
        }
    }

    private String getTypeName(FTPFile file) {
        switch (file.getType()) {
            case FTPFile.FILE_TYPE: return "File";
            case FTPFile.DIRECTORY_TYPE: return "Directory";
            case FTPFile.SYMBOLIC_LINK_TYPE: return "Symbolic Link";
            default: return "Unknown";
        }
    }
}
