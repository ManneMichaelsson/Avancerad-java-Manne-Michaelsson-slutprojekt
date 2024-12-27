package com.example.demo.controllers;

import com.example.demo.models.Book;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/books")

public class BookController {
    private List<Book> books = new ArrayList<>();

    public BookController() {
        books.add(new Book(1, "Book 1", "Author 1"));
        books.add(new Book(2, "Book 2", "Author 2"));
    }

    @GetMapping
    public List<Book> getBooks() {
        return books;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable int id) {
        return books.stream()
                .filter(book -> book.getId() == id)
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        if (book == null || book.getId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            books.add(book);
            return ResponseEntity.status(HttpStatus.CREATED).body(book);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable int id, @RequestBody Book updatedBook) {
        for (Book book : books) {
            if (book.getId() == id) {
                book.setTitle(updatedBook.getTitle());
                book.setAuthor(updatedBook.getAuthor());
                return  ResponseEntity.ok(book);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable int id) {
        boolean removed = false;
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId() == id) {
                books.remove(i);
                removed = true;
            }
        }
        if(removed){
            return ResponseEntity.ok("Book with ID" + id + " has been deleted.");
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book with ID" + id + " NOT FOUND.");
        }
    }
}
