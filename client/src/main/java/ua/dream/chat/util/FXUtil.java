package ua.dream.chat.util;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import lombok.experimental.UtilityClass;
import lombok.val;
import ua.dream.chat.model.User;

import java.io.IOException;
import java.util.Locale;

@UtilityClass
public class FXUtil {

    public final Image DEFAULT_AVATAR;

    static {
        try {
            DEFAULT_AVATAR = new Image(ResourceUtil.getImageResourceURL("img_avatar.png").openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Image getAvatarForName(String name, long userId) {
        val color = Color.color(
                userId / ((double) References.ID_MAX_LENGTH),
                userId / ((double) References.ID_MAX_LENGTH),
                userId / ((double) References.ID_MAX_LENGTH)
        );
        Canvas canvas = new Canvas();
        canvas.setWidth(48d);
        canvas.setHeight(48d);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(color);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Sans Serif", FontWeight.BOLD, 18d));
        gc.fillText(String.valueOf(name.charAt(0)).toUpperCase(Locale.ROOT), 24, 24 + 6);
        return canvas.snapshot(new SnapshotParameters(), null);
    }

    public void applyAvatarToView(User user, ImageView view) {
        val image = user.getAvatar() != null ? new Image(user.getAvatar().openInputStream()) : getAvatarForName(user.getName(), user.getId());

        view.setImage(image);

        double radius = view.getFitWidth() / 2;

        Circle circle = new Circle();
        circle.setRadius(radius);
        circle.setCenterX(radius);
        circle.setCenterY(radius);

        view.setClip(circle);
    }

}
