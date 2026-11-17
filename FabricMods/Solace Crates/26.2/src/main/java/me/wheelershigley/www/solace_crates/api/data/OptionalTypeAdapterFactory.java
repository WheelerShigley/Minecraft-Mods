package me.wheelershigley.www.solace_crates.api.data;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

public class OptionalTypeAdapterFactory implements TypeAdapterFactory {
    @Override
    @SuppressWarnings( {"unchecked", "rawtypes"} )
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if(type.getRawType() != Optional.class) {
            return null;
        }

        Type optionalType = type.getType();
        Type elementType = ( (ParameterizedType)optionalType ).getActualTypeArguments()[0];

        TypeAdapter<?> elementAdapter = gson.getAdapter(
            TypeToken.get(elementType)
        );

        return (TypeAdapter<T>) new TypeAdapter<Optional<?>>() {
            @Override
            public void write(JsonWriter out, Optional<?> value) throws IOException {
                if(value == null || value.isEmpty()) {
                    out.nullValue();
                    return;
                }
                ( (TypeAdapter)elementAdapter ).write( out, value.get() );
            }

            @Override
            public Optional<?> read(JsonReader in) throws IOException {
                if(in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    return Optional.empty();
                }

                return Optional.ofNullable(
                    elementAdapter.read(in)
                );
            }
        };
    }
}