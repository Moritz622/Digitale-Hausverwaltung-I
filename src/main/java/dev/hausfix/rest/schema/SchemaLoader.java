package dev.hausfix.rest.schema;

import org.everit.json.schema.Schema;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class SchemaLoader {

    public static JSONObject load(JSONObject json, String schemaPath){
        try {
            InputStream schemaStream = new FileInputStream(schemaPath);

            JSONObject schemaJson = new JSONObject(new JSONTokener(schemaStream));

            Schema schema = org.everit.json.schema.loader.SchemaLoader.load(schemaJson);

            schema.validate(json);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return json;
    }

}
