package app.config;

import com.google.gson.*;
import app.model.User;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class DataSeeder {
    private DataSeeder() {}


    public static void run() throws Exception {
        seedUsersIfEmpty();
        seedItemsFromJsonIfEmpty("/data/items.json");
    }

    private static void seedUsersIfEmpty() throws Exception {
        try (Connection c = Db.getConnection()) {
            if (countRows(c, "users") > 0) return;
            List<User> users = List.of(
                    new User("u1", "Ada Lovelace", "ada@example.com"),
                    new User("u2", "Linus Torvalds", "linus@example.com"),
                    new User("u3", "Grace Hopper", "grace@example.com")
            );
            try (PreparedStatement ps = c.prepareStatement("INSERT INTO users(id,name,email) VALUES(?,?,?)")) {
                for (User u : users) {
                    ps.setString(1, u.getId());
                    ps.setString(2, u.getName());
                    ps.setString(3, u.getEmail());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
    }

    private static void seedItemsFromJsonIfEmpty(String resourcePath) throws Exception {
        try (Connection c = Db.getConnection()) {
            if (countRows(c, "items") > 0) return;
            List<ItemSeed> items = loadItemsFromResource(resourcePath);
            if (items.isEmpty()) return;

            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO items(id,name,description,price,currency) VALUES(?,?,?,?,?)")) {
                for (ItemSeed s : items) {
                    BigDecimal price = parsePriceFlexible(s.price, s.priceNumber);
                    String currency = pickCurrency(s.currency, s.price);
                    if (currency == null || currency.isBlank()) currency = "USD";
                    ps.setString(1, s.id);
                    ps.setString(2, s.name);
                    ps.setString(3, s.description);
                    ps.setBigDecimal(4, price != null ? price : BigDecimal.ZERO);
                    ps.setString(5, currency.toUpperCase());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
    }

    private static int countRows(Connection c, String table) throws SQLException {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table)) {
            rs.next(); return rs.getInt(1);
        }
    }

    private static List<ItemSeed> loadItemsFromResource(String resourcePath) {
        try {
            InputStream in = DataSeeder.class.getResourceAsStream(resourcePath);
            if (in == null) return List.of();
            JsonArray arr = JsonParser.parseReader(new InputStreamReader(in)).getAsJsonArray();
            List<ItemSeed> list = new ArrayList<>();
            for (JsonElement e : arr) {
                JsonObject o = e.getAsJsonObject();
                ItemSeed s = new ItemSeed();
                s.id = getStr(o, "id");
                s.name = getStr(o, "name");
                s.description = getStr(o, "description");
                // NUEVO formato
                s.priceNumber = o.has("price") && o.get("price").isJsonPrimitive() && o.get("price").getAsJsonPrimitive().isNumber()
                        ? o.get("price").getAsBigDecimal() : null;
                s.currency = getStr(o, "currency");
                // COMPAT con formato viejo (price string con símbolo y sufijo)
                if (s.priceNumber == null) s.price = getStr(o, "price"); // "$621.34 USD"
                list.add(s);
            }
            return list;
        } catch (Exception ex) {
            return List.of();
        }
    }

    private static String getStr(JsonObject o, String prop) {
        return o.has(prop) && !o.get(prop).isJsonNull() ? o.get(prop).getAsString() : null;
    }

    private static BigDecimal parsePriceFlexible(String legacyPrice, BigDecimal numericPrice) {
        if (numericPrice != null) return numericPrice;
        if (legacyPrice == null) return null;
        String digits = legacyPrice.replaceAll("[^0-9.]", "");
        if (digits.isBlank()) return null;
        return new BigDecimal(digits);
    }

    private static String pickCurrency(String currencyField, String legacyPrice) {
        if (currencyField != null && !currencyField.isBlank()) return currencyField;
        if (legacyPrice != null) {
            var m = java.util.regex.Pattern.compile("([A-Za-z]{3})").matcher(legacyPrice);
            if (m.find()) return m.group(1);
        }
        return null;
    }

    private static final class ItemSeed {
        String id;
        String name;
        String description;
        String currency;
        BigDecimal priceNumber;
        String price;
    }
}
