package com.endava.workshop;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.apache.commons.compress.utils.Lists;

@Value
@Builder(toBuilder = true)
@NoArgsConstructor(force = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ClassDefinition {

  String packageName;
  String className;
  @Builder.Default
  List<FieldMemberDefinition> memberFields = Lists.newArrayList();

}
