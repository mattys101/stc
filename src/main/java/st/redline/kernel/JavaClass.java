/*
 * Redline Smalltalk, Copyright (c) James C. Ladd. All rights reserved. See LICENSE in the
 * root of this distribution.
 */

package st.redline.kernel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.emory.mathcs.backport.java.util.Arrays;

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
        jc.javaClassName(className);
        jc.javaValue(JavaWrapper.findClass(className));
        return jc;
    }
    
    public static JavaClass on(Class<?> aClass) {
        if (aClass == null) return null; // XXX: should return 'nil'
        
        JavaClass jc = new JavaClass();
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
}
