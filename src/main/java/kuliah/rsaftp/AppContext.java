package kuliah.rsaftp;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import kuliah.rsaftp.helper.KeyManager;
import org.apache.commons.net.ftp.FTPClient;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class AppContext {
    public static final FTPClient CLIENT = new FTPClient();
    public static final FileChooser CHOOSER = new FileChooser();
    private static final KeyPair PAIR = KeyManager.generatePair();

    public static Stage stage;
    public static PublicKey publicKey = PAIR.getPublic();
    public static PrivateKey privateKey = PAIR.getPrivate();
}
