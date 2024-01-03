package data.kaysaar.aotd.vok.plugins

import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

class ReflectionUtilis {
    //Taken fully form Grand Colonies code
    fun setPrivateVariable(fieldName: String, instanceToModify: Any, newValue: Any?) {
        val fieldClass = Class.forName("java.lang.reflect.Field", false, Class::class.java.classLoader)
        val setMethod = MethodHandles.lookup()
            .findVirtual(fieldClass, "set", MethodType.methodType(Void.TYPE, Any::class.java, Any::class.java))
        val getNameMethod =
            MethodHandles.lookup().findVirtual(fieldClass, "getName", MethodType.methodType(String::class.java))
        val setAcessMethod = MethodHandles.lookup().findVirtual(
            fieldClass,
            "setAccessible",
            MethodType.methodType(Void.TYPE, Boolean::class.javaPrimitiveType)
        )

        val instancesOfFields: Array<out Any> = instanceToModify.javaClass.getDeclaredFields()
        for (obj in instancesOfFields) {
            setAcessMethod.invoke(obj, true)
            val name = getNameMethod.invoke(obj)
            if (name.toString() == fieldName) {
                setMethod.invoke(obj, instanceToModify, newValue)
            }
        }
    }

     fun getPrivateVariable(fieldName: String, instanceToGetFrom: Any): Any? {
        val fieldClass = Class.forName("java.lang.reflect.Field", false, Class::class.java.classLoader)
        val getMethod = MethodHandles.lookup()
            .findVirtual(fieldClass, "get", MethodType.methodType(Any::class.java, Any::class.java))
        val getNameMethod =
            MethodHandles.lookup().findVirtual(fieldClass, "getName", MethodType.methodType(String::class.java))
        val setAcessMethod = MethodHandles.lookup().findVirtual(
            fieldClass,
            "setAccessible",
            MethodType.methodType(Void.TYPE, Boolean::class.javaPrimitiveType)
        )

        val instancesOfFields: Array<out Any> = instanceToGetFrom.javaClass.declaredFields
        for (obj in instancesOfFields) {
            setAcessMethod.invoke(obj, true)
            val name = getNameMethod.invoke(obj)
            if (name.toString() == fieldName) {
                return getMethod.invoke(obj, instanceToGetFrom)
            }
        }
        return null
    }
    fun getPrivateVariableFromSuperClass(fieldName: String, instanceToGetFrom: Any): Any? {
        val fieldClass = Class.forName("java.lang.reflect.Field", false, Class::class.java.classLoader)
        val getMethod = MethodHandles.lookup()
            .findVirtual(fieldClass, "get", MethodType.methodType(Any::class.java, Any::class.java))
        val getNameMethod =
            MethodHandles.lookup().findVirtual(fieldClass, "getName", MethodType.methodType(String::class.java))
        val setAcessMethod = MethodHandles.lookup().findVirtual(
            fieldClass,
            "setAccessible",
            MethodType.methodType(Void.TYPE, Boolean::class.javaPrimitiveType)
        )

        val instancesOfFields: Array<out Any> = instanceToGetFrom.javaClass.superclass.declaredFields
        for (obj in instancesOfFields) {
            setAcessMethod.invoke(obj, true)
            val name = getNameMethod.invoke(obj)
            if (name.toString() == fieldName) {
                return getMethod.invoke(obj, instanceToGetFrom)
            }
        }
        return null
    }

}