package app.repo;

import app.config.Db;
import app.model.Offer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OfferRepository {

    public void insert(Offer o) throws SQLException {
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO offers(item_id,name,email,amount) VALUES(?,?,?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, o.getItemId());
            ps.setString(2, o.getName());
            ps.setString(3, o.getEmail());
            ps.setBigDecimal(4, o.getAmount());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) o.setId(keys.getLong(1));
            }
        }
    }

    public List<Offer> findAll() throws SQLException {
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id,item_id,name,email,amount FROM offers ORDER BY id DESC");
             ResultSet rs = ps.executeQuery()) {
            List<Offer> out = new ArrayList<>();
            while (rs.next()) {
                out.add(new Offer(
                        rs.getLong("id"),
                        rs.getString("item_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getBigDecimal("amount")));
            }
            return out;
        }
    }

    public List<Offer> findByItemId(String itemId) throws SQLException {
        String sql = "SELECT id, item_id, name, email, amount, created_at " +
                "FROM offers WHERE item_id = ? ORDER BY created_at DESC";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Offer> list = new ArrayList<>();
                while (rs.next()) {
                    Offer o = new Offer(
                            rs.getLong("id"),
                            rs.getString("item_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getBigDecimal("amount")
                    );
                    list.add(o);
                }
                return list;
            }
        }
    }

}
