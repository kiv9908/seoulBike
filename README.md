# 따릉이 대여 서비스 구현
  김강온, 이봉욱

---

개요

    주제

    기능

    협업과정

    협의내용

    회원 Entity를 포함한 최소 2개 이상의 Entity 설계

    보완사항

### 주제

- 주제
    - 따릉이 대여 서비스 구현

### 기능

- Create
    - 회원가입
    - 이용내역(마이페이지) 추가
- Read
    - 이용 가능한 자전거 조회
    - 이용내역(마이페이지) 조회
- Update
    - 대여/반납에 따른 자전거 상태 변경(1: 대여 가능 /0: 대여 중)
    - 대여/반납 시간 등록
- Delete
    - 회원탈퇴

### 협업과정

- 노션을 통한 회의 내용 기록
- 각자 메서드 구현하여 [Github](https://github.com/kiv9908/seoulBike) 이용한 협업

### 협의내용

- ERD
- 객체지향적 설계 방법
- 예외처리

### 회원 Entity를 포함한 최소 2개 이상의 Entity 설계

- 최소 두 개의 Entity는 Relation을 가지고 있음

- ERD
![Untitled](https://github.com/kiv9908/seoulBike/assets/105219462/d5506dd4-ac26-4ac9-84c9-0c0b6b464443)

    

### 보완사항

*(객체지향, 자료구조, 디자인패턴, 예외처리 등)*

- 예외처리
    - 자전거는 한사람당 1대씩만 빌릴 수 있음
    - 아이디를 중복으로 회원가입 불가
    - 없는 대여소 코드 입력시 대여 불가
