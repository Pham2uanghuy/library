package com.library.libraryapp.mapper;

import com.library.libraryapp.dto.BookDTO;
import com.library.libraryapp.entity.Book;

public class BookMapper {

    // method to map entity to dto
    public static BookDTO mapToBookDTO(Book book) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(book.getId());
        bookDTO.setTitle(book.getTitle());
        bookDTO.setAuthor(book.getAuthor());
        bookDTO.setPublisher(book.getPublisher());
        bookDTO.setIsbn(book.getIsbn());
        bookDTO.setYearOfPublication(book.getYearOfPublication());
        bookDTO.setPlaceOfPublication(book.getPlaceOfPublication());
        bookDTO.setNoOfAvailableCopies(book.getNoOfAvailableCopies());
        bookDTO.setBarcodeNumber(book.getBarcodeNumber());
        return bookDTO;
    }
    // method to map dto to entity
    public  static Book mapToBookEntity(BookDTO bookDTO) {
        Book book = new Book();

        book.setId(bookDTO.getId());
        book.setTitle(bookDTO.getTitle());
        book.setIsbn(bookDTO.getIsbn());
        book.setAuthor(bookDTO.getAuthor());
        book.setPublisher(bookDTO.getPublisher());
        book.setIsbn(bookDTO.getIsbn());
        book.setYearOfPublication(bookDTO.getYearOfPublication());
        book.setPlaceOfPublication(bookDTO.getPlaceOfPublication());
        book.setNoOfAvailableCopies(bookDTO.getNoOfAvailableCopies());
        book.setBarcodeNumber(bookDTO.getBarcodeNumber());
        return book;
    }
}
