package sk.catheaven.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.io.InputStream;

@SpringBootConfiguration
@Import({ CPUConfig.class, InstructionsConfig.class })
public class AppConfig {

    @Bean
    public ObjectMapper getObjectMapper(){
        return new ObjectMapper();
    }

    @Bean(name = "cpuRootNode")
    public JsonNode getCpuRootNode(@Value("classpath:design/cpu.json") InputStream jsonResource,
                                   ObjectMapper objectMapper) throws IOException {
        return objectMapper.readTree(jsonResource);
    }

    @Bean(name = "instructionsRootNode")
    public JsonNode getInstructionsRootNode(@Value("classpath:design/instructions.json") InputStream jsonResource,
                                            ObjectMapper objectMapper) throws IOException {
        return objectMapper.readTree(jsonResource);
    }

}
