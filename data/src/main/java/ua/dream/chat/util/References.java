package ua.dream.chat.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.experimental.UtilityClass;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Pattern;

@UtilityClass
public class References {

    public final String SERVER_HOST = "195.201.70.95";
    public final int SERVER_PORT = 4444;

    public final Pattern EMAIL_PATTERN = Pattern.compile("[a-z\\d._%+-]+@[a-z\\d.-]+\\.[a-z]{2,4}$");

    public final DateFormat DATE_FORMAT = new SimpleDateFormat("MMMMM dd", Locale.ENGLISH);
    public final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    public final DateFormat TIME_SIMPLE_FORMAT = new SimpleDateFormat("HH:mm");

    public final int PASSWORD_MIN_LENGTH = 8;
    public final int PASSWORD_MAX_LENGTH = 32;

    public final int NAME_MIN_LENGTH = 4;
    public final int NAME_MAX_LENGTH = 16;

    public final long ID_MIN_LENGTH = 1_000_000_000_000L;
    public final long ID_MAX_LENGTH = 9_999_999_999_999L;



    public final Gson GSON = new GsonBuilder()
//            .setExclusionStrategies(new SerializationExcludeStrategy())
            // TODO: 29.05.2022 add serializer
            .create();

}
