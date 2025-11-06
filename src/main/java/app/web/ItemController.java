package app.web;

import app.model.dto.ItemRequestDTO;
import app.repo.ItemRepository;
import app.service.ItemService;
import com.google.gson.Gson;

import java.math.BigDecimal;

import static spark.Spark.*;

public class ItemController {
    private final Gson gson;
    private final ItemService service;

    public ItemController(Gson gson) {
        this.gson = gson;
        this.service = new ItemService(new ItemRepository());
    }
    private static BigDecimal parseDecimal(String s) {
        try { return (s == null || s.isBlank()) ? null : new BigDecimal(s); }
        catch (NumberFormatException nfe) { return null; }
    };

    public void register() {
        post("/items/:id", (req, res) -> {
            res.type("application/json");
            var dto = gson.fromJson(req.body(), ItemRequestDTO.class);
            var created = service.create(req.params(":id"), dto);
            res.status(201);

            app.realtime.WsEndpoint.broadcastItemCreated(
                    created.getId(), created.getName(), created.getDescription(),
                    created.getPrice(), created.getCurrency()
            );

            return gson.toJson(created);
        });

        put("/items/:id", (req, res) -> {
            res.type("application/json");
            var dto = gson.fromJson(req.body(), ItemRequestDTO.class);
            var updated = service.update(req.params(":id"), dto);

            app.realtime.WsEndpoint.broadcastItemUpdate(
                    updated.getId(), updated.getName(), updated.getDescription(),
                    updated.getPrice(), updated.getCurrency()
            );

            return gson.toJson(updated);
        });

        options("/items/:id", (req, res) -> {
            res.header("Allow", "GET,POST,PUT,DELETE,OPTIONS");
            try { service.get(req.params(":id")); res.status(204); }
            catch (ItemService.NotFound nf) { res.status(404); }
            return "";
        });

        delete("/items/:id", (req, res) -> { service.delete(req.params(":id")); res.status(204);
            app.realtime.WsEndpoint.broadcastItemDeleted(req.params(":id"));
            return ""; });

        get("/items", (req, res) -> {
            res.type("application/json");
            BigDecimal min = parseDecimal(req.queryParams("min_price"));
            BigDecimal max = parseDecimal(req.queryParams("max_price"));
            String currency = req.queryParams("currency");
            String q = req.queryParams("q");

            boolean noFilters =
                    min == null &&
                            max == null &&
                            (currency == null || currency.isBlank()) &&
                            (q == null || q.isBlank());

            if (noFilters) {
                return gson.toJson(service.list()); // ALL items
            }
            return gson.toJson(service.listFiltered(min, max, currency, q));
        });




    }
}
