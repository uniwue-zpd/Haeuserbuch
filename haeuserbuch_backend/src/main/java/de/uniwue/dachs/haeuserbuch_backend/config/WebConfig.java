package de.uniwue.dachs.haeuserbuch_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${spring.config.files.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = ResourceUtils.FILE_URL_PREFIX;
        if (uploadDir.endsWith("/")) {
            location += uploadDir;
        } else {
            location += uploadDir + "/";
        }

        registry
                .addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }
}
