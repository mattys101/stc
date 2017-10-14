/* Redline Smalltalk, Copyright (c) James C. Ladd. All rights reserved. See LICENSE in the root of this distribution. */

package st.redline.kernel;

/**
 * @author Matt Selway
 */
public interface JavaWrapper {

    public static Class<?> findClass(PrimObject className) {
        
        try {
            return Class.forName((String)className.javaValue());
        }
        catch (ClassNotFoundException | ClassCastException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }
    
    public default Object newInstanceOf(Class<?> aClass) {
        return null;
    }
    
    public PrimObject javaClassName();
    public PrimObject javaClassName(PrimObject className);
}
