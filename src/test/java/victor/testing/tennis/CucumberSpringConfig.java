package victor.testing.tennis;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

 @CucumberContextConfiguration //  from io.cucumber:cucumber-spring:7.0.0
@SpringBootTest(classes = CucumberTennisContext.class)
public class CucumberSpringConfig {

}
