package app.web;

import com.google.gson.Gson;
import spark.Response;

import static spark.Spark.exception;

public final class GlobalErrorHandler {
    private GlobalErrorHandler() {}

    public static void register(Gson gson) {
        // 404 Not Found
        exception(app.service.UserService.NotFound.class,
                (ex, req, res) -> send(res, 404, gson, ex.getMessage()));
        exception(app.service.ItemService.NotFound.class,
                (ex, req, res) -> send(res, 404, gson, ex.getMessage()));

        // 409 Conflict (dominio)
        exception(app.service.UserService.Conflict.class,
                (ex, req, res) -> send(res, 409, gson, ex.getMessage()));
        exception(app.service.ItemService.Conflict.class,
                (ex, req, res) -> send(res, 409, gson, ex.getMessage()));

        // 409 Conflict
        exception(java.sql.SQLIntegrityConstraintViolationException.class,
                (ex, req, res) -> send(res, 409, gson, "Conflict: duplicate or constraint violation"));
        // H2 lanza esta excepción específica en algunos casos
        exception(org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException.class,
                (ex, req, res) -> send(res, 409, gson, "Conflict: duplicate or constraint violation"));

        // 400 Bad Request
        exception(app.service.UserService.BadRequest.class,
                (ex, req, res) -> send(res, 400, gson, ex.getMessage()));
        exception(app.service.ItemService.BadRequest.class,
                (ex, req, res) -> send(res, 400, gson, ex.getMessage()));

        // 500 genérico (cualquier otra cosa)
        exception(Exception.class, (ex, req, res) -> {
            // aquí podrías log.error(..., ex) si quieres ver el stack trace en consola
            send(res, 500, gson, "Internal error");
        });
    }

    private static void send(Response res, int status, Gson gson, String msg) {
        res.type("application/json");
        res.status(status);
        res.body(gson.toJson(new ErrorBody(msg)));
    }

    private record ErrorBody(String message) {}
}
