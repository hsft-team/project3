# GitHub Self-Hosted Runner Deployment

미니PC에 설치한 Windows self-hosted runner를 사용해 `admin-web` 을 GitHub 웹 UI에서 원격 배포하는 방법입니다.

## 전제 조건

- 미니PC에 `admin-web` 소스가 `C:\attendance-app\admin-web` 에 있어야 함
- Java 17, Maven이 미니PC에 설치되어 있어야 함
- backend PostgreSQL 이 실행 중이어야 함
- GitHub runner 라벨에 `attendance-prod` 가 있어야 함

## 사용 workflow

- `.github/workflows/deploy-self-hosted.yml`

## 지원 동작

- `deploy`: GitHub 최신 코드를 pull 하고 jar 빌드 후 재기동
- `restart`: pull 없이 현재 코드 기준으로 재빌드/재기동

## 실행 방법

1. GitHub 저장소 `Actions`
2. `Deploy To Mini PC`
3. `Run workflow`
4. `deploy_type` 선택
5. branch 입력 (`main`)

## 실행되는 스크립트

- `deploy`: `C:\attendance-app\admin-web\scripts\deploy-prod.ps1`
- `restart`: `C:\attendance-app\admin-web\scripts\restart-prod.ps1`

