package ro.victor.unittest.spring.feed;

import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@Primary
@Profile("!test-real-fs")
public class FileRepoDummy implements IFileRepo{
   private final Map<String, String> fileContents = new HashMap<>();

   public void addTestFile(String fileName, String contents) {
      fileContents.put(fileName, contents);
   }

   public void cleanFiles() {
      fileContents.clear();
   }
   @Override
   public Set<String> getFileNames() {
      return fileContents.keySet();
   }

   @Override
   public InputStream openFile(String fileName) {
      return IOUtils.toInputStream(fileContents.get(fileName));
   }
}
