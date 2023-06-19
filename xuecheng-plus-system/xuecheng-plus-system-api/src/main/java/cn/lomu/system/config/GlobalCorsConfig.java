package cn.lomu.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author LoMu
 * Date  2023-05-05 17:58
 */

@Configuration
public class GlobalCorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //允许白名单来源跨域访问
        corsConfiguration.addAllowedOrigin("*");
        //允许跨域发送cookie
        corsConfiguration.setAllowCredentials(true);
        //允许全部原始请求头
        corsConfiguration.addAllowedHeader("*");
        //允许所有请求方法跨域调用
        corsConfiguration.addAllowedHeader("*");


        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}
