package victor.testing.time.rule;

import org.junit.rules.ExternalResource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TestTimeRule extends ExternalResource {

    private LocalDateTime testTime;

    public TestTimeRule() {
        this(LocalDateTime.now());
    }
    public TestTimeRule(LocalDateTime testTime) {
        this.testTime = testTime;
    }
    
    public void setTestTime(LocalDateTime testTime) {
        this.testTime = testTime;
    }
    public void setTestDate(LocalDate testDate) {
        this.testTime = LocalDateTime.of(testDate, LocalTime.MIDNIGHT);
    }

    public LocalDateTime getTestTime() {
        return testTime;
    }

    @Override
    protected void before() throws Throwable {
    	TimeProvider.setTestTime(testTime);
    }
    // in between runs each @Test
    @Override
    protected void after() {
        TimeProvider.clearTestTime();
    }
}