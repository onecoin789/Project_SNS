# <div align="center">Cube SNS</div>

**Cube SNS**는 유저들이 사진을 통해 일상 공유를 하고 서로 direct message를 통해 연락을 할 수 있도록 Firebase를 기반으로 만든 커뮤니티 앱 입니다.


ㅤ


ㅤ



## 목차
> 1. 앱 기능 소개
> 2. 기술 스택
> 3. Architecture
> 4. 시연 영상
> 5. 블로그 링크


ㅤ


ㅤ


## 앱 기능 소개

**회원가입**
 
사진 첨부
 
-  이메일을 통해서 계정을 생성할 수 있습니다.


ㅤ


ㅤ


**로그인**

사진 첨부

-  회원가입을 통해 생성한 이메일이나 카카오 계정을 통해 로그인이 가능합니다.


ㅤ


ㅤ


**게시글**

사진 첨부

- 사진(다중 이미지, 동영상)과 글을 통한 자신의 일상 게시글을 올릴 수 있습니다. <br />필요하시면 위치 정보도 함께 공유 가능합니다.


ㅤ


ㅤ


**게시글 수정**

사진 첨부

- 게시글을 수정할 수 있습니다.<br />수정하면 오른쪽 상단에 수정된 게시글이라고 나타납니다.


ㅤ


ㅤ


**위치 정보**

사진 첨부

- 게시글을 생성할 때 위치 정보를 입력했다면 위치 정보를 확인할 수 있습니다.<br />위치 정보는 Naver map api를 통해서 나타냈습니다.


ㅤ


ㅤ


**댓글 / 대댓글**

사진 첨부

- 게시글에 댓글을 남기고 대댓글을 작성할 수 있습니다.


ㅤ


ㅤ


**게시글 좋아요 및 싫어요**

사진 첨부

- 게시글에 대해 좋아요를 표시할 수 있습니다.


ㅤ


ㅤ


**마이 프로필**

사진 첨부

- 프로필에서 친구 목록과 프로필 수정을 할 수 있습니다.<br />프로필 수정은 자신의 이름, 사진을 수정할 수 있고 한줄 소개 내용을 작성할 수 있습니다.


ㅤ


ㅤ


**친구 요청 및 수락**

사진 첨부

- 친구를 요청하고 알림에서 수락을 할 수 있습니다.


ㅤ


ㅤ


**Direct Message**

사진 첨부

- 친구 목록에 있는 친구에게 메세지를 보낼 수 있습니다.<br />필요하다면 사진 및 동영상도 보낼 수 있습니다.


ㅤ


ㅤ


**검색**

사진 첨부

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

영상


ㅤ


ㅤ


## 블로그 링크

https://velog.io/@one_coin/posts
