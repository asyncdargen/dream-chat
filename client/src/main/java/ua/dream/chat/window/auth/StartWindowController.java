package ua.dream.chat.window.auth;

import javafx.fxml.FXML;
import ua.dream.chat.App;
import ua.dream.chat.window.WindowController;

import java.io.IOException;

public class StartWindowController extends WindowController {

    @FXML
    public void startMessaging() throws IOException {
        App.AUTH_WINDOW.showAsMainStage();
    }

}
