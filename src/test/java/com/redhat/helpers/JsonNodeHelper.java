package com.redhat.helpers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class JsonNodeHelper {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final JsonFactory factory = mapper.getFactory();
    public static final String INITIAL_ACRONYM = "FOO1";
    public static final String EXPLANATION = "BAR1";
    public static final String EXPLANATION_UPDATE = "BAR2";
    public static final String NON_EXISTENT_EXPLANATION = "BAR3";

    // Incorrect inputs
    private static final String INCORRECT_ACRONYM = "ACRONYM";
    private static final String INCORRECT_ACRONYM_SAVE = "!" + INCORRECT_ACRONYM + "  ";
    private static final String INCORRECT_ACRONYM_SAVE_WITH_EQUALS = "! " + INCORRECT_ACRONYM + " =   ";

    // Correct update inputs
    private static final String UPDATE_ACRONYM = "! " + INITIAL_ACRONYM + " = " + EXPLANATION_UPDATE;
    private static final String UPDATE_OLD_EXPLANATION = "!" + INITIAL_ACRONYM + "  =   " + EXPLANATION + "=>" + EXPLANATION_UPDATE;

    // Delete inputs
    private static final String DELETE_EXPLANATION = "!" + INITIAL_ACRONYM + "   =    "+ EXPLANATION_UPDATE + "=>";
    private static final String DELETE_ORIGINAL_EXPLANATION = "!" + INITIAL_ACRONYM + "    =    "+ EXPLANATION + "=>";

    // Setup acronym
    private static final String SETUP_DB_ACRONYM = "! " +  INITIAL_ACRONYM + "  =  " + EXPLANATION;

    // Misc
    private static final String NON_STANDARD_EMAIL = "rbecky@example.com";

    public static JsonNode getJsonNodeWithoutMessageArgumentText() {
        try {
            JsonParser parser = factory.createParser(getJsonStringNoArgumentText());
            return mapper.readTree(parser);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JsonNode getHelpRequest() {
        return alterArgumentText("help");
    }

    public static JsonNode getSetupDb() {
        return alterArgumentText(SETUP_DB_ACRONYM);
    }

    public static JsonNode getInitialAcronymLowercase() {
        return alterArgumentText(INITIAL_ACRONYM.toLowerCase());
    }

    public static JsonNode getInitialAcronymUppercase() {
        return alterArgumentText(INITIAL_ACRONYM);
    }

    public static JsonNode updateInitialAcronym() {
        return alterArgumentText(UPDATE_ACRONYM);
    }

    public static JsonNode getIncorrectAcronymSave() {
        return alterArgumentText(INCORRECT_ACRONYM_SAVE);
    }

    public static JsonNode getIncorrectAcronymSaveWithEquals() {
        return alterArgumentText(INCORRECT_ACRONYM_SAVE_WITH_EQUALS);
    }

    public static JsonNode updateAcronymExplanationSameEmail() {
        return alterArgumentText(UPDATE_OLD_EXPLANATION);
    }

    public static JsonNode deleteUpdatedAcronymExplanationSameEmail() {
        return alterArgumentText(DELETE_EXPLANATION);
    }

    public static JsonNode deleteAcronymInitialExplanation() {
        return alterArgumentText(DELETE_ORIGINAL_EXPLANATION);
    }

    public static JsonNode getUpdateAcronymExplanationDifferentUser() {
        return alterUser(updateAcronymExplanationSameEmail(), NON_STANDARD_EMAIL);
    }

    public static JsonNode getDeleteAcronymInitialExplanationDifferentUser() {
        return alterUser(deleteAcronymInitialExplanation(), NON_STANDARD_EMAIL);
    }

    public static JsonNode updateNonExistentExplanation() {
        return alterArgumentText("!" + INITIAL_ACRONYM + "=" + NON_EXISTENT_EXPLANATION + "=>");
    }

    public static JsonNode getIncorrectAcronym() {
        return alterArgumentText(INCORRECT_ACRONYM);
    }

    public static ObjectNode alterArgumentText(String argumentTextString) {
        JsonNode node = JsonNodeHelper.getJsonNodeWithoutMessageArgumentText();
        ObjectNode nodeHelpText = ((ObjectNode) node);
        ObjectNode messageNode = (ObjectNode) nodeHelpText.get("message");
        messageNode.put("argumentText", argumentTextString);
        nodeHelpText.set("message", messageNode);
        return nodeHelpText;
    }

    private static ObjectNode alterUser(JsonNode node, String user) {
        ObjectNode nodeHelpText = ((ObjectNode) node);
        ObjectNode userNode = (ObjectNode) nodeHelpText.get("user");
        userNode.put("email", user);
        nodeHelpText.set("user", userNode);
        return nodeHelpText;
    }

    public static JsonNode getDatabaseExceptionCausingMessage() {
        return alterArgumentText("!" + INITIAL_ACRONYM + "=" + getTooLongString());
    }

    private static String getTooLongString() {
        StringBuilder sb = new StringBuilder();
        while(sb.length() < 256) {
            sb.append("AAAA AAAA AAAAAA AAAA AAAA AAAAA");
        }
        return sb.toString();
    }

    private static String getJsonStringNoArgumentText() {
        return "{\n" +
                "    \"type\": \"MESSAGE\",\n" +
                "    \"eventTime\": \"2019-05-16T06:58:32.030735Z\",\n" +
                "    \"message\": {\n" +
                "        \"name\": \"spaces/a\",\n" +
                "        \"sender\": {\n" +
                "            \"name\": \"users/test\",\n" +
                "            \"displayName\": \"John Doe\",\n" +
                "            \"avatarUrl\": \"https://lh3.googleusercontent.com/a-/handsomeFella.jpg\",\n" +
                "            \"email\": \"jdoe@example.com\",\n" +
                "            \"type\": \"HUMAN\"\n" +
                "        },\n" +
                "        \"createTime\": \"2019-05-16T06:58:32.030735Z\",\n" +
                "        \"text\": \"@Acrobot\",\n" +
                "        \"annotations\": [\n" +
                "            {\n" +
                "                \"type\": \"USER_MENTION\",\n" +
                "                \"startIndex\": 0,\n" +
                "                \"length\": 13,\n" +
                "                \"userMention\": {\n" +
                "                    \"user\": {\n" +
                "                        \"name\": \"users/bb\",\n" +
                "                        \"displayName\": \"Acrobot\",\n" +
                "                        \"avatarUrl\": \"https://lh6.googleusercontent.com/proxy/botAwesomeness.jpeg\",\n" +
                "                        \"type\": \"BOT\"\n" +
                "                    },\n" +
                "                    \"type\": \"ADD\"\n" +
                "                }\n" +
                "            }\n" +
                "        ],\n" +
                "        \"thread\": {\n" +
                "            \"name\": \"spaces/aaa/threads/bbb\",\n" +
                "            \"retentionSettings\": {\n" +
                "                \"state\": \"PERMANENT\"\n" +
                "            }\n" +
                "        },\n" +
                "        \"space\": {\n" +
                "            \"name\": \"spaces/aaa\",\n" +
                "            \"type\": \"ROOM\",\n" +
                "            \"displayName\": \"Red Hat Goodness\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"user\": {\n" +
                "        \"name\": \"users/test\",\n" +
                "        \"displayName\": \"John Doe\",\n" +
                "        \"avatarUrl\": \"https://lh3.googleusercontent.com/a-/handsomeFella.jpg\",\n" +
                "        \"email\": \"jdoe@example.com\",\n" +
                "        \"type\": \"HUMAN\"\n" +
                "    },\n" +
                "    \"space\": {\n" +
                "        \"name\": \"spaces/AAAA9H9KKTo\",\n" +
                "        \"type\": \"ROOM\",\n" +
                "        \"displayName\": \"Red Hat Goodness\"\n" +
                "    }\n" +
                "}\n";
    }
}
