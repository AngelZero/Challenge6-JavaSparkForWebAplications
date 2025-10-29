package app.web;

import app.service.UserService.BadRequest;
import app.service.UserService.Conflict;
import app.service.UserService.NotFound;
import com.google.gson.Gson;
import spark.Response;

import static spark.Spark.exception;

public final class GlobalErrorHandler {
    private GlobalErrorHandler() {}
    public static void register(Gson gson) {
        exception(NotFound.class, (ex, req, res) -> send(res, 404, gson, ex.getMessage()));
        exception(Conflict.class, (ex, req, res) -> send(res, 409, gson, ex.getMessage()));
        exception(BadRequest.class, (ex, req, res) -> send(res, 400, gson, ex.getMessage()));
        exception(Exception.class, (ex, req, res) -> send(res, 500, gson, "Internal error"));
    }
    private static void send(Response res, int status, Gson gson, String msg) {
        res.type("application/json"); res.status(status);
        res.body(gson.toJson(new ErrorBody(msg)));
    }
    private record ErrorBody(String message) {}
}
