
package st.redline.kernel;

import junit.framework.Assert;

// Due to the shared code for the actual execution of method calls, the main call tests
// are in TestJavaClass and will not be included here  unless the code diverges.
// This testcase will mostly test the Java object creation and constructor calls (since
// classes don't have constructors).
public class TestJava {

    public void testJavaNew() {
        
        PrimObject className = new PrimObject();
        className.javaValue(String.class.getName());
        Java j = Java.newInstance(className);
        
        Assert.assertEquals(String.class.getName(), j.javaClassName().javaValue());
        Assert.assertEquals("", j.javaValue());
    }
    
    public void testJavaNewArg1() {

        PrimObject className = new PrimObject();
        className.javaValue(ClassForConstructorTest.class.getName());
        
        PrimObject arg1 = new PrimObject();
        arg1.javaValue("Test");
        
        Java j = Java.newInstance(className, arg1);
        
        Assert.assertEquals(ClassForConstructorTest.class.getName(), j.javaClassName().javaValue());
        Assert.assertEquals("Test", (j.call(Java.on("getArg1"))).javaValue());
        Assert.assertEquals("unassigned", (j.call(Java.on("getArg2"))).javaValue());
        Assert.assertEquals("unassigned", (j.call(Java.on("getArg3"))).javaValue());
        Assert.assertEquals("unassigned", (j.call(Java.on("getArg4"))).javaValue());
    }
    
    public void testJavaNewArg1Null() {
        
        PrimObject className = new PrimObject();
        className.javaValue(ClassForConstructorTest.class.getName());
        
        PrimObject arg1 = new PrimObject();
        arg1.javaValue(null);
        
        Java j = Java.newInstance(className, arg1);
        
        Assert.assertEquals(ClassForConstructorTest.class.getName(), j.javaClassName().javaValue());
        Assert.assertNotNull(j.javaValue());
        Assert.assertEquals(null, j.call(Java.on("getArg1")).javaValue());
        Assert.assertEquals("unassigned", (j.call(Java.on("getArg2"))).javaValue());
        Assert.assertEquals("unassigned", (j.call(Java.on("getArg3"))).javaValue());
        Assert.assertEquals("unassigned", (j.call(Java.on("getArg4"))).javaValue());
    }
    
    public void testJavaNewArg2() {
        
        PrimObject className = new PrimObject();
        className.javaValue(ClassForConstructorTest.class.getName());
        
        PrimObject arg1 = new PrimObject();
        arg1.javaValue("first arg");
        
        PrimObject arg2 = new PrimObject();
        arg2.javaValue(25);
        
        Java j = Java.newInstance(className, arg1, arg2);
        
        Assert.assertEquals(ClassForConstructorTest.class.getName(), j.javaClassName().javaValue());
        Assert.assertEquals("first arg", (j.call(Java.on("getArg1"))).javaValue());
        Assert.assertEquals(25, (j.call(Java.on("getArg2"))).javaValue());
        Assert.assertEquals("unassigned", (j.call(Java.on("getArg3"))).javaValue());
        Assert.assertEquals("unassigned", (j.call(Java.on("getArg4"))).javaValue());
    }
    
    public void testJavaNewArg3() {
        
        PrimObject className = new PrimObject();
        className.javaValue(ClassForConstructorTest.class.getName());
        
        PrimObject arg1 = new PrimObject();
        arg1.javaValue("first arg");
        
        PrimObject arg2 = new PrimObject();
        arg2.javaValue(25);
        
        PrimObject arg3 = new PrimObject();
        arg3.javaValue(true);
        
        Java j = Java.newInstance(className, arg1, arg2, arg3);
        
        Assert.assertEquals(ClassForConstructorTest.class.getName(), j.javaClassName().javaValue());
        Assert.assertEquals("first arg", (j.call(Java.on("getArg1"))).javaValue());
        Assert.assertEquals(25, (j.call(Java.on("getArg2"))).javaValue());
        Assert.assertEquals(true, (j.call(Java.on("getArg3"))).javaValue());
        Assert.assertEquals("unassigned", (j.call(Java.on("getArg4"))).javaValue());
    }
    
    public void testJavaNewArg4() {
        
        PrimObject className = new PrimObject();
        className.javaValue(ClassForConstructorTest.class.getName());
        
        PrimObject arg1 = new PrimObject();
        arg1.javaValue("first arg");
        
        PrimObject arg2 = new PrimObject();
        arg2.javaValue(25);
        
        PrimObject arg3 = new PrimObject();
        arg3.javaValue(true);
        
        PrimObject arg4 = new PrimObject();
        arg4.javaValue("this could be anything");
        
        Java j = Java.newInstance(className, arg1, arg2, arg3, arg4);
        
        Assert.assertEquals(ClassForConstructorTest.class.getName(), j.javaClassName().javaValue());
        Assert.assertEquals("first arg", (j.call(Java.on("getArg1"))).javaValue());
        Assert.assertEquals(25, (j.call(Java.on("getArg2"))).javaValue());
        Assert.assertEquals(true, (j.call(Java.on("getArg3"))).javaValue());
        Assert.assertEquals("this could be anything", (j.call(Java.on("getArg4"))).javaValue());
    }
    
    public void testJavaNewArg4Sig() {
        
        PrimObject className = new PrimObject();
        className.javaValue(ClassForConstructorTest.class.getName());
        
        PrimObject signature = new PrimObject();
        signature.javaValue("(java.lang.String;int;java.lang.Boolean;java.lang.Object)");
        
        PrimObject arg1 = new PrimObject();
        arg1.javaValue("first arg");
        
        PrimObject arg2 = new PrimObject();
        arg2.javaValue(25);
        
        PrimObject arg3 = new PrimObject();
        arg3.javaValue(Boolean.TRUE);
        
        PrimObject arg4 = new PrimObject();
        arg4.javaValue("this could be anything");
        
        Java j = Java.newInstanceSignature(className, signature, arg1, arg2, arg3, arg4);
        
        Assert.assertEquals(ClassForConstructorTest.class.getName(), j.javaClassName().javaValue());
        Assert.assertEquals("first arg", (j.call(Java.on("getArg1"))).javaValue());
        Assert.assertEquals(25, (j.call(Java.on("getArg2"))).javaValue());
        Assert.assertEquals(Boolean.FALSE, (j.call(Java.on("getArg3"))).javaValue());
        Assert.assertEquals("this could be anything", (j.call(Java.on("getArg4"))).javaValue());
    }
    
    public void testJavaOn() {
        
        Java j = Java.on("Some string value");
        
        Assert.assertEquals(String.class.getName(), j.javaClassName().javaValue());
        Assert.assertEquals("Some string value", j.javaValue());
    }
    
    public void testNestedJavaObjects() {
        
        Java j1 = Java.on("The original string");
        Java j2 = Java.on(j1);
        
        Assert.assertEquals(j1, j2);
    }
    
    public void testCalls() {
        PrimObject className = new PrimObject();
        className.javaValue(String.class.getName());
        PrimObject lengthMethod = new PrimObject();
        lengthMethod.javaValue("length");
        PrimObject concatMethod = new PrimObject();
        concatMethod.javaValue("concat");
        PrimObject substringMethod = new PrimObject();
        substringMethod.javaValue("substring");
        PrimObject regionMatches = new PrimObject();
        regionMatches.javaValue("regionMatches");
        
        Java j = Java.newInstance(className);
        
        Assert.assertEquals(0, j.call(lengthMethod).javaValue());
        
        Java j2 = Java.on("addendum");
        Java j3 = (Java) j.call(concatMethod, j2);
        
        Assert.assertEquals("addendum".length(), j3.call(lengthMethod).javaValue());
        
        PrimObject dum = j3.call(substringMethod, Java.on(Integer.valueOf(5)));
        Assert.assertEquals("dum", dum.javaValue());
        
        PrimObject add = j3.call(substringMethod, Java.on(0), Java.on(3));
        Assert.assertEquals("add", add.javaValue());
        
        PrimObject matches = j3.call(regionMatches, Java.on(5), dum, Java.on(0), Java.on(3));
        Assert.assertEquals(true, matches.javaValue());
        
        PrimObject contentEqualsSig = Java.on("(java.lang.StringBuffer)boolean");
        StringBuffer sb = new StringBuffer("add");
        PrimObject contentEquals = ((Java)add).callSignature(Java.on("contentEquals"), contentEqualsSig, Java.on(sb));
        Assert.assertEquals(true, contentEquals.javaValue());
        sb.append("not anymore");
        contentEquals = ((Java)add).callSignature(Java.on("contentEquals"), contentEqualsSig, Java.on(sb));
        Assert.assertEquals(false, contentEquals.javaValue());

        // We can even force it to use the more general method
        PrimObject contentEqualsSigGen = Java.on("(java.lang.CharSequence)boolean");
        sb = new StringBuffer("add");
        contentEquals = ((Java)add).callSignature(Java.on("contentEquals"), contentEqualsSigGen, Java.on(sb));
        Assert.assertEquals(true, contentEquals.javaValue());
        sb.append("not anymore");
        contentEquals = ((Java)add).callSignature(Java.on("contentEquals"), contentEqualsSigGen, Java.on(sb));
        Assert.assertEquals(false, contentEquals.javaValue());
        
    }
    
    public void testGetField() {
        PrimObject className = new PrimObject();
        className.javaValue(ClassForConstructorTest.class.getName()); 
        Java jc = Java.newInstance(className);
        
        PrimObject fieldName = new PrimObject();
        fieldName.javaValue("arg1");
        
        PrimObject fieldName2 = new PrimObject();
        fieldName2.javaValue("arg2");
        
        PrimObject result = jc.field(fieldName);
        
        Assert.assertEquals("unassigned", result.javaValue());
        
        result = jc.field(fieldName2);
        Assert.assertEquals(null, result.javaValue());
    }
    
    public static class ClassForConstructorTest {
        
        public Object arg1;
        private Object arg2;
        private Object arg3;
        private Object arg4;

        public ClassForConstructorTest() {
            this.arg1 = "unassigned";
            this.arg2 = "unassigned";
            this.arg3 = "unassigned";
            this.arg4 = "unassigned";
        }
        
        public ClassForConstructorTest(String arg1) {
            this();
            this.arg1 = arg1;
        }

        public ClassForConstructorTest(String arg1, int arg2) {
            this(arg1);
            this.arg2 = arg2;
        }
        
        public ClassForConstructorTest(String arg1, int arg2, boolean arg3) {
            this(arg1, arg2);
            this.arg3 = arg3;
        }
        
        public ClassForConstructorTest(String arg1, int arg2, boolean arg3, Object arg4) {
            this(arg1, arg2, arg3);
            this.arg4 = arg4;
        }
        
        public ClassForConstructorTest(String arg1, int arg2, Boolean arg3, Object arg4) {
            this(arg1, arg2, !arg3);
            this.arg4 = arg4;
        }

        public Object getArg1() {

            return arg1;
        }

        public Object getArg2() {

            return arg2;
        }

        public Object getArg3() {

            return arg3;
        }

        public Object getArg4() {

            return arg4;
        }

    }
}
