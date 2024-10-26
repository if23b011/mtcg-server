package at.technikum.mtcg.util;

import at.technikum.mtcg.User;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class JsonUtil {
    private static final Gson gson = new Gson();

    public static User parseUserFromJson(String json) {
        try {
            return gson.fromJson(json, User.class);
        } catch (JsonSyntaxException e) {
            System.out.println("Fehler beim Parsen der JSON-Anfrage: " + e.getMessage() + "\n");
            return null;
        }
    }
}