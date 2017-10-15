/* Redline Smalltalk, Copyright (c) James C. Ladd. All rights reserved. See LICENSE in the root of this distribution. */

package st.redline.kernel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Matt Selway
 */
public interface JavaWrapper {

    public static Class<?> findClass(PrimObject className) {
        
        Log LOG = LogFactory.getLog(JavaWrapper.class);
        LOG.info("Looking up class " + className);
        
        try {
            return Class.forName((String)className.javaValue());
        }
        catch (ClassNotFoundException | ClassCastException e) {
            LOG.error("Couldn't find the class.", e);
        }
        
        return null;
    }
    
    public PrimObject javaClassName();
    public PrimObject javaClassName(PrimObject className);
    
    public default Object newInstanceOf(Class<?> aClass) {
        Log LOG = LogFactory.getLog(JavaWrapper.class);
        LOG.info("Instantiating class " + aClass.getName());
        
        try {
            return aClass.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e) {
            LOG.error("Couldn't instantiate the class. No default constructor or it is hidden.", e);
        }
        
        return null;
    }
    
}
