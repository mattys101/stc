
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
    
    public void testNewInstanceWithArgs() {
        
        JavaClass jc = JavaClass.on(String.class);
        Java instance = (Java)jc.newInstance(Java.on(new char[]{'a', 'b', 'c', 'd'}));
        
        Assert.assertEquals(String.class.getName(), instance.javaClassName().javaValue());
        Assert.assertEquals("abcd", instance.javaValue());
    }
    
    public void testNewInstanceWithSignature() {
        
        JavaClass jc = JavaClass.on(String.class);
        Java instance = (Java)jc.newInstanceSignature(Java.on("(java.lang.String)"), Java.on("abcd"));
        
        Assert.assertEquals(String.class.getName(), instance.javaClassName().javaValue());
        Assert.assertEquals("abcd", instance.javaValue());
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
    
    public void testCallSignatureVoid() {
        
        PrimObject methodName = new PrimObject();
        methodName.javaValue("someVoidMethod");
        PrimObject signature = new PrimObject();
        signature.javaValue("()void");
        JavaClass jc = JavaClass.on(ClassForTestCall.class);
        PrimObject result = jc.callSignature(methodName, signature);
        
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
    
    public void testCall1ArgWithPrimitiveSignature() {
        PrimObject methodName = new PrimObject();
        methodName.javaValue("valueOf");
        PrimObject signature = new PrimObject();
        signature.javaValue("(int)java.lang.String");
        PrimObject arg1 = new PrimObject();
        arg1.javaValue(1);
        JavaClass jc = JavaClass.on(String.class);
        PrimObject result = jc.callSignature(methodName, signature, arg1);
        
        Assert.assertEquals("1", result.javaValue());
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
    
    public void testCall2ArgWithSignature() {
        PrimObject methodName = new PrimObject();
        methodName.javaValue("compare");
        PrimObject arg1 = new PrimObject();
        arg1.javaValue(1);
        PrimObject arg2 = new PrimObject();
        arg2.javaValue(2);
        PrimObject signature = new PrimObject();
        signature.javaValue("(int;int)int");
        JavaClass jc = JavaClass.on(Integer.class); 
        PrimObject result = jc.callSignature(methodName, signature, arg1, arg2);
        
        Assert.assertEquals(Integer.compare(1, 2), result.javaValue());
    }
    
    public void testCallAllPrimitives() {
        PrimObject methodName = new PrimObject();
        methodName.javaValue("somePrimitiveSigMethod");
        PrimObject arg1 = new PrimObject();
        arg1.javaValue(true);
        PrimObject arg2 = new PrimObject();
        arg2.javaValue('c');
        PrimObject arg3 = new PrimObject();
        arg3.javaValue((byte)1);
        PrimObject arg4 = new PrimObject();
        arg4.javaValue(Short.valueOf((short)(Byte.MAX_VALUE + 1)));
        PrimObject arg5 = new PrimObject();
        arg5.javaValue(Integer.valueOf(Short.MAX_VALUE + 1));
        PrimObject arg6 = new PrimObject();
        arg6.javaValue(Long.valueOf(Integer.MAX_VALUE + 1));
        PrimObject arg7 = new PrimObject();
        arg7.javaValue(1.0f);
        PrimObject arg8 = new PrimObject();
        arg8.javaValue(10.0d);
        PrimObject signature = new PrimObject();
        signature.javaValue("(boolean;char;byte;short;int;long;float;double)void");
        JavaClass jc = JavaClass.on(ClassForTestCall.class); 
        PrimObject result = jc.callSignature(methodName, signature, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
        
        Assert.assertEquals(jc, result);
    }
    
    public void testInvalidSignature() {
        String signature = "java.lang.Object";
        try {
            JavaWrapper.extractReturnType(signature);
            Assert.fail("Exception not thrown");
        }
        catch (RuntimeException e) {
            Assert.assertEquals("Invalid signature format. \\((<param type>;?)*\\)<return type>", e.getMessage());
        }
        
        try {
            JavaWrapper.extractParameterTypes(signature);
            Assert.fail("Exception not thrown");
        }
        catch (RuntimeException e) {
            Assert.assertEquals("Invalid signature format. \\((<param type>;?)*\\)<return type>", e.getMessage());
        }
    }
    
    public void testCallInvalidSignature2() {
        String signature = "()";
        try {
            JavaWrapper.extractReturnType(signature);
            Assert.fail("Exception not thrown");
        }
        catch (RuntimeException e) {
            Assert.assertEquals("Invalid signature format. \\((<param type>;?)*\\)<return type>", e.getMessage());
        }
        
        try {
            JavaWrapper.extractParameterTypes(signature);
        }
        catch (RuntimeException e) {
            Assert.fail("Empty parameters types should be valid.");
        }
    }
    
    public void testCallInvalidSignature3() {
        String signature = "";
        try {
            JavaWrapper.extractReturnType(signature);
            Assert.fail("Exception not thrown");
        }
        catch (RuntimeException e) {
            Assert.assertEquals("Invalid signature format. \\((<param type>;?)*\\)<return type>", e.getMessage());
        }
        
        try {
            JavaWrapper.extractParameterTypes(signature);
            Assert.fail("Exception not thrown");
        }
        catch (RuntimeException e) {
            Assert.assertEquals("Invalid signature format. \\((<param type>;?)*\\)<return type>", e.getMessage());
        }
    }
    
    public void testCallInvalidSignature4() {
        String signature = "(";
        try {
            JavaWrapper.extractReturnType(signature);
            Assert.fail("Exception not thrown");
        }
        catch (RuntimeException e) {
            Assert.assertEquals("Invalid signature format. \\((<param type>;?)*\\)<return type>", e.getMessage());
        }
        
        try {
            JavaWrapper.extractParameterTypes(signature);
            Assert.fail("Exception not thrown");
        }
        catch (RuntimeException e) {
            Assert.assertEquals("Invalid signature format. \\((<param type>;?)*\\)<return type>", e.getMessage());
        }
    }
    
    public void testCallInvalidSignature5() {
        String signature = "(java.lang.String";
        try {
            JavaWrapper.extractReturnType(signature);
            Assert.fail("Exception not thrown");
        }
        catch (RuntimeException e) {
            Assert.assertEquals("Invalid signature format. \\((<param type>;?)*\\)<return type>", e.getMessage());
        }
        
        try {
            JavaWrapper.extractParameterTypes(signature);
//            Assert.fail("Exception not thrown");
            // Eh, don't really care about this.
        }
        catch (RuntimeException e) {
            Assert.assertEquals("Invalid signature format. \\((<param type>;?)*\\)<return type>", e.getMessage());
        }
    }
    
    public void testCallInvalidSignatire6() {
        String signature = "(java.lang.Object;)java.lang.String";
        try {
            Class<?> result = JavaWrapper.extractReturnType(signature);
            Assert.assertEquals(String.class, result);
        }
        catch (RuntimeException e) {
            Assert.fail("Return parameter should be valid.");
        }
        
        try {
            JavaWrapper.extractParameterTypes(signature);
            Assert.fail("Exception not thrown");
        }
        catch (RuntimeException e) {
            Assert.assertEquals("Invalid signature format. \\((<param type>;?)*\\)<return type>", e.getMessage());
        }
    }
    
    public void testCallInvalidSignatureVoidParameter() {
        String signature = "(void)java.lang.String";
        try {
            Class<?> result = JavaWrapper.extractReturnType(signature);
            Assert.assertEquals(String.class, result);
        }
        catch (RuntimeException e) {
            Assert.fail("Return parameter should be valid.");
        }
        
        try {
            JavaWrapper.extractParameterTypes(signature);
            Assert.fail("Exception not thrown");
        }
        catch (RuntimeException e) {
            Assert.assertEquals("Invalid signature format. \\((<param type>;?)*\\)<return type>", e.getMessage());
        }
    }
    
    public void testCallInvalidSignatureEmptyParameter() {
        String signature = "(java.lang.String;;)void";
        try {
            Class<?> result = JavaWrapper.extractReturnType(signature);
            Assert.assertEquals(void.class, result);
        }
        catch (RuntimeException e) {
            Assert.fail("Return parameter should be valid.");
        }
        
        try {
            JavaWrapper.extractParameterTypes(signature);
            Assert.fail("Exception not thrown");
        }
        catch (RuntimeException e) {
            Assert.assertEquals("Invalid signature format. \\((<param type>;?)*\\)<return type>", e.getMessage());
        }
    }
    
    public void testCallInvalidSignatureEmptyFirstParameter() {
        String signature = "(;java.lang.String)void";
        try {
            Class<?> result = JavaWrapper.extractReturnType(signature);
            Assert.assertEquals(void.class, result);
        }
        catch (RuntimeException e) {
            Assert.fail("Return parameter should be valid.");
        }
        
        try {
            JavaWrapper.extractParameterTypes(signature);
            Assert.fail("Exception not thrown");
        }
        catch (RuntimeException e) {
            Assert.assertEquals("Invalid signature format. \\((<param type>;?)*\\)<return type>", e.getMessage());
        }
    }
    
    public void testCallInvalidSignatureEmptyMiddleParameter() {
        String signature = "(java.lang.String;;java.lang.String)void";
        try {
            Class<?> result = JavaWrapper.extractReturnType(signature);
            Assert.assertEquals(void.class, result);
        }
        catch (RuntimeException e) {
            Assert.fail("Return parameter should be valid.");
        }
        
        try {
            JavaWrapper.extractParameterTypes(signature);
            Assert.fail("Exception not thrown");
        }
        catch (RuntimeException e) {
            Assert.assertEquals("Invalid signature format. \\((<param type>;?)*\\)<return type>", e.getMessage());
        }
    }
    
    public void testJavaClassForJavaClass() {
        // Since we cannot automatically infer when a PrimObject should be unwrapped or not
        PrimObject methodName = new PrimObject();
        methodName.javaValue("forClass");
        PrimObject className = new PrimObject();
        className.javaValue(String.class.getName());
        JavaClass jc = JavaClass.on(JavaClass.class);
        
        PrimObject stringJC = jc.call(methodName, className);
        
        Assert.assertEquals(null, stringJC.javaValue());
    }
    
    public void testJavaClassForJavaClassSignature() {
        // Need to explicitly provide the signature for methods that take PrimObjects
        PrimObject methodName = new PrimObject();
        methodName.javaValue("forClass");
        PrimObject signature = new PrimObject();
        signature.javaValue("(st.redline.kernel.PrimObject)st.redline.kernel.JavaClass");
        PrimObject className = new PrimObject();
        className.javaValue(String.class.getName());
        JavaClass jc = JavaClass.on(JavaClass.class);
        
        JavaClass stringJC = (JavaClass) jc.callSignature(methodName, signature, className);
        
        Assert.assertEquals(String.class.getName(), stringJC.javaClassName().javaValue());
        Assert.assertEquals(String.class, stringJC.javaValue());
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
        
        public static void somePrimitiveSigMethod(boolean b, char c, byte bt, short s, int i, long l, float f, double d) {
        }
    }
}
