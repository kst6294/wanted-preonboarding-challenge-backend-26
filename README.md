# Wanted Market API

전자상거래 플랫폼을 위한 RESTful API 서버입니다.

## 기술 스택

### Backend Framework & Libraries

- Java 17
- Spring Boot 3.4.0
- Spring Security + JWT (0.12.6)
- Spring Data JPA
- Spring Validation
- Spring Docker Compose
- Lombok

### Database

- MySQL
- Redis
- H2 (테스트용)

### Documentation

- SpringDoc OpenAPI (Swagger) 2.7.0

## 프로젝트 구조

```txt
com.wanted.market
├──common
│   ├──dto         // 공통 응답 객체
│   └──exception   // 전역 예외 처리
├──config
│   ├──security    // JWT 인증, 보안 설정
│   ├──JpaConfig
│   └──OpenApiConfig
└──domain
    ├──base        // 기본 엔티티 설정
    ├──product     // 상품 도메인
    ├──transaction // 거래 도메인
    └──user        // 사용자 도메인
```

## 주요 기능

### 사용자 (User)

- 회원가입
- 로그인 (JWT 토큰 발급)
- 사용자 정보 조회

### 상품 (Product)

- 상품 등록/수정/삭제
- 상품 목록 조회
- 상품 상세 조회

### 거래 (Transaction)

- 거래 생성
- 거래 상태 관리
- 거래 내역 조회

## 실행 방법

### 요구사항

- Java 17
- Docker

### 데이터베이스 실행

```bash
docker-compose up -d
```

### 애플리케이션 실행

```bash
./gradlew bootRun
```

## API Documentation

API 문서는 [여기](https://doxxx-playground.github.io/wanted-preonboarding-challenge-backend-26)에서 확인하실 수 있습니다.
- [OpenAPI Specification (JSON)](docs/api/openapi.json)

### 주요 API 엔드포인트

#### 사용자 API
- `POST /api/users/signup` - 회원가입
- `POST /api/users/login` - 로그인

#### 상품 API
- `GET /api/products` - 상품 목록 조회
- `POST /api/products` - 상품 등록

#### 거래 API
- `POST /api/transactions` - 거래 생성
- `PATCH /api/transactions/{id}/status` - 거래 상태 변경

## 테스트

```bash
./gradlew test
```

- Repository 레이어 테스트 구현 완료
- JUnit 5 기반 테스트
- H2 인메모리 데이터베이스 사용

## 개발 로그

자세한 개발 과정과 의사결정은 [DEVELOPMENT_LOG.md](docs/DEVELOPMENT_LOG.md)를 참고해주세요.

## 요구사항

프로젝트의 상세 요구사항은 [REQUIREMENTS.md](docs/REQUIREMENTS.md)에서 확인하실 수 있습니다.

# 원티드 프리온보딩 챌린지 백엔드 사전과제

## 개발 로그

프로젝트 진행 상황과 의사결정 과정은 [DEVELOPMENT_LOG.md](docs/DEVELOPMENT_LOG.md)에서 확인하실 수 있습니다.

## 요구사항

프로젝트의 요구사항은 [REQUIREMENTS.md](docs/REQUIREMENTS.md)에서 확인하실 수 있습니다.

## TODO 목록

프로젝트의 TODO 목록은 [TODO.md](docs/TODO.md)에서 확인하실 수 있습니다.
