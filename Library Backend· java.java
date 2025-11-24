package com.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibraryApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }
}

// End of LibraryApplication.java 

package com.library.model;

import java.util.*;

public class Book {
    private Long id;
    private String title;
    private String author;
    private boolean available = true;

    public Book() {}

    public Book(Long id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.available = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}

// End of Book.java

package com.library.repository;

import com.library.model.Book;
import java.util.*;
import org.springframework.stereotype.Repository;

@Repository
public class BookRepository {

    private Map<Long, Book> data = new HashMap<>();
    private long index = 1;

    public Book save(Book book) {
        if (book.getId() == null) {
            book.setId(index++);
        }
        data.put(book.getId(), book);
        return book;
    }

    public List<Book> findAll() {
        return new ArrayList<>(data.values());
    }

    public Book findById(Long id) {
        return data.get(id);
    }

    public void delete(Long id) {
        data.remove(id);
    }
}

//  End of BookRepository.java

package com.library.service;

import com.library.model.Book;
import com.library.repository.BookRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class BookService {

    private final BookRepository repo;

    public BookService(BookRepository repo) {
        this.repo = repo;
    }

    public Book addBook(Book book) {
        return repo.save(book);
    }

    public List<Book> getAllBooks() {
        return repo.findAll();
    }

    public Book getBook(Long id) {
        return repo.findById(id);
    }

    public void removeBook(Long id) {
        repo.delete(id);
    }

    public Book updateBook(Long id, Book b) {
        Book old = repo.findById(id);
        if (old != null) {
            old.setTitle(b.getTitle());
            old.setAuthor(b.getAuthor());
            old.setAvailable(b.isAvailable()); 
            repo.save(old);
            return old;
        }
        return null;
    }
}

// End of BookService.java

package com.library.controller;

import com.library.model.Book;
import com.library.service.BookService;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @PostMapping("/add")
    public Book addBook(@RequestBody Book book) {
        return service.addBook(book);
    }

    @GetMapping("/all")
    public List<Book> getBooks() {
        return service.getAllBooks();
    }

    @GetMapping("/{id}")
    public Book getBook(@PathVariable Long id) {
        return service.getBook(id);
    }

    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable Long id) {
        service.removeBook(id);
        return "Book removed";
    }

    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book book) {
        return service.updateBook(id, book);
    }
}
// End of BookController.java