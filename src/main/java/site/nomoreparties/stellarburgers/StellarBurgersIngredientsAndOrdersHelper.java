package site.nomoreparties.stellarburgers;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class StellarBurgersIngredientsAndOrdersHelper extends RestAssuredBaseSpec {

    private List<String> ingredientsList;
    private Response orderResponse;

    @Step("Забираем тело ответа по заказу для обработки")
    public Response getOrderResponse() {
        return orderResponse;
    }

    @Step("Метод получения списка ингредиентов")
    public void setIngredientsList (){

        ingredientsList = given()
                .spec(setupAssured())
                .get("/api/ingredients")
                .then()
                .extract()
                .path("data._id");
    }

    @Step("Метод создания заказа с авторизацией")
    public void createOrderAuthorized (String accessToken){

        Random rand = new Random();
        String randomIngredientFromList = ingredientsList.get(rand.nextInt(ingredientsList.size()));

        String orderRequest = "{\"ingredients\": \"" + randomIngredientFromList + "\"}";

        orderResponse = given()
                .spec(setupAssured())
                .headers("authorization", accessToken)
                .body(orderRequest)
                .when()
                .post("/api/orders");

    }

    @Step("Метод создания заказа без авторизации")
    public void createOrderUnauthorized (){

        Random rand = new Random();
        String randomIngredientFromList = ingredientsList.get(rand.nextInt(ingredientsList.size()));

        String orderRequest = "{\"ingredients\": \"" + randomIngredientFromList + "\"}";

        orderResponse = given()
                .spec(setupAssured())
                .body(orderRequest)
                .when()
                .post("/api/orders");

    }

    @Step("Метод создания заказа без ингредиентов")
    public void createOrderWithoutIngredient (String accessToken){

        orderResponse = given()
                .spec(setupAssured())
                .headers("authorization", accessToken)
                .when()
                .post("/api/orders");

    }

    @Step("Метод создания заказа с некорректным хэшэм ингредиента")
    public void createOrderWithIncorrectIngredientHash (String accessToken){

        String orderRequest = "{\"ingredients\": \"incorrectHash\"}";
        orderResponse = given()
                .spec(setupAssured())
                .headers("authorization", accessToken)
                .body(orderRequest)
                .when()
                .post("/api/orders");

    }

    @Step("Метод получения списка заказов с авторизацией")
    public void getOrderListWithAccessToken (String accessToken){

        orderResponse = given()
                .spec(setupAssured())
                .headers("authorization", accessToken)
                .when()
                .get("/api/orders");
    }

    @Step("Метод получения списка заказов без авторизации")
    public void getOrderListWithoutAccessToken (){

        orderResponse = given()
                .spec(setupAssured())
                .when()
                .get("/api/orders");
    }
}
