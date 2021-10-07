package vetcilinicservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "vetcilinicservice.Repositories._elastic")
public class VetCilinicServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VetCilinicServiceApplication.class, args);
    }

}
