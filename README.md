# 스트렝스 훈련 및 식단 일지

## 작성자와 서비스 소개

- 학번 / 이름: 202117321 / 안태형
- 서비스 목적과 대상 사용자: 매일 리프팅 중량과 식단을 기록하고 성장을 공유하는 피트니스 게시판입니다. 로그인한 사용자만이 자신의 훈련 기록을 작성 및 관리할 수 있으며, 타인의 기록은 조회만 가능하도록 제한하여 개인의 훈련 데이터를 안전하게 보호하는 것을 목적으로 합니다.
- 선택한 기술: Vue + Spring Boot
- 주요 기능: JWT 기반 사용자 인증 및 인가, 게시글 CRUD, 타인 게시글 조작 방지(작성자 권한 검증), 토큰 만료 및 자동 로그아웃 처리, 서버 단 입력값 유효성 검사.

## 보고서와 테스트 증거

- [구현 보고서 PDF](docs/report.pdf)
- [테스트 증거 이미지 폴더](docs/evidence/)

## 프로젝트 구조

```text
.
├── README.md
├── .gitignore
├── frontend-vue/          # Vue.js 프론트 소스와 의존성 파일
├── backend-spring/           # Spring Boot 백엔드 소스와 의존성 파일
└── docs/
    ├── report.pdf
    └── evidence/      # 테스트 시나리오 증거 캡처본 (T01~T18)
```


## 실행 환경

| 항목 | 사용 버전 |
|---|---|
| 운영체제 | Windows 11 |
| 프론트 런타임 Node.js | 24.21.0 |
| 백엔드 런타임 | Java 21.0.12.1 (JDK 21) |
| 프레임워크 | Vue 3.5.43 / Spring Boot 3.5.16 |
| 데이터 저장 | SQLite (필수) |

## 설치와 실행

### 1 저장소 복제

```sh
git clone https://github.com/202117321antaehyoung/Assignment-1-Implementing-CRUD-and-Login.git
cd Assignment-1-Implementing-CRUD-and-Login
```

### 2 백엔드 준비와 실행
```sh
cd backend-spring
.\mvnw.cmd spring-boot:run
```

- 환경설정 예시 파일: backend-spring/src/main/resources/application.properties
- JWT_SECRET 설정 방법: 64자리 16진수 난수를 생성하여 application.properties의 jwt.secret 속성에 주입하여 사용했습니다. (보안을 위해 제출 코드에는 비밀 키를 제거했습니다.)
- TOKEN_TTL: 기본값은 90초(90000ms)입니다. 만료 테스트(T10) 진행 시에는 AuthService.java의 issueToken 로직에서 now.getTime() + 3000으로 3초 만료를 강제 설정하여 테스트 후 원상 복구했습니다.
- 백엔드 주소: http://127.0.0.1:8000
- 정상 실행 확인 방법: 브라우저에서 http://127.0.0.1:8000/api/health 접속 시 HTTP 200 상태와 {"status":"ok"} 응답 확인.

### 3 프론트 빌드와 로컬 실행

백엔드를 켜 두고 새 터미널에서 실행합니다. 

```sh
cd frontend-vue
npm ci
npm run build
npm run preview
```

- 최종 확인 주소: http://127.0.0.1:4173
- 개발 중 실행: `npm run dev`, http://127.0.0.1:5173
- API 연결 방식과 포트 변경 방법: 프론트엔드의 vite.config.js 파일 내 server.proxy 설정을 통해 /api로 시작하는 모든 요청을 백엔드 서버인 http://127.0.0.1:8000으로 프록시 우회하여 연결하도록 구성했습니다.
- 종료: 프론트와 백엔드를 실행한 각 터미널에서 Ctrl+C
- 코드 변경 후 재실행: 프론트는 npm run build를 다시 수행한 후 npm run preview 실행, 백엔드는 터미널 프로세스 완전 종료(Ctrl+C) 후 .\mvnw.cmd spring-boot:run 재실행.

### 4 테스트 계정과 DB

| 사용자 | 이메일 | 실습용 비밀번호 |
|---|---|---|
| Alice | alice@class.test | Classroom!2026 |
| Bob | bob@class.test | Classroom!2026 |

- SQLite 파일 위치와 자동 생성 방식: 백엔드 서버 첫 기동 시 Spring Data JPA/Hibernate를 통해 backend-spring/data/board.sqlite 위치에 DB 파일과 테이블 스키마가 자동 생성됩니다.
- DB 초기화 방법: 백엔드 서버를 종료한 후 backend-spring/data 폴더 내의 board.sqlite 파일을 수동으로 삭제하면, 다음 서버 기동 시 빈 DB로 초기화됩니다.
- 서버 재시작 후 데이터 유지 확인 결과: 게시글 작성 후 백엔드를 종료하고 다시 실행한 뒤 프론트 웹(127.0.0.1:4173)을 새로고침 하였을 때, 파일 DB를 통해 데이터와 작성자 정보가 유지됨을 확인했습니다.

## 구현 완료 범위

| 기능 | 완료 여부 | 주요 파일 또는 함수 |
|---|---|---|
| 공개 목록과 상세 | 완료| api.js (list, detail), App.vue |
| 로그인과 JWT 발급 | 완료 | AuthService.java (issueToken), api.js (login) |
| JWT 검증과 내 정보 조회 | 완료 | AuthService.java (currentUserId), api.js (me) |
| 글 작성과 작성자 저장 | 완료 | api.js (create) |
| 본인 글 수정·삭제 | 완료 | AuthService.java (assertOwner), api.js (update, remove) |
| 토큰 만료와 로그아웃 | 완료 | AuthService.java (예외처리), api.js (clearSession) |
| 입력 오류와 권한 오류 처리 | 완료 | App.vue (run 함수 내 catch 로직) |

알려진 문제와 미완성 기능: 없음

## 테스트 방법과 결과 요약

- 실행한 검사 명령과 실행 위치: 자동 검사 도구 대신 npm run preview 로컬 배포 환경(4173 포트)에서 브라우저 개발자 도구(Network, Console 탭)를 활용해 전체 시나리오를 수동으로 직접 검사했습니다.
- Alice/Bob 권한 구분 확인 결과: Bob 계정의 토큰으로 Alice의 글에 수정/삭제(PUT/DELETE) 직접 요청 시 백엔드 단에서 403 FORBIDDEN으로 차단됨을 확인했습니다.
- HTTP 상태와 오류 code 확인: 200, 201, 204 정상 응답과 400 BAD_INPUT, 401 TOKEN_REQUIRED, 401 INVALID_TOKEN, 403 FORBIDDEN, 404 NOT_FOUND 예외 응답이 명세대로 반환됨을 확인했습니다. [보고서](docs/report.pdf)
- 응답 헤더와 본문 확인: 201 Created 성공 시 Location 헤더 포함 반환, 인증 오류 시 WWW-Authenticate: Bearer 헤더 반환, 204 No Content 삭제 성공 시 빈 본문 처리 확인 완료.
- 토큰 없음·변조·만료 확인 결과: 토큰 누락, 서명 변조, 강제 만료(3초) 테스트 시 401 예외가 발생하며, 프론트엔드 메모리의 토큰 정보가 폐기되고 즉각 자동 로그아웃됨을 확인했습니다.
- 전체 T01~T17 결과와 증거:[보고서 페이지 8~15 "HTTP 응답 확인 기록" 절 및 테스트 증거 참조](docs/report.pdf)

## 핵심 구현 설명
클라이언트가 이메일과 비밀번호로 로그인(api.js의 login)을 요청하면, 백엔드(AuthService.issueToken)는 비밀키를 통해 서명된 JWT를 발급합니다.
프론트엔드는 발급받은 토큰을 로컬 스토리지 등에 남기지 않고 메모리(session 객체)에만 임시 보관하며, 보호된 API를 호출할 때마다 공통 request 함수를 통해 Authorization: Bearer 헤더에 담아 전송합니다.
백엔드(AuthService.currentUserId)는 JJWT 라이브러리를 사용해 토큰의 무결성(서명, 발급자, 대상, 만료 여부)을 검증하여 사용자 ID(sub)를 추출합니다.
게시글 수정/삭제 시에는 추출된 사용자 ID와 DB 상의 원작성자 ID를 AuthService.assertOwner로 대조하여 타인의 수정을 차단합니다. (상세 내용은 보고서 참고)

## AI 활용 요약과 참고 자료

- AI 사용 여부: 사용
- 도구와 사용 목적: Gemini / 백엔드 만료 시간 설정 버그 디버깅, 프론트엔드 은닉 변수 우회 테스트(위조 스크립트 작성), 수많은 테스트 결과의 표 구조화 및 보고서 문서 작성 보조.
- 반영 위치와 직접 수정·검증한 내용: AuthService.java 내 토큰 수명 단위 변환(* 1000) 논리 오류를 파악 후, (+ 3000)으로 강제 3초 만료 테스트 진행 후 스스로 원상 복구함. api.js에 위조 요청 코드를 임시 주입해 백엔드 위조 데이터 거부 검증 완료 후 삭제함.
- 상세 활용 기록: 보고서 "AI 활용과 참고 자료" 절 참조
- 참고한 수업 자료·외부 코드·공식 문서: 웹서비스설계 과제 공지 문서(01_과제공지.pdf)의 공통 API 명세서 및 요구사항 가이드라인.

## 제출 확인

- [o] README의 안내 문구를 실제 내용으로 교체했다.
- [o] 새 폴더에 clone하여 README만으로 설치·환경설정·빌드·로컬 실행했다.
- [o] 4173번 빌드 화면에서 API 연결과 전체 필수 기능을 확인했다.
- [o] GitHub에서 보고서와 증거 링크가 열린다.
- [o] 교수자가 저장소를 열람·복제할 수 있다.
- [o] 비밀 키·실제 토큰·DB·node_modules·.venv·target을 커밋하지 않았다.
- [o] 최종 push 후 저장소 URL과 제출 기준 커밋 SHA를 LMS에 제출했다.
