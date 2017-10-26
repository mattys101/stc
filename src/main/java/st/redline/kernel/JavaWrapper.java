/*
 * Redline Smalltalk, Copyright (c) James C. Ladd. All rights reserved. See LICENSE in the
 * root of this distribution.
 */

package st.redline.kernel;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * Note: Exceptions are currently being caught and ignored. The plan is to raise the
 * appropriate Smalltalk Exceptions later.
 * 
 * @author Matt Selway
 */
public interface JavaWrapper {
    
    public static Class<?> findClass(PrimObject className) {

        return findClass((String) className.javaValue());
    }

    public static Class<?> findClass(String className) {

        Log LOG = LogFactory.getLog(JavaWrapper.class);
        if (LOG.isTraceEnabled())
            LOG.trace("Looking up class " + className);

        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException | ClassCastException e) {
            LOG.error("Couldn't find the class.", e);
        }

        return null;
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
                       else match = moreSpecificMethod(match, m);
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

    public static Method moreSpecificMethod(Method match, Method m) {
        // XXX: determine which method is more specific
        Log LOG = LogFactory.getLog(JavaWrapper.class);
        if (LOG.isTraceEnabled())
            LOG.trace(match.toString() + " <-> " + m.toString());
        return m;
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
            // I am trying to make the behaviour as natural as expected, but is it too much perhaps?
            return searchClass.getField("TYPE").get(null).equals(targetClass);
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
        // XXX: handle primitive type mappings
        return findClass(returnTypeName);
    }

    public static Class<?>[] extractParameterTypes(String signature) {
        
        StringReader read = new StringReader(signature);
        ArrayList<Class<?>> types = new ArrayList<>();
        try {
            int next = read.read();
            if (next != '(') throw new RuntimeException("Invalid signature format. \\((<param type>;?)*\\)<return type>");
            
            StringBuilder sb = new StringBuilder();
            while (read.ready() && (next = read.read()) != ')') {
                if (next == ';') {
                    // XXX: handle primitive type mappings
                    types.add(findClass(sb.toString()));
                    sb = new StringBuilder();
                }
                else {
                    sb.append((char)next);
                }
            }
            
            if (sb.length() > 0) {
                // single type with no terminating ';'
                // XXX: handle primitive type mappings
                types.add(findClass(sb.toString()));
                sb = new StringBuilder();
            }
        }
        catch (IOException e) {
            // Should never happen since we are reading a string.
            throw new RuntimeException("Invalid signature format. \\((<param type>;?)*\\)<return type>");
        }
        
        return types.toArray(new Class<?>[types.size()]);
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

    public default PrimObject call(Class<?> aClass, PrimObject receiver, String methodName,
                                   PrimObject... args) {

        Log LOG = LogFactory.getLog(JavaWrapper.class);
        if (LOG.isTraceEnabled())
            LOG.trace("Calling " + aClass.getName() + "." + methodName + " "
                + Arrays.toString(args));
        
        // XXX: think about how we will deal with calls that actually take PrimObject. It 
        // might be that you would 'have' to use the callSignature variants to specify that
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
        catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            LOG.error("Couldn't find appropriate method", e);
            // Maybe send doesNotUnderstand?
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
        
        Object[] javaArgs = new Object[args.length];
        for (int i = 0, len = args.length ; i < len; i++) {
            javaArgs[i] = unwrap(args[i]);
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
        catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            LOG.error("Couldn't find appropriate method", e);
            // Maybe send doesNotUnderstand?
        }

        return wrap(null);
    }

    public default Object unwrap(PrimObject o) {
        // XXX: need to handle any special cases
        return o.javaValue();
    }
    
    public default PrimObject wrap(Object o) {
        // XXX: need to return Smalltalk nil and other special cases
        return o == null ? new PrimObject() : Java.on(o);
    }

}
