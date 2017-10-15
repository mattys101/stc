/* Redline Smalltalk, Copyright (c) James C. Ladd. All rights reserved. See LICENSE in the root of this distribution. */
package st.redline.kernel;

/**
 * @author Matt Selway
 */
public class Java extends PrimObject implements JavaWrapper {

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
}
