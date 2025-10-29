package app.service;

import app.model.Item;
import app.model.dto.ItemListDTO;
import app.model.dto.ItemRequestDTO;
import app.model.dto.ItemResponseDTO;
import app.repo.ItemRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ItemService {

    private final ItemRepository repo;
    private static final Pattern CURRENCY_3L = Pattern.compile("^[A-Z]{3}$");

    public ItemService(ItemRepository repo) { this.repo = repo; }

    public List<ItemListDTO> list() throws SQLException {
        return repo.findAll().stream()
                .map(i -> new ItemListDTO(i.getId(), i.getName(), i.getPrice(), i.getCurrency()))
                .collect(Collectors.toList());
    }

    public ItemResponseDTO get(String id) throws SQLException {
        return repo.findById(id)
                .map(i -> new ItemResponseDTO(i.getId(), i.getName(), i.getDescription(), i.getPrice(), i.getCurrency()))
                .orElseThrow(() -> new NotFound("Item not found"));
    }

    public ItemResponseDTO create(String id, ItemRequestDTO dto) throws SQLException {
        if (repo.exists(id)) throw new Conflict("Item already exists");
        validate(dto);
        Item it = new Item(id, dto.getName(), dto.getDescription(), dto.getPrice(), dto.getCurrency().toUpperCase());
        repo.insert(it);
        return new ItemResponseDTO(it.getId(), it.getName(), it.getDescription(), it.getPrice(), it.getCurrency());
    }

    public ItemResponseDTO update(String id, ItemRequestDTO dto) throws SQLException {
        repo.findById(id).orElseThrow(() -> new NotFound("Item not found"));
        validate(dto);
        Item it = new Item(id, dto.getName(), dto.getDescription(), dto.getPrice(), dto.getCurrency().toUpperCase());
        repo.update(it);
        return new ItemResponseDTO(it.getId(), it.getName(), it.getDescription(), it.getPrice(), it.getCurrency());
    }

    public void delete(String id) throws SQLException {
        if (repo.delete(id) == 0) throw new NotFound("Item not found");
    }

    private void validate(ItemRequestDTO dto) {
        if (dto == null) throw new BadRequest("Body is required");
        if (blank(dto.getName())) throw new BadRequest("Name is required");
        BigDecimal p = dto.getPrice();
        if (p == null) throw new BadRequest("Price is required");
        if (p.signum() < 0) throw new BadRequest("Price must be non-negative");
        if (blank(dto.getCurrency())) throw new BadRequest("Currency is required");
        String cur = dto.getCurrency().toUpperCase();
        if (!CURRENCY_3L.matcher(cur).matches())
            throw new BadRequest("Currency must be a 3-letter code (e.g., USD, MXN)");
    }

    private boolean blank(String s) { return s == null || s.isBlank(); }

    public static class NotFound extends RuntimeException { public NotFound(String m){super(m);} }
    public static class Conflict extends RuntimeException { public Conflict(String m){super(m);} }
    public static class BadRequest extends RuntimeException { public BadRequest(String m){super(m);} }
}
