package com.restapi.point.e2e;

import com.restapi.point.domain.model.User;
import com.restapi.point.infrastructure.repository.PointRepository;
import com.restapi.point.infrastructure.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // @BeforeAll 메서드를 static으로 선언하지 않기 위해 사용
public class PointRestControllerE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PointRepository pointRepository;


    @BeforeAll
    public void setUp() {
        // RestAssured의 기본 URL 설정
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        userRepository.deleteAll();
        pointRepository.deleteAll();
        userRepository.save(new User(123L ,0));
    }

    @Test
    @Order(1)
    @DisplayName("포인트충전-e2e")
    public void 포인트_충전_테스트() {
        //given

        String requestBody = "{ \"point\" : 50000 }" ;
        Response response  = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(requestBody)
        //when
                .when()
                .patch("/point/123/charge")
        //then
                .then()
                .statusCode(200)
                .log().all()
                .extract()
                .response();

        String responseBody = response.getBody().asString(); // 응답 본문을 문자열로 추출
        int statusCode = response.getStatusCode(); // 상태 코드 추출

        Assertions.assertThat(responseBody).isEqualTo("{\"code\":200,\"message\":\"충전완료\",\"data\":50000}");
        Assertions.assertThat(statusCode).isEqualTo(200);

    }

    @Test
    @Order(2)
    @DisplayName("포인트사용-e2e")
    public void 포인트_사용_테스트() {

        //given
        String requestBody = "{ \"point\" : 30000 }" ;
        Response response  = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(requestBody)
                //when
                .when()
                .patch("/point/123/use")
                //then
                .then()
                .statusCode(200)
                .log().all()
                .extract()
                .response();

        String responseBody = response.getBody().asString(); // 응답 본문을 문자열로 추출
        int statusCode = response.getStatusCode(); // 상태 코드 추출

        Assertions.assertThat(responseBody).isEqualTo("{\"code\":200,\"message\":\"사용완료\",\"data\":20000}");
        Assertions.assertThat(statusCode).isEqualTo(200);


    }

    @Test
    @Order(3)
    @DisplayName("포인트조회-e2e")
    public void 포인트_조회_테스트() {

        //given
        Response response  = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                //when
                .when()
                .get("/point/123")
                //then
                .then()
                .statusCode(200)
                .log().all()
                .extract()
                .response();

        String responseBody = response.getBody().asString(); // 응답 본문을 문자열로 추출
        int statusCode = response.getStatusCode(); // 상태 코드 추출

        Assertions.assertThat(responseBody).isEqualTo("{\"code\":200,\"message\":\"조회완료\",\"data\":20000}");
        Assertions.assertThat(statusCode).isEqualTo(200);
    }


    @Test
    @Order(4)
    @DisplayName("포인트 사용 내역 조회 e2e")
    public void 포인트_사용_내역_조회_e2e() {

        //given
        Response response  = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                //when
                .when()
                .get("/point/123/histories")
                //then
                .then()
                .statusCode(200)
                .log().all()
                .extract()
                .response();

        String responseBody = response.getBody().asString(); // 응답 본문을 문자열로 추출
        int statusCode = response.getStatusCode(); // 상태 코드 추출

        Assertions.assertThat(statusCode).isEqualTo(200);
        // 응답 본문을 JsonPath로 파싱
        JsonPath jsonPath = response.jsonPath();

        // JSON 배열에서 특정 index의 필드 값을 가져옴
        List<Integer> points = jsonPath.getList("data.point");
        List<String> types = jsonPath.getList("data.type");

        // 특정 데이터의 point와 type을 검증
        Assertions.assertThat(points.get(0)).isEqualTo(50000);  // 첫 번째 기록의 point가 50000인지 확인
        Assertions.assertThat(types.get(0)).isEqualTo("충전");   // 첫 번째 기록의 type이 "충전"인지 확인

        // 필요하다면 다른 기록도 검증 가능
        Assertions.assertThat(points.get(1)).isEqualTo(30000);
        Assertions.assertThat(types.get(1)).isEqualTo("사용");
    }
}
