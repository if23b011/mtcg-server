package at.technikum.mtcg.controller;

import at.technikum.httpserver.http.ContentType;
import at.technikum.httpserver.http.HttpStatus;
import at.technikum.httpserver.server.Response;
import at.technikum.mtcg.dal.repository.BattleRepository;
import at.technikum.mtcg.models.card.Card;
import at.technikum.mtcg.models.card.ElementType;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BattleController {
    private static final BlockingQueue<String> battleQueue = new LinkedBlockingQueue<>();
    private final BattleRepository battleRepository = new BattleRepository();

    public Response startBattle(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"error\":\"Unauthorized\"}");
        }
        token = token.substring(7);

        try {
            String username = battleRepository.getUsernameFromToken(token);
            if (username == null) {
                return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"error\":\"User not found\"}");
            }

            System.out.println("[DEBUG] Benutzer '" + username + "' möchte eine Schlacht starten.");

            if (!battleQueue.isEmpty()) {
                String opponent = battleQueue.poll();
                System.out.println("[DEBUG] Gegner gefunden: " + opponent);
                return executeBattle(username, opponent);
            } else {
                battleQueue.add(username);
                System.out.println("[DEBUG] Kein Gegner verfügbar. " + username + " wartet in der Battle-Queue...");
                return new Response(HttpStatus.OK, ContentType.JSON, "{\"message\":\"Waiting for an opponent...\"}");
            }
        } catch (SQLException e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\":\"Database error\"}");
        }
    }

    private Response executeBattle(String player1, String player2) throws SQLException {
        System.out.println("[DEBUG] Starte Battle: " + player1 + " vs " + player2);

        List<Card> deck1 = battleRepository.loadDeck(player1);
        List<Card> deck2 = battleRepository.loadDeck(player2);

        if (deck1.isEmpty() || deck2.isEmpty()) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\":\"One or both players have an empty deck\"}");
        }

        battleRepository.updateGamesPlayed(player1);
        battleRepository.updateGamesPlayed(player2);

        String battleLog = simulateBattle(player1, deck1, player2, deck2);

        if (deck1.isEmpty()) {
            battleRepository.updateElo(player2, player1);
            battleLog += "\n" + player2 + " gewinnt +3 ELO, " + player1 + " verliert -5 ELO.";
        } else if (deck2.isEmpty()) {
            battleRepository.updateElo(player1, player2);
            battleLog += "\n" + player1 + " gewinnt +3 ELO, " + player2 + " verliert -5 ELO.";
        } else {
            battleLog += "\nBeide Spieler haben noch Karten im Deck. Keine ELO-Änderung.";
        }
        battleLog += "\n";

        System.out.println(battleLog);

        return new Response(HttpStatus.OK, ContentType.JSON, "{\"log\":\"" + battleLog + "\"}");
    }
    boolean special = false;
    int rounds = 0;
    private String simulateBattle(String player1, List<Card> deck1, String player2, List<Card> deck2) {
        Random random = new Random();
        StringBuilder log = new StringBuilder("Battle between ").append(player1).append(" and ").append(player2).append(": \n");

        List<Card> shuffledDeck1 = new ArrayList<>(deck1);
        List<Card> shuffledDeck2 = new ArrayList<>(deck2);
        Collections.shuffle(shuffledDeck1);
        Collections.shuffle(shuffledDeck2);
        String[] terrains = {"Ozean", "Vulkan", "Gebirge"};

        String terrain = terrains[random.nextInt(terrains.length)];

        log.append("Terrain: ").append(terrain).append("\n");

        System.out.println("Spieler1: " + player1 + " Spieler2: " + player2);

        while (!shuffledDeck1.isEmpty() && !shuffledDeck2.isEmpty() && rounds < 100) {
            rounds++;

            Card card1 = shuffledDeck1.get(random.nextInt(shuffledDeck1.size()));
            Card card2 = shuffledDeck2.get(random.nextInt(shuffledDeck2.size()));

            System.out.println("[DEBUG] Runde " + rounds + ": " + card1.getName() + " (" + card1.getDamage() + ") vs " + card2.getName() + " (" + card2.getDamage() + ")");


            log.append("Round ").append(rounds).append(": ")
                    .append(card1.getName()).append(" (").append(card1.getElement()).append(", ").append(card1.getDamage()).append(") vs ")
                    .append(card2.getName()).append(" (").append(card2.getElement()).append(", ").append(card2.getDamage()).append(") ");

            special = false;
            // Spezialfähigkeiten prüfen
            if (isGoblinVsDragon(card1, card2)) {
                log.append("-> Goblin is too afraid to fight! ").append(player2).append(" wins this round!\n");
                shuffledDeck1.remove(card1);
                shuffledDeck2.add(card1); // Gewinner bekommt die Karte
                special = true;
                continue;
            } else if (isGoblinVsDragon(card2, card1)) {
                log.append("-> Goblin is too afraid to fight! ").append(player1).append(" wins this round!\n");
                shuffledDeck2.remove(card2);
                shuffledDeck1.add(card2); // Gewinner bekommt die Karte
                special = true;
                continue;
            }

            if (isWizardVsOrk(card1, card2)) {
                log.append("-> Wizard controls the Ork! ").append(player1).append(" wins this round!\n");
                shuffledDeck2.remove(card2);
                shuffledDeck1.add(card2); // Gewinner bekommt die Karte
                special = true;
                continue;
            } else if (isWizardVsOrk(card2, card1)) {
                log.append("-> Wizard controls the Ork! ").append(player2).append(" wins this round!\n");
                shuffledDeck1.remove(card1);
                shuffledDeck2.add(card1); // Gewinner bekommt die Karte
                special = true;
                continue;
            }

            if (isKnightVsWaterSpell(card1, card2)) {
                log.append("-> Knight drowns due to WaterSpell! ").append(player2).append(" wins this round!\n");
                shuffledDeck1.remove(card1);
                shuffledDeck2.add(card1); // Gewinner bekommt die Karte
                special = true;
                continue;
            } else if (isKnightVsWaterSpell(card2, card1)) {
                log.append("-> Knight drowns due to WaterSpell! ").append(player1).append(" wins this round!\n");
                shuffledDeck2.remove(card2);
                shuffledDeck1.add(card2); // Gewinner bekommt die Karte
                special = true;
                continue;
            }


            if (isKrakenVsSpell(card1, card2) || isKrakenVsSpell(card2, card1)) {
                log.append("-> Kraken is immune to spells! Round skipped.\n");
                special = true;
                continue;
            }

            if (isFireElfVsDragon(card1, card2) || isFireElfVsDragon(card2, card1)) {
                log.append("-> FireElf evades the Dragon’s attack! Round skipped.\n");
                special = true;
                continue;
            }
            if (!special) {
                // Element-Schaden berechnen
                double adjustedDamage1 = getEffectiveDamage(card1, card2, terrain);
                double adjustedDamage2 = getEffectiveDamage(card2, card1, terrain);

                if (adjustedDamage1 > adjustedDamage2) {
                    log.append("-> ").append(player1).append(" wins this round!\n");
                    shuffledDeck2.remove(card2);
                    shuffledDeck1.add(card2); // Spieler 1 gewinnt die Karte
                } else if (adjustedDamage1 < adjustedDamage2) {
                    log.append("-> ").append(player2).append(" wins this round!\n");
                    shuffledDeck1.remove(card1);
                    shuffledDeck2.add(card1); // Spieler 2 gewinnt die Karte
                } else {
                    log.append("-> It's a draw!\n");
                }
            }

        }
        // **Prüfen, wer gewonnen hat**
        if (shuffledDeck1.isEmpty()) {
            log.append(player2).append(" WINS the battle!n");
        } else if (shuffledDeck2.isEmpty()) {
            log.append(player1).append(" WINS the battle!\n");
        } else {
            log.append("\nThe battle ended in a DRAW after 100 rounds!\n");
        }
        System.out.println(log);
        return log.toString();
    }

    private boolean isGoblinVsDragon(Card c1, Card c2) {
        boolean isGoblin = c1.getName().contains("Goblin");
        boolean isDragon = c2.getName().contains("Dragon");
        return isGoblin && isDragon;
    }

    private boolean isWizardVsOrk(Card c1, Card c2) {
        boolean isWizard = c1.getName().contains("Wizzard");
        boolean isOrk = c2.getName().contains("Ork");
        return isWizard && isOrk;
    }

    private boolean isKnightVsWaterSpell(Card c1, Card c2) {
        boolean isKnight = c1.getName().contains("Knight");
        boolean isWaterSpell = c2.getElement() == ElementType.WATER && c2.getName().contains("Spell");

        return isKnight && isWaterSpell;
    }

    private boolean isKrakenVsSpell(Card c1, Card c2) {
        boolean isKraken = c1.getName().contains("Kraken");
        boolean isSpell = c2.getName().contains("Spell");
        return isKraken && isSpell;
    }

    private boolean isFireElfVsDragon(Card c1, Card c2) {
        boolean isFireElf = c1.getName().contains("Elf") && c1.getElement() == ElementType.FIRE;
        boolean isDragon = c2.getName().contains("Dragon");
        return isFireElf && isDragon;
    }

    private double getEffectiveDamage(Card attacker, Card defender, String terrain) {
        double damage = attacker.getDamage();
        System.out.println("Damage before: " + damage + " (" + attacker.getName() + " vs " + defender.getName() + ")");

        // Terrain-Effekt zuerst anwenden
        damage = applyTerrainEffect(terrain, attacker, damage);
        System.out.println("Damage after terrain: " + damage + " (" + attacker.getName() + " vs " + defender.getName() + ")");

        // Element-Typ-Effekt berechnen
        if (!attacker.getName().contains("Spell") && !defender.getName().contains("Spell")) {
            return damage; // Monster-Kämpfe sind nicht von Elementen betroffen
        }

        System.out.println("Element: " + attacker.getElement() + " vs " + defender.getElement());
        ElementType attackerElement = attacker.getElement();
        ElementType defenderElement = defender.getElement();

        if (attackerElement == ElementType.WATER && defenderElement == ElementType.FIRE) return damage * 2;
        if (attackerElement == ElementType.FIRE && defenderElement == ElementType.NORMAL) return damage * 2;
        if (attackerElement == ElementType.NORMAL && defenderElement == ElementType.WATER) return damage * 2;

        if (attackerElement == ElementType.WATER && defenderElement == ElementType.NORMAL) return damage / 2;
        if (attackerElement == ElementType.FIRE && defenderElement == ElementType.WATER) return damage / 2;
        if (attackerElement == ElementType.NORMAL && defenderElement == ElementType.FIRE) return damage / 2;
        return damage;
    }

    private double applyTerrainEffect(String terrain, Card card, double damage) {

        switch (terrain) {
            case "Ozean":
                if (card.getElement() == ElementType.WATER) return damage * 1.5;
                if (card.getElement() == ElementType.FIRE) return damage / 2;
                break;
            case "Gebirge":
                if (card.getElement() == ElementType.NORMAL) return damage * 1.5;
                if (card.getElement() == ElementType.WATER) return damage / 2;
                break;
            case "Vulkan":
                if (card.getElement() == ElementType.FIRE) return damage * 1.5;
                if (card.getElement() == ElementType.NORMAL) return damage / 2;
                break;
        }
        return damage;
    }

}
