package com.redhat.constants;

public enum MessageType {
    ADDED_TO_ROOM(Constants.ADDED_RESPONSE),
    SUDO_RESPONSE("SUDO_PASSWORD"),
    INVALID_MESSAGE(Constants.INCORRECT_FORMAT_FOR_SAVING_ACRONYM),
    HELP(Constants.HELP_TEXT),
    UPDATE_OR_REMOVE("UPDATE_REMOVE"),
    SAVE_OR_MERGE("SAVE_MERGE"),
    GET_ACRONYM("GET_ACRONYM");

    public final String eventType;

    private MessageType(String eventType) {
        this.eventType = eventType;
    }
}
