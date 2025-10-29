package app.web;

import app.model.dto.UserRequestDTO;
import app.repo.UserRepository;
import app.service.UserService;
import com.google.gson.Gson;

import static spark.Spark.*;

public class UserController {
    private final Gson gson;
    private final UserService service;

    public UserController(Gson gson) {
        this.gson = gson;
        this.service = new UserService(new UserRepository());
    }

    public void register() {
        get("/users", (req, res) -> {
            res.type("application/json"); return gson.toJson(service.list());
        });

        get("/users/:id", (req, res) -> {
            res.type("application/json"); return gson.toJson(service.get(req.params(":id")));
        });
        post("/users/:id", (req, res) -> {
            res.type("application/json");
            var dto = gson.fromJson(req.body(), UserRequestDTO.class);
            var created = service.create(req.params(":id"), dto);
            res.status(201);
            return gson.toJson(created);
        });
        put("/users/:id", (req, res) -> {
            res.type("application/json");
            var dto = gson.fromJson(req.body(), UserRequestDTO.class);
            return gson.toJson(service.update(req.params(":id"), dto));
        });
        options("/users/:id", (req, res) -> {
            res.header("Allow", "GET,POST,PUT,DELETE,OPTIONS");
            try { service.get(req.params(":id")); res.status(204); } catch (Exception e) { res.status(404); }
            return "";
        });
        delete("/users/:id", (req, res) -> { service.delete(req.params(":id")); res.status(204); return ""; });
    }
}
