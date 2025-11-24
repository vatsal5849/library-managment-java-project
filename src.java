/*
 * CORE JAVA SOURCE FILES
 */

// src/main/java/com.example.library/LibraryApplication.java
package com.example.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibraryApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }
}

// src/main/java/com.example.library/entity/Book.java
package com.example.library.entity;

import jakarta.persistence.*;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String description;
    private String category;
    
    private boolean issued = false; 

    public Book() {}

    public Book(String title, String author, String description, String category) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.category = category;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isIssued() { return issued; }
    public void setIssued(boolean issued) { this.issued = issued; }
}

// src/main/java/com.example.library/entity/Student.java
package com.example.library.entity;

import jakarta.persistence.*;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String branch;

    public Student() {}

    public Student(String name, String branch) {
        this.name = name;
        this.branch = branch;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }
}


// src/main/java/com.example.library/repository/BookRepository.java

package com.example.library.repository;

import com.example.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {}


// src/main/java/com.example.library/repository/StudentRepository.java
package com.example.library.repository;

import com.example.library.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {}


// src/main/java/com.example.library/service/BookService.java
package com.example.library.service;

import com.example.library.entity.Book;
import com.example.library.repository.BookRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository repo;

    public BookService(BookRepository repo) {
        this.repo = repo;
    }

    public List<Book> getAllBooks() {
        return repo.findAll();
    }
    
    public List<Book> getAllAvailableBooks() {
        return repo.findAll().stream()
                   .filter(b -> !b.isIssued())
                   .collect(Collectors.toList());
    }

    public List<Book> getAllIssuedBooks() {
        return repo.findAll().stream()
                   .filter(Book::isIssued)
                   .collect(Collectors.toList());
    }

    public Book addBook(Book book) {
        return repo.save(book);
    }
    
    public Optional<Book> getBookById(Long id) {
        return repo.findById(id);
    }

    public Book issueBook(Long id) {
        Optional<Book> bookOpt = repo.findById(id);
        if (bookOpt.isPresent()) {
            Book b = bookOpt.get();
            if (!b.isIssued()) {
                b.setIssued(true);
                return repo.save(b);
            }
        }
        return null; 
    }
    
    public Book returnBook(Long id) {
        Optional<Book> bookOpt = repo.findById(id);
        if (bookOpt.isPresent()) {
            Book b = bookOpt.get();
            if (b.isIssued()) {
                b.setIssued(false);
                return repo.save(b);
            }
        }
        return null; 
    }
    
    public void deleteBook(Long id) {
        repo.deleteById(id);
    }
}


// src/main/java/com.example.library/service/StudentService.java

package com.example.library.service;

import com.example.library.entity.Student;
import com.example.library.repository.StudentRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository repo;

    public StudentService(StudentRepository repo) {
        this.repo = repo;
    }

    public List<Student> getAllStudents() {
        return repo.findAll();
    }
    
    public Optional<Student> getStudentById(Long id) {
        return repo.findById(id);
    }

    public Student addStudent(Student s) {
        return repo.save(s);
    }
    
    public Student updateStudent(Long id, Student updatedStudent) {
        Optional<Student> studentOpt = repo.findById(id);
        
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            student.setName(updatedStudent.getName());
            student.setBranch(updatedStudent.getBranch());
            return repo.save(student);
        } else {
            return repo.save(updatedStudent);
        }
    }
    
    public void deleteStudent(Long id) {
        repo.deleteById(id);
    }
}


// src/main/java/com.example.library/controller/BookController.java

package com.example.library.controller;

import com.example.library.entity.Book;
import com.example.library.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/books")
@CrossOrigin
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public List<Book> allBooks() {
        return service.getAllBooks();
    }
    
    @GetMapping("/available")
    public List<Book> availableBooks() {
        return service.getAllAvailableBooks();
    }
    
    @GetMapping("/issued")
    public List<Book> issuedBooks() {
        return service.getAllIssuedBooks();
    }

    @PostMapping
    public Book create(@RequestBody Book book) {
        return service.addBook(book);
    }

    @PostMapping("/issue/{id}")
    public ResponseEntity<Book> issueBook(@PathVariable Long id) {
        Book result = service.issueBook(id);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }
    
    @PostMapping("/return/{id}")
    public ResponseEntity<Book> returnBook(@PathVariable Long id) {
        Book result = service.returnBook(id);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        service.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}

// src/main/java/com.example.library/controller/StudentController.java
package com.example.library.controller;

import com.example.library.entity.Student;
import com.example.library.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/students")
@CrossOrigin
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping
    public List<Student> allStudents() {
        return service.getAllStudents();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable Long id) {
        return service.getStudentById(id)
                      .map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Student add(@RequestBody Student student) {
        return service.addStudent(student);
    }
    
    @PutMapping("/{id}")
    public Student update(@PathVariable Long id, @RequestBody Student student) {
        return service.updateStudent(id, student);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}


/*
 * RESOURCE FILES
 */


// src/main/resources/application.properties
spring.datasource.url=jdbc:h2:mem:librarydb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create
spring.h2.console.enabled=true
spring.jpa.show-sql=false
spring.web.cors.allowed-origins=*
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*

// src/main/resources/data.sql
INSERT INTO BOOK (title, author, description, category, issued) VALUES ('Java Basics', 'Kumar', 'A foundational book on core Java programming principles.', 'Programming', false);
INSERT INTO BOOK (title, author, description, category, issued) VALUES ('Database Systems', 'Anita', 'Comprehensive guide to SQL and relational databases for beginners.', 'Database', false);
INSERT INTO BOOK (title, author, description, category, issued) VALUES ('Algorithms and Data Structures', 'Sharma', 'Essential guide for technical interviews, focusing on efficiency.', 'Computer Science', false);

INSERT INTO STUDENT (name, branch) VALUES ('Rahul', 'CSE');
INSERT INTO STUDENT (name, branch) VALUES ('Meera', 'ECE');
INSERT INTO STUDENT (name, branch) VALUES ('Priya', 'IT');