package at.technikum.mtcg.dal.repository;

import at.technikum.mtcg.dal.UnitOfWork;
import at.technikum.mtcg.models.card.Card;
import at.technikum.mtcg.models.card.ElementType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CardRepository {

    public List<Card> getCardsByOwner(String owner) {
        List<Card> cards = new ArrayList<>();
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            String sql = "SELECT id, name, damage, element FROM cards WHERE owner = ?";
            PreparedStatement stmt = unitOfWork.prepareStatement(sql);
            stmt.setString(1, owner);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cards.add(new Card(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("name"),
                        rs.getDouble("damage"),
                        ElementType.valueOf(rs.getString("element"))
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Abrufen der Karten für Benutzer: " + owner, e);
        }
        return cards;
    }

}
