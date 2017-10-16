/*
 * Redline Smalltalk, Copyright (c) James C. Ladd. All rights reserved. See LICENSE in the
 * root of this distribution.
 */

package st.redline.kernel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

        Log LOG = LogFactory.getLog(JavaWrapper.class);
        LOG.info("Looking up class " + className);

        try {
            return Class.forName((String) className.javaValue());
        }
        catch (ClassNotFoundException | ClassCastException e) {
            LOG.error("Couldn't find the class.", e);
        }

        return null;
    }
    
    public static Method findMethod(Class<?> aClass, String methodName, boolean exact, Class<?> returnType, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        // XXX: Ignore parameter and return types for the moment
        return aClass.getMethod(methodName, parameterTypes);
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
        LOG.info("Instantiating class " + aClass.getName());

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
        LOG.trace("Calling " + aClass.getName() + "." + methodName + " "
                + Arrays.toString(args));

        try {
            Method method = findMethod(aClass, methodName, false, null);
            // A side effect of this is that a Java object can (accidentally?) call a 
            // static method. 
            // XXX: As a general rule I am against this, but leaving as is for the moment.
            Object result = method.invoke(receiver.javaValue());
            LOG.trace("Method result = " + String.valueOf(result));
            
            return method.getReturnType().equals(void.class) ? receiver : wrap(result);
        }
        catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            LOG.error("Couldn't find method");
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

    public default PrimObject callSignature(Class<?> aClass, Object reciever,
                                            String methodName, String signature,
                                            PrimObject... args) {

        Log LOG = LogFactory.getLog(JavaWrapper.class);
        LOG.trace("Calling method " + aClass.getName() + "." + methodName + "(" + signature
                + ") " + Arrays.toString(args));

        return new PrimObject();
    }

}
