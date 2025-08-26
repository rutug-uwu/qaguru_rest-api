package hw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;

public class ApiTests extends TestBase {

    @DisplayName("Позитивный: GET /users/{id} - Получение пользователя по ID возвращает 200")
    @Test
    void successfulGetUserRequestReturns200Test() {
        given()
                .header("x-api-key", apiKey)
                .log().headers()
                .log().uri()
                .get("/users/1")
                .then()
                .log().status()
                .statusCode(200);
    }

    @DisplayName("Негативный: GET /users/{id} - Получение пользователя по несуществующему ID возвращает 404")
    @Test
    void unsuccessfulGetUserRequestReturns404Test() {
        given()
                .header("x-api-key", apiKey)
                .log().headers()
                .log().uri()
                .get("/users/1000")
                .then()
                .log().status()
                .statusCode(404);
    }

    @DisplayName("Позитивный: PUT /users/{id} - Обновление пользователя по ID возвращает 200 ОК и корректное тело ответа")
    @Test
    void successfulUpdateUserRequestReturnsCorrectResponseTest() {
        String authData = "{\"name\": \"Ivan\", \"lastname\": \"Ivanov\"}";

        given()
                .header("x-api-key", apiKey)
                .body(authData)
                .contentType(JSON)
                .log().uri()
                .log().headers()
                .log().body()

                .when()
                .put("/users/1")

                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("name", is("Ivan"))
                .body("lastname", is("Ivanov"));
    }

    @DisplayName("Негативный: PUT /users/{id} - Обновление пользователя по ID без заголовка 'x-api-key' возвращает 401")
    @Test
    void unsuccessfulUpdateUserRequestWithoutHeaderReturns401Test() {
        String authData = "{\"name\": \"Ivan\", \"lastname\": \"Ivanov\"}";

        given()
                .body(authData)
                .contentType(JSON)
                .log().uri()
                .log().headers()
                .log().body()

                .when()
                .put("/users/1")

                .then()
                .log().status()
                .log().body()
                .statusCode(401)
                .body("error", is("Missing API key"));
    }

    @DisplayName("Позитивный: DELETE /users/{id} - Удаление пользователя по ID возвращает 204")
    @Test
    void successfulDeleteUserRequestReturns204Test() {
        given()
                .header("x-api-key", apiKey)
                .log().headers()
                .log().uri()
                .delete("/users/5")
                .then()
                .log().status()
                .statusCode(204);
    }

    @DisplayName("Позитивный: POST /register - Создание пользователя происходит корректно, возвращает 200")
    @Test
    void successfulCreateUserRequestReturnsCorrectResponseTest() {
        String authData = "{\"email\": \"janet.weaver@reqres.in\", \"password\": \"anypassword\"}";

        given()
                .header("x-api-key", apiKey)
                .body(authData)
                .contentType(JSON)
                .log().uri()
                .log().headers()
                .log().body()

                .when()
                .post("/register")

                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("id", allOf(
                        instanceOf(Integer.class),
                        notNullValue(),
                        greaterThan(0)
                ))
                .body("token", allOf(
                        not(isEmptyOrNullString()),
                        hasLength(17)
                ));
    }
}
