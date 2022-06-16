package ua.dream.chat.util;

import javafx.scene.Parent;
import javafx.scene.Scene;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.val;
import ua.dream.chat.App;
import ua.dream.chat.window.Window;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@UtilityClass
public class LocalSettings {

    public String FILE = "settings.dat";
    public Map<String, String> SETTINGS;

    static {
        SETTINGS = LocalUtil.getContent(FILE, reader ->
                reader.lines()
                        .map(line -> line.split("="))
                        .filter(args -> args.length == 2)
                        .collect(Collectors.toMap(args -> args[0], args -> args[1]))
        );
        SETTINGS = SETTINGS == null ? new ConcurrentHashMap<>() : SETTINGS;
    }

    public void saveSettings() {
        LocalUtil.setContent(FILE, writer -> {
            SETTINGS.entrySet()
                    .stream()
                    .map(setting -> setting.getKey() + "=" + setting.getValue())
                    .forEach(line -> {
                        try {
                            writer.write(line + "\n");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        });
    }

    public void setCurrentTheme(Theme theme) {
        SETTINGS.put("theme", theme.name());
        saveSettings();
        updateStyles();
    }

    public String currentThemeName() {
        return SETTINGS.getOrDefault("theme", "DEFAULT");
    }

    public Theme getThemeByName(String name) {
        return Theme.valueOf(name.toUpperCase());
    }

    public Theme currentTheme() {
        return getThemeByName(currentThemeName());
    }

    public void applyStyles(Parent parent) {
        val stylesheets = parent.getStylesheets();
        stylesheets.clear();
        stylesheets.add(currentTheme().getExternal());
        parent.applyCss();
    }

    public void updateStyles() {
        Arrays.stream(App.class.getDeclaredFields())
                .filter(field -> field.getType().isAssignableFrom(Window.class))
                .map(field -> {
                    try {
                        field.setAccessible(true);
                        return ((Window<?>) field.get(null));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(Window::getScene)
                .map(Scene::getRoot)
                .forEach(LocalSettings::applyStyles);
    }

    public void changeTheme(String themeName) {
        SETTINGS.put("theme", getThemeByName(themeName) == null ? "default" : themeName);

        updateStyles();

        saveSettings();
    }

    @Getter
    @RequiredArgsConstructor
    public static enum Theme {

        DEFAULT("Default", "style.css"),
        FIRST("First", "first.css"),
        ;

        private final String name;
        private final String resource;

        public String getExternal() {
            return ResourceUtil.getCSSResourceURL(resource).toExternalForm();
        }

    }


}
