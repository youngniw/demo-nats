# demo-nats
[SpringBoot] Nats 활용 예제 프로젝트

---

<h3>- 간략한 실습 프로젝트 소개</h3>

본 실습 프로젝트에서는 차량 정보와 차량 운행 정보를 저장 및 조회할 수 있게 한다.

차량 운행 정보 저장 시, 차량의 운행 정보를 REST API로 받아 nats 서버를 통해 구독자(subscriber)에게 발행(publish)한다.

이때, nats 서버를 통해 publish-subscribe가 이루어질 수 있으며, websocket을 통해 전달받을 수도 있다.

<br/>
<h3>- 기술 스택</h3>

- Spring Boot
  - Lombok
  - Spring Web
  - Spring Websocket
  - Spring Data JPA
  - Spring Validation
- Gradle
- Java 11
- Database
  - PostgreSQL
- NATS

<br/>
<h3>- nats 서버</h3>

1. nats 서버 통로
   - Websocket 접근 URL: "ws://localhost:8080/ws/data"
     
   <br/>

   - Subject
     - 모든 차량 실시간 운행 정보 조회: "msg.vehicle.data"
     - 특정 차량 실시간 운행 정보 조회: "msg.vehicle.data.{차량번호}"
     - 차량 정보 요청(request): "msg.vehicle.request.{차량번호}"

   <br/>

2. nats 서버 실행 코드
   - 프로젝트 폴더에서 nats 폴더로 이동
    ```
    cd nats
    ```

   - docker로 nats 실행
     - Mac 운영체제
       ```
       docker run --name nats \          
       -v $(pwd)/etc/nats:/etc/nats \
       -p 4222:4222 \
       -p 8222:8222 \
       -p 8000:8000 \
       nats --http_port 8222 -c /etc/nats/vehicle.conf
       ```

     - Window 운영체제
       ```
       docker run --name nats -v $(pwd)/etc/nats:/etc/nats -p 4222:4222 -p 8222:8222 -p 8000:8000 nats --http_port 8222 -c /etc/nats/vehicle.conf
       ```

   - docker로 nats 컨테이너 종료
    ```
    docker rm nats
    ```
