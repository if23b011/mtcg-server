package at.technikum.mtcg.dal.repository;

import at.technikum.mtcg.dal.UnitOfWork;
import at.technikum.mtcg.models.User;
import at.technikum.mtcg.utils.PwHasher;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    public boolean addUser(User user) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            if (userExists(user.getUsername(), unitOfWork)) {
                return false;
            }

            String hashedPassword = PwHasher.hashPassword(user.getPassword());

            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement stmt = unitOfWork.prepareStatement(sql);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, hashedPassword);

            int affectedRows = stmt.executeUpdate();
            unitOfWork.commitTransaction();

            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Einfügen des Benutzers", e);
        }
    }

    public boolean userExists(String username, UnitOfWork unitOfWork) {
        try {
            String sql = "SELECT username FROM users WHERE username = ?";
            PreparedStatement stmt = unitOfWork.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Überprüfen des Benutzers", e);
        }
    }

    public User getUser(String username) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            String sql = "SELECT id, name, bio, image, token, coins FROM users WHERE username = ?";
            PreparedStatement stmt = unitOfWork.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        null,
                        rs.getInt("coins"),
                        rs.getString("token"),
                        rs.getString("bio"),
                        rs.getString("image")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Abrufen des Benutzers", e);
        }
    }


    public boolean updateUser(String username, String name, String bio, String image) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            String sql = "UPDATE users SET name = ?, bio = ?, image = ? WHERE username = ?";
            PreparedStatement stmt = unitOfWork.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, bio);
            stmt.setString(3, image);
            stmt.setString(4, username);

            int affectedRows = stmt.executeUpdate();
            unitOfWork.commitTransaction();

            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Aktualisieren des Benutzers", e);
        }
    }

    public boolean isValidToken(String username, String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return false;
        }

        String cleanToken = token.substring(7);

        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            String sql = "SELECT token FROM users WHERE username = ?";
            PreparedStatement stmt = unitOfWork.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedToken = rs.getString("token");
                System.out.println("Vergleich: gespeicherter Token = " + storedToken + ", empfangener Token = " + cleanToken);
                return cleanToken.equals(storedToken);
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Überprüfen des Tokens", e);
        }
    }



    public String authenticateUser(String username, String password) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            String sql = "SELECT password FROM users WHERE username = ?";
            PreparedStatement stmt = unitOfWork.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                if (PwHasher.verifyPassword(password, storedHash)) {
                    String token = username + "-mtcgToken";
                    saveToken(username, token, unitOfWork);
                    return token;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Authentifizieren des Benutzers", e);
        }
    }

    private void saveToken(String username, String token, UnitOfWork unitOfWork) throws SQLException {
        System.out.println("Speichere Token für: " + username + " mit Token: " + token);

        String sql = "UPDATE users SET token = ? WHERE username = ?";
        PreparedStatement stmt = unitOfWork.prepareStatement(sql);
        stmt.setString(1, token);
        stmt.setString(2, username);
        int affectedRows = stmt.executeUpdate();

        if (affectedRows > 0) {
            System.out.println("Token erfolgreich gespeichert.");
        } else {
            System.out.println("Fehler: Kein Benutzer mit diesem Namen gefunden.");
        }

        unitOfWork.commitTransaction();
    }



    public boolean buyPackage(String username) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            String checkCoinsSQL = "SELECT coins FROM users WHERE username = ?";
            PreparedStatement checkStmt = unitOfWork.prepareStatement(checkCoinsSQL);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Benutzer nicht gefunden: " + username);
                return false;
            }

            int coins = rs.getInt("coins");
            if (coins < 5) {
                System.out.println("Nicht genug Coins: " + coins);
                return false;
            }

            String findPackageSQL = "SELECT incrementedID FROM packages ORDER BY incrementedID LIMIT 1";
            PreparedStatement findStmt = unitOfWork.prepareStatement(findPackageSQL);
            ResultSet packageResult = findStmt.executeQuery();

            if (!packageResult.next()) {
                System.out.println("Keine Pakete verfügbar");
                return false;
            }

            int packageId = packageResult.getInt("incrementedID");

            String countCardsSQL = "SELECT COUNT(*) FROM cards WHERE package_id = ?";
            PreparedStatement countStmt = unitOfWork.prepareStatement(countCardsSQL);
            countStmt.setInt(1, packageId);
            ResultSet countResult = countStmt.executeQuery();

            if (!countResult.next() || countResult.getInt(1) != 5) {
                System.out.println("Fehler: Paket enthält nicht genau 5 Karten!");
                return false;
            }

            String updateCardsSQL = "UPDATE cards SET owner = ?, package_id = NULL WHERE package_id = ?";
            PreparedStatement updateStmt = unitOfWork.prepareStatement(updateCardsSQL);
            updateStmt.setString(1, username);
            updateStmt.setInt(2, packageId);
            int updatedRows = updateStmt.executeUpdate();

            if (updatedRows != 5) {
                System.out.println("Fehler: Nicht alle 5 Karten wurden aktualisiert!");
                return false;
            }

            String deletePackageSQL = "DELETE FROM packages WHERE incrementedID = ?";
            PreparedStatement deleteStmt = unitOfWork.prepareStatement(deletePackageSQL);
            deleteStmt.setInt(1, packageId);
            deleteStmt.executeUpdate();
            System.out.println("Paket gelöscht: " + packageId);

            String updateCoinsSQL = "UPDATE users SET coins = coins - 5 WHERE username = ?";
            PreparedStatement updateCoinsStmt = unitOfWork.prepareStatement(updateCoinsSQL);
            updateCoinsStmt.setString(1, username);
            updateCoinsStmt.executeUpdate();
            System.out.println("Coins abgezogen: " + username);

            unitOfWork.commitTransaction();
            System.out.println("Paket erfolgreich gekauft!");
            return true;

        } catch (SQLException e) {
            System.err.println("SQL-Fehler beim Kauf eines Pakets: " + e.getMessage());
            return false;
        }
    }
}
