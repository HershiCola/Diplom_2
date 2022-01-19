package site.nomoreparties.stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrdersHelperClassTest {

    StellarBurgersUserHelper user = new StellarBurgersUserHelper();
    StellarBurgersOrdersHelper ordersHelper = new StellarBurgersOrdersHelper();

    @Before
    public void setUp(){
        user.generateUserCredentials();
        ordersHelper.setIngredientsList();
    }

    @Test //тест покрывает случай заказа с ингредиентом
    @DisplayName("Тест на возможность создания заказа с авторизацией")
    public void orderCreationAuthorized(){
        user.registerUser();
        user.setUserAccessToken();
        ordersHelper.createOrderAuthorized(user.getUserAccessToken());
        ordersHelper.getOrderResponse().then().assertThat()
                .body("success", equalTo(true))
                .and()
                .body("name", notNullValue())
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Тест на невозможность создания заказа без авторизации")
    //баг документации\сервиса
    //тест не проходит, так как заказ создается без аксес токена, достаточно валидного ингредиента
    public void orderCreationUnauthorized(){
        //отсутствие пользователя равно отсутствию аксес токена, пользователя можно не регистрировать
        ordersHelper.createOrderUnauthorized();
        ordersHelper.getOrderResponse().then().assertThat()
                .body("success", equalTo(false))
                .and()
                .statusCode(401);
    }

    @Test
    @DisplayName("Тест на попытку создания заказа без ингредиентов")
    public void orderCreationWithoutIngredient(){
        user.registerUser();
        user.setUserAccessToken();
        ordersHelper.createOrderWithoutIngredient(user.getUserAccessToken());
        ordersHelper.getOrderResponse().then().assertThat()
                .body("message", equalTo("Ingredient ids must be provided"))
                .and()
                .statusCode(400);
    }

    @Test
    @DisplayName("Тест на попытку создания заказа с некорректным хэшэм ингредиента")
    public void orderCreationWithInvalidIngredientHash(){
        user.registerUser();
        user.setUserAccessToken();
        ordersHelper.createOrderWithIncorrectIngredientHash(user.getUserAccessToken());
        ordersHelper.getOrderResponse().then().assertThat().statusCode(500);
    }

    @Test
    @DisplayName("Тест на получение списка заказов с авторизацией")
    public void getOrdersByAuthorizedUser(){
        user.registerUser();
        user.setUserAccessToken();
        ordersHelper.createOrderAuthorized(user.getUserAccessToken());
        ordersHelper.getOrderListWithAccessToken(user.getUserAccessToken());
        ordersHelper.getOrderResponse().then().assertThat()
                .body("success", equalTo(true))
                .and()
                .body("orders", notNullValue())
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Тест на получение списка заказа без авторизации")
    public void getOrdersByUnauthorizedUser(){

        ordersHelper.getOrderListWithoutAccessToken();
        ordersHelper.getOrderResponse().then().assertThat()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(401);
    }
}
