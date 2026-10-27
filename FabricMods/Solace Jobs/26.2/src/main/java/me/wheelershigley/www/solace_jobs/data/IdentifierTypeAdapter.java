package me.wheelershigley.www.solace_jobs.data;

import com.google.gson.*;
import net.minecraft.resources.Identifier;

import java.lang.reflect.Type;

public class IdentifierTypeAdapter implements JsonSerializer<Identifier>, JsonDeserializer<Identifier> {
    @Override
    public JsonElement serialize(
        Identifier identifier,
        Type type,
        JsonSerializationContext context
    ) {
        return new JsonPrimitive( identifier.toString() );
    }

    @Override
    public Identifier deserialize(
        JsonElement element,
        Type type,
        JsonDeserializationContext context
    ) throws JsonParseException {
        return Identifier.parse( element.getAsString() );
    }
}