package com.endava.workshop;

import com.endava.workshop.Java8Parser.FieldDeclarationContext;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.RuleContext;

@Slf4j
@Getter
public class AvroClassProcessor extends Java8BaseListener {

  private static final String AVRO_BASE_CLASS = "org.apache.avro.specific.SpecificRecordBase";
  private ClassDefinition classDefinition;

  public AvroClassProcessor() {
    this.classDefinition = ClassDefinition.builder().build();
  }

  private void addPackage(String packageName) {
    this.classDefinition = this.classDefinition.toBuilder().packageName(packageName).build();
  }

  private void addClassName(String className) {
    this.classDefinition = this.classDefinition.toBuilder().className(className).build();
  }

  private void addField(String type, String fieldName) {
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
