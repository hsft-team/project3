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

## 운영 배포

`admin-web` 은 `backend` 와 같은 PostgreSQL DB를 직접 읽는 Spring Boot 웹앱입니다.

권장 운영 구조:

- `api.hsft.io.kr` -> backend Docker nginx
- `admin.hsft.io.kr` -> admin-web (`8081`)

운영 프로필 파일:

- `/Users/hyeonseobkim/workspace/attendance-app/admin-web/src/main/resources/application-prod.yml`

Nginx 전체 설정 예시:

- `/Users/hyeonseobkim/workspace/attendance-app/admin-web/infra/nginx/attendance.conf`

### 운영 실행

```bash
export DB_URL=jdbc:postgresql://localhost:5432/attendance_db
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
export SERVER_PORT=8081
mvn clean package
java -jar target/admin-web-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Windows 백그라운드 실행 예시

PowerShell에서:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/attendance_db"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="postgres"
$env:SERVER_PORT="8081"
Start-Process -FilePath "java" -ArgumentList "-jar target/admin-web-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod" -WorkingDirectory "C:\attendance-app\admin-web" -WindowStyle Hidden
```

실행 확인:

```powershell
curl.exe -I http://localhost:8081
```

그 다음 Docker nginx 설정에 `admin.hsft.io.kr` 서버 블록을 반영하고 nginx를 재시작하면 됩니다.

## 기본 로그인 계정

- 아이디: `ADMIN001`
- 비밀번호: `admin1234`

## 참고

- DB 스키마는 `../backend`의 `employees`, `companies`, `company_settings`, `attendance_records`를 사용합니다.
- 관리자 웹은 회사명/좌표/허용 반경을 읽고, 위치 좌표와 허용 반경을 수정할 수 있습니다.
- 백엔드와 동시에 실행할 수 있도록 기본 포트는 `8081`입니다.
