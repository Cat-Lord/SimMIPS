package sk.catheaven.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class TupleDeserializer extends StdDeserializer<Tuple<?,?>> {
    private static Logger log = LogManager.getLogger();
    private JsonNode node;
    
    public TupleDeserializer() {
        super(Tuple.class);
    }
    
    @Override
    public Tuple<?,?> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        var key = node.fields().next().getKey();
        var value = node.get(key).asText();
        
        return new Tuple<>(key, value);
    }
}