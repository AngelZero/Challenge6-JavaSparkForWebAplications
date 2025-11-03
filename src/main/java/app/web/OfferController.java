package app.web;

import app.model.dto.OfferRequestDTO;
import app.repo.ItemRepository;
import app.repo.OfferRepository;
import app.service.OfferService;
import com.google.gson.Gson;

import static spark.Spark.*;

public class OfferController {
    private final Gson gson;
    private final OfferService service;

    public OfferController(Gson gson) {
        this.gson = gson;
        this.service = new OfferService(new OfferRepository(), new ItemRepository());
    }

    public void register() {
        post("/api/offer", (req, res) -> {
            res.type("application/json");
            OfferRequestDTO dto;
            String ctype = req.contentType() == null ? "" : req.contentType();
            if (ctype.startsWith("application/json")) {
                dto = gson.fromJson(req.body(), OfferRequestDTO.class);
            } else {
                dto = new OfferRequestDTO(
                        req.queryParams("id") != null ? req.queryParams("id") : req.queryParams("itemId"), // support "id" (legacy) or "itemId"
                        req.queryParams("name"),
                        req.queryParams("email"),
                        req.queryParams("amount") != null ? new java.math.BigDecimal(req.queryParams("amount")) : null
                );
            }
            var created = service.create(dto);
            res.status(201);
            return gson.toJson(created);
        });

        // List offers
        get("/api/offers", (req, res) -> {
            res.type("application/json");
            return gson.toJson(service.list());
        });
    }
}
