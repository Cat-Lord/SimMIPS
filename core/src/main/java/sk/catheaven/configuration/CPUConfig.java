package sk.catheaven.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.catheaven.core.Component;
import sk.catheaven.model.cpu.ComponentImpl;
import sk.catheaven.model.cpu.Connector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class CPUConfig {
    @Bean
    public int getBitSize(@Qualifier("cpuRootNode") JsonNode cpuRootNode) {
        return cpuRootNode.get("CPU").get("BIT_SIZE").asInt();
    }

    @Bean
    public Map<String, List<Connector>> getConnectorsMap(ObjectMapper objectMapper,
                                                         @Qualifier("cpuRootNode") JsonNode cpuRootNode) throws JsonProcessingException {
        JsonNode connectors = cpuRootNode.path("CPU").path("connectors");
        List<Connector> connectorList = objectMapper.readValue(connectors.toString(), new TypeReference<>() {});

        Map<String, List<Connector>> sourceToTargetConnectors = new HashMap<>();

        for (Connector connector : connectorList) {
            List<Connector> connectorsToTargetComponents = sourceToTargetConnectors.get(connector.getFrom());

            if (connectorsToTargetComponents == null) {
                List<Connector> connectedTo = new ArrayList<>();
                connectedTo.add(connector);
                sourceToTargetConnectors.put(connector.getFrom(), connectedTo);
                continue;
            }

            connectorsToTargetComponents.add(connector);
        }

        return sourceToTargetConnectors;
    }

    @Bean
    public Map<String, Component> getComponentMap(ObjectMapper objectMapper,
                                                  @Qualifier("cpuRootNode")  JsonNode cpuRootNode) throws JsonProcessingException {
        Map<String, Component> componentMap = new LinkedHashMap<>();
        for (JsonNode node : cpuRootNode.path("CPU").path("components")) {
            ComponentImpl component = objectMapper.treeToValue(node, ComponentImpl.class);
            componentMap.put(component.getLabel(), component);
        }
        return componentMap;
    }
}
