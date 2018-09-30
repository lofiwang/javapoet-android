# JavaPoet-Utils

### JavaPoet生成Android常用代码

##### 一. 生成JavaBean
1. construct()
``` android
public static MethodSpec createConstructMethod(Modifier... modifiers) {
        MethodSpec.Builder method = MethodSpec.constructorBuilder()
                .addModifiers(modifiers);
        return method.build();
    }
```
2. set()
3. get()
4. toString()

