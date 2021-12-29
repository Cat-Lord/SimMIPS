package sk.catheaven.core.instructions;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import sk.catheaven.model.instructions.FieldImpl;

@JsonDeserialize(as = FieldImpl.class)
public interface Field {
    String getLabel();

    int getBitSize();
}
