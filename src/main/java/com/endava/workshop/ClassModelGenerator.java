package com.endava.workshop;

import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

@Slf4j
public class ClassModelGenerator {

  public static void main(String[] args) throws IOException {
    ClassModelGenerator gen = new ClassModelGenerator();
    gen.run();
  }

  public void run() throws IOException {
    log.info("Loading file to parse...");
    CharStream stream = createStream(new String[]{"/input/com/endava/workshop/avro/LogEvent.java"});
    Java8Lexer java8Lexer = new Java8Lexer(stream);
    CommonTokenStream tokens = new CommonTokenStream(java8Lexer);
    Java8Parser parser = new Java8Parser(tokens);
    log.info("Parse the loaded file...");
    ParseTree parseTree = parser.compilationUnit();
    ParseTreeWalker walker = new ParseTreeWalker();
    log.info("Process the loaded file...");
    AvroClassProcessor modelGenerator = new AvroClassProcessor();
    walker.walk(modelGenerator, parseTree);
    log.info("Generated model:\n{}", modelGenerator.getClassDefinition());
    log.info("Generate typescript");
    new TypeScriptGenerator().generateFile(modelGenerator.getClassDefinition());
    log.info("Generate entity");
    new JavaEntityGenerator().generateFile(modelGenerator.getClassDefinition());

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
