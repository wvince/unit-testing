package victor.testing.filebased.export;

import java.io.IOException;
import java.io.Writer;
import java.time.format.DateTimeFormatter;

public class PersonExporter {
   private final PersonRepo personRepo;

   public PersonExporter(PersonRepo personRepo) {
      this.personRepo = personRepo;
   }

   // This legacy code is in production for 7 years. NOt tests. ofc.
   // So, it has NO bugs, 🤞
   //    but your task is to change it... 😱
   // Ofc, you want to make sure you don't introduce bugs.

   // So: (** CHARACTERIZATION TESTS **)
   // - You capture its current output, you save it in in/out files
   // - Find inputs to go "everywhere" (Line coverage helps here)
   // - Save actual current output as files

   // Then, when refactoring/evolving it, the tests should stay green.
   public void export(Writer writer) throws IOException {
      writer.write("full_name;phones;birth_date\n");
      for (Person person : personRepo.findAll()) {
         writer.write(person.getFirstName() + " " + person.getLastName().toUpperCase());
         writer.write(";");
         if (!person.getPhoneList().isEmpty()) {
            writer.write( person.getPhoneList().get(0)); // TODO fix bug: what if no phone?

         }
//         writer.write(String.join(",", person.getPhoneList())); // TODO CR: output all phones comma-separated
         writer.write(";");
         writer.write(person.getBirthDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))); // TODO CR: change format to "12 Nov 2021"
         writer.write("\n");
      }
   }
}
