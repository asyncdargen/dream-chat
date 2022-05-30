package ua.dream.chat.window.settings;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import lombok.val;
import ua.dream.chat.App;
import ua.dream.chat.packet.auth.PacketAuthorizeUpdatePassword;
import ua.dream.chat.util.logger.Logger;
import ua.dream.chat.window.DialogWindow;

public class ChangePasswordDialogController extends DialogWindow<ChangePasswordDialogController> {

    @FXML
    private PasswordField oldPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField newPasswordRepeatField;

    public ChangePasswordDialogController() {
        super("settings/ChangePasswordWindow.fxml");
    }

    public void cancel() {
//        App.SETTINGS_WINDOW.getController().handleOpen();
        close();
    }

    public void save() {
        val oldPassword = oldPasswordField.getText();
        val newPassword = newPasswordField.getText();

        cancel();
//        Logger.LOGGER.info("НОВЫЙ ПАРОЛЬ " + newPassword);
//        Logger.LOGGER.info("НОВЫЙ ПАРОЛЬ ПАВТАРЕНИЕ " + newPasswordRepeatField.getText());
        if (newPassword.equals(newPasswordRepeatField.getText())) {
            Logger.LOGGER.info("Trying to change password...");
            App.getClient().write(new PacketAuthorizeUpdatePassword(oldPassword, newPassword));
        }
    }

}
