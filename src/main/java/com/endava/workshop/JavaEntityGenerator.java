package com.endava.workshop;

import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.stream.Collectors;

public class JavaEntityGenerator {

  public void generateFile(ClassDefinition definition) throws IOException {
    String ts = generate(definition);
    File tsFile = new File("LogEvent.java");
    Files.asCharSink(tsFile, Charset.defaultCharset(), FileWriteMode.APPEND).write(ts);
  }

  private String generate(ClassDefinition definition) {
    return "package " + definition.getPackageName() + ".entity;\n" +
        "import javax.persistence.Column;\n" +
        "import javax.persistence.Entity;\n" +
        "import javax.persistence.Table;\n" +
        "@Entity\n" +
        "@Table(name=\"" + definition.getClassName().toLowerCase() + "\")\n" +
        "class " + definition.getClassName() + "{\n" +
        generateEntityMembers(definition) +
        "}"
        ;
  }

  private String generateEntityMembers(ClassDefinition classDefinition) {
    return classDefinition.getMemberFields().stream()
        .map(
            field -> "\t" + "@Column(name=\"" + field.getName().toLowerCase() + "\")\n" +
                "\tpublic " + field.getType().toJava() + " " + field.getName() + ";\n"
        )
        .collect(Collectors.joining("")) + "\n";
  }
}
