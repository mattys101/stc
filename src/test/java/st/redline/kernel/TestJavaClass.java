
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

    public void testCall() {

        PrimObject methodName = new PrimObject();
        methodName.javaValue("someMethod");
        JavaClass jc = JavaClass.on(ClassForTestCall.class);
        PrimObject result = jc.call(methodName);
        
        Assert.assertEquals(Boolean.TRUE, result.javaValue());
    }
    
    public void testCallVoid() {
        
        PrimObject methodName = new PrimObject();
        methodName.javaValue("someVoidMethod");
        JavaClass jc = JavaClass.on(ClassForTestCall.class);
        PrimObject result = jc.call(methodName);
        
        Assert.assertEquals(jc, result);
    }
    
    public void testCallInstanceCallingStatic() {
        
        // Need to determine if this is desired behaviour
        PrimObject methodName = new PrimObject();
        methodName.javaValue("someMethod");
        Java jc = (Java) JavaClass.on(ClassForTestCall.class).newInstance();
        PrimObject result = jc.call(methodName);
        
        Assert.assertEquals(Boolean.TRUE, result.javaValue());
    }
    
    public static class ClassForTestCall {
        
        public static void someVoidMethod() {
            System.out.println("executing someVoidMethod");
        }
        
        public static Boolean someMethod() {
            return true;
        }
    }
}
