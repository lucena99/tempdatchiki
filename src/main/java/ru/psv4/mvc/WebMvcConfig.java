package ru.psv4.mvc;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // Static Resource Config
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Css resource.
        registry.addResourceHandler("/css/**") //
                .addResourceLocations("/WEB-INF/resources/css/").setCachePeriod(31556926);

        // js resource.
        registry.addResourceHandler("/js/**") //
                .addResourceLocations("/WEB-INF/resources/js/").setCachePeriod(31556926);

        // js resource.
        registry.addResourceHandler("/js/locales/**") //
                .addResourceLocations("/WEB-INF/resources/js/locales").setCachePeriod(31556926);

    }
}