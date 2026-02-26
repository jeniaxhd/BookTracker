package sk.upjs.paz.serverrest.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.upjs.paz.dao.AuthorDao;
import sk.upjs.paz.dao.DaoFactory;
import sk.upjs.paz.entity.Author;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private static final AuthorDao dao = DaoFactory.INSTANCE.getAuthorDao();

    @GetMapping
    public List<Author> all() { return dao.getAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Author> byId(@PathVariable("id") long id) {
        return dao.getById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Author add(@RequestBody Author a) {
        dao.add(a);
        return a;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Author> update(@PathVariable("id") long id, @RequestBody Author a) {
        if (dao.getById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        a.setId(id);
        dao.update(a);
        return ResponseEntity.ok(a);
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
