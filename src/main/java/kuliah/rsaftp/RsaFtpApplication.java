package kuliah.rsaftp;

import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import javafx.application.Application;
import javafx.stage.Stage;
import kuliah.rsaftp.controller.LoginPageController;

public class RsaFtpApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws FlowException {
        new Flow(LoginPageController.class).startInStage(primaryStage);
        AppContext.stage = primaryStage;
    }
}
