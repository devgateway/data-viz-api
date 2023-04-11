package org.devgateway.viz.commons.pojo.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.devgateway.viz.commons.pojo.Response;

import java.io.IOException;

public class ResponseSerializer extends StdSerializer<Response> {


    protected ResponseSerializer(Class<Response> t) {
        super(t);
    }

    protected ResponseSerializer() {
        this(null);
    }


    @Override
    public void serialize(final Response response, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        if (response.getType() != null) {
            jsonGenerator.writeObjectField("type", response.getType());
        }
        if (response.getValue() != null) {
            jsonGenerator.writeObjectField("value", response.getValue());
        }
        if (response.getCategory() != null) {
            jsonGenerator.writeObjectField("category", response.getCategory());
        }

        if (response.getAttrValues() != null) {
            response.getAttrValues().forEach(attrValue -> {
                try {
                    jsonGenerator.writeObjectField(attrValue.getAttribute(), attrValue.getValue());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }


        if (response.getChildren() != null) {
            jsonGenerator.writeObjectField("children", response.getChildren());
        }
        jsonGenerator.writeEndObject();
    }


}
