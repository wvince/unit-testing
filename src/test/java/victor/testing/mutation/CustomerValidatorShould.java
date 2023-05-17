package victor.testing.mutation;


import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class CustomerValidatorShould {
   CustomerValidator validator = new CustomerValidator();

   private static Customer aCustomer() { // generator de date de test
      // sau il citesti din JSON
      return new Customer()
          .setName("::name::")
          .setEmail("::email::")
          .setAddress(new Address()
              .setCity("::city::"));
   }

   @Test
   void valid() {
      Customer customer = aCustomer();
      validator.validate(customer);
   }

   @Test
   void throwForMissingName() {
      Customer customer = aCustomer().setName(null);
      assertThatThrownBy(()->validator.validate(customer))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("name");
   }
   @Test
   void throwForMissingEmail() {
      Customer customer = aCustomer().setEmail(null);
      assertThatThrownBy(()->validator.validate(customer))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("email");
   }

}