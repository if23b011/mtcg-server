package at.technikum.mtcg.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TradingDeal {
    private final String id;
    private final String cardToTrade;
    private final String type;
    private final int minimumDamage;

    @JsonCreator
    public TradingDeal(
            @JsonProperty("Id") String id,
            @JsonProperty("CardToTrade") String cardToTrade,
            @JsonProperty("Type") String type,
            @JsonProperty("MinimumDamage") int minimumDamage
    ) {
        this.id = id;
        this.cardToTrade = cardToTrade;
        this.type = type;
        this.minimumDamage = minimumDamage;
    }

    public String getId() { return id; }
    public String getCardToTrade() { return cardToTrade; }
    public String getType() { return type; }
    public int getMinimumDamage() { return minimumDamage; }
}
