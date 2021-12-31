package sk.catheaven.main;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.catheaven.ui.controllers.PopOverFactory;

@Configuration
public class Config {

    @Bean
    public PopOverFactory popoverFactory() {
        return new PopOverFactory();
    }
}
