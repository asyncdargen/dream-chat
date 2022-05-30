package ua.dream.chat.window.elements;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.val;
import ua.dream.chat.App;
import ua.dream.chat.util.FXUtil;
import ua.dream.chat.util.ResourceUtil;

import java.net.URL;

public class ContactView {

    public static final URL FXML_RESOURCE = ResourceUtil.getFxmlResourceURL("main/ContactView.fxml");

    @Getter
    private final long userId;

    @Getter
    @FXML
    private HBox root;

    @FXML
    private ImageView avatarImageView;

    @FXML
    private Label nickLabel;

    public ContactView(long userId) {
        this.userId = userId;
        try {
            FXMLLoader loader = new FXMLLoader(FXML_RESOURCE);
            loader.setController(this);
            loader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        val user = App.getUserRepository().getUser(userId);

        if (user != null) {
            nickLabel.setText(user.getName());
            FXUtil.applyAvatarToView(user, avatarImageView);
        }
    }

}
