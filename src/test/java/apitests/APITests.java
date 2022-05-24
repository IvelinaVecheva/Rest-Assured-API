package apitests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class APITests {

    static String loginToken;
    String username;
    Integer userId, postId, commentId;

    @BeforeTest
    @Parameters({"username", "password"})
    public void loginTest(String paramUsername, String paramPassword) throws JsonProcessingException {

        // create new LoginPOJO class object named login
        LoginPOJO login = new LoginPOJO();

        // set the login credentials to our login object
        login.setUsernameOrEmail(paramUsername);
        login.setPassword(paramPassword);

        //Convert pojo object to json using GSON

        String convertedJ = new Gson().toJson(login);
        System.out.println("CONVERTED JSON IS: ");
        System.out.println(convertedJ);

        // Convert pojo object to json using Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        String convertedJson = objectMapper.writeValueAsString(login);
        System.out.println("CONVERTED JSON IS: ");
        System.out.println(convertedJson);

        baseURI = "http://training.skillo-bg.com:3100";

        Response response = given()
                .header("Content-Type", "application/json")
                .body(convertedJson)
                .when()
                .post("/users/login");
        response
                .then()
                .statusCode(201);

        // convert the response body json into a string
//        String loginResponseBody = response.getBody().asString();
//
//        loginToken = JsonPath.parse(loginResponseBody).read("$.token");

//        Response response = given()
//                .header("Content-Type", "application/json")
//                .body(convertedJ)
//                .when()
//                .post("/users/login");
//        response
//                .then()
//                .statusCode(201);

        String loginResponseBody = response.getBody().asString();
        loginToken = JsonPath.parse(loginResponseBody).read("token");
        System.out.println(loginToken);

        userId = JsonPath.parse(loginResponseBody).read("user.id");
        System.out.println("UserId is " + userId);

        username = JsonPath.parse(loginResponseBody).read("user.username");
        System.out.println("UserId is " + username);
    }

    @Test(priority = -9)
    public void makePost() {
        ActionsPOJO makePost = new ActionsPOJO();
        makePost.setCaption("Nice!");
        makePost.setCoverUrl("https://i.imgur.com/p0UzQDL.jpg");
        makePost.setPostStatus("public");

        ValidatableResponse validatableResponse = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + loginToken)
                .body(makePost)
                .when()
                .post("/posts/")
                .then()
                .body("caption", equalTo("Nice!"))
                .body("coverUrl", equalTo("https://i.imgur.com/p0UzQDL.jpg"))
                .body("user.id", equalTo(userId))
                .log()
                .all()
                .statusCode(201);

        postId = validatableResponse.extract().path("id");
        System.out.println("PostId is " + postId);
    }

    @Test(priority = -8)
    public void getPost() {
        ValidatableResponse validatableResponse = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + loginToken)
                .when()
                .get("/posts?take=1&skip=0")
                .then()
                .log()
                .all()
                .statusCode(200);

        ArrayList<Integer> returnedPostId = validatableResponse.extract().path("id");
        Assert.assertEquals(returnedPostId.get(0), postId);

    }

    @Test(priority = -7)
    public void commentPost() {
        ActionsPOJO commentPost = new ActionsPOJO();
        commentPost.setContent("My New Comment!");

        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + loginToken)
                .body(commentPost)
                .when()
                .post("/posts/"+ postId + "/comment");

        response
                .then()
                .body("content", equalTo("My New Comment!"))
                .log()
                .all()
                .statusCode(201);

        String commentResponseBody = response.getBody().asString();

//        Integer localCommentId = JsonPath.parse(commentResponseBody).read("id");
//        commentId = Integer.toString(localCommentId);

        commentId = JsonPath.parse(commentResponseBody).read("id");

    }

    @Test(priority = -6)
    public void deleteComment() {
        System.out.println("CommentId is  " + commentId);
        ValidatableResponse validatableResponse = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + loginToken)
                .when()
                .delete("/posts/"+ postId + "/comments/" + commentId + "?commentId=" + commentId)
                .then()
                .log()
                .all()
                .statusCode(200);

        Integer returnedCommentId = validatableResponse.extract().path("id");
        Assert.assertEquals(returnedCommentId, commentId);

        Integer returnedUserId = validatableResponse.extract().path("user.id");
        Assert.assertEquals(returnedUserId, userId);

        String returnedUsername = validatableResponse.extract().path("user.username");
        Assert.assertEquals(returnedUsername, username);


    }

    @Test(priority = -5)
    public void likePost() {
        // create an object of ActionsPOJO class and add value for the fields
        ActionsPOJO likePost = new ActionsPOJO();
        likePost.setAction("likePost");

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + loginToken)
                .body(likePost)
                .when()
                .patch("/posts/"+postId)
                .then()
                .body("post.id", equalTo(postId))
                .log()
                .all();

    }

    @Test(priority = -4)
    public void deletePost()  {

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + loginToken)
                .when()
                .delete("/posts/" + postId)
                .then()
                .body("msg", equalTo("Post was deleted!"))
                .log()
                .all();

    }

    @Test(priority = -3)
    public void deleteUser()  { }


}
