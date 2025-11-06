package app.realtime;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WsEndpoint {
    private static final Set<Session> SESSIONS = ConcurrentHashMap.newKeySet();
    private static final Gson gson = new Gson();

    @OnWebSocketConnect
    public void onConnect(Session s) { SESSIONS.add(s); }

    @OnWebSocketClose
    public void onClose(Session s, int code, String reason) { SESSIONS.remove(s); }

    @OnWebSocketMessage
    public void onMessage(Session s, String msg) { }

    public static void broadcastPriceUpdate(String itemId, BigDecimal price) {
        var payload = new java.util.HashMap<String,Object>();
        payload.put("type", "updatePrice");
        payload.put("itemId", itemId);
        payload.put("price", price);
        broadcast(gson.toJson(payload));
    }

    public static void broadcastNewOffer(Long id, String itemId, String name, java.math.BigDecimal amount) {
        var p = new java.util.HashMap<String,Object>();
        p.put("type", "newOffer");
        p.put("id", id);
        p.put("itemId", itemId);
        p.put("name", name);
        p.put("amount", amount);
        broadcast(gson.toJson(p));
    }

    private static void broadcast(String json) {
        for (Session s : SESSIONS) {
            try { if (s.isOpen()) s.getRemote().sendString(json); } catch (IOException ignored) {}
        }
    }

    public static void broadcastItemUpdate(String id, String name, String description,
                                           BigDecimal price, String currency) {
        var p = new java.util.HashMap<String,Object>();
        p.put("type", "updateItem");
        p.put("id", id);
        p.put("name", name);
        p.put("description", description);
        p.put("price", price);
        p.put("currency", currency);
        broadcast(gson.toJson(p));
    }

    public static void broadcastItemCreated(String id, String name, String description,
                                            BigDecimal price, String currency) {
        var p = new java.util.HashMap<String,Object>();
        p.put("type", "itemCreated");
        p.put("id", id);
        p.put("name", name);
        p.put("description", description);
        p.put("price", price);
        p.put("currency", currency);
        broadcast(gson.toJson(p));
    }

    public static void broadcastItemDeleted(String id) {
        var p = new java.util.HashMap<String,Object>();
        p.put("type", "itemDeleted");
        p.put("id", id);
        broadcast(gson.toJson(p));
    }

    public static void broadcastNewOffer(Long id, String itemId, String name, String email, java.math.BigDecimal amount) {
        var p = new java.util.HashMap<String,Object>();
        p.put("type", "newOffer");
        p.put("id", id);
        p.put("itemId", itemId);
        p.put("name", name);
        p.put("email", email);
        p.put("amount", amount);
        broadcast(gson.toJson(p));
    }



}
