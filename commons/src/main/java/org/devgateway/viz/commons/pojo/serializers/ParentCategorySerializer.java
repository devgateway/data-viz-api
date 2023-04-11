package org.devgateway.viz.commons.pojo.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.devgateway.viz.commons.domain.Category;

import java.io.IOException;

public class ParentCategorySerializer extends StdSerializer<Category> {

    protected ParentCategorySerializer(Class<Category> t) {
        super(t);
    }

    public ParentCategorySerializer() {
        this(null);
    }

    @Override
    public void serialize(final Category category, final JsonGenerator jsonGenerator,
                          final SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(category.getValue());
    }
}
