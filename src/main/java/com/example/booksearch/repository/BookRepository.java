package com.example.booksearch.repository;

import com.example.booksearch.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByTitleContaining(String keyword);

    List<Book> findByAuthorContaining(String keyword);

    List<Book> findByTitleContainingOrAuthorContaining(String titleKeyword, String authorKeyword);

    Page<Book> findByTitleContainingOrAuthorContaining(String titleKeyword, String authorKeyword, Pageable pageable);

    boolean existsByIsbn(String isbn);
}