package victor.testing.designhints.signatures;

import org.junit.jupiter.api.Test;
import victor.testing.tools.CaptureSystemOutput;
import victor.testing.tools.CaptureSystemOutput.OutputCapture;

import static org.assertj.core.api.Assertions.assertThat;

class PreciseTest {
   @Test
   @CaptureSystemOutput
   void sendSprintFinishedEmail(OutputCapture outputCapture) {
      Project project = new Project();
      project.setPoEmail("boss@my.corp");

      Precise.sendSprintFinishedEmail(project);

      assertThat(outputCapture.toString()).isEqualTo("Sending email to boss@my.corp with subject 'Sprint Finished' and some body");
   }
}