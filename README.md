# Attendance Admin Web

Spring Boot + Thymeleaf 기반 관리자 페이지이며, `../backend`와 같은 PostgreSQL DB를 직접 조회합니다.

## 기능

- 로그인
- 오늘 출근 현황 대시보드
- 직원 목록
- 회사 위치 설정

## 실행 환경

- Java 17+
- Maven 3.9+

## 실행 방법

```bash
export DB_URL=jdbc:postgresql://localhost:5432/attendance_db
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
mvn spring-boot:run
```

브라우저에서 `http://localhost:8081` 접속 후 로그인하세요.

## 기본 로그인 계정

- 아이디: `ADMIN001`
- 비밀번호: `admin1234`

## 참고

- DB 스키마는 `../backend`의 `employees`, `companies`, `company_settings`, `attendance_records`를 사용합니다.
- 관리자 웹은 회사명/좌표/허용 반경을 읽고, 위치 좌표와 허용 반경을 수정할 수 있습니다.
- 백엔드와 동시에 실행할 수 있도록 기본 포트는 `8081`입니다.
