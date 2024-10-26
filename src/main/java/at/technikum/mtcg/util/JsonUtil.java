package at.technikum.mtcg.util;

import at.technikum.mtcg.models.User;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class JsonUtil {
    private static final Gson gson = new Gson();

    public static User parseUserFromJson(String json) {
        try {
            return gson.fromJson(json, User.class);
        } catch (JsonSyntaxException e) {
            System.out.println("Error parsing JSON request: " + e.getMessage() + "\n");
            return null;
        }
    }
}