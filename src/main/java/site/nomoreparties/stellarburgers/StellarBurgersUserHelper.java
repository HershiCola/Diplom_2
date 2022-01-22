package site.nomoreparties.stellarburgers;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;

import static io.restassured.RestAssured.given;

public class StellarBurgersUserHelper extends RestAssuredBaseSpec {

    private String userEmail;
    private String userPassword;
    private String userName;
    private String userAccessToken;

    private Response userResponse;

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUserName() {
        return userName;
    }

    @Step("Забираем тело ответа для обработки")
    public Response getUserResponse() {
        return userResponse;
    }

    @Step("Кладем в поле класса ответ для дальнейшей работы с ним")
    public void setUserResponse(Response userResponse) {
        this.userResponse = userResponse;
    }

    @Step("Генерация полей пользователя для регистрации")
    public void generateUserCredentials() {

        userEmail = (RandomStringUtils.randomAlphabetic(8) + "@testmail.com").toLowerCase();
        userPassword = RandomStringUtils.randomAlphabetic(8);
        userName = RandomStringUtils.randomAlphabetic(8).toLowerCase();
    }

    @Step("Запрос на создание пользователя")
    public void registerUser() {

        String registerRequestBody = "{\"email\":\"" + userEmail + "\","
                + "\"password\":\"" + userPassword + "\","
                + "\"name\":\"" + userName + "\"}";

        userResponse = given()
                .spec(setupAssured())
                .body(registerRequestBody)
                .when()
                .post("/api/auth/register");
    }

    @Step("Получение и сохранение в поле АксесТокена")
    public void setUserAccessToken(){
        userAccessToken = userResponse.then().extract().path("accessToken");
    }

    public String getUserAccessToken(){
        return userAccessToken;
    }

    @Step("Запрос на логин пользователя")
    public void userLoggingIn() {

        String requestByLoginAndPass = "{\"email\": \"" + userEmail + "\", \"password\": \"" + userPassword + "\"}";
        userResponse = given().spec(setupAssured())
                .body(requestByLoginAndPass).when().post("/api/auth/login");

    }

    @Step("Обновление данных пользователя c авторизацией")
    public void refreshUserCredentials(String userAccessToken){

        String refreshRequestBody = "{\"email\":\"" + userEmail + "\","
                + "\"password\":\"" + userPassword + "\","
                + "\"name\":\"" + userName + "\"}";

        userResponse = given()
                .spec(setupAssured())
                .headers("Authorization", userAccessToken)
                .body(refreshRequestBody)
                .when()
                .patch("/api/auth/user");
    }

    @Step("Обновление данных пользователя без авторизации")
    public void refreshUserCredentialsWithoutToken(){

        String refreshRequestBody = "{\"email\":\"" + userEmail + "\","
                + "\"password\":\"" + userPassword + "\","
                + "\"name\":\"" + userName + "\"}";

        userResponse = given()
                .spec(setupAssured())
                .body(refreshRequestBody)
                .when()
                .patch("/api/auth/user");
    }

    @Step("Получение данных пользователя")
    public void getUserCredentialsFromServer(){
        userResponse = given()
                .spec(setupAssured())
                .headers("Authorization", userAccessToken)
                .when()
                .get("/api/auth/user");
    }
}
