package at.technikum.mtcg.dal.repository;

import at.technikum.mtcg.dal.DataAccessException;
import at.technikum.mtcg.dal.DatabaseManager;
import at.technikum.mtcg.models.TradingDeal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TradingRepository {
    public List<TradingDeal> getAllTradingDeals() {
        List<TradingDeal> deals = new ArrayList<>();
        try (Connection connection = DatabaseManager.INSTANCE.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM trading_deals")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                deals.add(new TradingDeal(
                        rs.getString("id"),
                        rs.getString("card_to_trade"),
                        rs.getString("type"),
                        rs.getInt("minimum_damage")
                ));
            }
        } catch (Exception e) {
            throw new DataAccessException("Error fetching trading deals", e);
        }
        return deals;
    }

    public void addTradingDeal(TradingDeal deal) {
        try (Connection connection = DatabaseManager.INSTANCE.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "INSERT INTO trading_deals (id, card_to_trade, type, minimum_damage) VALUES (?, ?, ?, ?)")) {
            stmt.setObject(1, UUID.fromString(deal.getId()));
            stmt.setObject(2, UUID.fromString(deal.getCardToTrade()));
            stmt.setString(3, deal.getType());
            stmt.setInt(4, deal.getMinimumDamage());
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException("Error adding trading deal", e);
        }
    }


    public void deleteTradingDeal(String id) {
        try (Connection connection = DatabaseManager.INSTANCE.getConnection();
             PreparedStatement stmt = connection.prepareStatement("DELETE FROM trading_deals WHERE id = ?")) {
            stmt.setObject(1, UUID.fromString(id));
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("No trading deal found with ID: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataAccessException("Error deleting trading deal", e);
        }
    }
    public boolean executeTrade(String dealId, String offeredCardId, String username) {
        try (Connection connection = DatabaseManager.INSTANCE.getConnection();
             PreparedStatement checkStmt = connection.prepareStatement(
                     "SELECT owner FROM cards WHERE id = ?");
             PreparedStatement updateStmt = connection.prepareStatement(
                     "UPDATE cards SET owner = (SELECT owner FROM trading_deals WHERE id = ?) WHERE id = ? AND owner = ?"
             );
             PreparedStatement deleteStmt = connection.prepareStatement(
                     "DELETE FROM trading_deals WHERE id = ?")) {

            checkStmt.setObject(1, UUID.fromString(offeredCardId));
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next() || !rs.getString("owner").equals(username)) {
                return false;
            }

            updateStmt.setObject(1, UUID.fromString(dealId));
            updateStmt.setObject(2, UUID.fromString(offeredCardId));
            updateStmt.setString(3, username);

            int updatedRows = updateStmt.executeUpdate();

            if (updatedRows == 1) {
                deleteStmt.setObject(1, UUID.fromString(dealId));
                deleteStmt.executeUpdate();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new DataAccessException("Error executing trade", e);
        }
    }

    public String getTradingDealOwner(String dealId) {
        try (Connection connection = DatabaseManager.INSTANCE.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT c.owner FROM trading_deals t " +
                             "JOIN cards c ON t.card_to_trade = c.id " +
                             "WHERE t.id = ?")) {
            stmt.setObject(1, UUID.fromString(dealId));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("owner");
            }
        } catch (Exception e) {
            throw new DataAccessException("Error retrieving trading deal owner", e);
        }
        return null;
    }




}
