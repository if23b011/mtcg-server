package at.technikum.mtcg.models;

import com.google.gson.annotations.SerializedName;
import at.technikum.mtcg.utils.DatabaseUtil;
import java.util.UUID;

public class User {
    @SerializedName("Username")
    private final String username;
    @SerializedName("Password")
    private final String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String generateAuthToken() {
        return username + "-mtcgToken-" + UUID.randomUUID();
    }

    public boolean register() {
        return DatabaseUtil.registerUser(this);
    }

    public String login() {
        return DatabaseUtil.loginUser(this);
    }
}