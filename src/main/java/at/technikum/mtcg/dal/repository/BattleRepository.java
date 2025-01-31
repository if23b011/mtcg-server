package at.technikum.mtcg.dal.repository;

import at.technikum.mtcg.dal.UnitOfWork;
import at.technikum.mtcg.models.card.Card;
import at.technikum.mtcg.models.card.ElementType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class BattleRepository {

    public String getUsernameFromToken(String token) throws SQLException {
        try (UnitOfWork unit = new UnitOfWork()) {
            PreparedStatement stmt = unit.prepareStatement("SELECT username FROM users WHERE token = ?");
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("username");
            }
        }
        return null;
    }

    public void updateGamesPlayed(String username) throws SQLException {
        try (UnitOfWork unit = new UnitOfWork()) {
            PreparedStatement stmt = unit.prepareStatement("UPDATE users SET games_played = games_played + 1 WHERE username = ?");
            stmt.setString(1, username);
            stmt.executeUpdate();
            unit.commitTransaction();
        }
    }

    public void updateElo(String winner, String loser) throws SQLException {
        try (UnitOfWork unit = new UnitOfWork()) {
            PreparedStatement updateWinner = unit.prepareStatement("UPDATE users SET elo = elo + 3 WHERE username = ?");
            updateWinner.setString(1, winner);
            updateWinner.executeUpdate();

            PreparedStatement updateLoser = unit.prepareStatement("UPDATE users SET elo = GREATEST(elo - 5, 0) WHERE username = ?");
            updateLoser.setString(1, loser);
            updateLoser.executeUpdate();

            unit.commitTransaction();
        }
    }

    public List<Card> loadDeck(String username) throws SQLException {
        List<Card> deck = new ArrayList<>();
        try (UnitOfWork unit = new UnitOfWork()) {
            PreparedStatement stmt = unit.prepareStatement("SELECT id, name, damage, element FROM cards WHERE owner = ? AND in_deck = true");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                deck.add(new Card(UUID.fromString(rs.getString("id")), rs.getString("name"), rs.getDouble("damage"), ElementType.valueOf(rs.getString("element"))));
            }
        }
        return deck;
    }
}
