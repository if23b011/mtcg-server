package at.technikum.mtcg.models;

public class Card {
    private String id;
    private String name;
    private String type;
    private double damage;

    public Card(String id, String name, String type, double damage) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.damage = damage;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public double getDamage() {
        return damage;
    }
}
