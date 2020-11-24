package com.endava.workshop;

import com.endava.workshop.Java8Parser.FieldDeclarationContext;
import com.endava.workshop.Java8Parser.NormalClassDeclarationContext;
import com.endava.workshop.Java8Parser.PackageDeclarationContext;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

@Slf4j
@Getter
public class AvroClassProcessor extends Java8BaseListener {

  private ClassDefinition classDefinition;

  public AvroClassProcessor() {
    this.classDefinition = ClassDefinition.builder().build();
  }

  @Override
  public void enterPackageDeclaration(PackageDeclarationContext ctx) {
    String packageName = ctx.Identifier().stream().map(ParseTree::getText).collect(Collectors.joining("."));
    this.classDefinition = this.classDefinition.toBuilder().packageName(packageName).build();
  }

  @Override
  public void enterNormalClassDeclaration(NormalClassDeclarationContext ctx) {
    String superClassName = ctx.superclass().getText();
    if ("extendsorg.apache.avro.specific.SpecificRecordBase".equals(superClassName)) {
      String className = ctx.Identifier().getText();
      this.classDefinition = this.classDefinition.toBuilder().className(className).build();
    }
  }

  @Override
  public void enterFieldDeclaration(FieldDeclarationContext ctx) {
    super.enterFieldDeclaration(ctx);
    if (isExcluded(ctx)) {
      //  ignore this
    } else {
      String type = ctx.unannType().getText();
      String fieldName = ctx.variableDeclaratorList().getText();
      FieldMemberDefinition fieldMemberDefinition = FieldMemberDefinition.builder()
          .name(fieldName)
          .type(FieldMemberDataType.fromAvro(type))
          .build();
      List<FieldMemberDefinition> fields = Stream.concat(
          this.classDefinition.getMemberFields().stream(),
          Stream.of(fieldMemberDefinition)
      ).collect(Collectors.toList());
      this.classDefinition = this.classDefinition.toBuilder()
          .memberFields(fields)
          .build();
      log.info("Generate field: " + type + " " + fieldName);
    }
  }

  private boolean isExcluded(FieldDeclarationContext ctx) {
    Set<String> modifiers = ctx.fieldModifier()
        .stream()
        .map(RuleContext::getText)
        .map(String::toLowerCase)
        .collect(Collectors.toSet());
    return modifiers.contains("private") || modifiers.contains("static") || modifiers.contains("final");
  }
}
