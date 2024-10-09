package victor.testing;


import org.assertj.core.api.*;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftlyExtension;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;
import victor.testing.tools.TimeExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Disabled("AssertJ - enable on demand and compare failures of JUnit❌ and AssertJ💖")
public class AssertJ { // from org.assertj:assertj-core, or via spring-boot-starter-test

  @Nested
  @TestMethodOrder(MethodName.class)
  public class CollectionsPrimitives {
    List<Integer> aList = List.of(100, 200, 300, 300);

    @Test
    public void size1_JUnit() {
      assertEquals(1, aList.size());
    }

    @Test
    public void size1_AssertJ() {
      assertThat(aList).hasSize(1);
    }

    @Test
    public void onceInAnyOrder_JUnit() {
      assertTrue(aList.containsAll(List.of(100, 200, 700)));
      assertEquals(3, aList.size());
    }

    @Test
    public void onceInAnyOrder_AssertJ() {
      assertThat(aList).containsExactlyInAnyOrder(100, 200, 300);
    }

    @Test
    public void contains_JUnit() {
      assertTrue(aList.containsAll(List.of(100, 200, 400)));
      assertFalse(aList.contains(500));
    }

    @Test
    public void contains_AssertJ() {
      assertThat(aList)
          .contains(100, 200, 400)
          .doesNotContain(500);
    }

  }

  @Nested
  @TestMethodOrder(MethodName.class)
  public class CollectionsElements {
    record Character(String name, int age, Race race) {
    }

    record Race(String name) {
    }

    private final List<Character> fellowship = List.of(
        new Character("Frodo", 20, new Race("Hobbit")),
        new Character("Sam", 18, new Race("Hobbit")),
        new Character("Legolas", 1000, new Race("Elf")),
        new Character("Boromir", 37, new Race("Man")),
        new Character("Gandalf the Gray", 120, new Race("Man")),
        new Character("Aragorn", 39, new Race("Man")),
        new Character("Gimli", 40, new Race("Dwarf"))
    );

    @Test
    public void oneAttribute_JUnit() {
      // preprocess the collection before the assertion:
      Set<String> races = fellowship.stream()
          .map(Character::race)
          .map(Race::name)
          .collect(toSet());

      assertEquals(Set.of("Man", "Dwarf", "Elf", "Hobbit", "Orc"), races);
    }

    @Test
    public void oneAttribute_AssertJ() {
      assertThat(fellowship)
          .map(Character::race)
          .map(Race::name)
          .containsOnly("Man", "Dwarf", "Elf", "Hobbit", "Orc");
    }

    @Test
    public void multipleAttributes_JUnit() {
      assertTrue(fellowship.stream().anyMatch(c ->
          c.name().equals("Frodo") &&
          c.age() == 20 &&
          c.race().name().equals("Hobbit")));
      assertTrue(fellowship.stream().anyMatch(c ->
          c.name().equals("Aragorn") &&
          c.age() == 39 &&
          c.race().name().equals("Man")));
      assertTrue(fellowship.stream().anyMatch(c ->
          c.name().equals("Legolas") &&
          c.age() == 100 && // ❌
//          c.age() == 1000 && // ✅
          c.race().name().equals("Elf")));
    }

    @Test
    public void multipleAttributes_AssertJ() {
      assertThat(fellowship)
          .extracting("name", "age", "race.name") // alternative
//          .extracting(Character::name, Character::age, c -> c.race().name())
          .contains(
              tuple("Frodo", 20, "Hobbit"),
              tuple("Legolas", 100, "Elf"), // ❌
//              tuple("Legolas", 1000, "Elf"), // ✅
              tuple("Aragorn", 39, "Man")
          );
    }
  }

  @Nested
  @TestMethodOrder(MethodName.class)
  public class Strings {
    private final String string = "abcdef";

    @Test
    public void startsWith_JUnit() {
      assertTrue(string.startsWith("bcd")); // see the failure message
    }

    @Test
    public void startsWith_AssertJ() {
      assertThat(string).startsWith("bcd"); // see the failure message
    }

    @Test
    public void ignoreCase_JUnit() {
      assertEquals("ABCDE", string.toUpperCase()); // looses the original case
    }

    @Test
    public void ignoreCase_AssertJ() {
      assertThat(string).isEqualToIgnoringCase("AbCdE");
    }

    @Test
    public void regex_JUnit() {
      assertTrue(string.matches(".*bd.*"));
    }

    @Test
    public void regex_AssertJ() {
      assertThat(string)
          .matches(".*bd.*"); // means "contains 'bd'"
    }
  }

  @Nested
  @TestMethodOrder(MethodName.class)
  public class Time {
    private final LocalDateTime oneMinAgo = now().minusMinutes(1);

    @Test
    public void timeDelta_JUnit() {
      assertTrue(oneMinAgo.isAfter(now().minusSeconds(1)));
    }

    @Test
    public void timeDelta_AssertJ() {
      assertThat(oneMinAgo).isCloseTo(now(), byLessThan(1, SECONDS));
    }


    @RegisterExtension
    TimeExtension timeExtension = new TimeExtension("2023-12-25");

    @Test
    public void fixedTime() {
      // when
      LocalDate today = testedCode();

      assertThat(today).isEqualTo("2023-12-25");
    }

    @Test
    public void fixedTime2() {
      // when
      LocalDate today = testedCode();

      assertThat(today).isEqualTo("2023-12-25");
    }

    private static LocalDate testedCode() { // deep in a dark library
      return LocalDate.now();
    }
  }

  @Nested
  class SoftAssert {
    record Villa(int guests, String kitchen, String library) {
    }

    interface EventSender {
      void send(String event);
    }

    EventSender eventSender = Mockito.mock(EventSender.class);

    Villa testedCode() {
      return new Villa(6, "dirty", "clean");
    }

    // set this test on one thread
//    @Parallelizable(1)
    @Test
    void failsOnFirst_BAD() {
      Villa villa = testedCode();

      assertThat(villa)
//          .hasGuests(7) // java's desperate attempt to gain extension functions from Kt, Scala, etc. b
          .returns(7, Villa::guests)
          .returns("clean", Villa::kitchen)
          .returns("clean", Villa::library);
      Mockito.verify(eventSender).send("mansion-cleaned");
    }

//    public static CharacterAssert assertThat(Character actual) {
//      return new Assertions(actual) {
//        public static VillaAssert assertThat(AssertJ.SoftAssert.Villa actual) {
//          return new VillaAssert(actual);
//        }
//      };
//    }

    @Test
    void trySoftly() {
      Villa villa = testedCode();

//      assertThat(villa).hasGuests()
      try (var soft = new AutoCloseableSoftAssertions()) {
        soft.assertThat(villa.guests()).as("Living Guests").isEqualTo(7);
        soft.assertThat(villa.kitchen()).as("Kitchen").isEqualTo("clean");
        soft.assertThat(villa.library()).as("Library").isEqualTo("clean");
        soft.assertThatCode(() -> Mockito.verify(eventSender).send("mansion-cleaned"))
            .as("event published").doesNotThrowAnyException();
      }
    }
//    class VillaAssert extends AbstractAssert<C, Villa> {
//      protected VillaAssert(Villa actual, Class<?> selfType) {
//        super(actual, selfType);
//      }
//      public void hasGuests() {
//        assertThat(actual.guests()).as("Living Guests").isEqualTo(7);
//      }
//    }

    @Nested
    @ExtendWith(SoftlyExtension.class)
    class WithExtension {
      @InjectSoftAssertions
      SoftAssertions soft;

      @Test
      void usingExtension() {
        Villa villa = testedCode();

        soft.assertThat(villa.guests()).as("Living Guests").isEqualTo(7);
        soft.assertThat(villa.kitchen()).as("Kitchen").isEqualTo("clean");
        soft.assertThat(villa.library()).as("Library").isEqualTo("clean");
        soft.assertThatCode(() -> Mockito.verify(eventSender).send("mansion-cleaned"))
            .as("event published").doesNotThrowAnyException();
      }
    }
  }
}

