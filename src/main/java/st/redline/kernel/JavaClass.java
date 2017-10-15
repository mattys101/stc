/*
 * Redline Smalltalk, Copyright (c) James C. Ladd. All rights reserved. See LICENSE in the
 * root of this distribution.
 */

package st.redline.kernel;

/**
 * @author Matt Selway
 */
public class JavaClass extends PrimObject implements JavaWrapper {

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
    
    // Will just be called #new: in Smalltalk
    public PrimObject newInstance() {
        return Java.on(this.newInstanceOf((Class<?>)javaValue()));
    }
}
