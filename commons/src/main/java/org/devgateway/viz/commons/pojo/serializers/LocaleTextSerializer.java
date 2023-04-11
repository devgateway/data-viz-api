package org.devgateway.viz.commons.pojo.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.devgateway.viz.commons.domain.LocaleText;

import java.io.IOException;
import java.util.List;

public class LocaleTextSerializer extends StdSerializer<List<LocaleText>> {


    protected LocaleTextSerializer(Class<List<LocaleText>> t) {
        super(t);
    }

    protected LocaleTextSerializer() {
        this(null);
    }


    @Override
    public void serialize(final List<LocaleText> localeTexts, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartObject();
//        List<String> machineTranslated = new ArrayList<>();
        for (LocaleText localeText : localeTexts) {
//            if (localeText.isMachineTranslation()) {
//                machineTranslated.add(localeText.getLanguage().getCode());
//            }
            jsonGenerator.writeStringField(localeText.getLanguage().getCode(), localeText.getText());
        }

//        jsonGenerator.writeArrayFieldStart("machine");
//        for (String s : machineTranslated) {
//            jsonGenerator.writeString(s);
//        }
//        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();


    }


}
