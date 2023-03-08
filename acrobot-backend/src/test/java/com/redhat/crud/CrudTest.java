package com.redhat.crud;

import com.redhat.entities.Acronym;
import com.redhat.entities.Explanation;
import com.redhat.resources.RestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import javax.ws.rs.NotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@TestHTTPEndpoint(RestResource.class)
@QuarkusTestResource(H2DatabaseTestResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CrudTest {

    private final String acronymStr = "tla";
    private final String explanation = "three letter acronym";
    private final String modifiedExplanation = "three letter abbreviation";
    private final Acronym acronym = createAcronym();
    private final Acronym modifiedAcronym = modifyAcronym();

    private Acronym createAcronym() {
        var a = new Acronym(acronymStr);
        a.explanations = new HashSet<>(Collections.singletonList(new Explanation(explanation, a)));
        return a;
    }

    private Acronym modifyAcronym() {
        var a = createAcronym();
        a.explanations = new HashSet<>(Collections.singletonList(new Explanation(modifiedExplanation, a)));
        return a;
    }

    @Test
    @Order(0)
    public void testAcronymDoesNotExist() {
        given()
          .when()
          .get("/" + acronymStr)
          .then()
          .statusCode(200)
          .body("$.size()", is(0));
    }

    @Test
    @Order(5)
    public void testAddAcronym() {
        given()
          .when()
          .body(acronym)
          .contentType(ContentType.JSON)
          .put()
          .then()
          .statusCode(201);

        given()
          .when()
          .get("/" + acronymStr)
          .then()
          .statusCode(200)
          .body("$.size()", is(1))
          .body( "[0].explanations[0].explanation", is( explanation ) );
    }

    @Test
    @Order(10)
    public void testChangeAcronymExplanation() {
        given()
          .when()
          .body(modifiedAcronym)
          .contentType(ContentType.JSON)
          .post()
          .then()
          .statusCode(200);

        given()
          .when()
          .get("/" + acronymStr)
          .then()
          .statusCode(200)
          .body( "[0].explanations[0].explanation", is( modifiedExplanation ) );
    }

    @Test
    @Order(15)
    public void testAddAcronymExplanation() {
        var a = modifyAcronym();
        var e = new Explanation(explanation, a);
        a.explanations.add(e);

        given()
          .when()
          .body(a)
          .contentType(ContentType.JSON)
          .post()
          .then()
          .statusCode(200);

        var givenAcronym = Arrays.stream(given()
          .when()
          .get("/" + acronymStr)
          .then()
          .statusCode(200)
          .extract().body().as(Acronym[].class))
          .findFirst().orElseThrow(NotFoundException::new);

        Assertions.assertEquals(2, givenAcronym.explanations.size());
        Assertions.assertTrue(givenAcronym.explanations.containsAll(acronym.explanations));
        Assertions.assertTrue(givenAcronym.explanations.containsAll(modifiedAcronym.explanations));
    }

    @Test
    @Order(20)
    public void testGetAcronymCaseInsensitive() {
        given()
          .when()
          .get("/" + acronymStr.toUpperCase())
          .then()
          .statusCode(200)
          .body("$.size()", is(1));

        given()
          .when()
          .get("/" + acronymStr.replace('l', 'L'))
          .then()
          .statusCode(200)
          .body("$.size()", is(1));
    }

    @Test
    @Order(25)
    public void testDeleteAcronym() {
        given()
          .when()
          .delete("/" + acronymStr)
          .then()
          .statusCode(204);

        given()
          .when()
          .get("/" + acronymStr)
          .then()
          .statusCode(200)
          .body("$.size()", is(0));
    }

}
