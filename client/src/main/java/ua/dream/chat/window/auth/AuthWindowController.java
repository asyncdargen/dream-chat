package ua.dream.chat.window.auth;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.val;
import ua.dream.chat.App;
import ua.dream.chat.packet.auth.PacketAuthorize;
import ua.dream.chat.packet.auth.PacketAuthorizeResponse;
import ua.dream.chat.util.Base64Util;
import ua.dream.chat.util.LocalUtil;
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
    public boolean authorize() {
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
                    saveSessionFromFields();
                    App.MAIN_WINDOW.showAsMainStage();
                    return true;
                }
            } catch (Throwable t) {
                t.printStackTrace();
                statusLabel.setText("Error while authorize.");
            }
        }

        return false;
    }

    @Override
    public void handleOpen() {
        if (App.getUserRepository().getSelfId() != 0 || !App.getClient().getClient().isActive()) return;

        val encoded = LocalUtil.getStringContent(".session");

        if (encoded == null) return;

        val sessionData = Base64Util.decodeString(encoded);
        val sessionArgs = sessionData.split(":");

        if (sessionArgs.length != 2) return;

        val login = Base64Util.decodeString(sessionArgs[0]);
        val password = Base64Util.decodeString(sessionArgs[1]);

        emailField.setText(login);
        passwordField.setText(password);

        Platform.runLater(this::authorize);
    }

    public void saveSessionFromFields() {
        val login = Base64Util.encodeString(emailField.getText());
        val password = Base64Util.encodeString(passwordField.getText());

        val session = String.format("%s:%s", login, password);
        val encoded = Base64Util.encodeString(session);

        LocalUtil.setContent(".session", writer -> {
            try {
                writer.write(encoded);
            } catch (IOException e) {

            }
        });
    }

}
