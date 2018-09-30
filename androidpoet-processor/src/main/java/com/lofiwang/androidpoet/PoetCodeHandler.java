package com.lofiwang.androidpoet;

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
            createBeanFile(pkgName, beanClazzName, fieldMap, filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createBeanFile(String pkgName, String newClazzName, HashMap<String, TypeName> fieldMap, Filer filer) throws IOException {
        TypeSpec.Builder typeSpecB = TypeSpec.classBuilder(newClazzName);
        typeSpecB.addModifiers(Modifier.PUBLIC)
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
}
