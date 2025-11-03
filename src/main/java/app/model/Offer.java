package app.model;

import java.math.BigDecimal;

public class Offer {
    private Long id;
    private String itemId;
    private String name;
    private String email;
    private BigDecimal amount;

    public Offer() {}
    public Offer(Long id, String itemId, String name, String email, BigDecimal amount) {
        this.id = id; this.itemId = itemId; this.name = name; this.email = email; this.amount = amount;
    }

    public Long getId() { return id; }
    public String getItemId() { return itemId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public BigDecimal getAmount() { return amount; }

    public void setId(Long id) { this.id = id; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
