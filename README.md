# JavaPoet-Utils

### JavaPoet生成Android常用代码

##### [一. 生成JavaBean](https://github.com/chunshengwang/JavaPoet-Utils/blob/master/androidpoet-processor/src/main/java/com/lofiwang/androidpoet/PoetCodeHandler.java)
1. construct()
```
    public static MethodSpec createConstructMethod(Modifier... modifiers) {
        MethodSpec.Builder method = MethodSpec.constructorBuilder()
                .addModifiers(modifiers);
        return method.build();
    }
```
2. get()
```
    public static MethodSpec createGet(String fieldName, TypeName fieldType, Modifier... modifiers) {
        String methodName = "get" + upperFirstChar(fieldName);
        MethodSpec.Builder method = MethodSpec.methodBuilder(methodName)
                .addModifiers(modifiers)
                .returns(fieldType)
                .addStatement("return " + fieldName);
        return method.build();
    }
```
3. set()
```
    public static MethodSpec createSet(String fieldName, TypeName fieldType, Modifier... modifiers) {
        String methodName = "set" + upperFirstChar(fieldName);
        MethodSpec.Builder method = MethodSpec.methodBuilder(methodName)
                .addModifiers(modifiers)
                .addParameter(fieldType, fieldName)
                .addStatement("this." + fieldName + "=" + fieldName);
        return method.build();
    }
```
4. toString()
```
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
```

##### [二. 生成Parcelable](https://github.com/chunshengwang/JavaPoet-Utils/blob/master/androidpoet-processor/src/main/java/com/lofiwang/androidpoet/PoetCodeHandler.java)
1. implements Parcelable
```
ClassName parcelableType = ClassName.get("android.os", "Parcelable");
ClassName parcelType = ClassName.get("android.os", "Parcel");
ClassName parcelableCreator = ClassName.get("android.os", "Parcelable", "Creator");
TypeName creatorFieldType = ParameterizedTypeName.get(parcelableCreator, typeName);
typeSpecBuilder.addSuperinterface(parcelableType);
```
2. construct()
```
    public static MethodSpec createConstructMethod(Modifier... modifiers) {
        MethodSpec.Builder method = MethodSpec.constructorBuilder()
                .addModifiers(modifiers);
    }
```
3. construct(Parcel)
```
            MethodSpec.Builder method2 = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(parcelType, "in")
                    .addStatement("$T classLoader = this.getClass().getClassLoader()", ClassLoader.class);
            for (String field : fieldMap.keySet()) {
                method2.addStatement("this.$L = ($T)in.readValue(classLoader)", field, fieldMap.get(field));
            }
```
4. describeContents()
```
            MethodSpec.Builder method = MethodSpec.methodBuilder("describeContents")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(int.class)
                    .addStatement("return 0");
```
5. writeToParcel(Parcel dest, int flags)
```
            MethodSpec.Builder method1 = MethodSpec.methodBuilder("writeToParcel")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addParameter(parcelType, "dest")
                    .addParameter(int.class, "flags");
            for (String field : fieldMap.keySet()) {
                method1.addStatement("dest.writeValue($L)", "this." + field);
            }
```
6. CREATOR
```
            ArrayTypeName newTypeArray = ArrayTypeName.of(typeName);
            TypeSpec parcelableCreatorType = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(creatorFieldType)
                    .addMethod(MethodSpec.methodBuilder("createFromParcel")
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(parcelType, "source")
                            .returns(typeName)
                            .addStatement("return new $T($N)", typeName, "source")
                            .build())
                    .addMethod(MethodSpec.methodBuilder("newArray")
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(int.class, "size")
                            .returns(newTypeArray)
                            .addStatement("return new $T[$N]", typeName, "size")
                            .build())
                    .build();
            FieldSpec.Builder creatorField = FieldSpec.builder(creatorFieldType, "CREATOR")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$L", parcelableCreatorType);
```
