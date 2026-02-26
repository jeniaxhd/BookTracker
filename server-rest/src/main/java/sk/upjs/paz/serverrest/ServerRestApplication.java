package sk.upjs.paz.serverrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ServerRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerRestApplication.class, args);
    }

}
