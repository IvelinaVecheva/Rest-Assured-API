package apitests;

import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class FirstAPITest {

        @Test
        public void getPosts(){
                given()
                        .when()
                        .get("http://training.skillo-bg.com:3100/posts/public?take=1&skip=0")
                        .then()
                        .log()
                        .all();
        }

}
