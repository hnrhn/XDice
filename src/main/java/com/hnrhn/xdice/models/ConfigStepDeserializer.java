package com.hnrhn.xdice.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.hnrhn.xdice.enums.ConfigStep;
import com.hnrhn.xdice.enums.ConfigStepHelper;

import java.io.IOException;

public class ConfigStepDeserializer extends JsonDeserializer<ConfigStep> {
    @Override
    public ConfigStep deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return ConfigStepHelper.toEnum(p.readValueAs(Integer.class));
    }
}
