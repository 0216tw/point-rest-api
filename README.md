# 🚀 REST API 포인트 시스템

## 📖 프로젝트 개요

### [tag] 👉 Rest API , TDD, 동시성 제어, REST docs

<br> 

이 프로젝트는 포인트 **충전**, **사용**, **조회** 및 **이력 조회** 기능을 제공하는 REST API입니다. <br> 
**Spring Boot**와 **JPA**를 사용하여 개발되었으며, 동시성 문제 해결을 위해 다양한 접근 방식을 테스트했습니다. <br>
REST API 명세서를 위해 **Rest Docs** 를 적용하였습니다. <br> 
또한, **TDD** 방식을 적용해 단위 테스트, 통합 테스트, E2E 테스트를 진행했습니다. <br> 

## 🛠️ 기술 스택

| 기술 스택         | 사용 기술           |
|-------------------|---------------------|
| **Backend**       | ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white) |
| **Database**      | ![H2](https://img.shields.io/badge/H2-4479A1?style=for-the-badge&logo=h2&logoColor=white) |
| **REST Docs**     | ![REST Docs](https://img.shields.io/badge/Spring%20REST%20Docs-6DB33F?style=for-the-badge&logo=spring&logoColor=white) |
| **Testing**       | ![JUnit](https://img.shields.io/badge/JUnit-25A162?style=for-the-badge&logo=junit5&logoColor=white) ![RestAssured](https://img.shields.io/badge/RestAssured-0075A8?style=for-the-badge&logo=restassured&logoColor=white) |
| **Build Tool**    | ![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white) |
| **Version Control**| ![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white) |

## ✨ 주요 기능

### 1. ⚡ 포인트 충전 API
- **URL**: `PATCH /point/{userId}/charge`
- **Request Body**:
    ```json
    {
      "point": 50000
    }
    ```
- **Response Body**:
    ```json
    {
      "code": 200,
      "message": "충전 완료",
      "data": 50000
    }
    ```

### 2. 💸 포인트 사용 API
- **URL**: `PATCH /point/{userId}/use`
- **Request Body**:
    ```json
    {
      "point": 30000
    }
    ```
- **Response Body**:
    ```json
    {
      "code": 200,
      "message": "사용 완료",
      "data": 20000
    }
    ```

### 3. 📊 포인트 조회 API
- **URL**: `GET /point/{userId}`
- **Response Body**:
    ```json
    {
      "code": 200,
      "message": "조회 완료",
      "data": 20000
    }
    ```

### 4. 📜 포인트 이력 조회 API
- **URL**: `GET /point/{userId}/histories`
- **Response Body**:
    ```json
    {
      "code": 200,
      "message": "포인트 이력 조회 완료",
      "data": [
        { "id": 1, "userId": 123, "point": 50000, "type": "충전" },
        { "id": 2, "userId": 123, "point": 30000, "type": "사용" }
      ]
    }
    ```

<br><br>

## ⚙️ 테스트 전략

### 1. 🧪 단위 테스트 (Unit Test)
TDD 기반으로 개발 초기 단계에서 빠른 피드백을 제공하기 위해 JUnit을 사용하여 각 메소드의 기능을 검증했습니다.

```java
@Test
@DisplayName("포인트 충전 단위 테스트")
public void 포인트_충전_단위_테스트() {
    User user = new User(1L, 10000L);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    pointService.charge(1L, new RequestDTO(5000));

    assertEquals(15000, user.getPoint());
}
```

### 2. 🛠️ 통합 테스트 (Integration Test)
Spring Boot의 통합 테스트 기능을 사용하여 전체 애플리케이션의 흐름을 테스트했습니다.

```java
@SpringBootTest
public class PointServiceIntegrationTest {
    @Test
    @DisplayName("포인트 사용")
    public void 포인트_사용() {

        //given
        long userId = 1L;
        RequestDTO requestDTO = new RequestDTO(20000);
        userRepository.save(new User(userId , requestDTO.getPoint()));

        //when
        User user = pointService.use(1L, new RequestDTO(5000));

        //then
        Assertions.assertThat(user.getUserId()).isEqualTo(1L);
        Assertions.assertThat(user.getPoint()).isEqualTo(15000);
    }
```

### 3. 🔍 E2E (End-to-End) 테스트
RestAssured를 사용하여 API의 전체 흐름을 실제 서버 환경에서 검증하는 E2E 테스트를 진행했습니다.

```java
@Test
@Order(1)
public void 포인트_충전_E2E_테스트() {
    String requestBody = "{ \"point\": 50000 }";

    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .patch("/point/123/charge")
        .then()
        .statusCode(200)
        .body("message", equalTo("충전 완료"));
}
```

## 동시성 제어

### synchronized 사용
간단한 동시성 제어를 위해 메소드에 synchronized 키워드를 사용했습니다. 한 번에 하나의 스레드만 해당 메소드를 실행할 수 있습니다.<br> 

```java
public synchronized User chargeBySynchronized(long userId, RequestDTO requestDTO) {
    // 포인트 충전 로직
}
```


### ReentrantLock 사용
명시적으로 락을 지정 및 해제하는 방식으로 동시성을 제어했습니다. <br> 
```java
private final ReentrantLock lock = new ReentrantLock();

public User chargeByReentrantLock(long userId, RequestDTO requestDTO) {
    lock.lock();
    try {
        // 포인트 충전 로직
    } finally {
        lock.unlock();
    }
}
```

### 비관적 락(Pessimistic Lock) 사용
JPA의 비관적 락을 사용하여 동시에 여러 스레드가 같은 리소스를 변경할 때 충돌을 방지했습니다. (멀티스레드 환경일 경우) 

```java
@Transactional
@Lock(LockModeType.PESSIMISTIC_WRITE)
public User chargeByPessimisticLock(long userId, RequestDTO requestDTO) {
    User user = userRepository.findByIdUsingPessimisticLock(userId).get();
    user.setPoint(user.getPoint() + requestDTO.getPoint());
    return userRepository.save(user);
}
``` 


