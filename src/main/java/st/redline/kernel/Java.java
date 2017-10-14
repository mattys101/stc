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
        return new Java();
    }
    
    public static Java on(Object o) {
        Java value = new Java();
        value.javaValue(o);
        // XXX: to be converted into Smalltalk String
        PrimObject className = new PrimObject();
        className.javaValue(o.getClass().getName());
        value.javaClassName(className);
        // javaClass
        return value;
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
