package at.technikum.mtcg.dal.repository;

import at.technikum.mtcg.dal.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class StatsRepository {

    public Map<String, Object> getUserStats(String token) throws SQLException {
        Map<String, Object> userStats = new HashMap<>();
        try (UnitOfWork unit = new UnitOfWork()) {
            PreparedStatement stmt = unit.prepareStatement(
                    "SELECT username, elo, games_played FROM users WHERE token = ?");
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                userStats.put("username", rs.getString("username"));
                userStats.put("elo", rs.getInt("elo"));
                userStats.put("games_played", rs.getInt("games_played"));
            }
        }
        return userStats;
    }

    public List<Map<String, Object>> getScoreboard() throws SQLException {
        List<Map<String, Object>> scoreboard = new ArrayList<>();
        try (UnitOfWork unit = new UnitOfWork()) {
            PreparedStatement stmt = unit.prepareStatement(
                    "SELECT username, elo FROM users ORDER BY elo DESC");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> user = new HashMap<>();
                user.put("username", rs.getString("username"));
                user.put("elo", rs.getInt("elo"));
                scoreboard.add(user);
            }
        }
        return scoreboard;
    }
}
