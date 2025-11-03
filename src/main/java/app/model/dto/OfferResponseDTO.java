package app.model.dto;

import java.math.BigDecimal;

public class OfferResponseDTO {
    private Long id;
    private String itemId;
    private String name;
    private String email;
    private BigDecimal amount;

    public OfferResponseDTO() {}
    public OfferResponseDTO(Long id, String itemId, String name, String email, BigDecimal amount) {
        this.id = id; this.itemId = itemId; this.name = name; this.email = email; this.amount = amount;
    }

    public Long getId() { return id; }
    public String getItemId() { return itemId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public BigDecimal getAmount() { return amount; }
}
