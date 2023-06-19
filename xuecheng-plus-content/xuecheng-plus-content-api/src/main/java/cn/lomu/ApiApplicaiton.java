package cn.lomu;


import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author LoMu
 * Date  2023-05-01 11:09
 */

@EnableSwagger2Doc
@SpringBootApplication
public class ApiApplicaiton {
    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }
}