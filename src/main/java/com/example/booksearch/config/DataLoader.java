package com.example.booksearch.config;

import com.example.booksearch.domain.Book;
import com.example.booksearch.repository.BookRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;

@Component
@Profile("!test")
public class DataLoader {

    private final BookRepository bookRepository;

    public DataLoader(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @PostConstruct
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
            // 프로그래밍 & 소프트웨어 개발
            Book.builder().isbn("9788966262281").title("Clean Code").subtitle("애자일 소프트웨어 장인 정신").author("로버트 C. 마틴").publisher("인사이트").publicationDate(LocalDate.of(2013, 12, 24)).build(),
            Book.builder().isbn("9788966262298").title("Effective Java").subtitle("자바 플랫폼 모범사례").author("조슈아 블로크").publisher("인사이트").publicationDate(LocalDate.of(2018, 11, 1)).build(),
            Book.builder().isbn("9788966262305").title("Spring Boot in Action").author("크레이그 월즈").publisher("한빛미디어").publicationDate(LocalDate.of(2016, 3, 1)).build(),
            Book.builder().isbn("9788968481475").title("Java 8 in Action").subtitle("람다, 스트림, 함수형, 리액티브 프로그래밍").author("라울-가브리엘 우르마").publisher("한빛미디어").publicationDate(LocalDate.of(2015, 8, 12)).build(),
            Book.builder().isbn("9788960773417").title("Head First Design Patterns").subtitle("스토리가 있는 패턴 학습법").author("에릭 프리먼").publisher("한빛미디어").publicationDate(LocalDate.of(2005, 9, 1)).build(),
            Book.builder().isbn("9791162241264").title("Spring 5.0 마스터").author("라지브 라티").publisher("에이콘출판사").publicationDate(LocalDate.of(2018, 4, 30)).build(),
            Book.builder().isbn("9788968482731").title("모던 자바스크립트 Deep Dive").author("이웅모").publisher("위키북스").publicationDate(LocalDate.of(2020, 9, 25)).build(),
            Book.builder().isbn("9791158390747").title("리팩토링").subtitle("기존 코드를 안전하게 개선하는 방법").author("마틴 파울러").publisher("한빛미디어").publicationDate(LocalDate.of(2019, 4, 10)).build(),
            Book.builder().isbn("9788966262687").title("코드 컴플리트").subtitle("더 나은 소프트웨어 구현을 위한 실무 지침서").author("스티브 맥코넬").publisher("인사이트").publicationDate(LocalDate.of(2005, 3, 15)).build(),
            Book.builder().isbn("9788966261024").title("실용주의 프로그래머").subtitle("심층적이고 실용적인 프로그래밍 철학").author("데이비드 토머스").publisher("인사이트").publicationDate(LocalDate.of(2014, 1, 23)).build(),
            
            // Python
            Book.builder().isbn("9788968482977").title("파이썬 코딩의 기술").subtitle("Brett Slatkin's Effective Python").author("브렛 슬라킨").publisher("길벗").publicationDate(LocalDate.of(2016, 3, 10)).build(),
            Book.builder().isbn("9791162241899").title("전문가를 위한 파이썬").subtitle("효과적인 파이썬 코딩의 비법").author("루시아누 하말류").publisher("한빛미디어").publicationDate(LocalDate.of(2016, 8, 17)).build(),
            Book.builder().isbn("9788968482014").title("파이썬 웹 프로그래밍").subtitle("Django로 배우는 쉽고 빠른 웹 개발").author("김석훈").publisher("한빛미디어").publicationDate(LocalDate.of(2015, 7, 30)).build(),
            Book.builder().isbn("9788968481918").title("파이썬 머신러닝 완벽 가이드").author("권철민").publisher("위키북스").publicationDate(LocalDate.of(2020, 1, 6)).build(),
            Book.builder().isbn("9791162242025").title("데이터 사이언스를 위한 파이썬").author("웨스 맥키니").publisher("한빛미디어").publicationDate(LocalDate.of(2018, 9, 1)).build(),
            
            // JavaScript & Web
            Book.builder().isbn("9788968481796").title("You Don't Know JS").subtitle("타입과 문법, 스코프와 클로저").author("카일 심슨").publisher("한빛미디어").publicationDate(LocalDate.of(2017, 7, 31)).build(),
            Book.builder().isbn("9788968482458").title("리액트를 다루는 기술").author("김민준").publisher("길벗").publicationDate(LocalDate.of(2019, 8, 2)).build(),
            Book.builder().isbn("9791162241875").title("Node.js 교과서").subtitle("기본기에충실한 Node.js 10 입문서").author("조현영").publisher("길벗").publicationDate(LocalDate.of(2018, 7, 25)).build(),
            Book.builder().isbn("9788968482793").title("Vue.js 퀵 스타트").author("원형섭").publisher("루비페이퍼").publicationDate(LocalDate.of(2018, 3, 26)).build(),
            Book.builder().isbn("9791162241301").title("TypeScript 프로그래밍").subtitle("타입스크립트로 하는 안전한 웹 개발").author("보리스 체르니").publisher("인사이트").publicationDate(LocalDate.of(2019, 9, 2)).build(),
            
            // 데이터베이스
            Book.builder().isbn("9788968481642").title("Real MySQL").subtitle("개발자와 DBA를 위한 MySQL 실무 가이드").author("이성욱").publisher("위키북스").publicationDate(LocalDate.of(2012, 4, 16)).build(),
            Book.builder().isbn("9791162241912").title("MongoDB 완벽 가이드").author("크리스티나 초도로우").publisher("한빛미디어").publicationDate(LocalDate.of(2020, 12, 28)).build(),
            Book.builder().isbn("9788968481369").title("NoSQL 철저 입문").subtitle("빅데이터 세상으로 떠나는 간결한 안내서").author("다나카 마사히로").publisher("제이펍").publicationDate(LocalDate.of(2013, 7, 22)).build(),
            Book.builder().isbn("9788968482519").title("SQL 첫걸음").subtitle("하루 30분 36강으로 배우는 완전 초보 SQL 따라잡기").author("아사이 아츠시").publisher("한빛미디어").publicationDate(LocalDate.of(2015, 4, 27)).build(),
            Book.builder().isbn("9791162242445").title("PostgreSQL로 배우는 SQL 입문").author("시마다 요시카즈").publisher("한빛미디어").publicationDate(LocalDate.of(2020, 9, 21)).build(),
            
            // 컴퓨터 과학 & 알고리즘
            Book.builder().isbn("9788968481543").title("알고리즘").subtitle("문제 해결과 분석").author("로버트 세지윅").publisher("길벗").publicationDate(LocalDate.of(2018, 12, 26)).build(),
            Book.builder().isbn("9788968481079").title("Introduction to Algorithms").subtitle("알고리즘 입문").author("토머스 H. 코멘").publisher("한빛아카데미").publicationDate(LocalDate.of(2014, 7, 25)).build(),
            Book.builder().isbn("9788968482106").title("그림으로 개념을 이해하는 알고리즘").author("아디트야 바르가바").publisher("한빛미디어").publicationDate(LocalDate.of(2017, 8, 1)).build(),
            Book.builder().isbn("9791162241684").title("자료구조와 함께 배우는 알고리즘 입문").subtitle("C언어편").author("보요 시바타").publisher("한빛미디어").publicationDate(LocalDate.of(2018, 10, 22)).build(),
            Book.builder().isbn("9788968481253").title("컴퓨터 과학이 여는 세계").subtitle("세상을 바꾸는 컴퓨팅 사고력").author("이광근").publisher("생능출판사").publicationDate(LocalDate.of(2015, 2, 27)).build(),
            
            // 네트워크 & 보안
            Book.builder().isbn("9788968481451").title("HTTP 완벽 가이드").subtitle("웹은 어떻게 작동하는가").author("데이빗 고울리").publisher("인사이트").publicationDate(LocalDate.of(2014, 9, 30)).build(),
            Book.builder().isbn("9788968482366").title("TCP/IP 쉽게, 더 쉽게").author("기다 다이스케").publisher("네트워크타임즈").publicationDate(LocalDate.of(2015, 12, 28)).build(),
            Book.builder().isbn("9791162241417").title("해킹 공격의 예술").subtitle("침투 테스터를 위한 창의적 문제 해결법").author("존 에릭슨").publisher("에이콘출판사").publicationDate(LocalDate.of(2019, 3, 29)).build(),
            Book.builder().isbn("9788968482847").title("웹 해킹 & 보안 완벽 가이드").author("조성문").publisher("한빛미디어").publicationDate(LocalDate.of(2020, 6, 22)).build(),
            Book.builder().isbn("9791162241158").title("네트워크 해킹과 보안").subtitle("화이트햇을 위한 네트워크 보안").author("브래덴 R. 영").publisher("에이콘출판사").publicationDate(LocalDate.of(2018, 5, 31)).build(),
            
            // 시스템 & 운영
            Book.builder().isbn("9788968481888").title("리눅스 시스템 프로그래밍").author("로버트 러브").publisher("한빛미디어").publicationDate(LocalDate.of(2014, 5, 26)).build(),
            Book.builder().isbn("9788968482595").title("Docker 교과서").subtitle("컨테이너 기술과 가상화").author("엘튼 스톤맨").publisher("길벗").publicationDate(LocalDate.of(2020, 7, 31)).build(),
            Book.builder().isbn("9791162242018").title("쿠버네티스 인 액션").subtitle("그림과 상세한 설명을 통한 쿠버네티스 완벽 이해").author("마르코 룩샤").publisher("에이콘출판사").publicationDate(LocalDate.of(2019, 5, 31)).build(),
            Book.builder().isbn("9788968482748").title("DevOps와 SE를 위한 리눅스 커널 이야기").author("강진우").publisher("인사이트").publicationDate(LocalDate.of(2020, 2, 24)).build(),
            Book.builder().isbn("9791162241585").title("AWS 공인 솔루션스 아키텍트 스터디 가이드").author("벤 파이퍼").publisher("에이콘출판사").publicationDate(LocalDate.of(2019, 8, 30)).build(),
            
            // 인공지능 & 머신러닝
            Book.builder().isbn("9788968482922").title("핸즈온 머신러닝").subtitle("사이킷런과 텐서플로를 활용한 머신러닝, 딥러닝 실무").author("오렐리앙 제롱").publisher("한빛미디어").publicationDate(LocalDate.of(2018, 5, 14)).build(),
            Book.builder().isbn("9791162241772").title("파이썬 라이브러리를 활용한 머신러닝").subtitle("사이킷런 핵심 개발자가 쓴 머신러닝과 데이터 과학 실무서").author("안드레아스 뮐러").publisher("한빛미디어").publicationDate(LocalDate.of(2017, 7, 4)).build(),
            Book.builder().isbn("9788968482892").title("밑바닥부터 시작하는 딥러닝").subtitle("파이썬으로 익히는 딥러닝 이론과 구현").author("사이토 고키").publisher("한빛미디어").publicationDate(LocalDate.of(2017, 1, 24)).build(),
            Book.builder().isbn("9791162242094").title("딥러닝").subtitle("인공지능 혁명의 핵심동력").author("이안 굿펠로").publisher("제이펍").publicationDate(LocalDate.of(2018, 1, 19)).build(),
            Book.builder().isbn("9788968482328").title("패턴 인식과 머신 러닝").author("크리스토퍼 M. 비숍").publisher("비제이퍼블릭").publicationDate(LocalDate.of(2012, 12, 31)).build(),
            
            // 데이터 과학
            Book.builder().isbn("9788968482755").title("데이터 과학을 위한 통계").subtitle("파이썬을 이용한 통계학습 입문서").author("피터 브루스").publisher("한빛미디어").publicationDate(LocalDate.of(2018, 2, 1)).build(),
            Book.builder().isbn("9791162241943").title("R을 활용한 데이터 사이언스").subtitle("데이터 임포트, 정제, 변환, 시각화, 모델링").author("해들리 위컴").publisher("인사이트").publicationDate(LocalDate.of(2018, 2, 26)).build(),
            Book.builder().isbn("9788968482182").title("빅데이터 분석 도구 R 프로그래밍").author("서민구").publisher("자유아카데미").publicationDate(LocalDate.of(2014, 8, 25)).build(),
            Book.builder().isbn("9791162242131").title("Apache Spark를 활용한 빅데이터 분석").author("모하마드 구프타").publisher("에이콘출판사").publicationDate(LocalDate.of(2017, 11, 30)).build(),
            Book.builder().isbn("9788968482533").title("Hadoop 완벽 가이드").subtitle("대용량 데이터 저장, 처리, 분석을 위한 아파치 하둡").author("톰 화이트").publisher("한빛미디어").publicationDate(LocalDate.of(2015, 5, 29)).build(),
            
            // 모바일 개발
            Book.builder().isbn("9788968482090").title("안드로이드 프로그래밍").subtitle("The Big Nerd Ranch Guide").author("빌 필립스").publisher("비제이퍼블릭").publicationDate(LocalDate.of(2017, 6, 30)).build(),
            Book.builder().isbn("9791162241820").title("스위프트 프로그래밍").subtitle("Swift 5").author("야곰").publisher("한빛미디어").publicationDate(LocalDate.of(2019, 10, 10)).build(),
            Book.builder().isbn("9788968482694").title("React Native 인 액션").subtitle("자바스크립트로 크로스 플랫폼 앱 개발하기").author("네이더 다빗").publisher("한빛미디어").publicationDate(LocalDate.of(2018, 12, 10)).build(),
            Book.builder().isbn("9791162241363").title("Flutter 인 액션").subtitle("구글의 크로스 플랫폼 모바일 개발 프레임워크").author("에릭 윈드밀").publisher("한빛미디어").publicationDate(LocalDate.of(2020, 3, 23)).build(),
            Book.builder().isbn("9788968482779").title("Kotlin 인 액션").subtitle("Dmitry Jemerov, Svetlana Isakova 지음").author("드미트리 제메로프").publisher("에이콘출판사").publicationDate(LocalDate.of(2017, 2, 28)).build(),
            
            // 게임 개발
            Book.builder().isbn("9788968482601").title("Unity 게임 개발 교과서").subtitle("2D & 3D 스마트폰 게임 만들기").author("기타무라 마나미").publisher("길벗").publicationDate(LocalDate.of(2018, 1, 15)).build(),
            Book.builder().isbn("9791162241134").title("언리얼 엔진 4 게임 개발").subtitle("블루프린트 완전 정복").author("라이언 샤").publisher("에이콘출판사").publicationDate(LocalDate.of(2017, 12, 29)).build(),
            Book.builder().isbn("9788968482885").title("게임 엔진 아키텍처").subtitle("게임 개발자를 위한 게임 엔진의 이론과 실제").author("제이슨 그레고리").publisher("한빛미디어").publicationDate(LocalDate.of(2018, 7, 2)).build(),
            Book.builder().isbn("9791162242088").title("실시간 렌더링").subtitle("Real-Time Rendering 4th Edition").author("토마스 아케니네-몰러").publisher("에이콘출판사").publicationDate(LocalDate.of(2020, 8, 31)).build(),
            Book.builder().isbn("9788968481796").title("게임 프로그래밍 패턴").subtitle("더 빠르고 깔끔한 게임 코드 작성법").author("로버트 나이스트롬").publisher("한빛미디어").publicationDate(LocalDate.of(2016, 9, 26)).build(),
            
            // UI/UX & 디자인
            Book.builder().isbn("9788968482762").title("About Face").subtitle("인터랙션 디자인의 본질").author("앨런 쿠퍼").publisher("에이콘출판사").publicationDate(LocalDate.of(2015, 11, 30)).build(),
            Book.builder().isbn("9791162241578").title("디자인 오브 에브리데이 띵스").subtitle("사용자 중심 디자인과 인지 공학 이야기").author("도널드 A. 노먼").publisher("학지사").publicationDate(LocalDate.of(2013, 4, 25)).build(),
            Book.builder().isbn("9788968482427").title("사용자 경험 요소").subtitle("사용자 중심의 웹 디자인").author("제시 제임스 개릿").publisher("한빛미디어").publicationDate(LocalDate.of(2011, 12, 26)).build(),
            Book.builder().isbn("9791162241714").title("인터랙션 디자인").subtitle("Human-Computer Interaction").author("제니 프리스").publisher("에이콘출판사").publicationDate(LocalDate.of(2019, 1, 31)).build(),
            Book.builder().isbn("9788968482069").title("웹 디자인 2.0 고급 CSS").subtitle("CSS3와 jQuery를 활용한 실무 웹사이트 제작").author("앤디 클라크").publisher("에이콘출판사").publicationDate(LocalDate.of(2011, 6, 30)).build(),
            
            // 창업 & 비즈니스
            Book.builder().isbn("9788968481932").title("린 스타트업").subtitle("지속적 혁신을 실현하는 창업의 과학").author("에릭 리스").publisher("한빛미디어").publicationDate(LocalDate.of(2012, 2, 22)).build(),
            Book.builder().isbn("9791162241325").title("The Lean Startup").subtitle("Continuous Innovation").author("Eric Ries").publisher("포지션북스").publicationDate(LocalDate.of(2017, 11, 8)).build(),
            Book.builder().isbn("9788968482540").title("스타트업 하는 법").subtitle("폴 그레이엄의 스타트업 바이블").author("폴 그레이엄").publisher("한빛미디어").publicationDate(LocalDate.of(2016, 11, 7)).build(),
            Book.builder().isbn("9791162241431").title("블itzscaling").subtitle("번개처럼 성장하는 회사들의 비밀").author("리드 호프만").publisher("한빛비즈").publicationDate(LocalDate.of(2018, 11, 12)).build(),
            Book.builder().isbn("9788968481635").title("제로 투 원").subtitle("거대한 성공을 이끄는 창업의 비밀").author("피터 틸").publisher("한빛비즈").publicationDate(LocalDate.of(2014, 11, 17)).build(),
            
            // 프로젝트 관리 & 애자일
            Book.builder().isbn("9788968482823").title("스크럼").subtitle("켄 슈와버와 제프 서덜랜드의 스크럼 완벽 가이드").author("켄 슈와버").publisher("인사이트").publicationDate(LocalDate.of(2016, 6, 20)).build(),
            Book.builder().isbn("9791162241486").title("애자일 소프트웨어 개발").subtitle("원리와 패턴, 실천 방법").author("로버트 C. 마틴").publisher("인사이트").publicationDate(LocalDate.of(2005, 3, 21)).build(),
            Book.builder().isbn("9788968481475").title("칸반").subtitle("소프트웨어 개발의 혁신적 방법").author("데이비드 J. 앤더슨").publisher("한빛미디어").publicationDate(LocalDate.of(2011, 8, 29)).build(),
            Book.builder().isbn("9791162241950").title("프로젝트가 서쪽으로 간 까닭은").subtitle("소프트웨어 프로젝트 실패담과 교훈").author("톰 드마르코").publisher("인사이트").publicationDate(LocalDate.of(2014, 9, 1)).build(),
            Book.builder().isbn("9788968481109").title("맨먼스 미신").subtitle("소프트웨어 공학에 관한 에세이").author("프레더릭 브룩스").publisher("인사이트").publicationDate(LocalDate.of(2015, 8, 31)).build(),
            
            // 소프트웨어 아키텍처
            Book.builder().isbn("9791162242537").title("소프트웨어 아키텍처 101").subtitle("아키텍트가 알아야 할 101가지").author("마크 리처즈").publisher("한빛미디어").publicationDate(LocalDate.of(2021, 4, 26)).build(),
            Book.builder().isbn("9788968482861").title("마이크로서비스 패턴").subtitle("마이크로서비스 아키텍처의 모든 것").author("크리스 리처드슨").publisher("에이콘출판사").publicationDate(LocalDate.of(2019, 7, 31)).build(),
            Book.builder().isbn("9791162241677").title("도메인 주도 설계").subtitle("소프트웨어 복잡성을 다루는 지혜").author("에릭 에반스").publisher("위키북스").publicationDate(LocalDate.of(2011, 4, 12)).build(),
            Book.builder().isbn("9788968482700").title("Building Microservices").subtitle("마이크로서비스 설계와 구축").author("샘 뉴먼").publisher("한빛미디어").publicationDate(LocalDate.of(2016, 2, 29)).build(),
            Book.builder().isbn("9791162241592").title("클린 아키텍처").subtitle("소프트웨어 구조와 설계의 원칙").author("로버트 C. 마틴").publisher("인사이트").publicationDate(LocalDate.of(2019, 8, 20)).build(),
            
            // 개발자 성장 & 커리어
            Book.builder().isbn("9788968481802").title("개발자의 코드").subtitle("생산성을 높이는 동료 개발자와의 관계법").author("카 웰").publisher("한빛미디어").publicationDate(LocalDate.of(2013, 10, 28)).build(),
            Book.builder().isbn("9791162241448").title("소프트 스킬").subtitle("평범한 개발자의 비범한 인생 전략 71가지").author("존 손메즈").publisher("길벗").publicationDate(LocalDate.of(2017, 5, 8)).build(),
            Book.builder().isbn("9788968481291").title("프로그래머의 길, 멘토에게 묻다").subtitle("더 나은 개발자가 되기 위한 안내서").author("데이브 후버").publisher("인사이트").publicationDate(LocalDate.of(2010, 7, 26)).build(),
            Book.builder().isbn("9791162241509").title("실용주의 사고와 학습").subtitle("당신의 두뇌를 리팩토링하라").author("앤디 헌트").publisher("인사이트").publicationDate(LocalDate.of(2009, 12, 14)).build(),
            Book.builder().isbn("9788968482335").title("개발자를 넘어 기술 리더로").subtitle("성공하는 기술 리더가 갖춰야 할 90가지 요소").author("카밀 푸르니에").publisher("한빛미디어").publicationDate(LocalDate.of(2018, 10, 15)).build(),
            
            // 수학 & 논리학
            Book.builder().isbn("9788968481659").title("프로그래머를 위한 수학").subtitle("기초 수학부터 정수론, 암호학까지").author("세오 미나토").publisher("길벗").publicationDate(LocalDate.of(2013, 11, 25)).build(),
            Book.builder().isbn("9791162241721").title("이산수학").subtitle("프로그래밍을 위한 수학적 사고").author("케네스 H. 로젠").publisher("한빛아카데미").publicationDate(LocalDate.of(2017, 2, 28)).build(),
            Book.builder().isbn("9788968482137").title("프로그래머를 위한 선형대수").subtitle("선형대수와 통계학으로 배우는 머신러닝의 기초").author("히라오카 카즈야").publisher("길벗").publicationDate(LocalDate.of(2017, 9, 25)).build(),
            Book.builder().isbn("9791162242001").title("머신러닝을 위한 수학").subtitle("이공계열을 위한 확률과 통계").author("마크 피터 다이젠로스").publisher("에이콘출판사").publicationDate(LocalDate.of(2020, 5, 29)).build(),
            Book.builder().isbn("9788968481574").title("구체수학").subtitle("컴퓨터 과학의 기초를 이루는 구체적 수학").author("로널드 그레이엄").publisher("한빛아카데미").publicationDate(LocalDate.of(2018, 3, 2)).build(),
            
            // 기타 기술서
            Book.builder().isbn("9788968482656").title("Git 교과서").subtitle("버전 관리 시스템의 이해와 활용").author("이고잉").publisher("길벗").publicationDate(LocalDate.of(2020, 1, 6)).build(),
            Book.builder().isbn("9791162241554").title("프로 Git").subtitle("Git을 제대로 활용하기 위한 책").author("스캇 샤콘").publisher("인사이트").publicationDate(LocalDate.of(2016, 5, 2)).build(),
            Book.builder().isbn("9788968481826").title("정규표현식").subtitle("다양한 예제로 배우는 정규표현식").author("제프리 E.F. 프리들").publisher("한빛미디어").publicationDate(LocalDate.of(2007, 11, 26)).build(),
            Book.builder().isbn("9791162241387").title("vim 실용 가이드").subtitle("생각의 속도로 편집하기").author("드류 닐").publisher("인사이트").publicationDate(LocalDate.of(2015, 3, 16)).build(),
            Book.builder().isbn("9788968482274").title("해커와 화가").subtitle("빅 아이디어에 관한 에세이").author("폴 그레이엄").publisher("한빛미디어").publicationDate(LocalDate.of(2014, 12, 22)).build()
        );
    }
}