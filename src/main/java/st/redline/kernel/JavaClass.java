/*
 * Redline Smalltalk, Copyright (c) James C. Ladd. All rights reserved. See LICENSE in the
 * root of this distribution.
 */

package st.redline.kernel;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import st.redline.Smalltalk;

/**
 * @author Matt Selway
 */
public class JavaClass extends PrimObject implements JavaWrapper {
    
    private static Log LOG = LogFactory.getLog(JavaClass.class);
    
    // XXX: instance variables will need to be converted to conform to PrimObject so
    // that this class fully conforms to Smalltalk.
    protected PrimObject javaClassName;
    
    // will just be #for: in Smalltalk
    public static JavaClass forClass(PrimObject className) {
        JavaClass jc = new JavaClass();
        jc.clazz(classes.get(JavaClass.class.getName()));
        jc.javaClassName(className);
        jc.javaValue(JavaWrapper.findClass(className));
        return jc;
    }
    
    public static JavaClass on(Class<?> aClass) {
        if (aClass == null) return null; // XXX: should return 'nil'
        
        JavaClass jc = new JavaClass();
        jc.clazz(classes.get(JavaClass.class.getName()));
        jc.javaValue(aClass);
        // XXX: to be converted into Smalltalk String
        PrimObject className = new PrimObject();
        className.javaValue(aClass.getName());
        jc.javaClassName(className);
        return jc;
    }
    
    protected JavaClass() {
    }
    
    public PrimObject javaClassName(PrimObject javaClassName) {
        this.javaClassName = javaClassName;
        return this;
    }
    
    public PrimObject javaClassName() {
        return this.javaClassName;
    }
    
    // Will just be called #new in Smalltalk
    public Java newInstance() {
        return Java.on(this.newInstanceOf((Class<?>)javaValue()));
    }
    
    // Will just be called #newWithArgs: in Smalltalk
    public Java newInstance(PrimObject... args) {
        return Java.on(this.newInstanceOf((Class<?>)javaValue(), args));
    }
    
    // Will just be called #newFromSignature:withArgs: in Smalltalk
    public Java newInstanceSignature(PrimObject signature, PrimObject... args) {
        return Java.on(this.newInstanceOf((Class<?>)javaValue(), (String)signature.javaValue(), args));
    }

    @Override
    public PrimObject call(PrimObject methodName) {
        if (LOG.isTraceEnabled())
            LOG.trace(this + " " + methodName);

        return this.call((Class<?>)javaValue(), this, (String)methodName.javaValue());
    }

    @Override
    public PrimObject call(PrimObject methodName, PrimObject arg1) {
        if (LOG.isTraceEnabled())
            LOG.trace(this + " " + methodName + " " + arg1);
        
        return this.call((Class<?>)javaValue(), this, (String)methodName.javaValue(), arg1);
    }

    @Override
    public PrimObject call(PrimObject methodName, PrimObject arg1, PrimObject arg2) {
        if (LOG.isTraceEnabled())
            LOG.trace(this + " " + methodName + " " + arg1 + " " + arg2);
        
        return this.call((Class<?>)javaValue(), this, (String)methodName.javaValue(), arg1, arg2);
    }

    @Override
    public PrimObject call(PrimObject methodName, PrimObject arg1, PrimObject arg2,
                           PrimObject arg3) {
        if (LOG.isTraceEnabled())
            LOG.trace(this + " " + methodName + " " + arg1 + " " + arg2 + " " + arg3);
        
        return this.call((Class<?>)javaValue(), this, (String)methodName.javaValue(), arg1, arg2, arg3);
    }

    @Override
    public PrimObject call(PrimObject methodName, PrimObject... args) {
        if (LOG.isTraceEnabled())
            LOG.trace(this + " " + methodName + " " + Arrays.toString(args));
        
        return this.call((Class<?>)javaValue(), this, (String)methodName.javaValue(), args);
    }

    @Override
    public PrimObject callSignature(PrimObject methodName, PrimObject signature,
                                    PrimObject arg1) {
        if (LOG.isTraceEnabled())
            LOG.trace(this + " " + methodName + "(" + signature + ") " + arg1);
        
        return this.callSignature((Class<?>)javaValue(), this, (String)methodName.javaValue(), (String)signature.javaValue(), arg1);
    }

    @Override
    public PrimObject callSignature(PrimObject methodName, PrimObject signature,
                                    PrimObject arg1, PrimObject arg2) {
        if (LOG.isTraceEnabled())
            LOG.trace(this + " " + methodName + "(" + signature + ") " + arg1 + " " + arg2);
        
        return this.callSignature((Class<?>)javaValue(), this, (String)methodName.javaValue(), (String)signature.javaValue(), arg1, arg2);
    }

    @Override
    public PrimObject callSignature(PrimObject methodName, PrimObject signature,
                                    PrimObject arg1, PrimObject arg2, PrimObject arg3) {
        if (LOG.isTraceEnabled())
            LOG.trace(this + " " + methodName + "(" + signature + ") " + arg1 + " " + arg2 + " " + arg3);
        
        return this.callSignature((Class<?>)javaValue(), this, (String)methodName.javaValue(), (String)signature.javaValue(), arg1, arg2, arg3);
    }

    @Override
    public PrimObject callSignature(PrimObject methodName, PrimObject signature,
                                    PrimObject... args) {
        if (LOG.isTraceEnabled())
            LOG.trace(this + " " + methodName + "(" + signature + ") " + Arrays.toString(args));
        
        return this.callSignature((Class<?>)javaValue(), this, (String)methodName.javaValue(), (String)signature.javaValue(), args);
    }

    @Override
    public PrimObject field(PrimObject fieldName) {
        if (LOG.isTraceEnabled())
            LOG.trace(this + "." + fieldName);
        
        return this.field((Class<?>)javaValue(), this, (String)fieldName.javaValue());
    }

    // for bootstrapping
    @Override
    public PrimObject sendMessages(Smalltalk smalltalk) {
    
        JavaWrapper.super.sendMessages(smalltalk);
        
        PrimClass clazz = classes.get(getClass().getName());
        
        PrimObject method = new PrimMethod().function((m, receiver, context) -> ((JavaClass)receiver).newInstance());
        method.javaValue("Method new");
        clazz.methodAtPut("new", method);
        
        method = new PrimMethod().function((m, receiver, context) -> ((JavaClass)receiver).newInstance(getArgs(context)));
        method.javaValue("Method newWith:");
        clazz.methodAtPut("newWith:", method);
        
        method = new PrimMethod().function((m, receiver, context) -> ((JavaClass)receiver).newInstance(getArgs(context)));
        method.javaValue("Method newWith:with:");
        clazz.methodAtPut("newWith:with:", method);
        
        method = new PrimMethod().function((m, receiver, context) -> ((JavaClass)receiver).newInstance(getArgs(context)));
        method.javaValue("Method newWith:with:with:");
        clazz.methodAtPut("newWith:with:with:", method);
        
        method = new PrimMethod().function((m, receiver, context) -> ((JavaClass)receiver).newInstanceSignature(context.argumentAt(0), getArgs(context, 1)));
        method.javaValue("Method newSignature:with:");
        clazz.methodAtPut("newSignature:with:", method);
        
        method = new PrimMethod().function((m, receiver, context) -> ((JavaClass)receiver).newInstanceSignature(context.argumentAt(0), getArgs(context, 1)));
        method.javaValue("Method newSignature:with:with:");
        clazz.methodAtPut("newSignature:with:with:", method);
        
        method = new PrimMethod().function((m, receiver, context) -> ((JavaClass)receiver).newInstanceSignature(context.argumentAt(0), getArgs(context, 1)));
        method.javaValue("Method newWith:with:with:");
        clazz.methodAtPut("newSignature:with:with:with:", method);
        
        return this;
    }

    // for bootstrapping only
    private PrimObject[] getArgs(PrimContext context) {
        return getArgs(context, 0);
    }
    
    private PrimObject[] getArgs(PrimContext context, int startingAt) {
        
        int size = context.selector().split(":").length - startingAt;
        PrimObject[] args = new PrimObject[size];
        for (int i = startingAt; i < size; i++) {
            args[i] = context.argumentAt(i);
        }
        return args;
    }
    
}
