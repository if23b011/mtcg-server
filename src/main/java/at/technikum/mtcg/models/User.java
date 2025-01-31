package at.technikum.mtcg.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

public class User {
    @Setter
    @Getter
    @JsonAlias({"id"})
    private Integer id;
    @Setter
    @Getter
    @JsonAlias({"username", "Username"})
    private String username;
    @Getter
    @JsonAlias({"password", "Password"})
    private String password;
    @JsonAlias({"coins"})
    @Getter
    private int coins = 20;
    private boolean loggedIn;
    @Getter
    @Setter
    @JsonAlias({"token", "Token"})
    private String token;
    @Getter
    @JsonAlias({"bio", "Bio"})
    private String bio;
    @Getter
    @JsonAlias({"image", "Image"})
    private String image;

    public User() {}

    public User(Integer id, String username, String password, int coins, String token, String bio, String image) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.coins = coins;
        this.token = token;
        this.bio = bio;
        this.image = image;
    }

    public User(Integer id, String username, String password, int coins) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.coins = coins;
    }

}
