package victor.testing.design.time;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDate.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TimeLogic2Test {
  OrderRepo orderRepoMock = mock(OrderRepo.class);
  TimeProvider timeProviderMock = mock(TimeProvider.class);
  TimeLogic2 target = new TimeLogic2(orderRepoMock,timeProviderMock);

  @Test
  void isFrequentBuyer() {
    LocalDate testDay = parse("2023-01-08");
    LocalDate oneWeekAgo = parse("2023-01-01");
    Order order = new Order().setTotalAmount(130d);
    when(orderRepoMock.findByCustomerIdAndCreatedOnBetween(
        13, oneWeekAgo, testDay))
        .thenReturn(List.of(order));
    when(timeProviderMock.today()).thenReturn(testDay);

    assertThat(target.isFrequentBuyer(13)).isTrue();
  }
}
// Ways to control time from tests:
// - inject a Clock dependency, pass a fixed Clock from tests (see TimeUtils)
// - pass time as an argument to a package-protected method ("subcutaneous test")
// - mock the static method LocalDate.now()
// - inject an (Mock-able) object wrapping the static call: class TimeProvider { LocalDate today() {return LocalDate.now();} }
//   - variation: Supplier<LocalDate>
//   - variation: UUIDGenerator