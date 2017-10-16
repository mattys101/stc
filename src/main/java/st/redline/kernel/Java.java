/* Redline Smalltalk, Copyright (c) James C. Ladd. All rights reserved. See LICENSE in the root of this distribution. */
package st.redline.kernel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @author Matt Selway
 */
public class Java extends PrimObject implements JavaWrapper {
    
    private static Log LOG = LogFactory.getLog(Java.class);

    // XXX: instance variables will need to be converted to conform to PrimObject so
    // that this class fully conforms to Smalltalk.
    protected PrimObject javaClassName;
    
    // Will just be called #new: in Smalltalk
    public static Java newInstance(PrimObject className) {
        Class<?> aClass = JavaWrapper.findClass(className);
        Java instance = new Java();
        instance.javaValue(instance.newInstanceOf(aClass));
        instance.javaClassName(className);
        return instance;
    }
    
    public static Java on(Object o) {
        if (o == null) return null; // XXX: should be 'nil'
        if (o instanceof Java) return (Java) o; // avoid nested Java objects
        
        Java instance = new Java();
        instance.javaValue(o);
        // XXX: to be converted into Smalltalk String
        PrimObject className = new PrimObject();
        className.javaValue(o.getClass().getName());
        instance.javaClassName(className);
        return instance;
    }
    
    protected Java() {
        
    }
    
    public PrimObject javaClassName(PrimObject javaClassName) {
        this.javaClassName = javaClassName;
        return this;
    }
    
    public PrimObject javaClassName() {
        return this.javaClassName;
    }
    
    @Override
    public PrimObject call(PrimObject methodName) {
        if (LOG.isTraceEnabled())
            LOG.trace(this + " " + methodName);

        return this.call(JavaWrapper.findClass(javaClassName()), this, (String)methodName.javaValue());
    }

    @Override
    public PrimObject call(PrimObject methodName, PrimObject arg1) {
        if (LOG.isTraceEnabled())
            LOG.trace(this + " " + methodName + " " + arg1);
        
        return this.call(JavaWrapper.findClass(javaClassName()), this, (String)methodName.javaValue(), arg1);
    }

    @Override
    public PrimObject call(PrimObject methodName, PrimObject arg1, PrimObject arg2) {
        if (LOG.isTraceEnabled())
            LOG.trace(this + " " + methodName + " " + arg1 + " " + arg2);
        
        return this.call(JavaWrapper.findClass(javaClassName()), this, (String)methodName.javaValue(), arg1, arg2);
    }

    @Override
    public PrimObject call(PrimObject methodName, PrimObject arg1, PrimObject arg2,
                           PrimObject arg3) {
        if (LOG.isTraceEnabled())
            LOG.trace(this + " " + methodName + " " + arg1 + " " + arg2 + " " + arg3);
        
        return this.call(JavaWrapper.findClass(javaClassName()), this, (String)methodName.javaValue(), arg1, arg2, arg3);
    }

    @Override
    public PrimObject call(PrimObject methodName, PrimObject... args) {
        if (LOG.isTraceEnabled())
            LOG.trace(this + " " + methodName + " " + Arrays.toString(args));
        
        return this.call(JavaWrapper.findClass(javaClassName()), this, (String)methodName.javaValue(), args);
    }

    @Override
    public PrimObject callSignature(PrimObject methodName, PrimObject signature,
                                    PrimObject arg1) {
        if (LOG.isTraceEnabled())
            LOG.trace(this + " " + methodName + "(" + signature + ") " + arg1);
        
        return this.callSignature(JavaWrapper.findClass(javaClassName()), this, (String)methodName.javaValue(), (String)signature.javaValue(), arg1);
    }

    @Override
    public PrimObject callSignature(PrimObject methodName, PrimObject signature,
                                    PrimObject arg1, PrimObject arg2) {
        if (LOG.isTraceEnabled())
            LOG.trace(this + " " + methodName + "(" + signature + ") " + arg1 + " " + arg2);
        
        return this.callSignature(JavaWrapper.findClass(javaClassName()), this, (String)methodName.javaValue(), (String)signature.javaValue(), arg1, arg2);
    }

    @Override
    public PrimObject callSignature(PrimObject methodName, PrimObject signature,
                                    PrimObject arg1, PrimObject arg2, PrimObject arg3) {
        if (LOG.isTraceEnabled())
            LOG.trace(this + " " + methodName + "(" + signature + ") " + arg1 + " " + arg2 + " " + arg3);
        
        return this.callSignature(JavaWrapper.findClass(javaClassName()), this, (String)methodName.javaValue(), (String)signature.javaValue(), arg1, arg2, arg3);
    }

    @Override
    public PrimObject callSignature(PrimObject methodName, PrimObject signature,
                                    PrimObject... args) {
        if (LOG.isTraceEnabled())
            LOG.trace(this + " " + methodName + "(" + signature + ") " + Arrays.toString(args));
        
        return this.callSignature(JavaWrapper.findClass(javaClassName()), this, (String)methodName.javaValue(), (String)signature.javaValue(), args);
    }
}
