
package st.redline.kernel;

import junit.framework.Assert;

public class TestJava {

    public void testJavaNew() {
        
        PrimObject className = new PrimObject();
        className.javaValue(String.class.getName());
        Java j = Java.newInstance(className);
        
        Assert.assertEquals(String.class.getName(), j.javaClassName().javaValue());
        Assert.assertEquals("", j.javaValue());
    }
    
    public void testJavaOn() {
        
        Java j = Java.on("Some string value");
        
        Assert.assertEquals(String.class.getName(), j.javaClassName().javaValue());
        Assert.assertEquals("Some string value", j.javaValue());
    }
    
    public void testJavaOnPrimObject() {
        
        Java j1 = Java.on("The original string");
        Java j2 = Java.on(j1);
        
        Assert.assertEquals(j1, j2);
    }
}
