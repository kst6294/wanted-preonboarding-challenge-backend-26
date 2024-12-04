package wanted.market.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import wanted.market.login.interceptor.LoginCheckInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/webjars/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/",
                        "/member/signup",
                        "/login/**",
                        "/error",
                        "/item/items",
                        "/item/info",
                        "/portone/**",
                        "/member/reset/**",
                        "/favicon.ico"
                );
    }
}
