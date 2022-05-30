package ua.dream.chat.util.serialization;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class SerializationExcludeStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        return fieldAttributes.getAnnotation(IgnoreSerialization.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
        return !aClass.isAnnotationPresent(IgnoreSerialization.class);
    }
}
