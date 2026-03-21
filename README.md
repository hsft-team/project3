# Attendance Admin Web

Spring Boot + Thymeleaf 기반 관리자 페이지이며, `../backend`와 같은 PostgreSQL DB를 직접 조회합니다.

## 기능

- 로그인
- 오늘 출근 현황 대시보드
- 월별 출근 현황
- 직원 목록
- 직원 엑셀 업로드 및 샘플 다운로드
- 회사 위치 설정
- 직원별 출근/퇴근 기준 시간 설정

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

### Windows 미니PC 자동 배포 스크립트

GitHub에서 최신 코드를 받아 빌드하고 기존 프로세스를 종료한 뒤 새 jar를 실행하는 스크립트가 포함되어 있습니다.

- 배포 스크립트: [scripts/deploy-prod.ps1](/Users/hyeonseobkim/workspace/attendance-app/admin-web/scripts/deploy-prod.ps1)
- 더블클릭용: [scripts/deploy-prod.bat](/Users/hyeonseobkim/workspace/attendance-app/admin-web/scripts/deploy-prod.bat)
- 재시작 전용: [scripts/restart-prod.ps1](/Users/hyeonseobkim/workspace/attendance-app/admin-web/scripts/restart-prod.ps1)
- 더블클릭용: [scripts/restart-prod.bat](/Users/hyeonseobkim/workspace/attendance-app/admin-web/scripts/restart-prod.bat)

실행 예시:

```powershell
cd C:\attendance-app\admin-web
powershell -ExecutionPolicy Bypass -File .\scripts\deploy-prod.ps1
```

주의:

- `scripts/*.ps1` 안의 DB 접속 정보는 실제 운영값으로 맞춰야 합니다.
- 현재 기본 예시는 `attendance_user / change-this-db-password` 기준입니다.

## 기본 로그인 계정

- 아이디: `ADMIN001`
- 비밀번호: `admin1234`

## 참고

- DB 스키마는 `../backend`의 `employees`, `companies`, `company_settings`, `attendance_records`를 사용합니다.
- 관리자 웹은 회사명/좌표/허용 반경을 읽고, 위치 좌표와 허용 반경을 수정할 수 있습니다.
- 백엔드와 동시에 실행할 수 있도록 기본 포트는 `8081`입니다.
