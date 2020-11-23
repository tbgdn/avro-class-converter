package com.endava.workshop;

public enum FieldMemberDataType {
  INTEGER,
  DATE,
  DATE_TIME,
  STRING;

  public static FieldMemberDataType fromAvro(String type) {
    switch (type) {
      case "java.time.LocalDate":
        return DATE;
      case "java.time.LocalTime":
        return DATE_TIME;
      case "int":
        return INTEGER;
      case "java.lang.CharSequence":
      default:
        return STRING;
    }
  }

  public String toTypeScript() {
    switch (this){
      case DATE:
        return "Date";
      case DATE_TIME:
        return "Date";
      case INTEGER:
        return "number";
      case STRING:
      default:
        return "string";
    }
  }

  public String toJava() {
    switch (this){
      case DATE:
        return "java.time.LocalDate";
      case DATE_TIME:
        return "java.time.LocalTime";
      case INTEGER:
        return "java.lang.Integer";
      case STRING:
      default:
        return "java.lang.String";
    }
  }
}
