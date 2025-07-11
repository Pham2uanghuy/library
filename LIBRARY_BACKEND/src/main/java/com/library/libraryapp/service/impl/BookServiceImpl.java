package com.library.libraryapp.service.impl;

import com.library.libraryapp.dto.BookDTO;
import com.library.libraryapp.entity.Book;
import com.library.libraryapp.exception.ResourceNotFoundException;
import com.library.libraryapp.mapper.BookMapper;
import com.library.libraryapp.repository.BookRepository;
import com.library.libraryapp.service.BookService;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {
    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);
    private BookRepository bookRepository;
    private EntityManager entityManager;

    public BookDTO addBook(BookDTO bookDTO) {
        // save (create) book in DB
        logger.info("Trying to add a new book: {}", bookDTO);
        Book book = BookMapper.mapToBookEntity(bookDTO);
        logger.info("Book entity after the mapping: {}", book);
        book = bookRepository.save(book);
        logger.info("The book succesffuly saved in db: {}", book);
        return BookMapper.mapToBookDTO(book);
    }

    @Override
    public List<BookDTO> getAllBook() {
        List<Book> books = bookRepository.findAll();

        //iterate over the list of entities
        // then map every enity to dto
        // return list of dto
        return books.stream().map(BookMapper::mapToBookDTO).collect(Collectors.toList());
    }

    @Override
    public BookDTO getBookById(Long bookId) {
//        Optional<Book> optionalBook = bookRepository.findById(bookId);
//        Book book = optionalBook.get();
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new ResourceNotFoundException("Book", "ID", bookId));
        return BookMapper.mapToBookDTO(book);
    }

    @Override
    public BookDTO updateBook(BookDTO bookDTO) {
        // find existing book by id
        Optional<Book> bookOptional = bookRepository.findById(bookDTO.getId());

        // do partial update of the book ( update only non-null fields)
        Book bookToUpdate = bookOptional.orElseThrow(
                () -> new ResourceNotFoundException("Book", "ID", bookDTO.getId())
        );
        updateBookEntityFromDTO(bookToUpdate, bookDTO);

        // save updated book to db
        Book savedBook = bookRepository.save(bookToUpdate);
        // return bookDTO using mapper
        return BookMapper.mapToBookDTO(savedBook);
    }

    private void updateBookEntityFromDTO(Book bookToUpdate, BookDTO bookDTO) {
        if (bookDTO.getTitle() != null) {
            bookToUpdate.setTitle(bookDTO.getTitle());
        }
        if (bookDTO.getAuthor() != null) {
            bookToUpdate.setAuthor(bookDTO.getAuthor());
        }
        if (bookDTO.getIsbn() != null) {
            bookToUpdate.setIsbn(bookDTO.getIsbn());
        }
        if (bookDTO.getPublisher() != null) {
            bookToUpdate.setPublisher(bookDTO.getPublisher());
        }
        if (bookDTO.getYearOfPublication() != null) {
            bookToUpdate.setYearOfPublication(bookDTO.getYearOfPublication());
        }
        if (bookDTO.getPlaceOfPublication() != null) {
            bookToUpdate.setPlaceOfPublication(bookDTO.getPlaceOfPublication());
        }
        if (bookDTO.getNoOfAvailableCopies() != null) {
            bookToUpdate.setNoOfAvailableCopies(bookDTO.getNoOfAvailableCopies());
        }
        if (bookDTO.getBarcodeNumber() != null) {
            bookToUpdate.setBarcodeNumber(bookDTO.getBarcodeNumber());
        }
    }

    @Override
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book", "ID", id);
        }
        bookRepository.deleteById(id);
    }


    public List<BookDTO> findByTitleContaining(String title) {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(title);
        return books.stream().map(BookMapper::mapToBookDTO).collect(Collectors.toList());
    }
    public  List<BookDTO> findByTitleAndAuthorContaining(String title, String author) {
        List<Book> books = bookRepository.findByTitleAndAuthorContainingIgnoreCase(title, author);
        return books.stream().map(BookMapper::mapToBookDTO).collect(Collectors.toList());
    }

    public  List<BookDTO> findByCriteria(String title, String author, String isbn, String barcodeNumber) {
        CriteriaBuilder cb  = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);
        List<Predicate> predicates = new ArrayList<>();
        if (title != null && !title.isEmpty()) {
            predicates.add(cb.like(cb.lower(book.get("title")), "%" + title.toLowerCase() + "%"));
        }
        if (author != null && !author.isEmpty()) {
            predicates.add(cb.like(cb.lower(book.get("author")), "%" + author.toLowerCase() + "%"));
        }
        if (isbn != null && !isbn.isEmpty()) {
            predicates.add(cb.like(cb.lower(book.get("isbn")), "%" + isbn.toLowerCase() + "%"));
        }
        if (barcodeNumber != null && !barcodeNumber.isEmpty()) {
            predicates.add(cb.like(cb.lower(book.get("barcodeNumber")), "%" + barcodeNumber.toLowerCase() + "%"));
        }
        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        List<Book> result = entityManager.createQuery(cq).getResultList();
        return result.stream().map(BookMapper::mapToBookDTO).collect(Collectors.toList());
    }
}
