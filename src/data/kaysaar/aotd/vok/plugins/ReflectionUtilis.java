package data.kaysaar.aotd.vok.plugins;

import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.api.util.Pair;
import com.fs.starfarer.campaign.CampaignEngine;
import com.fs.starfarer.ui.impl.CargoTooltipFactory;
import kaysaar.bmo.buildingmenu.BuildingMenuMisc;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
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
    private static final MethodHandle getMethodNameHandle;
    private static final MethodHandle invokeMethodHandle;
    private static final MethodHandle setMethodAccessable;
    private static final MethodHandle getModifiersHandle;
    private static final MethodHandle  getParameterTypesHandle;

    static {
        try {
            fieldClass = Class.forName("java.lang.reflect.Field", false, Class.class.getClassLoader());
            setFieldHandle = lookup.findVirtual(fieldClass, "set", MethodType.methodType(Void.TYPE, Object.class, Object.class));
            getFieldHandle = lookup.findVirtual(fieldClass, "get", MethodType.methodType(Object.class, Object.class));
            getFieldNameHandle = lookup.findVirtual(fieldClass, "getName", MethodType.methodType(String.class));
            setFieldAccessibleHandle = lookup.findVirtual(fieldClass, "setAccessible", MethodType.methodType(Void.TYPE, boolean.class));

            methodClass = Class.forName("java.lang.reflect.Method", false, Class.class.getClassLoader());
            getMethodNameHandle = lookup.findVirtual(methodClass, "getName", MethodType.methodType(String.class));
            invokeMethodHandle = lookup.findVirtual(methodClass, "invoke", MethodType.methodType(Object.class, Object.class, Object[].class));
            setMethodAccessable = lookup.findVirtual(methodClass, "setAccessible", MethodType.methodType(Void.TYPE, boolean.class));
            getModifiersHandle = lookup.findVirtual(methodClass, "getModifiers", MethodType.methodType(int.class));
            getParameterTypesHandle = lookup.findVirtual(methodClass, "getParameterTypes", MethodType.methodType(Class[].class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
            throw new RuntimeException(e);
        }
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
            return null;
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