
package st.redline.kernel;

import junit.framework.Assert;

public class TestJavaClass {

    public void testJavaClassFor() {
        
        PrimObject className = new PrimObject();
        className.javaValue(String.class.getName());
        JavaClass jc = JavaClass.forClass(className);
        
        Assert.assertEquals(String.class.getName(), jc.javaClassName().javaValue());
        Assert.assertEquals(String.class , jc.javaValue());
    }
    
    public void testJavaOn() {
        
        JavaClass jc = JavaClass.on(String.class);
        
        Assert.assertEquals(String.class.getName(), jc.javaClassName().javaValue());
        Assert.assertEquals(String.class, jc.javaValue());
    }
    
    public void testNewInstance() {
        
        JavaClass jc = JavaClass.on(String.class);
        Java instance = (Java)jc.newInstance();
        
        Assert.assertEquals(String.class.getName(), instance.javaClassName().javaValue());
        Assert.assertEquals("", instance.javaValue());
    }

}
