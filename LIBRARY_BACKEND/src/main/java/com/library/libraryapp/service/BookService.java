package com.library.libraryapp.service;

import com.library.libraryapp.dto.BookDTO;
import org.springframework.stereotype.Service;

import java.util.List;


public interface BookService {
    BookDTO addBook(BookDTO bookDTO);

    List<BookDTO> getAllBook();

    BookDTO getBookById(Long id);

    BookDTO updateBook(BookDTO bookOTD);

    void deleteBook(Long id);


    List<BookDTO> findByTitleContaining(String title);
    List<BookDTO> findByTitleAndAuthorContaining(String title, String author);
    List<BookDTO> findByCriteria(String title, String author, String isbn, String barcodeNumber);
}
