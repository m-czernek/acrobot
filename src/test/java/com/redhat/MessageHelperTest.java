package com.redhat;

import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.constants.Constants;
import com.redhat.helpers.JsonNodeHelper;
import com.redhat.messages.AdministrativeMessageHelper;
import com.redhat.messages.MessageHelper;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

public class MessageHelperTest {
    private MessageHelper helper = new MessageHelper();

    @ClassRule
    public static final EnvironmentVariables environmentVariables = new EnvironmentVariables()
            .set("JDBC_URL", "jdbc:h2:mem:test")
            .set("JDBC_USER","sa")
            .set("JDBC_PASSWORD","")
            .set("PU","test")
            .set("SUDO_PASSWORD", "test");

    /**
     * Test whether MessageHelper can handle a JSON message which has no argument text. Such a message
     * occurs when the bot gets added to a new room via mention and not via the "add a bot" button.
     */
    @Test
    public void addedToRoomViaMentionTest() {
        Assertions
                .assertThat(helper.handleMessageAction(JsonNodeHelper.getJsonNodeWithoutMessageArgumentText()))
                .isEqualTo(Constants.ADDED_RESPONSE);
    }

    /**
     * Test whether MessageHelper can handle help request, e.g. "@Acrobot help".
     */
    @Test
    public void helpMessageTest() {
        Assertions
                .assertThat(helper.handleMessageAction(JsonNodeHelper.getHelpRequest()))
                .isEqualTo(Constants.HELP_TEXT);
    }

    /**
     * Test whether upon saving an acronym, we can get acronym. This should work regardless of whether the user
     * inputs the acronym in lowercase or uppercase.
     */
    @Test
    public void setAcronymGetAcronymTest() {
        Assertions
                .assertThat(helper.handleMessageAction(JsonNodeHelper.getInitialAcronymLowercase()))
                .isEqualTo(JsonNodeHelper.EXPLANATION + "\n");
        Assertions
                .assertThat(helper.handleMessageAction(JsonNodeHelper.getInitialAcronymUppercase()))
                .isEqualTo(JsonNodeHelper.EXPLANATION + "\n");
    }

    /**
     * Test whether we can update an acronym. Because we cannot guarantee order of execution (and don't want to
     * guarantee it), we first save the acronym and then update it.
     */
    @Test
    public void updateAcronymTest() {
        helper.handleMessageAction(JsonNodeHelper.updateInitialAcronym());
        Assertions
                .assertThat(helper.handleMessageAction(JsonNodeHelper.getInitialAcronymLowercase()))
                .contains(JsonNodeHelper.EXPLANATION)
                .contains(JsonNodeHelper.EXPLANATION_UPDATE);
    }

    /**
     * Test whether incorrect acronym format returns helpful message to the user, and did not get saved.
     */
    @Test
    public void testIncorrectAcronymSave() {
        Assertions
                .assertThat(helper.handleMessageAction(JsonNodeHelper.getIncorrectAcronymSave()))
                .isEqualTo(Constants.INCORRECT_FORMAT_FOR_SAVING_ACRONYM);
        Assertions
                .assertThat(helper.handleMessageAction(JsonNodeHelper.getIncorrectAcronymSaveWithEquals()))
                .isEqualTo(Constants.INCORRECT_FORMAT_FOR_SAVING_ACRONYM);
        Assertions
                .assertThat(helper.handleMessageAction(JsonNodeHelper.getIncorrectAcronym()))
                .isEqualTo(Constants.ACRONYM_NOT_FOUND);
    }

    /**
     * Test whether we can update and delete an acronym that we can delete (i.e. a user can update their own acronyms only)
     */
    @Test
    public void updateDeleteTest() {
        Assertions
                .assertThat(helper.handleMessageAction(JsonNodeHelper.updateAcronymExplanationSameEmail()))
                .isEqualTo(Constants.ACRONYM_UPDATED);
        Assertions
                .assertThat(helper.handleMessageAction(JsonNodeHelper.getInitialAcronymLowercase()))
                .isEqualTo(JsonNodeHelper.EXPLANATION_UPDATE + "\n");
        Assertions
                .assertThat(helper.handleMessageAction(JsonNodeHelper.deleteUpdatedAcronymExplanationSameEmail()))
                .contains(Constants.EXPLANATION_REMOVED);
        Assertions
                .assertThat(helper.handleMessageAction(JsonNodeHelper.getInitialAcronymLowercase()))
                .doesNotContain(JsonNodeHelper.EXPLANATION_UPDATE);
    }

    /**
     * Test whether update and delete fails when user tries to modify someone else's acronym
     */
    @Test
    public void updateDeleteFailTest() {
        Assertions
                .assertThat(helper.handleMessageAction(JsonNodeHelper.getUpdateAcronymExplanationDifferentUser()))
                .as("Updating acronym with different email address did not fail properly")
                .isEqualTo(Constants.INSUFFICIENT_PRIVILEGES);

        Assertions
                .assertThat(helper.handleMessageAction(JsonNodeHelper.getInitialAcronymLowercase()))
                .as("Even though we returned insufficient privileges, the update was persisted")
                .contains(JsonNodeHelper.EXPLANATION);

        Assertions
                .assertThat(helper.handleMessageAction(JsonNodeHelper.getDeleteAcronymInitialExplanationDifferentUser()))
                .as("Deleting acronym with different email address did not fail properly")
                .isEqualTo(Constants.INSUFFICIENT_PRIVILEGES);

        Assertions
                .assertThat(helper.handleMessageAction(JsonNodeHelper.getInitialAcronymLowercase()))
                .as("Even though we returned insufficient privileges, the delete was persisted")
                .contains(JsonNodeHelper.EXPLANATION);
    }

    /**
     * Test handling of updating non-existent explanation, e.g. acronym=non-existent explanation=>new explanation.
     * Since we don't know what to update, we return an error message to the user.
     */
    @Test
    public void updateNonExistentExplanationTest() {
        Assertions
                .assertThat(helper.handleMessageAction(JsonNodeHelper.updateNonExistentExplanation()))
                .isEqualTo(Constants.EXPLANATION_NOT_FOUND);
    }

    /**
     * Test behavior when the user deletes all the explanations from an acronym. Such acronym is not deleted from the DB,
     * but should be capable of acting as a regular acronym, e.g. it should accept new explanations upon adding, and
     * it should not return any results when it contains no explanations.
     */
    @Test
    public void removeAllExplanationAndReAddTest() {
        JsonNode removeExplanation = JsonNodeHelper.alterArgumentText("!" + JsonNodeHelper.INITIAL_ACRONYM +
                "=" + JsonNodeHelper.EXPLANATION + "=>");

        // Removing explanation is already tested
        helper.handleMessageAction(removeExplanation);
        Assertions
                .assertThat(helper.handleMessageAction(removeExplanation))
                .as("Removing explanation from an already empty explanation set failed")
                .isEqualTo(Constants.EXPLANATION_NOT_FOUND);

        Assertions
                .assertThat(helper.handleMessageAction(JsonNodeHelper.getInitialAcronymLowercase()))
                .as("An acronym with an empty explanation set should be evaluated as not found")
                .isEqualTo(Constants.ACRONYM_NOT_FOUND);

        // Updating original acronym with a new explanation
        helper.handleMessageAction(JsonNodeHelper.updateInitialAcronym());

        Assertions
                .assertThat(helper.handleMessageAction(JsonNodeHelper.getInitialAcronymLowercase()))
                .as("Re-initializing explanations for an acronym with originally empty explanation set failed")
                .isEqualTo(JsonNodeHelper.EXPLANATION_UPDATE + "\n");
    }

    @Before
    public void setupDB() {
        // Connecting will re-create all tables, and we insert an initial acronym for testing purposes
        AdministrativeMessageHelper.handleAdminMessage("connect");
        helper.handleMessageAction(JsonNodeHelper.getSetupDb());
    }

    @After
    public void cleanDB() {
        // Disconnecting will drop all tables, and all changes to the DB
        AdministrativeMessageHelper.handleAdminMessage("disconnect");
    }
}
