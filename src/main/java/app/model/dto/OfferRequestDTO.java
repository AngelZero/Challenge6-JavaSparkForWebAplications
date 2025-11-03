package app.model.dto;

import java.math.BigDecimal;

public class OfferRequestDTO {
    private String itemId;
    private String name;
    private String email;
    private BigDecimal amount;

    public OfferRequestDTO() {}
    public OfferRequestDTO(String itemId, String name, String email, BigDecimal amount) {
        this.itemId = itemId; this.name = name; this.email = email; this.amount = amount;
    }

    public String getItemId() { return itemId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public BigDecimal getAmount() { return amount; }

    public void setItemId(String itemId) { this.itemId = itemId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
