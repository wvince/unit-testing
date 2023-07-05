package victor.testing.design.spy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static victor.testing.design.spy.Order.PaymentMethod.CARD;

@ExtendWith(MockitoExtension.class)
class GodTest {
//   God god = spy(God.class);
   @Spy
   God god;

   @Test
   void high() { // + 5 more tests like this
      Order order = new Order().setPaymentMethod(CARD);
      // from calling low() since we already tested that
      // Optiunea #1
//      order.setCreationDate(now().minusMonths(2));
      // Optiunea #2 - RAU: partial mocks (spy) ==> break class
      doNothing().when(god).low(any()); // blochez apel in aceeasi clasa testata

      String result = god.high(order);

      assertThat(result).isEqualTo("bonus");
   }

   @Test
   void throwsForOrderTooRecent() {
      Order recentOrder = new Order().setCreationDate(now());
      assertThatThrownBy(() -> god.low(recentOrder));
   }
   @Test
   void throwsForOrderTooRecent7() {
      Order recentOrder = new Order().setCreationDate(now());
      assertThatThrownBy(() -> god.low(recentOrder));
   }

}