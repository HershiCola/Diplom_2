package site.nomoreparties.stellarburgers;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class IngredientsAndOrdersHelperClassTest {

    StellarBurgersUserHelper user = new StellarBurgersUserHelper();
    StellarBurgersIngredientsAndOrdersHelper ordersHelper = new StellarBurgersIngredientsAndOrdersHelper();

    @Before
    public void setUp(){
        user.generateUserCredentials();
        ordersHelper.setIngredientsList();
    }

    @Test //тест покрывает случай заказа с ингредиентом
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
    public void orderCreationWithInvalidIngredientHash(){
        user.registerUser();
        user.setUserAccessToken();
        ordersHelper.createOrderWithIncorrectIngredientHash(user.getUserAccessToken());
        ordersHelper.getOrderResponse().then().assertThat().statusCode(500);
    }

    @Test
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
