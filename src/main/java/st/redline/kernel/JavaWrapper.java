/*
 * Redline Smalltalk, Copyright (c) James C. Ladd. All rights reserved. See LICENSE in the
 * root of this distribution.
 */

package st.redline.kernel;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import st.redline.Smalltalk;
import st.redline.classloader.Script;

/**
 * Note: Exceptions are currently being caught and ignored. The plan is to raise the
 * appropriate Smalltalk Exceptions later.
 * 
 * @author Matt Selway
 */
public interface JavaWrapper extends Script {
    
    public static final Map<String, Class<?>> PRIMITIVE_TYPES = primitiveTypes();
    
    public static Map<String, Class<?>> primitiveTypes() {

        if (PRIMITIVE_TYPES == null) {
            // Initialise the Java primitive class mappings.
            Map<String, Class<?>> map = new HashMap<>(12);
            map.put("char", char.class);
            map.put("boolean", boolean.class);
            map.put("byte", byte.class);
            map.put("short", short.class);
            map.put("int", int.class);
            map.put("long", long.class);
            map.put("float", float.class);
            map.put("double", double.class);
            map.put("void", void.class);
            return Collections.unmodifiableMap(map);
        }
        return PRIMITIVE_TYPES;
    }
    
    public static Class<?> findClass(PrimObject className) {

        return findClass((String) className.javaValue());
    }

    public static Class<?> findClass(String className) {

        Log LOG = LogFactory.getLog(JavaWrapper.class);
        if (LOG.isTraceEnabled())
            LOG.trace("Looking up class " + className);
        
        try {
            Class<?> result = PRIMITIVE_TYPES.getOrDefault(className, null);
            return result == null ? Class.forName(className) : result;
        }
        catch (ClassNotFoundException | ClassCastException e) {
            LOG.error("Couldn't find the class.", e);
        }
        
        return null;
    }
    
    public static Constructor<?> findConstructor(Class<?> aClass, boolean exact, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        Log LOG = LogFactory.getLog(JavaWrapper.class);
        if (LOG.isTraceEnabled())
            LOG.trace("Searching " + (exact ? "(exact) " : "(not exact) ") + aClass.getName() + "(" + Arrays.toString(parameterTypes) + ")" );
        
        Constructor<?> match = null;
        for (Constructor<?> c : aClass.getConstructors()) {
            if (parameterTypes.length == c.getParameterCount() &&
                    classesCompatible(c.getParameterTypes(), parameterTypes, exact)) {
                if (match == null) match = c;
                else match = (Constructor<?>)moreSpecificMethod(match, c);
            }
        }
        
        if (match == null) {
            throw new NoSuchMethodException(aClass.getName() + "(" + Arrays.toString(parameterTypes) + ")");
        }
        
        if (LOG.isTraceEnabled())
            LOG.trace("Constructor found --- " + match.toString());
        
        return match;
    }
    
    public static Method findMethod(Class<?> aClass, String methodName, boolean exact, Class<?> returnType, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        Log LOG = LogFactory.getLog(JavaWrapper.class);
        if (LOG.isTraceEnabled())
            LOG.trace("Searching " + (exact ? "(exact) " : "(not exact) ") + (returnType == null ? "void" : returnType.getName()) + " " + aClass.getName() + "." + methodName + "(" + Arrays.toString(parameterTypes) + ")" );
        
        Method match = null;
        for (Method m : aClass.getMethods()) {
            if (m.getName().equals(methodName)) {
               if (returnType == null || isClassCompatible(m.getReturnType(), returnType, exact)) {
                   if (parameterTypes.length == m.getParameterCount() &&
                           classesCompatible(m.getParameterTypes(), parameterTypes, exact)) {
                       if (match == null) match = m;
                       else match = (Method)moreSpecificMethod(match, m);
                   }
               }
            }
        }
        
        if (match == null) {
            throw new NoSuchMethodException((returnType == null ? "void" : returnType.getName()) + " " + aClass.getName() + "." + methodName + "(" + Arrays.toString(parameterTypes) + ")");
        }
        
        if (LOG.isTraceEnabled())
            LOG.trace("Method found --- " + match.toString());
        
        return match;
    }

    public static Executable moreSpecificMethod(Executable m1, Executable m2) {
        // XXX: determine which method is more specific
        // Should be mostly the same for Constructors and Methods. Just need to include 
        // return type for Methods.
        Log LOG = LogFactory.getLog(JavaWrapper.class);
        if (LOG.isTraceEnabled())
            LOG.trace(m1.toString() + " <-> " + m2.toString());
        return m2;
    }

    public static boolean classesCompatible(Class<?>[] targetClasses, Class<?>[] searchClasses, boolean exact) {
        for (int i = 0, len = targetClasses.length; i  < len; i++) {
            if (!isClassCompatible(targetClasses[i], searchClasses[i], exact)) return false;
        }
        return true;
    }

    public static boolean isClassCompatible(Class<?> targetClass, Class<?> searchClass, boolean exact) {
        // void is compatible with everything regardless
        if (searchClass == void.class) return true;
        else if (targetClass.isPrimitive()) return isClassPrimitiveCompatible(targetClass, searchClass, exact);
        else if (exact) return targetClass.equals(searchClass);
        else return targetClass.isAssignableFrom(searchClass);
    }

    public static boolean isClassPrimitiveCompatible(Class<?> targetClass,
                                                     Class<?> searchClass,
                                                     boolean exact) {
        try {
            // XXX allow widening if not exact match?
            // I am trying to make the behaviour as natural as expected, but is that too much perhaps?
            return targetClass.equals(searchClass) ||
                    (!exact && targetClass.equals(searchClass.getField("TYPE").get(null)));
        }
        catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
               | SecurityException e) {
            
            return false;
        }
    }
    
    public static Class<?> extractReturnType(String signature) {
        
        int endBracket = signature.indexOf(')');
        if (endBracket == -1 || endBracket + 1 >= signature.length()) 
            throw new RuntimeException("Invalid signature format. \\((<param type>;?)*\\)<return type>");
        
        String returnTypeName = signature.substring(endBracket + 1);
        return findClass(returnTypeName);
    }
    
    public static Class<?>[] extractParameterTypes(String signature) {
        
        try {
            StringReader read = new StringReader(signature);
            ArrayList<Class<?>> types = new ArrayList<>();
            int next = -1;
            if ((next = read.read()) != '(') throw new RuntimeException("Invalid signature format. \\((<param type>;?)*\\)<return type>");
            
            read.mark(1); // for peeking (I can't believe a StringReader can't peek !?!)
            if ((next = read.read()) == ')') return new Class<?>[0];
            read.reset();

            do {
                types.add(readNextType(read));
            } while ((next = read.read()) != ')' && next > -1);
            
            return types.toArray(new Class<?>[types.size()]);
        }
        catch (IOException e) {
            // Should never happen since we are reading a string.
            throw new RuntimeException("Invalid signature format. \\((<param type>;?)*\\)<return type>");
        }
    }
    
    public static Class<?> readNextType(StringReader read) throws IOException {
        StringBuilder sb = new StringBuilder();
        int next = -1;
        read.mark(1);
        
        while (read.ready() && (next = read.read()) != ')' && next != ';' && next > -1) {
            sb.append((char)next);
            read.mark(1);
        }
        
        read.reset();
        
        Class<?> type = findClass(sb.toString());
        if (type == null || type == void.class) throw new RuntimeException("Invalid signature format. \\((<param type>;?)*\\)<return type>");
    
        return type;
    }

    public PrimObject javaClassName();
    public PrimObject javaClassName(PrimObject className);

    public PrimObject call(PrimObject methodName);
    public PrimObject call(PrimObject methodName, PrimObject arg1);
    public PrimObject call(PrimObject methodName, PrimObject arg1, PrimObject arg2);
    public PrimObject call(PrimObject methodName, PrimObject arg1, PrimObject arg2, PrimObject arg3);
    public PrimObject call(PrimObject methodName, PrimObject... args);
    
    public PrimObject callSignature(PrimObject methodName, PrimObject signature, PrimObject arg1);
    public PrimObject callSignature(PrimObject methodName, PrimObject signature, PrimObject arg1, PrimObject arg2);
    public PrimObject callSignature(PrimObject methodName, PrimObject signature, PrimObject arg1, PrimObject arg2, PrimObject arg3);
    public PrimObject callSignature(PrimObject methodName, PrimObject signature, PrimObject... args);
    
    public PrimObject field(PrimObject fieldName);

    public default Object newInstanceOf(Class<?> aClass) {

        Log LOG = LogFactory.getLog(JavaWrapper.class);
        if (LOG.isTraceEnabled())
            LOG.trace("Instantiating class " + aClass.getName());

        try {
            return aClass.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e) {
            LOG.error("Couldn't instantiate the class. No default constructor or it is hidden.",
                      e);
        }

        return null;
    }
    
    public default Object newInstanceOf(Class<?> aClass, PrimObject... args) {
        
        Log LOG = LogFactory.getLog(JavaWrapper.class);
        if (LOG.isTraceEnabled())
            LOG.trace("Instantiating class " + aClass.getName() + " with args " + Arrays.toString(args));
        
        Object[] javaArgs = new Object[args.length];
        Class<?>[] parameterTypes = new Class[args.length];
        for (int i = 0, len = parameterTypes.length ; i < len; i++) {
            Object o = unwrap(args[i]);
            javaArgs[i] = o;
            parameterTypes[i] = o == null ? void.class : o.getClass();
        }
        
        try {
            Constructor<?> constructor = findConstructor(aClass, false, parameterTypes);
            return constructor.newInstance(javaArgs);
        }
        catch (InstantiationException e) {
            LOG.error("Couldn't instantiate the class. No default constructor or it is hidden.",
                      e);
        }
        catch (NoSuchMethodException | SecurityException | IllegalAccessException e) {
            LOG.error("Couldn't find appropriate method", e);
            // Maybe send doesNotUnderstand?
        }
        catch (IllegalArgumentException e) {
            LOG.error("Receiver or method args invalid.", e);
        }
        catch (InvocationTargetException e) {
            LOG.error("Error occurred during method execution.", e);
        }
        
        return null;
    }
    
    public default Object newInstanceOf(Class<?> aClass, String signature, PrimObject... args) {
        
        Log LOG = LogFactory.getLog(JavaWrapper.class);
        if (LOG.isTraceEnabled())
            LOG.trace("Instantiating class " + aClass.getName() + "(" + signature
                      + ") " + " with args " + Arrays.toString(args));
        
        Class<?>[] parameterTypes = extractParameterTypes(signature);
        
        if (LOG.isTraceEnabled())
            LOG.trace("Types: params " + Arrays.toString(parameterTypes));
        
        Object[] javaArgs = new Object[args.length];
        for (int i = 0, len = args.length ; i < len; i++) {
            javaArgs[i] = PrimObject.class.isAssignableFrom(parameterTypes[i])
                    ? args[i]
                    : unwrap(args[i]);
        }
        
        try {
            Constructor<?> constructor = findConstructor(aClass, true, parameterTypes);
            return constructor.newInstance(javaArgs);
        }
        catch (InstantiationException e) {
            LOG.error("Couldn't instantiate the class. No default constructor or it is hidden.",
                      e);
        }
        catch (NoSuchMethodException | SecurityException | IllegalAccessException e) {
            LOG.error("Couldn't find appropriate method", e);
            // Maybe send doesNotUnderstand?
        }
        catch (IllegalArgumentException e) {
            LOG.error("Receiver or method args invalid.", e);
        }
        catch (InvocationTargetException e) {
            LOG.error("Error occurred during method execution.", e);
        }
        
        return null;
    }

    public default PrimObject call(Class<?> aClass, PrimObject receiver, String methodName,
                                   PrimObject... args) {

        Log LOG = LogFactory.getLog(JavaWrapper.class);
        if (LOG.isTraceEnabled())
            LOG.trace("Calling " + aClass.getName() + "." + methodName + " "
                + Arrays.toString(args));
        
        Object[] javaArgs = new Object[args.length];
        Class<?>[] parameterTypes = new Class[args.length];
        for (int i = 0, len = parameterTypes.length ; i < len; i++) {
            Object o = unwrap(args[i]);
            javaArgs[i] = o;
            parameterTypes[i] = o == null ? void.class : o.getClass();
        }

        try {
            Method method = findMethod(aClass, methodName, false, null, parameterTypes);
            // A side effect of this is that a Java object can (accidentally?) call a 
            // static method. 
            // XXX: As a general rule I am against this, but leaving as is for the moment.
            Object result = method.invoke(receiver.javaValue(), javaArgs);
            if (LOG.isTraceEnabled())
                LOG.trace("Method result = " + String.valueOf(result));
            
            return method.getReturnType().equals(void.class) ? receiver : wrap(result);
        }
        catch (NoSuchMethodException | SecurityException | IllegalAccessException e) {
            LOG.error("Couldn't find appropriate method", e);
            // Maybe send doesNotUnderstand?
        }
        catch (IllegalArgumentException e) {
            LOG.error("Receiver or method args invalid.", e);
        }
        catch (InvocationTargetException e) {
            LOG.error("Error occurred during method execution.", e);
        }

        return wrap(null);
    }
    
    public default PrimObject callSignature(Class<?> aClass, PrimObject receiver,
                                            String methodName, String signature,
                                            PrimObject... args) {
        
        Log LOG = LogFactory.getLog(JavaWrapper.class);
        if (LOG.isTraceEnabled())
            LOG.trace("Calling method " + aClass.getName() + "." + methodName + "(" + signature
                      + ") " + Arrays.toString(args));
        
        Class<?> returnType = extractReturnType(signature);
        Class<?>[] parameterTypes = extractParameterTypes(signature);
        
        if (LOG.isTraceEnabled())
            LOG.trace("Types: return " + returnType + " || params " + Arrays.toString(parameterTypes));
        
        Object[] javaArgs = new Object[args.length];
        for (int i = 0, len = args.length ; i < len; i++) {
            javaArgs[i] = PrimObject.class.isAssignableFrom(parameterTypes[i])
                    ? args[i]
                    : unwrap(args[i]);
        }
        
        try {
            Method method = findMethod(aClass, methodName, true, returnType, parameterTypes);
            // A side effect of this is that a Java object can (accidentally?) call a 
            // static method. 
            // XXX: As a general rule I am against this, but leaving as is for the moment.
            Object result = method.invoke(receiver.javaValue(), javaArgs);
            if (LOG.isTraceEnabled())
                LOG.trace("Method result = " + String.valueOf(result));
            
            return method.getReturnType().equals(void.class) ? receiver : wrap(result);
        }
        catch (NoSuchMethodException | SecurityException | IllegalAccessException e) {
            LOG.error("Couldn't find appropriate method", e);
            // Maybe send doesNotUnderstand?
        }
        catch (IllegalArgumentException e) {
            LOG.error("Receiver or method args invalid.", e);
        }
        catch (InvocationTargetException e) {
            LOG.error("Error occurred during method execution.", e);
        }

        return wrap(null);
    }
    
    public default PrimObject field(Class<?> aClass, PrimObject receiver, String fieldName) {
        Log LOG = LogFactory.getLog(JavaWrapper.class);
        if (LOG.isTraceEnabled())
            LOG.trace("Retrieving field " + aClass.getName() + "." + fieldName);
        
        try {
            // XXX similar to methods, this allows instances to retrieve static fields
            Field field = aClass.getField(fieldName);
            
            if (LOG.isTraceEnabled())
                LOG.trace("Field = " + field);
            
            return wrap(field.get(receiver.javaValue()));
        }
        catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            LOG.error("Couldn't find appropriate field", e);
        }
        
        return wrap(null);
    }

    public default Object unwrap(PrimObject o) {
        // XXX: need to handle any special cases
        return o.javaValue();
    }
    
    public default PrimObject wrap(Object o) {
        // XXX: need to return Smalltalk nil and other special cases
        if (o instanceof PrimObject) return (PrimObject) o;
        else if (o == null) return new PrimObject();
        else return Java.on(o);
    }
    
    
    // For bootstrapping. To be removed.
    public static Map<String, PrimClass> classes = new HashMap<>();

    @Override
    public default PrimObject sendMessages(Smalltalk smalltalk) {
        // MS TODO: currently for bootstrapping, will remove once we have it all 
        // implemented in Smalltalk source files. 

        PrimObject objectClass = smalltalk.resolve("Object", getClass().getName(), "st.redline.kernel");
        PrimClass clazz = (PrimClass) objectClass.perform(smalltalk.createSymbol(getClass().getSimpleName()), "subclass:");
        PrimClass metaClass = (PrimClass) clazz.clazz();
        
        classes.put(this.getClass().getName(), clazz);
        classes.put(this.getClass().getName() + " class", metaClass);
        
        PrimObject method = new PrimMethod().function((m, receiver, context) -> JavaClass.forClass(context.argumentAt(0)));
        method.javaValue("Method for:");
        metaClass.methodAtPut("for:", method);
        
        method = new PrimMethod().function((m, receiver, context) -> ((JavaWrapper) receiver).call(context.argumentAt(0)) );
        method.javaValue("Method call:");
        clazz.methodAtPut("call:", method);
        
        method = new PrimMethod().function((m, receiver, context) -> ((JavaWrapper) receiver).call(context.argumentAt(0), context.argumentAt(1)) );
        method.javaValue("Method call:with:");
        clazz.methodAtPut("call:with:", method);
        
        method = new PrimMethod().function((m, receiver, context) -> ((JavaWrapper) receiver).call(context.argumentAt(0), context.argumentAt(1), context.argumentAt(2)) );
        method.javaValue("Method call:with:with:");
        clazz.methodAtPut("call:with:with:", method);
        
        method = new PrimMethod().function((m, receiver, context) -> ((JavaWrapper) receiver).field(context.argumentAt(0)) );
        method.javaValue("Method field:");
        clazz.methodAtPut("field:", method);
        
        return (PrimObject) this;
    }

}
