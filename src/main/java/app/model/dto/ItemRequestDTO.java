package app.model.dto;

import java.math.BigDecimal;

public class ItemRequestDTO {
    private String name;
    private String description;
    private BigDecimal price;
    private String currency;

    public ItemRequestDTO() {}
    public ItemRequestDTO(String name, String description, BigDecimal price, String currency) {
        this.name = name; this.description = description; this.price = price; this.currency = currency;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public String getCurrency() { return currency; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setCurrency(String currency) { this.currency = currency; }
}
