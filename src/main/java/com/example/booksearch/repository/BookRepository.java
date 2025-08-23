package com.example.booksearch.repository;

import com.example.booksearch.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT DISTINCT b FROM Book b WHERE " +
           "(b.title LIKE %:term1% OR b.author LIKE %:term1%) OR " +
           "(b.title LIKE %:term2% OR b.author LIKE %:term2%)")
    Page<Book> findByIncludeTermsOr(@Param("term1") String term1, @Param("term2") String term2, Pageable pageable);

    @Query("SELECT DISTINCT b FROM Book b WHERE " +
           "((b.title LIKE %:term1% OR b.author LIKE %:term1%) OR " +
           "(b.title LIKE %:term2% OR b.author LIKE %:term2%)) AND " +
           "(b.title NOT LIKE %:excludeTerm% AND b.author NOT LIKE %:excludeTerm%)")
    Page<Book> findByIncludeTermsOrAndExclude(@Param("term1") String term1, @Param("term2") String term2, 
                                              @Param("excludeTerm") String excludeTerm, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE " +
           "(b.title LIKE %:includeTerm% OR b.author LIKE %:includeTerm%) AND " +
           "(b.title NOT LIKE %:excludeTerm% AND b.author NOT LIKE %:excludeTerm%)")
    Page<Book> findByIncludeTermAndExclude(@Param("includeTerm") String includeTerm, 
                                           @Param("excludeTerm") String excludeTerm, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE " +
           "(b.title NOT LIKE %:excludeTerm% AND b.author NOT LIKE %:excludeTerm%)")
    Page<Book> findByExcludeTerm(@Param("excludeTerm") String excludeTerm, Pageable pageable);

    boolean existsByIsbn(String isbn);
}