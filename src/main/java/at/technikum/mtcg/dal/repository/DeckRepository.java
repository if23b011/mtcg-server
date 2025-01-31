package at.technikum.mtcg.dal.repository;

import at.technikum.mtcg.dal.UnitOfWork;
import at.technikum.mtcg.models.card.Card;
import at.technikum.mtcg.models.card.ElementType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DeckRepository {

    public List<Card> getDeckByOwner(String owner) throws SQLException {
        List<Card> deck = new ArrayList<>();
        try (UnitOfWork unit = new UnitOfWork()) {
            PreparedStatement stmt = unit.prepareStatement(
                    "SELECT id, name, damage, element FROM cards WHERE owner = ? AND in_deck = TRUE");
            stmt.setString(1, owner);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                deck.add(new Card(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("name"),
                        rs.getDouble("damage"),
                        ElementType.valueOf(rs.getString("element"))
                ));
            }
        }
        return deck;
    }

    public boolean configureDeck(String owner, List<String> cardIds) throws SQLException {
        try (UnitOfWork unit = new UnitOfWork()) {
            PreparedStatement checkStmt = unit.prepareStatement(
                    "SELECT COUNT(*) FROM cards WHERE owner = ? AND id = ANY(?)");
            checkStmt.setString(1, owner);
            checkStmt.setArray(2, unit.getConnection().createArrayOf("UUID", cardIds.toArray()));

            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) != 4) {
                return false;
            }

            PreparedStatement resetStmt = unit.prepareStatement("UPDATE cards SET in_deck = FALSE WHERE owner = ?");
            resetStmt.setString(1, owner);
            resetStmt.executeUpdate();

            PreparedStatement updateStmt = unit.prepareStatement("UPDATE cards SET in_deck = TRUE WHERE id = ANY(?)");
            updateStmt.setArray(1, unit.getConnection().createArrayOf("UUID", cardIds.toArray()));
            updateStmt.executeUpdate();

            unit.commitTransaction();
            return true;
        }
    }
}
