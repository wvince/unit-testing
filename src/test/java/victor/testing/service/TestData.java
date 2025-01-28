package victor.testing.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import victor.testing.entity.Address;
import victor.testing.entity.Customer;

public class TestData {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public static Customer aCustomerJson() {
        return objectMapper.readValue(TestData.class.getResource("/canonical/customer.json"), Customer.class);
    }

    static Customer aValidCustomerForEveryone() {
        return new Customer()
                .setName("::name::")
                .setEmail("::email::")
                .setAddress(new Address()
                        .setCity("::city::"));
    }
}
