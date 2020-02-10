package eu.seal.linking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication(scanBasePackages =  { "eu.seal.linking" })
public class LinkServiceApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(LinkServiceApplication.class, args);
    }
}
