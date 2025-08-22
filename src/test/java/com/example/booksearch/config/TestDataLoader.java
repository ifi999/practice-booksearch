package com.example.booksearch.config;

import com.example.booksearch.domain.Book;
import com.example.booksearch.repository.BookRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@Profile("test")
public class TestDataLoader {

    private final BookRepository bookRepository;

    public TestDataLoader(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public void loadSampleData() {
        if (bookRepository.count() > 0) {
            return;
        }

        List<Book> sampleBooks = createSampleBooks();
        
        for (Book book : sampleBooks) {
            if (!bookRepository.existsByIsbn(book.getIsbn())) {
                bookRepository.save(book);
            }
        }
    }

    private List<Book> createSampleBooks() {
        return List.of(
            Book.builder().isbn("9788966262281").title("Clean Code").subtitle("애자일 소프트웨어 장인 정신").author("로버트 C. 마틴").publisher("인사이트").publicationDate(LocalDate.of(2013, 12, 24)).build(),
            Book.builder().isbn("9788966262298").title("Effective Java").subtitle("자바 플랫폼 모범사례").author("조슈아 블로크").publisher("인사이트").publicationDate(LocalDate.of(2018, 11, 1)).build(),
            Book.builder().isbn("9788966262305").title("Spring Boot in Action").author("크레이그 월즈").publisher("한빛미디어").publicationDate(LocalDate.of(2016, 3, 1)).build(),
            Book.builder().isbn("9788968481475").title("Java 8 in Action").subtitle("람다, 스트림, 함수형, 리액티브 프로그래밍").author("라울-가브리엘 우르마").publisher("한빛미디어").publicationDate(LocalDate.of(2015, 8, 12)).build(),
            Book.builder().isbn("9788968482977").title("파이썬 코딩의 기술").subtitle("Brett Slatkin's Effective Python").author("브렛 슬라킨").publisher("길벗").publicationDate(LocalDate.of(2016, 3, 10)).build()
        );
    }
}