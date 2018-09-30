package com.lofiwang.androidpoet;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.util.HashMap;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by chunsheng.wang on 2018/9/25.
 */

public class PoetCodeUtil {

    /**
     * @param targetClass
     * @return field and field type
     */
    public static HashMap<String, TypeName> createFieldMap(TypeElement targetClass) {
        HashMap<String, TypeName> fieldMap = new HashMap<>();
        for (Element encloseElement : targetClass.getEnclosedElements()) {
            if (encloseElement.getKind() == ElementKind.FIELD) {
                String fieldName = encloseElement.getSimpleName().toString();
                TypeName fieldTypeName = TypeName.get(encloseElement.asType());
                fieldMap.put(fieldName, fieldTypeName);
            }
        }
        return fieldMap;
    }

    public static String createNewClazzName(TypeElement targetClass, String suffix) {
        String originClazzName = targetClass.getSimpleName().toString();
        return originClazzName + suffix;
    }

    public static ClassName createNewClazzType(String pkg, String newClazzName) {
        return ClassName.get(pkg, newClazzName);
    }

    public static String getTargetPkgName(TypeElement targetClass,Elements elementUtils) {
        PackageElement pkg = elementUtils.getPackageOf(targetClass);
        return pkg.getQualifiedName().toString();
    }

    public static MethodSpec createConstructMethod(Modifier... modifiers) {
        MethodSpec.Builder method = MethodSpec.constructorBuilder()
                .addModifiers(modifiers);
        return method.build();
    }

    public static MethodSpec createSetReturn(TypeName returnType, String fieldName, TypeName fieldType, Modifier... modifiers) {
        String methodName = "set" + upperFirstChar(fieldName);
        MethodSpec.Builder method = MethodSpec.methodBuilder(methodName)
                .addModifiers(modifiers)
                .returns(returnType)
                .addParameter(fieldType, fieldName)
                .addStatement("this." + fieldName + "=" + fieldName)
                .addStatement("return this");
        return method.build();
    }

    public static MethodSpec createSet(String fieldName, TypeName fieldType, Modifier... modifiers) {
        String methodName = "set" + upperFirstChar(fieldName);
        MethodSpec.Builder method = MethodSpec.methodBuilder(methodName)
                .addModifiers(modifiers)
                .addParameter(fieldType, fieldName)
                .addStatement("this." + fieldName + "=" + fieldName);
        return method.build();
    }

    public static MethodSpec createGet(String fieldName, TypeName fieldType, Modifier... modifiers) {
        String methodName = "get" + upperFirstChar(fieldName);
        MethodSpec.Builder method = MethodSpec.methodBuilder(methodName)
                .addModifiers(modifiers)
                .returns(fieldType)
                .addStatement("return " + fieldName);
        return method.build();
    }

    public static MethodSpec createToString(String clazzName, HashMap<String, TypeName> fieldMap, Modifier... modifiers) {
        String methodName = "toString";
        MethodSpec.Builder method = MethodSpec.methodBuilder(methodName)
                .addModifiers(modifiers)
                .addAnnotation(Override.class)
                .returns(String.class);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\"")
                .append(clazzName)
                .append("{\"");
        for (String field : fieldMap.keySet()) {
            stringBuilder.append(" + \"").append(field).append(":\" + ").append(field);
        }
        stringBuilder.append(" + \"}\"");
        method.addStatement("return " + stringBuilder.toString());
        return method.build();
    }


    public static String upperFirstChar(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }
}
