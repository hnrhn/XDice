package com.hnrhn.xdice.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.hnrhn.xdice.enums.CritFailBehaviour;
import com.hnrhn.xdice.enums.CritFailBehaviourHelper;

import java.io.IOException;

public class CritFailBehaviourDeserializer extends JsonDeserializer<CritFailBehaviour> {
    @Override
    public CritFailBehaviour deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return CritFailBehaviourHelper.toEnum(p.readValueAs(Integer.class));
    }
}
