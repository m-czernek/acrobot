package com.redhat;

import com.redhat.client.AcrobotBE;
import com.redhat.constants.Constants;
import com.redhat.entities.Acronym;
import com.redhat.entities.Explanation;
import com.redhat.helpers.JsonNodeHelper;
import com.redhat.messages.MessageHelper;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.HashSet;

@QuarkusTest
@ApplicationScoped
public class MessageHelperTest {


    private final MessageHelper helper = new MessageHelper();

    @BeforeEach
    public void setup() {
        AcrobotBE acrobotBE = Mockito.mock(AcrobotBE.class);
        Acronym a = new Acronym(JsonNodeHelper.INITIAL_ACRONYM);
        Explanation e = new Explanation(JsonNodeHelper.EXPLANATION, JsonNodeHelper.STANDARD_EMAIL);
        e.acronym = a;
        a.explanations = new HashSet<>(Collections.singletonList(e));
        Mockito.when(acrobotBE.getByName("test")).thenReturn(Collections.singletonList(a));

        helper.acrobotResource.acrobotBE = acrobotBE;
    }

    /**
     * Test whether MessageHelper can handle a JSON message which has no argument text. Such a message
     * occurs when the bot gets added to a new room via mention and not via the "add a bot" button.
     */
    @Test
    public void addedToRoomViaMentionTest() {
        Assertions
          .assertEquals(
            helper.handleMessageAction(JsonNodeHelper.getJsonNodeWithoutMessageArgumentText()),
            Constants.ADDED_RESPONSE);
    }

    /**
     * Test whether MessageHelper can handle help request, e.g. "@Acrobot help".
     */
//    @Test
//    public void helpMessageTest() {
//        Assertions
//          .assertThat(helper.handleMessageAction(JsonNodeHelper.getHelpRequest()))
//          .isEqualTo(Constants.HELP_TEXT);
//    }

    /**
     * Test whether upon saving an acronym, we can get acronym. This should work regardless of whether the user
     * inputs the acronym in lowercase or uppercase.
     */
//    @Test
//    public void setAcronymGetAcronymTest() {
//        Assertions
//          .assertThat(helper.handleMessageAction(JsonNodeHelper.getInitialAcronymLowercase()))
//          .isEqualTo(JsonNodeHelper.EXPLANATION + "\n");
//        Assertions
//          .assertThat(helper.handleMessageAction(JsonNodeHelper.getInitialAcronymUppercase()))
//          .isEqualTo(JsonNodeHelper.EXPLANATION + "\n");
//    }

    /**
     * Test whether we can update an acronym. Because we cannot guarantee order of execution (and don't want to
     * guarantee it), we first save the acronym and then update it.
     */
//    @Test
//    public void updateAcronymTest() {
//        helper.handleMessageAction(JsonNodeHelper.updateInitialAcronym());
//        Assertions
//          .assertThat(helper.handleMessageAction(JsonNodeHelper.getInitialAcronymLowercase()))
//          .contains(JsonNodeHelper.EXPLANATION)
//          .contains(JsonNodeHelper.EXPLANATION_UPDATE);
//    }

    /**
     * Test whether incorrect acronym format returns helpful message to the user, and did not get saved.
     */
//    @Test
//    public void testIncorrectAcronymSave() {
//        Assertions
//          .assertThat(helper.handleMessageAction(JsonNodeHelper.getIncorrectAcronymSave()))
//          .isEqualTo(Constants.INCORRECT_FORMAT_FOR_SAVING_ACRONYM);
//        Assertions
//          .assertThat(helper.handleMessageAction(JsonNodeHelper.getIncorrectAcronymSaveWithEquals()))
//          .isEqualTo(Constants.INCORRECT_FORMAT_FOR_SAVING_ACRONYM);
//        Assertions
//          .assertThat(helper.handleMessageAction(JsonNodeHelper.getIncorrectAcronym()))
//          .isEqualTo(Constants.ACRONYM_NOT_FOUND);
//    }

    /**
     * Test whether we can update and delete an acronym that we can delete (i.e. a user can update their own acronyms only)
     */
//    @Test
//    public void updateDeleteTest() {
//        Assertions
//          .assertThat(helper.handleMessageAction(JsonNodeHelper.updateAcronymExplanationSameEmail()))
//          .isEqualTo(Constants.ACRONYM_UPDATED);
//        Assertions
//          .assertThat(helper.handleMessageAction(JsonNodeHelper.getInitialAcronymLowercase()))
//          .isEqualTo(JsonNodeHelper.EXPLANATION_UPDATE + "\n");
//        Assertions
//          .assertThat(helper.handleMessageAction(JsonNodeHelper.deleteUpdatedAcronymExplanationSameEmail()))
//          .contains(Constants.EXPLANATION_REMOVED);
//        Assertions
//          .assertThat(helper.handleMessageAction(JsonNodeHelper.getInitialAcronymLowercase()))
//          .doesNotContain(JsonNodeHelper.EXPLANATION_UPDATE);
//    }

    /**
     * Test whether update and delete fails when user tries to modify someone else's acronym
     */
//    @Test
//    public void updateDeleteFailTest() {
//        Assertions
//          .assertThat(helper.handleMessageAction(JsonNodeHelper.getUpdateAcronymExplanationDifferentUser()))
//          .as("Updating acronym with different email address did not fail properly")
//          .isEqualTo(Constants.INSUFFICIENT_PRIVILEGES);
//
//        Assertions
//          .assertThat(helper.handleMessageAction(JsonNodeHelper.getInitialAcronymLowercase()))
//          .as("Even though we returned insufficient privileges, the update was persisted")
//          .contains(JsonNodeHelper.EXPLANATION);
//
//        Assertions
//          .assertThat(helper.handleMessageAction(JsonNodeHelper.getDeleteAcronymInitialExplanationDifferentUser()))
//          .as("Deleting acronym with different email address did not fail properly")
//          .isEqualTo(Constants.INSUFFICIENT_PRIVILEGES);
//
//        Assertions
//          .assertThat(helper.handleMessageAction(JsonNodeHelper.getInitialAcronymLowercase()))
//          .as("Even though we returned insufficient privileges, the delete was persisted")
//          .contains(JsonNodeHelper.EXPLANATION);
//    }

    /**
     * Test handling of updating non-existent explanation, e.g. acronym=non-existent explanation=>new explanation.
     * Since we don't know what to update, we return an error message to the user.
     */
//    @Test
//    public void updateNonExistentExplanationTest() {
//        Assertions
//          .assertThat(helper.handleMessageAction(JsonNodeHelper.updateNonExistentExplanation()))
//          .isEqualTo(Constants.EXPLANATION_NOT_FOUND);
//    }

    /**
     * Test behavior when the user deletes all the explanations from an acronym. Such acronym is not deleted from the DB,
     * but should be capable of acting as a regular acronym, e.g. it should accept new explanations upon adding, and
     * it should not return any results when it contains no explanations.
     */
//    @Test
//    public void removeAllExplanationAndReAddTest() {
//        JsonNode removeExplanation = JsonNodeHelper.alterArgumentText("!" + JsonNodeHelper.INITIAL_ACRONYM +
//          "=" + JsonNodeHelper.EXPLANATION + "=>");
//
//        // Removing explanation is already tested
//        helper.handleMessageAction(removeExplanation);
//        Assertions
//          .assertThat(helper.handleMessageAction(removeExplanation))
//          .as("Removing explanation from an already empty explanation set failed")
//          .isEqualTo(Constants.EXPLANATION_NOT_FOUND);
//
//        Assertions
//          .assertThat(helper.handleMessageAction(JsonNodeHelper.getInitialAcronymLowercase()))
//          .as("An acronym with an empty explanation set should be evaluated as not found")
//          .isEqualTo(Constants.ACRONYM_NOT_FOUND);
//
//        // Updating original acronym with a new explanation
//        helper.handleMessageAction(JsonNodeHelper.updateInitialAcronym());
//
//        Assertions
//          .assertThat(helper.handleMessageAction(JsonNodeHelper.getInitialAcronymLowercase()))
//          .as("Re-initializing explanations for an acronym with originally empty explanation set failed")
//          .isEqualTo(JsonNodeHelper.EXPLANATION_UPDATE + "\n");
//    }

//    @Test
//    public void exceptionPropagationTest() {
//        Assertions
//          .assertThat(helper.handleMessageAction(JsonNodeHelper.getDatabaseExceptionCausingMessage()))
//          .as("An acronym with explanation over 255 characters should return an exception to the user")
//          .contains("Value too long for column");
//    }
}
