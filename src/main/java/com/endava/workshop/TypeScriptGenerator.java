package com.endava.workshop;

import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.stream.Collectors;

public class TypeScriptGenerator {

  public void generateFile(ClassDefinition definition) throws IOException {
    String ts = generate(definition);
    File tsFile = new File("log-event.model.ts");
    Files.asCharSink(tsFile, Charset.defaultCharset(), FileWriteMode.APPEND).write(ts);
  }

  private String generate(ClassDefinition definition) {
    return "interface " + definition.getClassName() + "{\n" +
        generateTypeScriptMembers(definition) +
        "}"
        ;
  }

  private String generateTypeScriptMembers(ClassDefinition classDefinition) {
    return classDefinition.getMemberFields().stream()
        .map(field -> "\t" + field.getName() + ":" + field.getType().toTypeScript())
        .collect(Collectors.joining(",\n")) + "\n";
  }
}
