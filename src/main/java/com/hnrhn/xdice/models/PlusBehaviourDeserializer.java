package com.hnrhn.xdice.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.hnrhn.xdice.enums.PlusBehaviour;
import com.hnrhn.xdice.enums.PlusBehaviourHelper;

import java.io.IOException;

public class PlusBehaviourDeserializer extends JsonDeserializer<PlusBehaviour> {
    @Override
    public PlusBehaviour deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return PlusBehaviourHelper.toEnum(p.readValueAs(Integer.class));
    }
}
