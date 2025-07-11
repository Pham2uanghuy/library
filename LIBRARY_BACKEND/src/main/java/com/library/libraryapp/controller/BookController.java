package com.library.libraryapp.controller;

import com.library.libraryapp.dto.BookDTO;
import com.library.libraryapp.service.BookService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/books")
@AllArgsConstructor
public class BookController {
    private static  final Logger logger = LoggerFactory.getLogger(BookController.class);
    private BookService bookService;


    /*crud: create, read, update, delete
     *  c: post request
     * r : get request
     * u : put or patch request
     * d : delete request
     * */
    @PostMapping("addBook")
    // URL: http://localhost:8080/api/books/addBook
    public ResponseEntity<BookDTO> addBook(@RequestBody BookDTO bookDTO) {
        logger.info("Adding the book");
        BookDTO savedBook = bookService.addBook(bookDTO);
        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }

    @GetMapping("listAll")
    // URL: http://localhost:8080/api/books/listAll
    public ResponseEntity<List<BookDTO>> listAll() {
        List<BookDTO> allBooks = bookService.getAllBook();
        return new ResponseEntity<>(allBooks, HttpStatus.OK);
    }

    @GetMapping("{id}")
    //URL http://localhost:8080/api/books/1
    public ResponseEntity<BookDTO> getBookById(@PathVariable("id") Long bookId) {
        BookDTO bookDTO = bookService.getBookById(bookId);

        return new ResponseEntity<>(bookDTO, HttpStatus.OK);
    }

    @PatchMapping("updateBook/{id}")
    // URL: http://localhost:8080/api/books/updateBook/1
    public ResponseEntity<BookDTO> updateBook(@PathVariable("id") Long bookId, @RequestBody BookDTO bookDTO) {
        bookDTO.setId(bookId);
        BookDTO updatedBook  = bookService.updateBook(bookDTO);
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    @DeleteMapping("deleteBook/{id}")

    public ResponseEntity<String> deleteBook(@PathVariable("id") Long id) {
        bookService.deleteBook(id);
        return new ResponseEntity<>("Book successfully deleted", HttpStatus.OK);
    }

    @GetMapping("search-title")
    // http://localhost:8080/api/books/search-title?title =lordoftherings
    public ResponseEntity<List<BookDTO>> searchBooksByTitle(@RequestParam String title) {
        List<BookDTO> books = bookService.findByTitleContaining(title);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("search-title-author")
    //http://localhost:8080/api/books/search-title?title=lord&author=tolkien
    public  ResponseEntity<List<BookDTO>> searchBooksByTitleAndAuthor(@RequestParam String title, @RequestParam String author) {
        List<BookDTO> books = bookService.findByTitleAndAuthorContaining(title, author);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("search")
    // http://localhost:8080/api/books/search/title=l&author=t
    public ResponseEntity<List<BookDTO>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String barcodeNumber
    ) {
        List<BookDTO> books = bookService.findByCriteria(title, author, isbn, barcodeNumber );
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

}
