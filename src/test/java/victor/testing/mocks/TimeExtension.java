package victor.testing.mocks;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.mockito.MockedStatic;

import java.lang.reflect.Method;
import java.time.LocalDate;

import static org.mockito.Mockito.mockStatic;

public class TimeExtension implements InvocationInterceptor {
	private final LocalDate fixed;

  public TimeExtension(LocalDate fixed) {
    this.fixed = fixed;
  }
  public TimeExtension(String fixed) {
    this.fixed = LocalDate.parse(fixed);
  }
  @Override
	public void interceptTestMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
		try (MockedStatic<LocalDate> mock = mockStatic(LocalDate.class)) {
			mock.when(LocalDate::now).thenReturn(fixed);
			invocation.proceed();
		}
	}
}