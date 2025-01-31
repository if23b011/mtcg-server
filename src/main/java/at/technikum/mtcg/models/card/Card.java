package at.technikum.mtcg.models.card;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class Card {
    private final UUID id;
    private final String name;
    private final double damage;
    private final CardType type;
    private final ElementType element;

    @JsonCreator
    public Card(@JsonProperty("Id") UUID id,
                @JsonProperty("Name") String name,
                @JsonProperty("Damage") double damage,
                @JsonProperty("Element") ElementType element) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.element = determineElementType(name);
        this.type = determineCardType(name);
    }


    private ElementType determineElementType(String name) {
        if (name.toLowerCase().contains("fire")) return ElementType.FIRE;
        if (name.toLowerCase().contains("water")) return ElementType.WATER;
        return ElementType.NORMAL;
    }


    private CardType determineCardType(String name) {
        return name.toLowerCase().contains("spell") ? CardType.SPELL : CardType.MONSTER;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public double getDamage() { return damage; }
    public CardType getType() { return type; }
    public ElementType getElement() { return element; }
}
