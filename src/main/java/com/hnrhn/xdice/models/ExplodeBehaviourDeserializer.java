package com.hnrhn.xdice.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.hnrhn.xdice.enums.ExplodeBehaviour;
import com.hnrhn.xdice.enums.ExplodeBehaviourHelper;

import java.io.IOException;

public class ExplodeBehaviourDeserializer extends JsonDeserializer<ExplodeBehaviour> {
    @Override
    public ExplodeBehaviour deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return ExplodeBehaviourHelper.toEnum(p.readValueAs(Integer.class));
    }
}
