package sk.catheaven.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import sk.catheaven.core.cpu.CPU;
import sk.catheaven.core.cpu.Component;
import sk.catheaven.core.cpu.Connector;
import sk.catheaven.model.cpu.CPUImpl;
import sk.catheaven.model.cpu.ComponentImpl;
import sk.catheaven.model.cpu.components.CPUBase;
import sk.catheaven.service.Assembler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class CPUConfig {
    private static Logger log = LogManager.getLogger();

    @Bean
    public int bitSize(@Qualifier("cpuRootNode") JsonNode cpuRootNode) {
        return cpuRootNode.get("CPU").get("BIT_SIZE").asInt();
    }

    @Bean
    @Primary
    public Map<String, List<Connector>> connectorsMap(ObjectMapper objectMapper,
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
    @Primary
    public Map<String, Component> componentsMap(ObjectMapper objectMapper,
                                                  @Qualifier("cpuRootNode")  JsonNode cpuRootNode) throws JsonProcessingException {
        Map<String, Component> componentMap = new LinkedHashMap<>();
        for (JsonNode node : cpuRootNode.path("CPU").path("components")) {
            ComponentImpl component = objectMapper.treeToValue(node, ComponentImpl.class);
            componentMap.put(component.getLabel(), component);
        }
        return componentMap;
    }

    @Bean
    public CPU cpu(int bitSize,
                   Map<String, List<Connector>> connectorsMap,
                   Map<String, Component> componentsMap,
                   Assembler assembler) {
        CPUBase cpuBase = new CPUBase(bitSize, componentsMap, connectorsMap);
        return new CPUImpl(cpuBase, assembler);
    }

//    @Bean
//    public RegBank registerBank(Map<String, Component> componentsMap) {
//        for (Component component : componentsMap.values())
//            if (component instanceof RegBank)
//                return (RegBank) component;
//        throw new RuntimeException("Register bank not found");
//    }

}
