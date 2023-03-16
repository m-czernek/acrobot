package com.redhat;

import com.redhat.client.AcrobotBE;
import com.redhat.constants.Constants;
import com.redhat.entities.Acronym;
import com.redhat.entities.Explanation;
import com.redhat.helpers.JsonNodeHelper;
import com.redhat.messages.MessageController;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashSet;

@QuarkusTest
public class MessageHelperTest {

    @InjectMock
    @RestClient
    AcrobotBE acrobotBE;

    @Inject
    MessageController helper;

    @BeforeEach
    public void configure() {
        Mockito.when(
            acrobotBE.getByName(JsonNodeHelper.INITIAL_ACRONYM))
          .thenReturn(Collections.singletonList(getAcronym(JsonNodeHelper.EXPLANATION)));

        Mockito.when(
            acrobotBE.getByName(JsonNodeHelper.INITIAL_ACRONYM.toLowerCase()))
          .thenReturn(Collections.singletonList(getAcronym(JsonNodeHelper.EXPLANATION)));
    }

    private static Acronym getAcronym(String expl) {
        Acronym a = new Acronym(JsonNodeHelper.INITIAL_ACRONYM);
        Explanation e = new Explanation(expl, JsonNodeHelper.STANDARD_EMAIL);
        e.acronym = a;
        a.explanations = new HashSet<>(Collections.singletonList(e));
        return a;
    }

    /**
     * Test whether MessageHelper can handle a JSON message which has no argument text. Such a message
     * occurs when the bot gets added to a new room via mention and not via the "add a bot" button.
     */
    @Test
    public void addedToRoomViaMentionTest() {
        Assertions
          .assertEquals(
            Constants.ADDED_RESPONSE,
            helper.handleMessageAction(JsonNodeHelper.getJsonNodeWithoutMessageArgumentText()));
    }

    /**
     * Test whether MessageHelper can handle help request, e.g. "@Acrobot help".
     */
    @Test
    public void helpMessageTest() {
        Assertions
          .assertEquals(Constants.HELP_TEXT,
            helper.handleMessageAction(JsonNodeHelper.getHelpRequest()));
    }

    /**
     * Test whether we can get an acronym explanation.
     */
    @Test
    public void getAcronymTest() {
        Assertions.assertEquals(JsonNodeHelper.EXPLANATION + "\n",
          helper.handleMessageAction(JsonNodeHelper.getInitialAcronymLowercase()));
    }

    /**
     * Test whether we can update an acronym.
     */
    @Test
    public void updateAcronymTest() {
          Assertions.assertEquals(Constants.ACRONYM_UPDATED,
          helper.handleMessageAction(JsonNodeHelper.updateInitialAcronym()));
    }

    /**
     * Test whether incorrect acronym format returns helpful message to the user, and did not get saved.
     */
    @Test
    public void testIncorrectAcronymSave() {
        Assertions.assertEquals(Constants.INCORRECT_FORMAT_FOR_SAVING_ACRONYM,
          helper.handleMessageAction(JsonNodeHelper.getIncorrectAcronymSave()));

        Assertions.assertEquals(Constants.INCORRECT_FORMAT_FOR_SAVING_ACRONYM,
          helper.handleMessageAction(JsonNodeHelper.getIncorrectAcronymSaveWithEquals()));
    }

    /**
     * Test whether we can delete an acronym that we can delete (i.e. a user can update their own acronyms only)
     */
    @Test
    public void deleteAcronymExplanationTest() {
        Assertions.assertEquals(Constants.ACRONYM_UPDATED,
            helper.handleMessageAction(JsonNodeHelper.deleteAcronymInitialExplanation()));
    }

    /**
     * Test whether update and delete fails when user tries to modify someone else's acronym
     */
    @Test
    public void updateDeleteFailTest() {
        Assertions.assertEquals(Constants.INSUFFICIENT_PRIVILEGES,
          helper.handleMessageAction(JsonNodeHelper.getUpdateAcronymExplanationDifferentUser()));

        Assertions.assertEquals(Constants.INSUFFICIENT_PRIVILEGES,
          helper.handleMessageAction(JsonNodeHelper.getDeleteAcronymInitialExplanationDifferentUser()));
    }

    /**
     * Test handling of updating non-existent explanation, e.g. acronym=non-existent explanation=>new explanation.
     * Since we don't know what to update, we return an error message to the user.
     */
    @Test
    public void updateNonExistentExplanationTest() {
        Assertions.assertEquals(Constants.EXPLANATION_NOT_FOUND,
            helper.handleMessageAction(JsonNodeHelper.updateNonExistentExplanation()));
    }

    /**
     * Test behavior when the user deletes all the explanations from an acronym. Such acronym is not deleted from the DB,
     * but should be capable of acting as a regular acronym, e.g. it should accept new explanations upon adding, and
     * it should not return any results when it contains no explanations.
     */
    @Test
    public void getAcronymEmptyExplanationSet() {
        Acronym a = getAcronym("");
        a.explanations = Collections.EMPTY_SET;
        Mockito.when(
            acrobotBE.getByName(JsonNodeHelper.INITIAL_ACRONYM.toLowerCase()))
          .thenReturn(Collections.singletonList(a));

        Assertions
          .assertEquals(Constants.ACRONYM_NOT_FOUND,
            helper.handleMessageAction(JsonNodeHelper.getInitialAcronymLowercase()));
    }

    @Test
    public void exceptionPropagationTest() {
        Assertions
          .assertEquals(Constants.MESSAGE_TOO_LONG,
            helper.handleMessageAction(JsonNodeHelper.getDatabaseExceptionCausingMessage()));
    }
}
