package at.technikum.mtcg.dal.repository;

import at.technikum.mtcg.dal.UnitOfWork;
import at.technikum.mtcg.models.card.Card;
import at.technikum.mtcg.models.Package;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PackageRepository {

    public void addPackage(Package pack) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            String sqlPackage = "INSERT INTO packages DEFAULT VALUES RETURNING incrementedID";
            PreparedStatement stmtPackage = unitOfWork.prepareStatement(sqlPackage);
            ResultSet rs = stmtPackage.executeQuery();

            if (!rs.next()) {
                throw new SQLException("Fehler: Paket konnte nicht gespeichert werden!");
            }

            int packageId = rs.getInt("incrementedID");


            String sqlCard = "INSERT INTO cards (id, name, damage, type, element, package_id) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmtCard = unitOfWork.prepareStatement(sqlCard);

            for (Card card : pack.getCards()) {
                stmtCard.setObject(1, card.getId());
                stmtCard.setString(2, card.getName());
                stmtCard.setDouble(3, card.getDamage());
                stmtCard.setString(4, card.getType().name());
                stmtCard.setString(5, card.getElement().name());
                stmtCard.setInt(6, packageId);
                stmtCard.addBatch();
            }

            stmtCard.executeBatch();
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Speichern des Pakets", e);
        }
    }
}
