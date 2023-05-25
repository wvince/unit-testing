package victor.testing.design.spy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import wiremock.com.google.common.reflect.Reflection;

import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static victor.testing.design.spy.Order.PaymentMethod.CARD;
import static victor.testing.design.spy.Order.PaymentMethod.CASH_ON_DELIVERY;

class GodTest {
   private God god = new God();
   @Test
   void high() { // + 5 more tests like this
      String actual = god.high(new Order()
          .setPaymentMethod(CARD)
          .setCreationDate(now())// WHY>>!!> #LIFE IS NOT FAIR
      );

      assertThat(actual).isEqualTo("bonus");
   }
   // is it fair to have tests on public method FAIL because of the private method?
   // REMEMBER: I already fully tested the private one directly below
   @Test
   void testLowDirectly() {
      assertThatThrownBy(()-> god.low(new Order().setCreationDate(now().minusMonths(2))))
          .isInstanceOf(IllegalArgumentException.class);

   }
}