# <div align="center">![Image](https://github.com/user-attachments/assets/e3a01b87-5b8f-452e-a0d2-1b5ce68784f0)<br/>Cube SNS</div>

<p align="center">
 <img src="https://github.com/user-attachments/assets/4da317ed-ef6f-4880-aa44-8877f26520b8">
</p>

- **Cube SNS**는 유저들이 사진을 통해 일상 공유를 하고 서로 direct message를 통해 연락을 할 수 있도록 Firebase를 기반으로 만든 커뮤니티 앱 입니다.

- 개발 기간 : 7개월(2024.07 ~ 2024.02)
  
- 개발 인원 : 1명
ㅤ


ㅤ



## 목차
> 1. [앱 기능 소개](#앱-기능-소개)
> 2. [기술 스택](#기술-스택)
> 3. [Architecture](#architecture)
> 4. [시연 영상](#시연-영상)
> 5. [블로그 링크](#블로그-링크)


ㅤ


ㅤ


## 앱 기능 소개

**회원가입**
 
![Image](https://github.com/user-attachments/assets/98eeb31d-8de6-48b5-8924-58821800308a)
 
-  이메일을 통해서 계정을 생성할 수 있습니다.<br/>프로필 사진과 이름도 설정할 수 있습니다.


ㅤ


ㅤ


**로그인**

![Image](https://github.com/user-attachments/assets/044e26ea-30a2-4697-92b6-d7d470a7fbfd)

-  회원가입을 통해 생성한 이메일이나 카카오 계정을 통해 로그인이 가능합니다.


ㅤ


ㅤ


**게시글**

![Image](https://github.com/user-attachments/assets/5f99d670-c599-4ee3-88c2-79a735b7d9d2)

- 사진(다중 이미지, 동영상)과 글을 통한 자신의 일상 게시글을 올릴 수 있습니다. <br />필요하시면 위치 정보도 함께 공유 가능합니다.


ㅤ


ㅤ


**게시글 수정**

![Image](https://github.com/user-attachments/assets/e93cc2da-35a5-4828-820b-b2834f1402d9)

- 게시글을 수정할 수 있습니다.<br />수정하시면 오른쪽 상단에 수정된 게시글(수정됨) 이라고 나타납니다.


ㅤ


ㅤ


**위치 정보**

![Image](https://github.com/user-attachments/assets/8485296e-6785-4391-b1b5-b91fe843aba2)

- 게시글을 생성할 때 위치 정보를 입력했다면 위치 정보를 확인할 수 있습니다.<br />위치 정보는 Naver map api를 통해서 나타냈습니다.


ㅤ


ㅤ


**댓글 / 대댓글**

![Image](https://github.com/user-attachments/assets/46156cca-ffe3-426a-952d-4b7cc2ebd080)

- 게시글에 댓글을 남기고 대댓글을 작성할 수 있습니다.<br/>댓글과 대댓글을 삭제하거나 수정도 가능합니다.


ㅤ


ㅤ


**게시글 좋아요 및 싫어요**

![Image](https://github.com/user-attachments/assets/b441825c-b200-4f8c-a9a9-654a45fef97b)

- 게시글에 대해 좋아요를 표시할 수 있습니다.


ㅤ


ㅤ


**마이 프로필**

![Image](https://github.com/user-attachments/assets/b9d4555c-e897-4624-ba35-aad384a35a78)

- 프로필에서 친구 목록과 프로필 수정을 할 수 있습니다.<br />프로필 수정은 자신의 이름, 사진을 수정할 수 있고 한줄 소개 내용을 작성할 수 있습니다.


ㅤ


ㅤ


**친구 요청 및 수락**

![Image](https://github.com/user-attachments/assets/c265891a-ff4a-4132-b7f4-0ad8af2a8b34)

- 친구를 요청하고 알림에서 친구 요청을 수락 할 수 있습니다.


ㅤ


ㅤ


**Direct Message**

![Image](https://github.com/user-attachments/assets/d8df0dd7-6e0f-4b37-b099-05e08ff91e8f)

- 친구 목록에 있는 친구에게 메세지를 보낼 수 있습니다.<br />필요하다면 사진 및 동영상도 보낼 수 있습니다.


ㅤ


ㅤ


**검색**

![Image](https://github.com/user-attachments/assets/febec056-ad81-4fb4-a9e7-9259deffa487)

- 계정 검색 및 게시글 검색 기능을 통해 사용자와 게시물에 대한 정보를 찾을 수 있습니다.


ㅤ


ㅤ


## 기술 스택 

- Jetpack
  - ViewModel
  - ViewBinding
  - Hilt
  - ViewPager2
  - SwipeRefreshLayout

  
- Architecture
  - Clean Architecture
  - Domain Layer
  - Repository Pattern

  
- Firebase
  - Firebase Firestore
  - Firebase Authentication
  - Firebase Functions
  - Firebase Cloud Messaging


- Library
  - Retrofit2 및 OkHttp3
  - Glide
  - ExoPlayer
  - Naver Maps Api
  - Kakao Maps Api
  - kakao login api
  - OAuth2

- 비동기
  - Async
  - Flow
  - Coroutine


ㅤ


ㅤ


## Architecture

**Cube SNS**는 Clean Architecture를 기반으로 하고 있습니다.

![Image](https://github.com/user-attachments/assets/80955ea4-f3f1-4e04-b2be-0f179c423acf)

Cube SNS는 세 가지 계층으로 구성되어 있습니다.

### UI Layer
- UI Layer 에서는 사용자와 상호작용 하는 화면을 구성하는 UI 요소와 ViewModel이 위치해 있습니다.<br/>ViewModel의 LiveData가 데이터를 관찰하고 있다가 변화를 감지해 UI에 적용시켜 줍니다.


### Domain Layer
- Domain Layer 를 통하여 나중에 앱의 기능이 추가 되거나 수정될 경우를 대비해 유지 보수에 이점을 얻었습니다.


### Data Layer
- 외부의 데이터를 받아오는 역할을 하는 계층입니다.


ㅤ


ㅤ


## 시연 영상

[https://youtu.be/BBEdpLKbM00](https://youtu.be/BBEdpLKbM00)


ㅤ


ㅤ


## 블로그 링크

https://velog.io/@one_coin/posts
