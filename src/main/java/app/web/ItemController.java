package app.web;

import app.model.dto.ItemRequestDTO;
import app.repo.ItemRepository;
import app.service.ItemService;
import com.google.gson.Gson;

import static spark.Spark.*;

public class ItemController {
    private final Gson gson;
    private final ItemService service;

    public ItemController(Gson gson) {
        this.gson = gson;
        this.service = new ItemService(new ItemRepository());
    }

    public void register() {
        get("/items", (req, res) -> { res.type("application/json"); return gson.toJson(service.list()); });

        get("/items/:id", (req, res) -> { res.type("application/json"); return gson.toJson(service.get(req.params(":id"))); });

        post("/items/:id", (req, res) -> {
            res.type("application/json");
            var dto = gson.fromJson(req.body(), ItemRequestDTO.class);
            var created = service.create(req.params(":id"), dto);
            res.status(201);
            return gson.toJson(created);
        });

        put("/items/:id", (req, res) -> {
            res.type("application/json");
            var dto = gson.fromJson(req.body(), ItemRequestDTO.class);
            return gson.toJson(service.update(req.params(":id"), dto));
        });

        options("/items/:id", (req, res) -> {
            res.header("Allow", "GET,POST,PUT,DELETE,OPTIONS");
            try { service.get(req.params(":id")); res.status(204); }
            catch (ItemService.NotFound nf) { res.status(404); }
            return "";
        });

        delete("/items/:id", (req, res) -> { service.delete(req.params(":id")); res.status(204); return ""; });
    }
}
