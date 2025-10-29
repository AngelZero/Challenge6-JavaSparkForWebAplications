package app.model.dto;

import java.math.BigDecimal;

public class ItemResponseDTO {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private String currency;

    public ItemResponseDTO() {}
    public ItemResponseDTO(String id, String name, String description, BigDecimal price, String currency) {
        this.id = id; this.name = name; this.description = description; this.price = price; this.currency = currency;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public String getCurrency() { return currency; }
}
