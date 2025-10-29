package app.model;

import java.math.BigDecimal;

public class Item {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private String currency;

    public Item() {}
    public Item(String id, String name, String description, BigDecimal price, String currency) {
        this.id = id; this.name = name; this.description = description; this.price = price; this.currency = currency;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
