package site.nomoreparties.stellarburgers;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class RestAssuredBaseSpec {

        public RequestSpecification setupAssured (){

            return new RequestSpecBuilder()
                    .setBaseUri("https://stellarburgers.nomoreparties.site/")
                    .setContentType(ContentType.JSON)
                    .build();
        }
    }

