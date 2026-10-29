package me.wheelershigley.www.solace_jobs.data;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.io.IOException;

public class ItemTypeAdapter extends TypeAdapter<Item> {

    @Override
    public void write(JsonWriter out, Item item) throws IOException {
        if (item == null) {
            out.nullValue();
            return;
        }

        Identifier id = BuiltInRegistries.ITEM.getKey(item);
        out.value(id.toString());
    }

    @Override
    public Item read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        Identifier id = Identifier.parse(in.nextString());

        return BuiltInRegistries.ITEM.getValue(id);
    }
}