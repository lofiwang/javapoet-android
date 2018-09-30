# JavaPoet-Utils

### JavaPoet生成Android常用代码

##### 一. 生成JavaBean
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
