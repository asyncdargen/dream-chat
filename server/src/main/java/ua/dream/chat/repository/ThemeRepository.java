package ua.dream.chat.repository;

import lombok.Getter;
import lombok.val;
import ua.dream.chat.App;
import ua.dream.chat.model.Theme;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ThemeRepository {

    private final Map<String, Theme> cache = new ConcurrentHashMap<>();

    {
        App.getDatabase().update("CREATE TABLE IF NOT EXISTS `user_themes` (`id` VARCHAR(32), `name` VARCHAR(128), `style` TEXT);");

        try {
            val resultSet = App.getDatabase().query("SELECT * FROM `user_themes`;");
            while (resultSet.next()) cache.put(resultSet.getString("id").toLowerCase(), new Theme(
                    resultSet.getString("name"),
                    resultSet.getString("style").split("\n")
            ));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
