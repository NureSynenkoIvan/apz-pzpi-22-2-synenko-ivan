package com.bastionserver.analysis.controller;

import com.fasterxml.jackson.databind.JsonNode;

public class IncomingMessageWrapper {
    private String type;
    private JsonNode payload;

    public String getType() { return type; }
    public JsonNode getPayload() { return payload; }
}