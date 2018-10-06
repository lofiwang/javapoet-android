package com.lofiwang.androidpoet;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by chunsheng.wang on 2018/9/25.
 */

public class PoetCodeHandler {

    public static void createPoetCodeFile(Messager messager, Elements elementUtils, TypeElement targetClassElement, Filer filer) {
        HashMap<String, TypeName> fieldMap = PoetCodeUtil.createFieldMap(targetClassElement);
        String pkgName = PoetCodeUtil.getTargetPkgName(targetClassElement, elementUtils);

        String SUFFIX_BEAN = "Bean";
        String beanClazzName = PoetCodeUtil.createNewClazzName(targetClassElement, SUFFIX_BEAN);
        try {
            createJavaBeanFile(pkgName, beanClazzName, fieldMap, filer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String SUFFIX_PARCELABLE = "Parcelable";
        String parcelableClazzName = PoetCodeUtil.createNewClazzName(targetClassElement, SUFFIX_PARCELABLE);
        try {
            createParcelableFile(pkgName, parcelableClazzName, fieldMap, filer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String SUFFIX_BUILDER_PATTERN = "BuilderPattern";
        String BuilderPatternClazzName = PoetCodeUtil.createNewClazzName(targetClassElement, SUFFIX_BUILDER_PATTERN);
        try {
            createBuilderPatternFile(pkgName, BuilderPatternClazzName, fieldMap, filer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String SUFFIX_PCS_BEAN = "PcsBean";
        String PcsBeanClazzName = PoetCodeUtil.createNewClazzName(targetClassElement, SUFFIX_PCS_BEAN);
        try {
            createPcsBeanFile(pkgName, PcsBeanClazzName, fieldMap, filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createJavaBeanFile(String pkgName, String newClazzName, HashMap<String, TypeName> fieldMap, Filer filer) throws IOException {
        TypeSpec.Builder typeSpecB = TypeSpec.classBuilder(newClazzName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(PoetCodeUtil.createConstructMethod(Modifier.PUBLIC));

        for (String field : fieldMap.keySet()) {
            typeSpecB.addField(fieldMap.get(field), field, Modifier.PRIVATE)
                    .addMethod(PoetCodeUtil.createGet(field, fieldMap.get(field), Modifier.PUBLIC))
                    .addMethod(PoetCodeUtil.createSet(field, fieldMap.get(field), Modifier.PUBLIC));
        }
        typeSpecB.addMethod(PoetCodeUtil.createToString(newClazzName, fieldMap, Modifier.PUBLIC));
        TypeSpec typeSpec = typeSpecB.build();
        JavaFile.builder(pkgName, typeSpec).build().writeTo(filer);
    }

    private static void createParcelableFile(String pkgName, String newClazzName, HashMap<String, TypeName> fieldMap, Filer filer) throws IOException {
        ClassName newClassName = PoetCodeUtil.createNewClazzType(pkgName, newClazzName);
        TypeSpec.Builder typeSpecB = TypeSpec.classBuilder(newClazzName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(PoetCodeUtil.createConstructMethod(Modifier.PUBLIC));
        for (String field : fieldMap.keySet()) {
            typeSpecB.addField(fieldMap.get(field), field, Modifier.PRIVATE);
        }
        PoetCodeUtil.Parcelable.createParcelable(newClassName, typeSpecB, fieldMap);
        TypeSpec typeSpec = typeSpecB.build();
        JavaFile.builder(pkgName, typeSpec).build().writeTo(filer);
    }

    private static void createBuilderPatternFile(String pkgName, String newClazzName, HashMap<String, TypeName> fieldMap, Filer filer) throws IOException {
        TypeSpec.Builder typeSpecB = TypeSpec.classBuilder(newClazzName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(PoetCodeUtil.createConstructMethod(Modifier.PUBLIC));
        for (String field : fieldMap.keySet()) {
            typeSpecB.addField(fieldMap.get(field), field, Modifier.PRIVATE)
                    .addMethod(PoetCodeUtil.createGet(field, fieldMap.get(field), Modifier.PUBLIC))
                    .addMethod(PoetCodeUtil.createSet(field, fieldMap.get(field), Modifier.PUBLIC));
        }
        typeSpecB.addType(PoetCodeUtil.Builder.createBuilder(pkgName, newClazzName, fieldMap));
        TypeSpec typeSpec = typeSpecB.build();
        JavaFile.builder(pkgName, typeSpec).build().writeTo(filer);
    }

    private static void createPcsBeanFile(String pkgName, String newClazzName, HashMap<String, TypeName> fieldMap, Filer filer) throws IOException {
        TypeSpec.Builder typeSpecB = TypeSpec.classBuilder(newClazzName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(PoetCodeUtil.createConstructMethod(Modifier.PUBLIC));
        for (String field : fieldMap.keySet()) {
            typeSpecB.addField(fieldMap.get(field), field, Modifier.PRIVATE)
                    .addMethod(PoetCodeUtil.createGet(field, fieldMap.get(field), Modifier.PUBLIC))
                    .addMethod(PoetCodeUtil.createPcsSet(field, fieldMap.get(field), Modifier.PUBLIC));
        }
        typeSpecB.addField(PoetCodeUtil.PcsBean.createPcsField());
        typeSpecB.addMethod(PoetCodeUtil.PcsBean.createAddListenerMethod());
        typeSpecB.addMethod(PoetCodeUtil.PcsBean.createRemoveListenerMethod());
        TypeSpec typeSpec = typeSpecB.build();
        JavaFile.builder(pkgName, typeSpec).build().writeTo(filer);
    }
}
