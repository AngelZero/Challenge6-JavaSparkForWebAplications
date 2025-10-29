package app.service;

import app.model.User;
import app.model.dto.UserRequestDTO;
import app.model.dto.UserResponseDTO;
import app.model.dto.UserListItemDTO;
import app.repo.UserRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class UserService {

    private final UserRepository repo;
    public UserService(UserRepository repo) { this.repo = repo; }

    public List<UserListItemDTO> list() throws SQLException {
        return repo.findAll().stream()
                .map(u -> new UserListItemDTO(u.getName(), u.getEmail())) // no id here
                .collect(Collectors.toList());
    }

    public UserResponseDTO get(String id) throws SQLException {
        return repo.findById(id)
                .map(u -> new UserResponseDTO(u.getId(), u.getName(), u.getEmail()))
                .orElseThrow(() -> new NotFound("User not found"));
    }

    public UserResponseDTO create(String id, UserRequestDTO dto) throws SQLException {
        if (repo.exists(id)) throw new Conflict("User already exists");
        if (dto == null || isBlank(dto.getEmail())) throw new BadRequest("Email is required");
        if (isBlank(dto.getName())) throw new BadRequest("Name is required");
        if (repo.existsByEmail(dto.getEmail())) throw new Conflict("Email already in use");

        var u = new User(id, dto.getName(), dto.getEmail());
        repo.insert(u);
        return new UserResponseDTO(u.getId(), u.getName(), u.getEmail());
    }

    public UserResponseDTO update(String id, UserRequestDTO dto) throws SQLException {
        var cur = repo.findById(id).orElseThrow(() -> new NotFound("User not found"));
        if (dto == null || isBlank(dto.getEmail())) throw new BadRequest("Email is required");
        if (isBlank(dto.getName())) throw new BadRequest("Name is required");
        if (!dto.getEmail().equals(cur.getEmail()) && repo.existsByEmail(dto.getEmail()))
            throw new Conflict("Email already in use");

        var u = new User(id, dto.getName(), dto.getEmail());
        repo.update(u);
        return new UserResponseDTO(u.getId(), u.getName(), u.getEmail());
    }

    public void delete(String id) throws SQLException {
        if (repo.delete(id) == 0) throw new NotFound("User not found");
    }

    private static boolean isBlank(String s) { return s == null || s.isBlank(); }

    public static class NotFound extends RuntimeException { public NotFound(String m){super(m);} }
    public static class Conflict extends RuntimeException { public Conflict(String m){super(m);} }
    public static class BadRequest extends RuntimeException { public BadRequest(String m){super(m);} }
}
