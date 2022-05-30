package ua.dream.chat.window.auth;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.val;
import ua.dream.chat.App;
import ua.dream.chat.packet.auth.PacketAuthorize;
import ua.dream.chat.packet.auth.PacketAuthorizeResponse;
import ua.dream.chat.util.References;
import ua.dream.chat.util.logger.Logger;
import ua.dream.chat.window.WindowController;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AuthWindowController extends WindowController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label statusLabel;

    @FXML
    public void authorize() throws IOException {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (!References.EMAIL_PATTERN.matcher(email).find())
            statusLabel.setText("Invalid email format!");
        else if (password.length() < References.PASSWORD_MIN_LENGTH)
            statusLabel.setText("Password to small. (< " + References.PASSWORD_MIN_LENGTH + ")");
        else if (password.length() > References.PASSWORD_MAX_LENGTH)
            statusLabel.setText("Password to long. (> " + References.PASSWORD_MAX_LENGTH + ")");
        else {
            statusLabel.setText("Authorizing...");
            val callback = App.getClient().<PacketAuthorizeResponse>write(new PacketAuthorize(email, password));
            try {
                val response = callback.await(2, TimeUnit.SECONDS);
                val result = response.getResult();
                if (result.isError())
                    statusLabel.setText(result.getMessage());
                else {
                    App.getUserRepository().setSelfId(response.getId());
                    Logger.LOGGER.info("Successful login!");
                    App.MAIN_WINDOW.showAsMainStage();
                }
            } catch (Throwable t) {
                t.printStackTrace();
                statusLabel.setText("Error while authorize.");
            }
        }
    }
}
