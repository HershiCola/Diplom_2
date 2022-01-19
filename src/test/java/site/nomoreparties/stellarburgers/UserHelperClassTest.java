package site.nomoreparties.stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;


public class UserHelperClassTest {

    StellarBurgersUserHelper user = new StellarBurgersUserHelper();

    @Before
    public void setUp(){
        user.generateUserCredentials();
    }

    @Test
    @DisplayName("Тест на возможность создания пользователя")
    public void userCreationIsPossible(){
        user.registerUser();
        user.getUserResponse().then().assertThat().body("success", equalTo(true)).and().statusCode(200);
    }

    @Test
    @DisplayName("Тест на невозможность создать пользователя с теми же данными")
    public void sameUserCreationNotPossible(){
        user.registerUser();
        user.registerUser();
        user.getUserResponse().then().assertThat().body("success", equalTo(false)).and().statusCode(403);
    }

    @Test
    @DisplayName("Тест на попытку создать пользователя без передачи поля почты")
    public void userCreationWithoutEmailNotPossible (){
        String createRequestBody = "{\"password\":\"" + user.getUserPassword() + "\","
                + "\"name\":\"" + user.getUserName() + "\"}";
        user.setUserResponse(given()
                .spec(user.setupAssured())
                .body(createRequestBody)
                .when()
                .post("/api/auth/register"));
        user.getUserResponse()
                .then().assertThat()
                .body("message", equalTo("Email, password and name are required fields"))
                .and().statusCode(403);
    }

    @Test
    @DisplayName("Тест на попытку создать пользователя без передачи поля пароля")
    public void userCreationWithoutPasswordNotPossible (){
        String createRequestBody = "{\"email\":\"" + user.getUserEmail() + "\","
                + "\"name\":\"" + user.getUserName() + "\"}";
        user.setUserResponse(given()
                .spec(user.setupAssured())
                .body(createRequestBody)
                .when()
                .post("/api/auth/register"));
        user.getUserResponse()
                .then().assertThat()
                .body("message", equalTo("Email, password and name are required fields"))
                .and().statusCode(403);
    }

    @Test
    @DisplayName("Тест на попытку создать пользователя без передачи поля имени")
    public void userCreationWithoutNameNotPossible (){
        String createRequestBody = "{\"email\":\"" + user.getUserEmail() + "\","
                + "\"password\":\"" + user.getUserPassword() + "\"}";
        user.setUserResponse(given()
                .spec(user.setupAssured())
                .body(createRequestBody)
                .when()
                .post("/api/auth/register"));
        user.getUserResponse()
                .then().assertThat()
                .body("message", equalTo("Email, password and name are required fields"))
                .and().statusCode(403);
    }

    @Test
    @DisplayName("Тест на возможность корректной авторизации")
    public void userLoginIsPossible(){
        user.registerUser();
        user.userLoggingIn();
        user.getUserResponse().then().assertThat()
                .body("success", equalTo(true))
                .and().statusCode(200);
    }

    @Test //незарегистрированный юзер не может залогиниться = система возвращает ошибку если указать неверный логин и пароль
    @DisplayName("Тест на логин с невалидными данными пользователя")
    public void loginWithIncorrectCredentialsNotAvailable(){
        //пропускаем шаг создания(регистрации)
        user.userLoggingIn();
        user.getUserResponse().then().assertThat()
                .body("message", equalTo("email or password are incorrect"))
                .and().statusCode(401);
    }

    @Test
    @DisplayName("Тест на возможность изменить данные пользователя с авторизацией")
    public void userCredentialsIsPossibleToChangeAuthorized (){
        user.registerUser();
        user.setUserAccessToken();
        user.generateUserCredentials(); //эмулируем новые данные пользователя
        user.refreshUserCredentials(user.getUserAccessToken());
        user.getUserCredentialsFromServer();
        user.getUserResponse().then().assertThat()
                .body("user.email", equalTo(user.getUserEmail()))
                .and() //возвращаемые значения всегда в lowerCase а генерация полей происходит с разным регистром
                .body("user.name", equalTo(user.getUserName()))
                .and().statusCode(200);
    }

    @Test
    @DisplayName("Тест на невозможность изменить данные пользователя без авторизации")
    public void userCredentialsNotChangeableUnauthorized (){
        //шаг регистрации не нужен, поскольку незарегистрированный пользователь как раз не имеет АксесТокена
        user.refreshUserCredentialsWithoutToken();
        user.getUserResponse().then().assertThat()
                .body("success", equalTo(false))
                .and().statusCode(401);
    }
}
