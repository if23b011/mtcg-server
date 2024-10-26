package at.technikum.mtcg.models;

import java.util.ArrayList;
import java.util.List;

public class Package {
    private List<Card> cards;

    public Package() {
        this.cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        if (cards.size() < 5) {
            cards.add(card);
        } else {
            throw new IllegalStateException("Paket kann nicht mehr als 5 Karten enthalten.");
        }
    }

    public List<Card> getCards() {
        return cards;
    }
}
