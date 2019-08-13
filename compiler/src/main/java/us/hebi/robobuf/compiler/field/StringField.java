package us.hebi.robobuf.compiler.field;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import us.hebi.robobuf.compiler.RequestInfo;
import us.hebi.robobuf.compiler.RuntimeClasses;

import javax.lang.model.element.Modifier;

/**
 * @author Florian Enner
 * @since 09 Aug 2019
 */
class StringField {

    static class OptionalStringField extends FieldGenerator {

        protected OptionalStringField(RequestInfo.FieldInfo info) {
            super(info);
            m.put("default", info.getDescriptor().getDefaultValue());
        }

        @Override
        public void generateMemberFields(TypeSpec.Builder type) {
            type.addField(FieldSpec.builder(RuntimeClasses.STRING_CLASS, info.getFieldName())
                    .addJavadoc(info.getJavadoc())
                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                    .initializer("new $T(0)", RuntimeClasses.STRING_CLASS)
                    .build());
        }

        @Override
        protected void generateSetter(TypeSpec.Builder type) {
            MethodSpec setter = MethodSpec.methodBuilder(info.getSetterName())
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(CharSequence.class, "value")
                    .returns(info.getParentType())
                    .addNamedCode("" +
                            "$setHas:L;\n" +
                            "$field:N.setLength(0);\n" +
                            "$field:N.append(value);\n" +
                            "return this;\n", m)
                    .build();
            type.addMethod(setter);
        }

        @Override
        public void generateClearCode(MethodSpec.Builder method) {
            method.addNamedCode("$field:N.setLength(0);\n", m);
            if (!info.getDescriptor().getDefaultValue().isEmpty()) {
                method.addNamedCode("$field:N.append($default:S);\n", m);
            }
        }

        @Override
        public void generateCopyFromCode(MethodSpec.Builder method) {
            method.addNamedCode("$field:N.setLength(0);\n", m);
            method.addNamedCode("$field:N.append(other);\n", m);
        }

        @Override
        public void generateMergingCode(MethodSpec.Builder method) {
            method.addNamedCode("" +
                    "input.readString($field:N);\n" +
                    "$setHas:L;\n", m);
        }

        @Override
        public void generateEqualsStatement(MethodSpec.Builder method) {
            method.addNamedCode("$roboUtil:T.equals($field:N, other.$field:N)", m);
        }

    }

    static class RepeatedStringField extends RepeatedReferenceField {

        protected RepeatedStringField(RequestInfo.FieldInfo info) {
            super(info);
        }

    }

}
