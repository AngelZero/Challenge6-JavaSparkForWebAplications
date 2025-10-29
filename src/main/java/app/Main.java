package app;

import app.config.Migrations;
import app.web.GlobalErrorHandler;
import app.web.UserController;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws Exception {
        port(4567);
        log.info("Starting server on port 4567");
        Migrations.run();

        get("/health", (req, res) -> "OK");
        GlobalErrorHandler.register(new Gson());
        new UserController().register();

        awaitInitialization();
        log.info("Server on http://localhost:4567");
    }
}
