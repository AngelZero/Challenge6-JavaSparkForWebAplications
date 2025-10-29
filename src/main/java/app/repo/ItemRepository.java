package app.repo;

import app.config.Db;
import app.model.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemRepository {

    public List<Item> findAll() throws SQLException {
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id,name,description,price,currency FROM items ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            List<Item> out = new ArrayList<>();
            while (rs.next()) {
                out.add(new Item(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getBigDecimal("price"),
                        rs.getString("currency")));
            }
            return out;
        }
    }

    public Optional<Item> findById(String id) throws SQLException {
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id,name,description,price,currency FROM items WHERE id=?")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Item(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getBigDecimal("price"),
                            rs.getString("currency")));
                }
                return Optional.empty();
            }
        }
    }

    public boolean exists(String id) throws SQLException {
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM items WHERE id=?")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public void insert(Item it) throws SQLException {
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO items(id,name,description,price,currency) VALUES(?,?,?,?,?)")) {
            ps.setString(1, it.getId());
            ps.setString(2, it.getName());
            ps.setString(3, it.getDescription());
            ps.setBigDecimal(4, it.getPrice());
            ps.setString(5, it.getCurrency());
            ps.executeUpdate();
        }
    }

    public int update(Item it) throws SQLException {
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE items SET name=?, description=?, price=?, currency=? WHERE id=?")) {
            ps.setString(1, it.getName());
            ps.setString(2, it.getDescription());
            ps.setBigDecimal(3, it.getPrice());
            ps.setString(4, it.getCurrency());
            ps.setString(5, it.getId());
            return ps.executeUpdate();
        }
    }

    public int delete(String id) throws SQLException {
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM items WHERE id=?")) {
            ps.setString(1, id);
            return ps.executeUpdate();
        }
    }
}
