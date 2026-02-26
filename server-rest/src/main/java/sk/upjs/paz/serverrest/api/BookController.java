package sk.upjs.paz.serverrest.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sk.upjs.paz.dao.BookDao;
import sk.upjs.paz.dao.DaoFactory;

import sk.upjs.paz.entity.Book;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private static final BookDao dao = DaoFactory.INSTANCE.getBookDao();

    @GetMapping
    public List<Book> all() { return dao.getAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Book> byId(@PathVariable("id") long id) {
        return dao.getById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Book add(@RequestBody Book b) {
        dao.add(b);
        return b;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> update(@PathVariable("id") long id, @RequestBody Book b) {
        if (dao.getById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        b.setId(id);
        dao.update(b);
        return ResponseEntity.ok(b);
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
