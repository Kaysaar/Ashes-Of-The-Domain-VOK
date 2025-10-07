package data.kaysaar.aotd.vok.plugins;

import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.api.util.Pair;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtilis {
    // Code taken and modified from Grand Colonies
    private static final Class<?> fieldClass;
    private static final MethodHandles.Lookup lookup = MethodHandles.lookup();
    private static final MethodHandle setFieldHandle;
    private static final MethodHandle getFieldHandle;
    private static final MethodHandle getFieldNameHandle;
    private static final MethodHandle setFieldAccessibleHandle;
    private static final Class<?> methodClass;
    private static final Class<?> constructorClass;
    private static final MethodHandle getMethodNameHandle;
    private static final MethodHandle invokeMethodHandle;
    private static final MethodHandle setMethodAccessable;
    private static final MethodHandle getModifiersHandle;
    private static final MethodHandle  getParameterTypesHandle;
    private static final MethodHandle  getFieldTypeHandle;
    private static final MethodHandle getDeclaredConstructorsHandle;
    private static final Class<?>fileClass;
    static {
        try {
            fieldClass = Class.forName("java.lang.reflect.Field", false, Class.class.getClassLoader());
            setFieldHandle = lookup.findVirtual(fieldClass, "set", MethodType.methodType(Void.TYPE, Object.class, Object.class));
            getFieldHandle = lookup.findVirtual(fieldClass, "get", MethodType.methodType(Object.class, Object.class));
            getFieldNameHandle = lookup.findVirtual(fieldClass, "getName", MethodType.methodType(String.class));
            getFieldTypeHandle = lookup.findVirtual(fieldClass, "getType", MethodType.methodType(Class.class));
            setFieldAccessibleHandle = lookup.findVirtual(fieldClass, "setAccessible", MethodType.methodType(Void.TYPE, boolean.class));

            methodClass = Class.forName("java.lang.reflect.Method", false, Class.class.getClassLoader());
            getMethodNameHandle = lookup.findVirtual(methodClass, "getName", MethodType.methodType(String.class));
            invokeMethodHandle = lookup.findVirtual(methodClass, "invoke", MethodType.methodType(Object.class, Object.class, Object[].class));
            setMethodAccessable = lookup.findVirtual(methodClass, "setAccessible", MethodType.methodType(Void.TYPE, boolean.class));
            getModifiersHandle = lookup.findVirtual(methodClass, "getModifiers", MethodType.methodType(int.class));
            getParameterTypesHandle = lookup.findVirtual(methodClass, "getParameterTypes", MethodType.methodType(Class[].class));

            constructorClass = Class.forName("java.lang.reflect.Constructor", false, Class.class.getClassLoader());
            getDeclaredConstructorsHandle = lookup.findVirtual(constructorClass, "getParameterTypes", MethodType.methodType(Class[].class));
            fileClass = Class.forName("java.io.File", false, Class.class.getClassLoader());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object instantiateExact(Class<?> clazz, Class<?>[] parameterTypes, Object... arguments) {
        try {
            // Match constructor exactly with the provided parameter types
            MethodType ctorType = MethodType.methodType(void.class, parameterTypes);
            MethodHandle constructorHandle = lookup.findConstructor(clazz, ctorType);

            // Invoke the constructor with the provided arguments
            return constructorHandle.invokeWithArguments(arguments);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to instantiate (exact) " + clazz.getName(), e);
        }
    }
    public static ButtonAPI findButtonWithText(Object instance, String textQuery,
                                               boolean caseInsensitive, boolean substringMatch) {
        if (instance == null || textQuery == null) return null;

        try {
            Class<?> current = instance.getClass();
            while (current != null) {
                Object[] fields = current.getDeclaredFields();
                for (Object field : fields) {
                    try {
                        // Make field accessible and get its value
                        setFieldAccessibleHandle.invoke(field, true);
                        Object value = getFieldHandle.invoke(field, instance);
                        if (!(value instanceof ButtonAPI)) continue;

                        String text = ((ButtonAPI) value).getText();
                        if (text == null) continue;

                        String a = caseInsensitive ? text.toLowerCase() : text;
                        String b = caseInsensitive ? textQuery.toLowerCase() : textQuery;

                        boolean match = substringMatch ? a.contains(b) : a.equals(b);
                        if (match) return (ButtonAPI) value;
                    } catch (Throwable inner) {
                        // Mirror your style: don't abort on a single bad field
                        inner.printStackTrace();
                    }
                }
                current = current.getSuperclass();
            }
            return null;
        } catch (Throwable e) {
            throw new RuntimeException("Failed to find ButtonAPI with text \"" + textQuery + "\"", e);
        }
    }
    public static boolean doesHaveConstructorExact(Class<?> clazz, Class<?>... parameterTypes) {
        try {
            if (parameterTypes == null) parameterTypes = new Class<?>[0];

            Class<?>[] normalized = new Class<?>[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> p = parameterTypes[i];
                normalized[i] = (p != null && getPrimitiveType(p) != null) ? getPrimitiveType(p) : p;
            }

            MethodType ctorType = MethodType.methodType(void.class, normalized);
            lookup.findConstructor(clazz, ctorType);
            return true; // found & accessible
        } catch (Throwable t) {
            return false; // not found or not accessible
        }
    }

    public static boolean doesHaveConstructor(Class<?> clazz, Object... arguments) {
        if (arguments == null) arguments = new Object[0];

        // Null args are ambiguous for an "exact" signature check.
        for (Object arg : arguments) {
            if (arg == null) return false;
        }

        try {
            Class<?>[] parameterTypes = new Class<?>[arguments.length];
            for (int i = 0; i < arguments.length; i++) {
                Class<?> c = arguments[i].getClass();
                Class<?> prim = getPrimitiveType(c);
                parameterTypes[i] = (prim != null) ? prim : c;
            }

            MethodType ctorType = MethodType.methodType(void.class, parameterTypes);
            lookup.findConstructor(clazz, ctorType);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }
    @SuppressWarnings("unchecked")
    public static Object instantiateAutoProjected(Class<?> targetClass, Object... arguments) {
        try {
            if (arguments == null) arguments = new Object[0];

            // Get ALL declared ctors via indirection (no direct Constructor usage)
            Object[] ctors = (Object[]) invokeMethodWithAutoProjection("getDeclaredConstructors", targetClass);
            if (ctors == null || ctors.length == 0) {
                throw new NoSuchMethodException("No constructors on " + targetClass.getName());
            }

            for (Object ctor : ctors) {
                try {
                    // Parameter types of this ctor
                    Class<?>[] paramTypes = (Class<?>[]) getDeclaredConstructorsHandle.invoke(ctor);
                    boolean varArgs = false;
                    try {
                        varArgs = (boolean) invokeMethodWithAutoProjection("isVarArgs", ctor);
                    } catch (Throwable ignored) {}

                    // Quick arity checks
                    if (!varArgs && paramTypes.length != arguments.length) continue;
                    if (varArgs && arguments.length < paramTypes.length - 1) continue;

                    // Try to project/convert arguments for this ctor shape
                    Object[] projected = projectCtorArgs(arguments, paramTypes, varArgs);
                    if (projected == null) continue; // couldn't convert for this ctor

                    // Be permissive with access like elsewhere
                    try { invokeMethodWithAutoProjection("setAccessible", ctor, true); } catch (Throwable ignored) {}

                    // NOTE: Constructor::newInstance takes a single Object[] parameter
                    return invokeMethodWithAutoProjection("newInstance", ctor, (Object) projected);
                } catch (Throwable perCtor) {
                    // Try other ctors; mirror your style (don't fail the loop on one bad attempt)
                    perCtor.printStackTrace();
                }
            }

            throw new NoSuchMethodException("No compatible constructor on " + targetClass.getName() +
                    " for " + arguments.length + " args after auto-projection");
        } catch (Throwable e) {
            throw new RuntimeException("Failed to instantiate (auto-projected) " + targetClass.getName(), e);
        }
    }

    /** Build the argument array for a constructor (handles fixed + varargs). */
    private static Object[] projectCtorArgs(Object[] args, Class<?>[] paramTypes, boolean varArgs) {
        try {
            if (!varArgs) {
                Object[] out = new Object[paramTypes.length];
                for (int i = 0; i < paramTypes.length; i++) {
                    out[i] = convertArgumentAuto(args[i], paramTypes[i]);
                }
                return out;
            }

            // varargs: last param is T[]
            int fixedCount = paramTypes.length - 1;
            Class<?> arrayType = paramTypes[fixedCount];
            Class<?> compType = arrayType.getComponentType();

            // Fixed portion
            Object[] fixed = new Object[fixedCount];
            for (int i = 0; i < fixedCount; i++) {
                fixed[i] = convertArgumentAuto(args[i], paramTypes[i]);
            }

            int varCount = args.length - fixedCount;

            // If caller passed the varargs array already (exact type), just use it
            if (varCount == 1 && args[fixedCount] != null && arrayType.isInstance(args[fixedCount])) {
                Object[] out = new Object[paramTypes.length];
                System.arraycopy(fixed, 0, out, 0, fixedCount);
                out[fixedCount] = args[fixedCount];
                return out;
            }

            // Build T[] and fill
            Object varArray = java.lang.reflect.Array.newInstance(compType, varCount);
            for (int i = 0; i < varCount; i++) {
                Object converted = convertArgumentAuto(args[fixedCount + i], compType);
                java.lang.reflect.Array.set(varArray, i, converted);
            }

            Object[] out = new Object[paramTypes.length];
            System.arraycopy(fixed, 0, out, 0, fixedCount);
            out[fixedCount] = varArray;
            return out;
        } catch (Throwable t) {
            // Signal this ctor doesn't fit
            return null;
        }
    }

    /**
     * Wrapper around your convertArgument(...) with a few safe, common widenings:
     *  - to String / CharSequence via String.valueOf(...)
     *  - booleans & chars from String
     *  - enums from String (name, case-insensitive fallback) or Number (ordinal)
     *  - java.io.File from String (constructed via MethodHandles, not "new")
     *  - falls back to your convertArgument(...) for primitives/boxing/casts
     */
    public static Object convertArgumentAuto(Object arg, Class<?> targetType) {
        // Null handling
        if (arg == null) {
            if (targetType.isPrimitive()) {
                throw new IllegalArgumentException("null for primitive " + targetType.getName());
            }
            return null;
        }

        // Already assignable
        if (targetType.isAssignableFrom(arg.getClass())) return arg;

        // Strings / CharSequence
        if (targetType == String.class || CharSequence.class.isAssignableFrom(targetType)) {
            return String.valueOf(arg);
        }

        // Enums
        if (targetType.isEnum()) {
            return toEnumCoerce((Class<? extends Enum<?>>) targetType, arg);
        }

        // File from String (constructed via our MethodHandles path, no direct "new File")
        try {
            if (fileClass != null && fileClass.isAssignableFrom(targetType) && arg instanceof CharSequence) {
                return instantiateExact(fileClass, new Class<?>[]{String.class}, arg.toString());
            }
        } catch (Throwable ignored) {}

        // Booleans/Chars from String
        if (targetType == boolean.class || targetType == Boolean.class) {
            if (arg instanceof CharSequence) return Boolean.parseBoolean(arg.toString().trim());
        }
        if (targetType == char.class || targetType == Character.class) {
            if (arg instanceof CharSequence) {
                String s = arg.toString();
                if (s.length() == 1) return s.charAt(0);
            }
        }

        // Let your original converter handle numbers, primitives, and normal casts
        return convertArgument(arg, targetType);
    }

    /** Enum coercion helpers – no direct reflection; uses standard Enum APIs. */
    private static Object toEnumCoerce(Class<? extends Enum<?>> enumType, Object arg) {
        if (enumType.isInstance(arg)) return arg;

        if (arg instanceof CharSequence) {
            String name = arg.toString();
            try {
                // exact first
                return Enum.valueOf((Class) enumType, name);
            } catch (IllegalArgumentException ignored) {}
            // case-insensitive fallback
            for (Object e : enumType.getEnumConstants()) {
                if (((Enum<?>) e).name().equalsIgnoreCase(name)) return e;
            }
            throw new IllegalArgumentException("No enum constant " + enumType.getName() + "." + name);
        }

        if (arg instanceof Number) {
            int ord = ((Number) arg).intValue();
            Object[] all = enumType.getEnumConstants();
            if (ord >= 0 && ord < all.length) return all[ord];
            throw new IllegalArgumentException("Enum ordinal out of range: " + ord + " for " + enumType.getName());
        }

        throw new IllegalArgumentException("Cannot convert " + arg.getClass().getName() + " to enum " + enumType.getName());
    }
    public static Object instantiate(Class<?> clazz, Object... arguments) {
        try {
            // Auto-derive parameter types from arguments
            Class<?>[] parameterTypes = new Class<?>[arguments.length];
            for (int i = 0; i < arguments.length; i++) {
                Object arg = arguments[i];
                parameterTypes[i] = (arg != null && arg.getClass().isPrimitive())
                        ? arg.getClass() // Won't really hit here often — boxing will happen
                        : (arg != null && getPrimitiveType(arg.getClass()) != null)
                        ? getPrimitiveType(arg.getClass())
                        : (arg != null ? arg.getClass() : Object.class);
            }

            MethodType ctorType = MethodType.methodType(void.class, parameterTypes);
            MethodHandle constructorHandle = lookup.findConstructor(clazz, ctorType);

            return constructorHandle.invokeWithArguments(arguments);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to instantiate " + clazz.getName(), e);
        }
    }



    public static Object enumFromExampleEnum( Class<?>  exampleEnumValue, int ordinal) {
        if (exampleEnumValue == null) throw new IllegalArgumentException("exampleEnumValue is null");
        try {
            boolean isEnum = (boolean) invokeMethodWithAutoProjection("isEnum", exampleEnumValue);
            if (!isEnum) throw new IllegalArgumentException("exampleEnumValue is not an enum: " + exampleEnumValue.getName());
            Object[] constants = (Object[]) invokeMethodWithAutoProjection("getEnumConstants", exampleEnumValue);
            if (constants == null) throw new IllegalStateException("Enum constants array is null for: " + exampleEnumValue.getName());
            if (ordinal < 0 || ordinal >= constants.length) {
                throw new IllegalArgumentException("Ordinal " + ordinal + " out of range 0.." + (constants.length - 1) + " for " + exampleEnumValue.getName());
            }
            return constants[ordinal];
        } catch (Throwable e) {
            throw new RuntimeException("Failed to obtain enum from example enum value", e);
        }
    }


    public static Class<?> findFirstEnumClassWithConstantsCount(Object instance, int expectedCount) {
        try {
            Class<?> current = instance.getClass();
            while (current != null) {
                Object[] fields = current.getDeclaredFields();
                for (Object field : fields) {
                    try {
                        setFieldAccessibleHandle.invoke(field, true);
                        Class<?> fieldType = (Class<?>) getFieldTypeHandle.invoke(field);

                        // Must be an enum
                        boolean isEnum = (boolean) invokeMethodWithAutoProjection("isEnum", fieldType);
                        if (!isEnum) continue;

                        Object constantsArr = invokeMethodWithAutoProjection("getEnumConstants", fieldType);
                        Object[] constants = (Object[]) constantsArr;
                        if (constants != null && constants.length == expectedCount) {
                            return fieldType;
                        }
                    } catch (Throwable inner) {
                        // Keep scanning; mirror your existing pattern
                        inner.printStackTrace();
                    }
                }
                current = current.getSuperclass();
            }
            return null;
        } catch (Throwable e) {
            throw new RuntimeException("Failed to find enum class with " + expectedCount + " ordinals", e);
        }
    }

    // Helper: map boxed -> primitive types
    private static Class<?> getPrimitiveType(Class<?> boxed) {
        if (boxed == Integer.class) return int.class;
        if (boxed == Long.class) return long.class;
        if (boxed == Double.class) return double.class;
        if (boxed == Float.class) return float.class;
        if (boxed == Short.class) return short.class;
        if (boxed == Byte.class) return byte.class;
        if (boxed == Boolean.class) return boolean.class;
        if (boxed == Character.class) return char.class;
        return null;
    }

    public static Object getPrivateVariable(String fieldName, Object instanceToGetFrom) {
        try {
            Class<?> instances = instanceToGetFrom.getClass();
            while (instances != null) {
                for (Object obj : instances.getDeclaredFields()) {
                    setFieldAccessibleHandle.invoke(obj, true);
                    String name = (String) getFieldNameHandle.invoke(obj);
                    if (name.equals(fieldName)) {
                        return getFieldHandle.invoke(obj, instanceToGetFrom);
                    }
                }
                for (Object obj : instances.getFields()) {
                    setFieldAccessibleHandle.invoke(obj, true);
                    String name = (String) getFieldNameHandle.invoke(obj);
                    if (name.equals(fieldName)) {
                        return getFieldHandle.invoke(obj, instanceToGetFrom);
                    }
                }
                instances = instances.getSuperclass();
            }
            return null;
        } catch (Throwable e) {
            return null;
        }
    }
    public static Object findNestedMarketApiFieldFromOutpostParams(Object instance) {
        try {
            Class<?> outerClass = instance.getClass();
            while (outerClass != null) {
                Object[] outerFields = outerClass.getDeclaredFields();
                for (Object outerField : outerFields) {
                    try {
                        // Make outer field accessible
                        setFieldAccessibleHandle.invoke(outerField, true);
                        Object innerObject = getFieldHandle.invoke(outerField, instance);
                        if (innerObject == null) continue;

                        // Get the class of the inner object
                        Class<?> innerClass = innerObject.getClass();
                        Object[] innerFields = innerClass.getDeclaredFields();

                        // Check: inner class must have exactly one field
                        if (innerFields.length == 1) {
                            Object innerField = innerFields[0];
                            Class<?> innerFieldType = (Class<?>) getFieldTypeHandle.invoke(innerField);

                            // Check if that single field is a MarketAPI
                            if (com.fs.starfarer.api.campaign.econ.MarketAPI.class.isAssignableFrom(innerFieldType)) {
                                setFieldAccessibleHandle.invoke(innerField, true);
                                return getFieldHandle.invoke(innerField, innerObject);
                            }
                        }
                    } catch (Throwable innerEx) {
                        innerEx.printStackTrace();
                    }
                }
                outerClass = outerClass.getSuperclass();
            }
            return null; // No match found
        } catch (Throwable e) {
            throw new RuntimeException("Failed to find nested MarketAPI field", e);
        }
    }

    private static boolean isMatchingConstructor(MethodType ctorType) {
        // We want 5 params exactly
        if (ctorType.parameterCount() != 5) return false;

        Class<?>[] params = ctorType.parameterArray();

        // Check first 4 parameters exactly
        if (params[0] == float.class &&
                params[1] == float.class &&
                params[2] == boolean.class &&
                params[3] == boolean.class) {
            // We don't check params[4] because it's inaccessible, accept any class
            return true;
        }

        return false;
    }

    public static String getFloatFieldNameMatchingValue(Object instance, float targetValue) {
        try {
            Class<?> currentClass = instance.getClass();

            while (currentClass != null) {
                Object[] fields = currentClass.getDeclaredFields();

                for (Object field : fields) {
                    try {
                        // Make field accessible
                        setFieldAccessibleHandle.invoke(field, true);

                        // Check if field is float
                        Class<?> type = (Class<?>) getFieldTypeHandle.invoke(field);
                        if (type == float.class) {
                            float fieldValue = (float) getFieldHandle.invoke(field, instance);

                            if (Float.compare(fieldValue, targetValue) == 0) {
                                return (String) getFieldNameHandle.invoke(field);
                            }
                        }
                    } catch (Throwable innerEx) {
                        innerEx.printStackTrace(); // or log silently if preferred
                    }
                }

                currentClass = currentClass.getSuperclass();
            }
        } catch (Throwable e) {
            throw new RuntimeException("Failed to find float field name", e);
        }

        return null; // No matching field found
    }

    public static String getStringFieldMatchingValue(Object instance, String targetValue) {
        try {
            Class<?> currentClass = instance.getClass();

            while (currentClass != null) {
                Object[] fields = currentClass.getDeclaredFields();

                for (Object field : fields) {
                    try {
                        // Make field accessible
                        setFieldAccessibleHandle.invoke(field, true);

                        // Check if field is float
                        Class<?> type = (Class<?>) getFieldTypeHandle.invoke(field);
                        if (type == String.class) {
                            String fieldValue = (String) getFieldHandle.invoke(field, instance);

                            if (targetValue.equals(fieldValue)) {
                                return (String) getFieldNameHandle.invoke(field);
                            }
                        }
                    } catch (Throwable innerEx) {
                        innerEx.printStackTrace(); // or log silently if preferred
                    }
                }

                currentClass = currentClass.getSuperclass();
            }
        } catch (Throwable e) {
            throw new RuntimeException("Failed to find float field name", e);
        }

        return null; // No matching field found
    }

    public static Object getPrivateVariableFromSuperClass(String fieldName, Object instanceToGetFrom) {
        try {
            Class<?> instances = instanceToGetFrom.getClass();
            while (instances != null) {
                for (Object obj : instances.getDeclaredFields()) {
                    setFieldAccessibleHandle.invoke(obj, true);
                    String name = (String) getFieldNameHandle.invoke(obj);
                    if (name.equals(fieldName)) {
                        return getFieldHandle.invoke(obj, instanceToGetFrom);
                    }
                }
                for (Object obj : instances.getFields()) {
                    setFieldAccessibleHandle.invoke(obj, true);
                    String name = (String) getFieldNameHandle.invoke(obj);
                    if (name.equals(fieldName)) {
                        return getFieldHandle.invoke(obj, instanceToGetFrom);
                    }
                }
                instances = instances.getSuperclass();
            }
            return null;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void setPrivateVariableFromSuperclass(String fieldName, Object instanceToModify, Object newValue) {
        try {
            Class<?> instances = instanceToModify.getClass();
            while (instances != null) {
                for (Object obj : instances.getDeclaredFields()) {
                    setFieldAccessibleHandle.invoke(obj, true);
                    String name = (String) getFieldNameHandle.invoke(obj);
                    if (name.equals(fieldName)) {
                        setFieldHandle.invoke(obj, instanceToModify, newValue);
                        return;
                    }
                }
                for (Object obj : instances.getFields()) {
                    setFieldAccessibleHandle.invoke(obj, true);
                    String name = (String) getFieldNameHandle.invoke(obj);
                    if (name.equals(fieldName)) {
                        setFieldHandle.invoke(obj, instanceToModify, newValue);
                        return;
                    }
                }
                instances = instances.getSuperclass();
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean hasMethodOfName(String name, Object instance) {
        try {
            for (Object method : instance.getClass().getMethods()) {
                if (getMethodNameHandle.invoke(method).equals(name)) {
                    return true;
                }
            }
            return false;
        } catch (Throwable e) {
            return false;
        }
    }

    public static Object invokeMethod(String methodName, Object instance, Object... arguments) {
        try {
            Object method = instance.getClass().getMethod(methodName);
            return invokeMethodHandle.invoke(method, instance, arguments);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    public static Object invokeMethodDirectly(Object method,Object instance, Object... arguments) {
        try {

            return invokeMethodHandle.invoke(method,null, arguments);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    public static List<UIComponentAPI> getChildrenCopy(UIPanelAPI panel) {
        try {
            return (List<UIComponentAPI>) invokeMethod("getChildrenCopy", panel);
        } catch (Throwable e) {
            return new ArrayList<>();
        }
    }

    public static Pair<Object, Class<?>[]> getMethodFromSuperclass(String methodName, Object instance) {
        Class<?> currentClass = instance.getClass();

        while (currentClass != null) {
            // Retrieve all declared methods in the current class
            Object[] methods = currentClass.getDeclaredMethods();

            for (Object method : methods) {
                try {
                    // Retrieve the MethodHandle for the getParameterTypes method
                    MethodHandle getParameterTypesHandle = ReflectionBetterUtilis.getParameterTypesHandle(method.getClass(), "getParameterTypes");
                    // Use the MethodHandle to retrieve the method's name

                    // Check if the method name matches
                    if (getMethodNameHandle.invoke(method).equals(methodName)) {
                        // Invoke the MethodHandle to get the parameter types
                        Class<?>[] parameterTypes = (Class<?>[]) getParameterTypesHandle.invoke(method);
                        return new Pair<>(method, parameterTypes);
                    }
                } catch (Throwable e) {

                    e.printStackTrace();  // Handle any reflection errors
                }
            }
            // Move to the superclass if no match is found
            currentClass = currentClass.getSuperclass();
        }

        // Return null if the method was not found in the class hierarchy
        return null;
    }
    public static Object findFieldOfClass(Object instance, Class<?> fieldType) {
        try {
            Class<?> currentClass = instance.getClass();
            while (currentClass != null) {
                Object[] fields = currentClass.getDeclaredFields();
                for (Object field : fields) {
                    try {
                        Class<?> type = (Class<?>) getFieldTypeHandle.invoke(field);
                        if (fieldType.isAssignableFrom(type)) {
                            setFieldAccessibleHandle.invoke(field, true);
                            return getFieldHandle.invoke(field, instance);
                        }
                    } catch (Throwable inner) {
                        inner.printStackTrace();
                    }
                }
                currentClass = currentClass.getSuperclass();
            }
            return null;
        } catch (Throwable e) {
            throw new RuntimeException("Failed to find field of type " + fieldType.getName(), e);
        }
    }

    public static Object invokeStaticMethodWithAutoProjection(Class<?> targetClass, String methodName, Object... arguments) {
        try {
            // Find the method by its name and parameter types
            Object[] methods = targetClass.getDeclaredMethods();

            Object matchingMethod = null;
            Class<?>[] parameterTypes = null;

            for (Object method : methods) {
                // Get the method name dynamically
                String currentName = (String) getMethodNameHandle.invoke(method);

                // Check if names match and method is static
                int modifiers = (int) getModifiersHandle.invoke(method);
                if (currentName.equals(methodName) && (modifiers & 0x0008) != 0) { // Static check
                    // Retrieve parameter types
                    parameterTypes = (Class<?>[]) getParameterTypesHandle.invoke(method);
                    if(parameterTypes.length== arguments.length){
                        matchingMethod = method;
                        break;
                    }

                }
            }

            if (matchingMethod == null) {
                throw new NoSuchMethodException("Static method " + methodName + " not found in class " + targetClass.getName());
            }

            // Project arguments to the correct types
            Object[] projectedArgs = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                Object arg = (arguments.length > i) ? arguments[i] : null;

                if (arg == null) {
                    if (parameterTypes[i].isPrimitive()) {
                        throw new IllegalArgumentException("Null cannot be used for primitive type: " + parameterTypes[i].getName());
                    }
                    projectedArgs[i] = null;
                } else {
                    projectedArgs[i] = convertArgument(arg, parameterTypes[i]);
                }
            }

            // Ensure the method is accessible
            setMethodAccessable.invoke(matchingMethod, true);

            // Invoke the static method (pass null as the instance for static methods)
            return invokeMethodHandle.invoke(matchingMethod, null, projectedArgs);
        } catch (Throwable e) {
            if (e instanceof InvocationTargetException) {
                Throwable cause = ((InvocationTargetException) e).getTargetException();
                System.err.println("Root cause of InvocationTargetException: " + cause.getClass().getName());
                cause.printStackTrace(); // Print root cause
            } else {
                e.printStackTrace();
            }
            throw new RuntimeException(e);
        }
    }
    public static String findFieldWithMatchingCtor(Object instance) {
        try {
            Class<?> currentClass = instance.getClass();

            while (currentClass != null) {
                Object[] fields = currentClass.getDeclaredFields();

                for (Object field : fields) {
                    try {
                        setFieldAccessibleHandle.invoke(field, true);
                        Object fieldValue = getFieldHandle.invoke(field, instance);
                        if (fieldValue == null) continue;

                        Class<?> fieldType = (Class<?>) getFieldTypeHandle.invoke(field);
                        if(fieldType.getConstructors().length!=0){
                            Object[] constructors = (Object[]) invokeMethod("getConstructors",fieldType);
                            Class<?>[] classes = (Class<?>[]) invokeMethod("getParameterTypes",constructors[0]);
                            if(classes.length==5){
                                if(classes[0]==float.class&&classes[1]==float.class&&classes[2]==boolean.class&&classes[3]==boolean.class){
                                    return (String) getFieldNameHandle.invoke(field);
                                }
                            }
                        }


                    } catch (Throwable inner) {
                        inner.printStackTrace();
                    }
                }

                currentClass = currentClass.getSuperclass();
            }

        } catch (Throwable e) {
            throw new RuntimeException("Failed to find matching constructor field", e);
        }

        return null;
    }



    public static Object invokeMethodWithAutoProjection(String methodName, Object instance, Object... arguments) {
        // Retrieve the method and its parameter types
        Pair<Object, Class<?>[]> methodPair = getMethodFromSuperclass(methodName, instance);

        // Check if the method was found
        if (methodPair == null) {
            try {
                throw new NoSuchMethodException("Method " + methodName + " not found in class hierarchy of " + instance.getClass().getName());
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        Object method = methodPair.one;
        Class<?>[] parameterTypes = methodPair.two;

        // Prepare arguments by projecting them to the correct types
        Object[] projectedArgs = new Object[parameterTypes.length];
        for (int index = 0; index < parameterTypes.length; index++) {
            Object arg = (arguments.length > index) ? arguments[index] : null;

            if (arg == null) {
                // If the expected type is a primitive type, throw an exception
                if (parameterTypes[index].isPrimitive()) {
                    throw new IllegalArgumentException("Argument at index " + index + " cannot be null for primitive type " + parameterTypes[index].getName());
                }
                projectedArgs[index] = null; // Keep nulls as null for reference types
            } else {
                // Try to convert the argument to the expected parameter type
                try {
                    projectedArgs[index] = convertArgument(arg, parameterTypes[index]);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Cannot convert argument at index " + index + " to " + parameterTypes[index].getName(), e);
                }
            }
        }

        // Ensure the method is accessible
        try {
            setMethodAccessable.invoke(method, true);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        // Invoke the method with the projected arguments
        try {
            return invokeMethodHandle.invoke(method, instance, projectedArgs);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    // Helper function to convert an argument to the expected type
    public static Object convertArgument(Object arg, Class<?> targetType) {
        if (targetType.isAssignableFrom(arg.getClass())) {
            return arg; // Use as-is if types match
        } else if (targetType.isPrimitive()) {
            // Handle primitive types by boxing
            if (targetType == int.class) {
                return ((Number) arg).intValue();
            } else if (targetType == long.class) {
                return ((Number) arg).longValue();
            } else if (targetType == double.class) {
                return ((Number) arg).doubleValue();
            } else if (targetType == float.class) {
                return ((Number) arg).floatValue();
            } else if (targetType == short.class) {
                return ((Number) arg).shortValue();
            } else if (targetType == byte.class) {
                return ((Number) arg).byteValue();
            } else if (targetType == boolean.class) {
                return arg;
            } else if (targetType == char.class) {
                return arg;
            } else {
                throw new IllegalArgumentException("Unsupported primitive type: " + targetType.getName());
            }
        } else {
            // For reference types, perform a cast if possible
            return targetType.cast(arg);
        }
    }
    public static Object invokeStaticMethod(Class<?> targetClass, String methodName, Object... arguments) {
        try {
            // Retrieve the parameter types of the arguments
            Class<?>[] parameterTypes = new Class[arguments.length];
            for (int i = 0; i < arguments.length; i++) {
                parameterTypes[i] = arguments[i].getClass();
            }

            // Find the method by its name and parameter types
            Object method = findStaticMethodByParameterTypes(targetClass, parameterTypes);
            if (method == null) {
                throw new NoSuchMethodException("Static method " + methodName + " not found in class " + targetClass.getName());
            }

            // Invoke the method (static methods do not need an instance)
            return invokeMethodHandle.invoke(method, null, arguments);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    public static Object findFieldByType(Object targetObject, Class<?> fieldType) {
        try {
            Class<?> currentClass = targetObject.getClass();

            while (currentClass != null) {
                // Retrieve all declared fields dynamically
                Object[] fields = currentClass.getDeclaredFields();

                for (Object field : fields) {
                    try {
                        // Retrieve field type dynamically
                        Class<?> fieldClass = (Class<?>) invokeMethodWithAutoProjection("getType",field);

                        // Check if the field type matches or is assignable
                        if (fieldType.isAssignableFrom(fieldClass)) {
                            setFieldAccessibleHandle.invoke(field, true);
                            return  getFieldHandle.invoke(field, targetObject);
                        }
                    } catch (Throwable e) {
                        // Handle exceptions gracefully during field inspection
                        e.printStackTrace();
                    }
                }

                // Move to the superclass dynamically
                currentClass = currentClass.getSuperclass();
            }

            // Return null if no matching field is found
            return null;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    public static Object findStaticMethodByParameterTypes(Class<?> targetClass, Class<?>... parameterTypes) {
        try {
            Class<?> currentClass = targetClass;

            while (currentClass != null) {
                // Retrieve all declared methods dynamically
                Object[] methods = currentClass.getDeclaredMethods();

                for (Object method : methods) {
                    try {
                        // Retrieve method modifiers dynamically
                        int modifiers = (int) getModifiersHandle.invoke(method);

                        // Check if the method is static
                        if ((modifiers & 0x0008) != 0) { // 0x0008 is the `static` modifier bit
                            // Retrieve parameter types dynamically
                            Class<?>[] methodParamTypes = (Class<?>[]) getParameterTypesHandle.invoke(method);

                            // Compare parameter types
                            if (areParameterTypesMatching(methodParamTypes, parameterTypes)) {
                                return method; // Return the matching method
                            }
                        }
                    } catch (Throwable e) {
                        // Handle exceptions gracefully during method inspection
                        e.printStackTrace();
                    }
                }

                // Move to the superclass dynamically
                currentClass = (Class<?>) invokeMethodHandle.invoke(currentClass, "getSuperclass");
            }

            // Return null if no matching method is found
            return null;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }




    // Helper function to compare parameter types
    private static boolean areParameterTypesMatching(Class<?>[] methodParamTypes, Class<?>[] targetParamTypes) {
        if (methodParamTypes.length != targetParamTypes.length) {
            return false;
        }

        for (int i = 0; i < methodParamTypes.length; i++) {
            if (!methodParamTypes[i].isAssignableFrom(targetParamTypes[i])) {
                return false;
            }
        }

        return true;
    }


    public static List<UIComponentAPI> getChildren(UIPanelAPI panelAPI) {
        return ReflectionUtilis.getChildrenCopy(panelAPI);
    }
}