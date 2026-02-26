package sk.upjs.paz.serverrest.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sk.upjs.paz.dao.DaoFactory;
import sk.upjs.paz.dao.GenreDao;
import sk.upjs.paz.entity.Genre;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private static final GenreDao dao = DaoFactory.INSTANCE.getGenreDao();

    @GetMapping
    public List<Genre> all() { return dao.getAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Genre> byId(@PathVariable("id") long id) {
        return dao.getById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Genre add(@RequestBody Genre g) {
        dao.add(g);
        return g;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Genre> update(@PathVariable("id") long id, @RequestBody Genre g) {
        if (dao.getById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Genre updated = new Genre(id, g.name());
        dao.update(updated);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        if (dao.getById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        dao.delete(id);
        return ResponseEntity.noContent().build();
    }
}
