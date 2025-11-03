package app.service;

import app.model.Offer;
import app.model.dto.OfferRequestDTO;
import app.model.dto.OfferResponseDTO;
import app.repo.OfferRepository;
import app.repo.ItemRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OfferService {
    private final OfferRepository repo;
    private final ItemRepository itemRepo;
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public OfferService(OfferRepository repo, ItemRepository itemRepo) {
        this.repo = repo; this.itemRepo = itemRepo;
    }

    public OfferResponseDTO create(OfferRequestDTO dto) throws SQLException {
        validate(dto);
        var offer = new Offer(null, dto.getItemId(), dto.getName(), dto.getEmail(), dto.getAmount());
        repo.insert(offer);
        return new OfferResponseDTO(offer.getId(), offer.getItemId(), offer.getName(), offer.getEmail(), offer.getAmount());
    }

    public List<OfferResponseDTO> list() throws SQLException {
        return repo.findAll().stream()
                .map(o -> new OfferResponseDTO(o.getId(), o.getItemId(), o.getName(), o.getEmail(), o.getAmount()))
                .collect(Collectors.toList());
    }

    private void validate(OfferRequestDTO dto) throws SQLException {
        if (dto == null) throw new BadRequest("Body is required");
        if (blank(dto.getItemId())) throw new BadRequest("itemId is required");
        if (blank(dto.getName())) throw new BadRequest("name is required");
        if (blank(dto.getEmail())) throw new BadRequest("email is required");
        if (!EMAIL.matcher(dto.getEmail()).matches()) throw new BadRequest("email is invalid");
        BigDecimal a = dto.getAmount();
        if (a == null || a.signum() <= 0) throw new BadRequest("amount must be > 0");
        itemRepo.findById(dto.getItemId()).orElseThrow(() -> new NotFound("Item not found"));
    }

    private boolean blank(String s) { return s == null || s.isBlank(); }

    public static class NotFound extends RuntimeException { public NotFound(String m){super(m);} }
    public static class BadRequest extends RuntimeException { public BadRequest(String m){super(m);} }
}
