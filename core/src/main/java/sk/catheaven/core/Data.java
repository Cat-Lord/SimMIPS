package sk.catheaven.core;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import sk.catheaven.model.DataImpl;

@JsonDeserialize(as = DataImpl.class)
public interface Data {
    void setBitSize(int bitSize);

    Tuple<Integer, Integer> getRange();

    // integer values get loaded by jackson as strings
    void setRange(Tuple<?, ?> range);

    void setData(int data);

    void setData(Data data);

    int getData();

    int getMask();

    int getBitSize();

    Data newInstance();

}
