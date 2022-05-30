package ua.dream.chat.window.settings;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.val;
import ua.dream.chat.App;
import ua.dream.chat.util.FXUtil;
import ua.dream.chat.window.WindowController;

public class SettingsMenuController extends WindowController {

    @FXML
    private StackPane root;
    @FXML
    private ImageView avatarImageView;
    @FXML
    private Label nameLabel, emailLabel;
    @FXML
    private Pane blockingPane;

    @FXML
    private void initialize() {
        root.setOnMouseClicked(event -> {
            if (isBlocked()) {
                unblockWindow();
                closeDialogs();
            }
        });
    }

    public boolean closeDialogs() {
        val opened = App.CHANGE_AVATAR_DIALOG.isShowing()
                || App.CHANGE_NAME_DIALOG.isShowing()
                || App.CHANGE_PASSWORD_DIALOG.isShowing();

        App.CHANGE_NAME_DIALOG.getController().cancel();
        App.CHANGE_PASSWORD_DIALOG.getController().cancel();
        App.CHANGE_AVATAR_DIALOG.getController().cancel();

        return opened;
    }

    @Override
    public void handleOpen() {
        val user = App.getUserRepository().getSelfUser();
        FXUtil.applyAvatarToView(user, avatarImageView);
        emailLabel.setText(user.getEmail());
        nameLabel.setText(user.getName());
    }

    @FXML
    public void changeNick() {
        blockWindow();

        App.CHANGE_NAME_DIALOG.showOnTopStage();
    }

    @FXML
    public void changePassword() {
        blockWindow();

        App.CHANGE_PASSWORD_DIALOG.showOnTopStage();
    }

    @FXML
    public void changeAvatar() {
        blockWindow();

        App.CHANGE_AVATAR_DIALOG.showOnTopStage();
    }

    public void blockWindow() {
        blockingPane.setVisible(true);
    }

    public void unblockWindow() {
        blockingPane.setVisible(false);
    }

    public boolean isBlocked() {
        return blockingPane.isVisible();
    }

    public void close() {
        if (isBlocked()) {
            unblockWindow();
            closeDialogs();
        } else {
            App.MAIN_WINDOW.getController().unblockWindow();

            Stage stage = (Stage) avatarImageView.getScene().getWindow();
            stage.hide();
        }
    }

}
