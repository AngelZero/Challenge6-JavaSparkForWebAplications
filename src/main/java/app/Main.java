package app;

import app.config.DataSeeder;
import app.config.Migrations;
import app.realtime.WsEndpoint;
import app.web.*;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;


public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws Exception {

        Gson gson = new Gson();

        port(4567);
        log.info("Starting server on port 4567");

        staticFiles.location("/public");
        Migrations.run();
        DataSeeder.run();

        webSocket("/ws", WsEndpoint.class);


        get("/health", (req, res) -> "OK");
        GlobalErrorHandler.register(gson);

        new UserController(gson).register();
        new ItemController(gson).register();

        new OfferController(gson).register();
        new ItemViewController().register();
        new OfferViewController().register();


        init();
        awaitInitialization();
        log.info("Server on http://localhost:4567");
    }
}
