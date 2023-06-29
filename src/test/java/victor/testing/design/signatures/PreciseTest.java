package victor.testing.design.signatures;

import org.junit.jupiter.api.Test;
import victor.testing.tools.CaptureSystemOutput;
import victor.testing.tools.CaptureSystemOutput.OutputCapture;

import static org.assertj.core.api.Assertions.assertThat;

class PreciseTest {
   @Test
   @CaptureSystemOutput
   // IMAGINE MANY TESTS LIKE THIS
   void sendSprintFinishedEmail(OutputCapture outputCapture) {
//      Project project = new Project("WTF?!", "WHY NAME"); // WHY???
//      project.setPoEmail("boss@my.corp");

      Precise.sendSprintFinishedEmail("boss@my.corp");

      assertThat(outputCapture.toString())
          .isEqualToIgnoringNewLines(
              "Sending email to boss@my.corp with subject " +
              "'Sprint Finished' and some body");
   }
}