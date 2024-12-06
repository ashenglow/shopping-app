<div align="center">

<!-- logo -->
<img src="https://user-images.githubusercontent.com/80824750/208554611-f8277015-12e8-48d2-b2cc-d09d67f03c02.png" width="400"/>


[<img src="https://img.shields.io/badge/-readme.md-important?style=flat&logo=google-chrome&logoColor=white" />]() [<img src="https://img.shields.io/badge/-project notion-blue?style=flat&logo=google-chrome&logoColor=white" />]() 

</div> 

## 📝 소개
전통주 판매를 주제로 한 E-commerce 플랫폼을 구현한 개인 프로젝트입니다.
<br />
Footer, About, Login 페이지에 테스트 로그인용 버튼이 있습니다
<br />
<br />
👉 [배포 페이지 바로가기](https://soolstore.r-e.kr/)
<br />

## 🛠️ 프로젝트 아키텍쳐
<img src="https://i.ibb.co/vhSfJzM/shop-flow.png" alt="shop-flow">

<br />

### 화면 구성
|메인|회원가입&로그인|
|:---:|:---:|
|<img src="https://i.ibb.co/N9Nr27F/2024-10-21-3-40-51.png" >|<img src="https://i.ibb.co/cYDSQQQ/2024-12-06-4-31-17.png" >|
<br />
메인의 Footer, 메뉴의 회원가입 페이지, 어바웃 페이지에 테스트 로그인 버튼이 있습니다.
<br />
로그인 후 Redis에 저장된 Refresh Token이 만료되기 전까지 로그인 상태가 유지됩니다. 
<br />
<br />

|상품 목록|상품 상세|
|:---:|:---:|
|<img src="https://i.ibb.co/pn3bqWL/2024-12-06-4-24-11.png" >|<img src="https://i.ibb.co/r6YM4JV/2024-12-06-4-25-06.png" >|
<br />
상품 종류, 가격, 별점으로 필터링하여 상품 목록을 조회할 수 있습니다.
<br />
상세 페이지에서 장바구니에 추가, 리뷰 조회 및 작성이 가능합니다.
<br />
<br />

|장바구니|결제|
|:---:|:---:|
|<img src="https://i.ibb.co/sqvtNKM/2024-12-06-4-23-46.png" >|<img src="https://i.ibb.co/C6Ffvk6/2024-12-06-4-25-36.png" >|
<br />
장바구니에서 수량 변경 및 결제가 가능합니다.
<br />
카카오페이 테스트 API결제가 완료되면 주문 완료페이지로 이동합니다. 
<br />
<br />

## 🗂️ APIs
작성한 API는 아래에서 확인할 수 있습니다.

👉🏻 [API 바로보기](https://soolstore.r-e.kr/api-docs)


<br />

## ⚙ 기술 스택
> Java Springboot, Spring Security, Spring JPA
### Back-end
<div>
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Java.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/SpringBoot.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/SpringSecurity.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/SpringDataJPA.png?raw=true" width="80">
</div>

### Infra
<div>
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/AWSEC2.png?raw=true" width="80">
</div>

### Tools
<div>
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Github.png?raw=true" width="80">
</div>

<br />

## 🤔 기술적 이슈와 해결 과정
- DB 성능 개선
    - [Spring JPA 최적화](https://www.notion.so/ashen-glow/Sool-STORE-E-commerce-546d42ae6c224cdbab478c47a6e7e139?pvs=4#126b54d8388680668f9ad7f5aab074b3)
    - [쿼리 성능 모니터링 시스템](https://www.notion.so/ashen-glow/Sool-STORE-E-commerce-546d42ae6c224cdbab478c47a6e7e139?pvs=4#149b54d83886809f897ed5153756f34c)
    
<br />


## 💁‍♂️ 프로젝트 팀원

|Backend & Frontend|
|:---:|
|<img src="https://i.ibb.co/t4Dc1L8/Fc3c1cc9303cd4629e830d62fa051127.jpg" >|
|[김다혜](https://github.com/ashenglow)|
<br />

## 💁‍♂️ 프로젝트 링크
- [프로젝트 배포 페이지](https://soolstore.r-e.kr/)
- [프로젝트 소개 노션 페이지](https://ashen-glow.notion.site/Sool-STORE-E-commerce-546d42ae6c224cdbab478c47a6e7e139?pvs=4)
- [프론트엔드 Github](https://github.com/ashenglow/shopping-app-front)
