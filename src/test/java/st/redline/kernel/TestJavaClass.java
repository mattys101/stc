
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
    
    public void testCallPrimitiveResult() {
        
        PrimObject methodName = new PrimObject();
        methodName.javaValue("somePrimitiveMethod");
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
    
    public void testCall1Arg() {
        PrimObject methodName = new PrimObject();
        methodName.javaValue("valueOf");
        PrimObject arg1 = new PrimObject();
        arg1.javaValue(System.out);
        JavaClass jc = JavaClass.on(String.class);
        PrimObject result = jc.call(methodName, arg1);
        
        Assert.assertEquals(System.out.toString(), result.javaValue());
    }
    
    public void testCall1ArgMultipleMatchingMethods() {
        PrimObject methodName = new PrimObject();
        methodName.javaValue("valueOf");
        PrimObject arg1 = new PrimObject();
        arg1.javaValue(1);
        JavaClass jc = JavaClass.on(String.class);
        PrimObject result = jc.call(methodName, arg1);
        
        Assert.assertEquals(String.valueOf(1), result.javaValue());
    }
    
    public void testCall1ArgNull() {
        // This is an expected failure for the moment, until a proper strategy for 
        // multiple matches is developed.
        PrimObject methodName = new PrimObject();
        methodName.javaValue("valueOf");
        PrimObject arg1 = new PrimObject();
        arg1.javaValue(null);
        JavaClass jc = JavaClass.on(String.class);
        PrimObject result = jc.call(methodName, arg1);
        
        Assert.assertEquals(String.valueOf((Object)null), result.javaValue());
    }
    
    public void testCall1ArgWithSignature() {
        PrimObject methodName = new PrimObject();
        methodName.javaValue("valueOf");
        PrimObject signature = new PrimObject();
        signature.javaValue("(java.lang.Object)java.lang.String");
        PrimObject arg1 = new PrimObject();
        arg1.javaValue(System.out);
        JavaClass jc = JavaClass.on(String.class);
        PrimObject result = jc.callSignature(methodName, signature, arg1);
        
        Assert.assertEquals(System.out.toString(), result.javaValue());
    }
    
    public void testCall1ArgNullWithSignature() {
        // This should be successful as we can call the 'correct' method using the signature.
        PrimObject methodName = new PrimObject();
        methodName.javaValue("valueOf");
        PrimObject arg1 = new PrimObject();
        PrimObject signature = new PrimObject();
        signature.javaValue("(java.lang.Object)java.lang.String");
        arg1.javaValue(null);
        JavaClass jc = JavaClass.on(String.class);
        PrimObject result = jc.callSignature(methodName, signature, arg1);
        
        Assert.assertEquals(String.valueOf((Object)null), result.javaValue());
    }
    
    public void testCallInvalidSignature() {
        // This should be successful as we can call the 'correct' method using the signature.
        PrimObject methodName = new PrimObject();
        methodName.javaValue("valueOf");
        PrimObject arg1 = new PrimObject();
        PrimObject signature = new PrimObject();
        signature.javaValue("java.lang.Object;java.lang.String");
        arg1.javaValue(null);
        JavaClass jc = JavaClass.on(String.class);

        try {
            jc.callSignature(methodName, signature, arg1);
            Assert.fail("Exception not thrown");
        }
        catch (RuntimeException e) {
            Assert.assertEquals("Invalid signature format. \\((<param type>;?)*\\)<return type>", e.getMessage());
        }
        
    }
    
    public static class ClassForTestCall {
        
        public static void someVoidMethod() {
            System.out.println("executing someVoidMethod");
        }
        
        public static Boolean someMethod() {
            return true;
        }
        
        public static boolean somePrimitiveMethod() {
            return true;
        }
    }
}
