package com.endava.workshop;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

@Slf4j
public class Generator {

  public static void main(String[] args) {
    Generator gen = new Generator();
    gen.run();
  }

  private void run() {
    log.info("Loading file to parse...");
    CharStream stream = createStream(new String[]{"/input/com/endava/workshop/avro/LogEvent.java"});
    Java8Lexer java8Lexer = new Java8Lexer(stream);
    CommonTokenStream tokens = new CommonTokenStream(java8Lexer);
    Java8Parser parser = new Java8Parser(tokens);
    log.info("Parse the loaded file...");
    ParseTree parseTree = parser.compilationUnit();
    ParseTreeWalker walker = new ParseTreeWalker();
    log.info("Process the loaded file...");
    GenerateAdditionalCode modelGenerator = new GenerateAdditionalCode(parser);
    walker.walk(modelGenerator, parseTree);
    log.info("Generated model:\n{}", modelGenerator.getClassDefinition());
    log.info("Generate Typescript:\n{}", generateTypeScript(modelGenerator.getClassDefinition()));
    log.info("Generate Entity:\n{}", generateEntity(modelGenerator.getClassDefinition()));
  }

  private String generateTypeScript(ClassDefinition classDefinition) {
    return "interface " + classDefinition.getClassName() + "{\n" +
        generateTypeScriptMembers(classDefinition) +
        "}"
        ;
  }

  private String generateTypeScriptMembers(ClassDefinition classDefinition) {
    return classDefinition.getMemberFields().stream()
        .map(field -> "\t" + field.getName() + ":" + field.getType().toTypeScript())
        .collect(Collectors.joining(",\n")) + "\n";
  }

  private String generateEntity(ClassDefinition classDefinition) {
    return "package " + classDefinition.getPackageName() + ";\n" +
        "@Entity\n" +
        "@Table(\"" + classDefinition.getClassName().toLowerCase() + "\")\n" +
        "class " + classDefinition.getClassName() + "{\n" +
        generateEntityMembers(classDefinition) +
        "}"
        ;
  }

  private String generateEntityMembers(ClassDefinition classDefinition) {
    return classDefinition.getMemberFields().stream()
        .map(
            field -> "\t" + "@Column(name=\"" + field.getName().toLowerCase() + "\")\n" +
                "\tpublic " + field.getType().toJava() + " " + field.getName()
        )
        .collect(Collectors.joining(";\n")) + "\n";
  }

  private CharStream createStream(String[] args) {
    String inputFilePath = null;
    if (args.length == 1) {
      inputFilePath = args[0];
    }
    InputStream in = System.in;
    if (inputFilePath == null) {
      System.out.println("[WARNING] Reading input from System Input...");
    } else {
      in = this.getClass().getResourceAsStream(inputFilePath);
    }
    try {
      return CharStreams.fromStream(in);
    } catch (IOException e) {
      return CharStreams.fromString("");
    }
  }
}
