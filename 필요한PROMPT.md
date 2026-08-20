이 프로젝트를 기반으로 boilerplate 코드를 생성해야해
DB 가 현재 프로젝트에서는 h2 로 되어있지만, 사용하려는 DB 에따라 (postgresql, mysql, etc) 설정, 코드가 변경되야함.
base package 를 확인하고 package 이름 맞춰야함.

디렉토리 : /Users/moohee.lee/Study/ai_workflow/springboot-kotlin-base
이곳에 있는 프로젝트에는 springboot 4, kotlin 2.3.20, gradle kotlin dsl, yaml, jooq, webflux, coroutine,
virtual thread, java 25 환경에서 초기 프로젝트 설정과 필요한 util 들을 해놓은거야

이 프로젝트구조를 파악하고 이 구조를 따르는 skill 을 생성하고 싶어

springboot 4.x 대의 공식문서와
헥사고날 아키텍처
kotlin 언어 관련한 것

- coding convention, reflection, coroutine, extension 등등
- 공식문서 : https://kotlinlang.org/docs/home.html

정적분석은 detekt 사용

common 패키지 위치와 공통 파일들 처리방법
다국어 처리방법
Enum 처리시 DB 에 저장및 읽기위한 GenericEnum 과 adapter.input 쪽에서 처리하기위한 DisplayEnum 사용법이 적용되어야해

또한 Application 로직 진행중 에러로 인해 에러 응답을 처리하기위한 Exception 처리방법 (CommonException, DefaultException)

모든것은 spring 공식문서, springboot 공식문서, kotlin 공식문서를 기반으로 개발이 진행되어야함.

----
정적분석에 문제가 발생하면 detekt, .editorconfig 를 수정하지말고 코드를 수정해라
