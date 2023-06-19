package cn.lomu;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author LoMu
 * Date  2023-05-01 11:26
 */

@SpringBootApplication
@EnableFeignClients(basePackages = {"cn.lomu.content.feignClient"})
public class ServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }
}