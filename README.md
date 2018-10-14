# javapoet-android [square JavaPoet中文翻译](https://github.com/lofiwang/javapoet-android/wiki/javapoet-android-home)

### JavaPoet生成Android常用代码
1. [生成JavaBean](https://github.com/lofiwang/javapoet-android)
2. [生成Parcelable](https://github.com/lofiwang/javapoet-android)
3. [生成Builder](https://github.com/lofiwang/javapoet-android)
4. [生成PcsBean(PropertyChangeSupport)](https://github.com/lofiwang/javapoet-android)
5. 持续更新中...

```
原始代码
@PoetCode
public class Person {
    private String name;
}
```

##### [一. 生成JavaBean](https://github.com/lofiwang/javapoet-android/blob/master/androidpoet-processor/src/main/java/com/lofiwang/androidpoet/PoetCodeHandler.java)
1. construct()
```
    public static MethodSpec createConstructMethod(Modifier... modifiers) {
        MethodSpec.Builder method = MethodSpec.constructorBuilder()
                .addModifiers(modifiers);
        return method.build();
    }
    --------------------------------------------------------------------------------
    public PersonBean() {
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
    --------------------------------------------------------------------------------
    public String getName() {
        return name;
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
    --------------------------------------------------------------------------------
    public void setName(String name) {
        this.name = name;
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
    --------------------------------------------------------------------------------
    @Override
    public String toString() {
        return "PersonBean{" + "name:" + name + "}";
    }
```

##### [二. 生成Parcelable](https://github.com/lofiwang/javapoet-android/blob/master/androidpoet-processor/src/main/java/com/lofiwang/androidpoet/PoetCodeHandler.java)
1. implements Parcelable
```
ClassName parcelableType = ClassName.get("android.os", "Parcelable");
ClassName parcelType = ClassName.get("android.os", "Parcel");
ClassName parcelableCreator = ClassName.get("android.os", "Parcelable", "Creator");
TypeName creatorFieldType = ParameterizedTypeName.get(parcelableCreator, typeName);
typeSpecBuilder.addSuperinterface(parcelableType);
--------------------------------------------------------------------------------
public class PersonParcelable implements Parcelable {}
```
2. construct()
```
    public static MethodSpec createConstructMethod(Modifier... modifiers) {
        MethodSpec.Builder method = MethodSpec.constructorBuilder()
                .addModifiers(modifiers);
    }
    --------------------------------------------------------------------------------
    public PersonParcelable() {
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
            --------------------------------------------------------------------------------
    public PersonParcelable(Parcel in) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        this.name = (String) in.readValue(classLoader);
    }
```
4. describeContents()
```
            MethodSpec.Builder method = MethodSpec.methodBuilder("describeContents")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(int.class)
                    .addStatement("return 0");
            --------------------------------------------------------------------------------
    @Override
    public int describeContents() {
        return 0;
    } 
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
            --------------------------------------------------------------------------------
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.name);
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
            --------------------------------------------------------------------------------
    public static final Parcelable.Creator<PersonParcelable> CREATOR = new Parcelable.Creator<PersonParcelable>() {
        @Override
        public PersonParcelable createFromParcel(Parcel source) {
            return new PersonParcelable(source);
        }

        @Override
        public PersonParcelable[] newArray(int size) {
            return new PersonParcelable[size];
        }
    };
```
##### [三. 生成Builder](https://github.com/lofiwang/javapoet-android/blob/master/androidpoet-processor/src/main/java/com/lofiwang/androidpoet/PoetCodeHandler.java)
1. Builder
```
        public static void createBuilder(String pkgName, String newClazzName, TypeSpec.Builder typeSpecBuilder, HashMap<String, TypeName> fieldMap) {
            ClassName newClassName = PoetCodeUtil.createNewClazzType(pkgName, newClazzName);
            ClassName builderClassName = PoetCodeUtil.createNewClazzType(pkgName, newClazzName, "Builder");
            TypeSpec.Builder typeSpecB = TypeSpec.classBuilder("Builder")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
            for (String field : fieldMap.keySet()) {
                typeSpecB.addField(fieldMap.get(field), field, Modifier.PRIVATE)
                        .addMethod(PoetCodeUtil.createSetReturn(builderClassName, field, fieldMap.get(field), Modifier.PUBLIC));
            }
            MethodSpec.Builder methodSpecB = MethodSpec.methodBuilder("create")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(newClassName)
                    .addStatement("$T outClazz = new $T()", newClassName, newClassName);
            for (String field : fieldMap.keySet()) {
                methodSpecB.addStatement("outClazz.$L = $L", field, field);
            }
            methodSpecB.addStatement("return outClazz");
            typeSpecB.addMethod(methodSpecB.build());
            typeSpecBuilder.addType(typeSpecB.build());
        }
        --------------------------------------------------------------------------------
  public static class Builder {
    private String name;

    private int age;

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setAge(int age) {
      this.age = age;
      return this;
    }

    public PersonBuilderPattern create() {
      PersonBuilderPattern outClazz = new PersonBuilderPattern();
      outClazz.name = name;
      outClazz.age = age;
      return outClazz;
    }
  }
```
##### [四. 生成PcsBean](https://github.com/lofiwang/javapoet-android/blob/master/androidpoet-processor/src/main/java/com/lofiwang/androidpoet/PoetCodeHandler.java)
1. field
```
        public static FieldSpec createPcsField() {
            FieldSpec.Builder pcsField = FieldSpec.builder(PropertyChangeSupport.class, "pcs")
                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                    .initializer("new $T(this)", PropertyChangeSupport.class);
            return pcsField.build();
        }
        --------------------------------------------------------------------------------
  private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
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
    --------------------------------------------------------------------------------
    public String getName() {
        return name;
    }
```
3. set()
```
        public static MethodSpec createPcsSet(String fieldName, TypeName fieldType, Modifier... modifiers) {
            String methodName = "set" + upperFirstChar(fieldName);
            MethodSpec.Builder method = MethodSpec.methodBuilder(methodName)
                    .addModifiers(modifiers)
                    .addParameter(fieldType, fieldName)
                    .addStatement("$T old$L = this.$L",fieldType,fieldName,fieldName)
                    .addStatement("this.$L = $L", fieldName, fieldName)
                    .addStatement("this.pcs.firePropertyChange($S,old$L,$L)",fieldName,fieldName,fieldName);
            return method.build();
        }
        --------------------------------------------------------------------------------
  public void setName(String name) {
    String oldname = this.name;
    this.name = name;
    this.pcs.firePropertyChange("name",oldname,name);
  }
```
4. add listener
```
        public static MethodSpec createAddListenerMethod() {
            MethodSpec.Builder pcsMethod = MethodSpec.methodBuilder("addPropertyChangeListener")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(PropertyChangeListener.class,"listener")
                    .addStatement("this.pcs.addPropertyChangeListener(listener)");
            return pcsMethod.build();
        }
        --------------------------------------------------------------------------------
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    this.pcs.addPropertyChangeListener(listener);
  }
```
5. remove listener
```
        public static MethodSpec createRemoveListenerMethod() {
            MethodSpec.Builder pcsMethod = MethodSpec.methodBuilder("removePropertyChangeListener")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(PropertyChangeListener.class,"listener")
                    .addStatement("this.pcs.removePropertyChangeListener(listener)");
            return pcsMethod.build();
        }
        --------------------------------------------------------------------------------
  public void removePropertyChangeListener(PropertyChangeListener listener) {
    this.pcs.removePropertyChangeListener(listener);
  }
```
