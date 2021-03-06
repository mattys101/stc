/* Redline Smalltalk, Copyright (c) James C. Ladd. All rights reserved. See LICENSE in the root of this distribution. */
package st.redline.kernel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import st.redline.Smalltalk;
import st.redline.classloader.Script;
import st.redline.classloader.SmalltalkClassLoader;
import st.redline.classloader.Source;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static st.redline.compiler.Trace.isTraceEnabled;

public class RedlineSmalltalk extends PrimObject implements Smalltalk {

    private static Log LOG = LogFactory.getLog(RedlineSmalltalk.class);

    private Map<String, PrimObject> classes = new HashMap<>();
    private Map<String, Map<String, Map<String, String>>> imports = new HashMap<>();
    private String currentPackage;
    private String currentClass;

    public RedlineSmalltalk() {
        this.javaValue("a RedlineSmalltalk");
        bootstrap();
    }

    private void bootstrap() {
        classes.put("st.redline.kernel.PrimObject", createPrimObjectClass());
        classes.put("st.redline.kernel.Smalltalk", this);
        
        // 'this' is really an instance of the RedlineSmalltalk class registered as the global 'Smalltalk'
        // Minimal bootstrap
        PrimClass redlineClass = new PrimClass("RedlineSmalltalk", false);
        redlineClass.methodAtPut("package:in:", new PrimPackageInMethod());
        redlineClass.methodAtPut("import:", new PrimImportMethod());
        this.clazz(redlineClass);
        // Now create the class and reassign 'this' to the new class.
        tryCompileClass("st.redline.kernel.RedlineSmalltalkClass");
        redlineClass = (PrimClass)classes.get("st.redline.kernel.RedlineSmalltalkClass");
        redlineClass.methodAtPut("package:in:", new PrimPackageInMethod());
        redlineClass.methodAtPut("import:", new PrimImportMethod());
        this.clazz(redlineClass);
        
        // load Object, Behavior, ClassDescription, Metaclass
        // Metaclass needs to be loaded before Class so that it is present when the 
        // definition of the subclass: method on Class takes over from the PrimSubclassMethod.
        tryCompileClass("st.redline.kernel.Metaclass");
        tryCompileClass("st.redline.kernel.Class"); // Load Class
        
        // TODO: put the Smalltalk class in the correct location of the hierarchy
        ((PrimClass)this.clazz()).superclass(classes.get("st.redline.kernel.Object"));
        ((PrimClass)this.clazz().clazz()).superclass(classes.get("st.redline.kernel.Object").clazz());
        
        // Rewire the class hierarchy, introduce the circular Metaclass and Class aspects.
        PrimClass primObject = (PrimClass)classes.get("st.redline.kernel.PrimObject");
        PrimClass kernelClass = (PrimClass)classes.get("st.redline.kernel.Class");
        PrimClass metaclass = (PrimClass)classes.get("st.redline.kernel.Metaclass");
        classes.forEach((key, value) -> {
            if (value != this) value.clazz().clazz(metaclass);
        });
        ((PrimClass)primObject.clazz()).superclass(kernelClass);
        
        // Remove the original bootstrapped primitive methods
        primObject.removeMethodAt("class");
        primObject.removeMethodAt("subclass:");
        primObject.removeMethodAt("atSelector:put:");
        ((PrimClass)primObject.clazz()).removeMethodAt("class");
        ((PrimClass)primObject.clazz()).removeMethodAt("subclass:");
        ((PrimClass)primObject.clazz()).removeMethodAt("atSelector:put:");
    }
    
    private PrimObject createPrimObjectClass() {

        PrimClass primObject = new PrimClass("PrimObject", false);
        PrimClass primObjectMeta = new PrimClass(primObject.javaValue() + "(meta)", true);
        primObject.clazz(primObjectMeta);
        
        primObject.methodAtPut("class", new PrimClassMethod());
        primObject.methodAtPut("subclass:", new PrimSubclassMethod());
        primObject.methodAtPut("atSelector:put:", new PrimAtSelectorPutMethod());
        primObjectMeta.methodAtPut("class", new PrimClassMethod());
        primObjectMeta.methodAtPut("subclass:", new PrimSubclassMethod());
        primObjectMeta.methodAtPut("atSelector:put:", new PrimAtSelectorPutMethod());
        
        PrimMethod doesNotUnderstand = new PrimMethod();
        doesNotUnderstand.function = new TriFunction<PrimObject, PrimObject, PrimContext, PrimObject>() {
            
            @Override
            public PrimObject apply(PrimObject receiver, PrimObject method, PrimContext context) {
                // TODO: complete doesNotUnderstand: bootstrapping.
                throw new RuntimeException(receiver + " doesNotUnderstand: " + context.argumentAt(0) + ".");
            }
        };
        primObject.methodAtPut("doesNotUnderstand:", doesNotUnderstand);
        primObjectMeta.methodAtPut("doesNotUnderstand:", doesNotUnderstand);
        
        // XXX: temp workaround for the superclass hierarchy running out
        primObject.superclass(new PrimObject());
        
        return primObject;
    }

    @Override
    public PrimObject createString(String value) {
        // TODO.JCL - When Smalltalk String is available create one, ie: after bootstrap
        PrimObject object = new PrimObject();
        object.clazz(classes.get("st.redline.kernel.Object"));
        object.javaValue(value);
        return object;
    }

    @Override
    public PrimObject createSymbol(String value) {
        // TODO.JCL - When Smalltalk Symbol is available create one, ie: after bootstrap
        PrimObject object = new PrimObject();
        object.clazz(classes.get("st.redline.kernel.Object"));
        object.javaValue(value);
        return object;
    }

    @Override
    public PrimObject createInteger(String value) {
        // TODO.JCL - When Smalltalk Symbol is available create one, ie: after bootstrap
        PrimObject object = new PrimObject();
        object.clazz(classes.get("st.redline.kernel.Object"));
        object.javaValue(Integer.valueOf(value));
        return object;
    }

    @Override
    public PrimObject createCharacter(String value) {
        // TODO.JCL - When Smalltalk Symbol is available create one, ie: after bootstrap
        PrimObject object = new PrimObject();
        object.clazz(classes.get("st.redline.kernel.Object"));
        object.javaValue(value.charAt(0));
        return object;
    }

    @Override
    public PrimObject booleanSingleton(String value) {
        // TODO.JCL - When Smalltalk Symbol is available create one, ie: after bootstrap
        PrimObject object = new PrimObject();
        object.javaValue(Boolean.valueOf(value));
        return object;
    }

    @Override
    public PrimObject nilSingleton(String value) {
        // TODO.JCL - When Smalltalk Symbol is available create one, ie: after bootstrap
        PrimObject object = new PrimObject();
        object.clazz(classes.get("st.redline.kernel.Object"));
        object.javaValue("nil");
        return object;
    }

    public PrimObject resolve(String reference, String className, String packageName) {
        if (isTraceEnabled(LOG))
            LOG.trace(reference + " for " + className + " in " + packageName);
        String fullPath = importFor(packageName, className, reference);
        if (fullPath == null)
            if (reference.equals("PrimObject"))
                return classes.get("st.redline.kernel.PrimObject");
            else if (reference.equals("Smalltalk"))
                return classes.get("st.redline.kernel.Smalltalk");
            else
                throw new RuntimeException("Import for '" + reference + "' not found in " + packageName + "." + className);

        if (classes.containsKey(fullPath))
            return classes.get(fullPath);

        tryCompileClass(fullPath);

        if (!classes.containsKey(fullPath))
            throw new RuntimeException("Compilation of class '" + fullPath + "' did not result in a Smalltalk class.");

        return classes.get(fullPath);
    }

    @Override
    public PrimObject currentPackageForIs(String className, String packageName) {
        currentPackage = packageName;
        currentClass = className;
        return this;
    }

    @Override
    public String currentPackage() {
        return currentPackage;
    }

    @Override
    public String currentClass() {
        return currentClass;
    }

    @Override
    public PrimObject imports(String spec) {
        if (isTraceEnabled(LOG))
            LOG.trace("importing " + spec);
        SmalltalkClassLoader smalltalkClassLoader = classLoader();
        Source source = smalltalkClassLoader.findSource(spec);
        if (source != null) {
            if (isTraceEnabled(LOG))
                LOG.trace("found " + source.fullClassName());
            addImport(currentPackage(), currentClass(), source.className(), source.packageName() + "." + source.className());
            return this;
        } else
            throw new RuntimeException("Import not found: " + spec);
    }

    @Override
    public Smalltalk register(PrimObject newClass, String className) {
        if (isTraceEnabled(LOG))
            LOG.trace(className + " in " + currentPackage());
        String fullClassName = currentPackage() + "." + className;
        classes.put(fullClassName, newClass);
        addImport(currentPackage(), className, className, fullClassName);
        return this;
    }


    private void tryCompileClass(String path) {
        String oldPackage = currentPackage();
        String oldClass = currentClass();
        try {
            Class cls = tryLoadScript(path);
            Script script = newInstance(cls);
            script.sendMessages(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // Ensure the context is correct when return to processing the previous script
            currentPackageForIs(oldClass, oldPackage);  
        }
    }

    private Script newInstance(Class cls) throws IllegalAccessException, InstantiationException {
        return (Script) cls.newInstance();
    }

    private Class tryLoadScript(String fullClassName) throws ClassNotFoundException {
        return classLoader().loadScript(fullClassName);
    }

    private SmalltalkClassLoader classLoader() {
        return (SmalltalkClassLoader) Thread.currentThread().getContextClassLoader();
    }

    @SuppressWarnings("unchecked")
    private String importFor(String packageName, String className, String reference) {
        Map emptyMap = emptyMap();
        // TODO.JCL remove this conditional when importing is implemented.
        if (reference.equals("Object"))
            return "st.redline.kernel.Object";
        Map<String, Map<String, String>> thePackage = imports.getOrDefault(packageName, emptyMap);
        Map<String, String> theClass = thePackage.getOrDefault(className, emptyMap);
        return theClass.getOrDefault(reference, null);
    }

    private void addImport(String packageName, String className, String reference, String fullClassName) {
        if (isTraceEnabled(LOG))
            LOG.trace(reference + " in " + packageName + "." + className + " as " + fullClassName);
        Map<String, Map<String, String>> packageMap = imports.computeIfAbsent(packageName, k -> new HashMap<>());
        Map<String, String> classMap = packageMap.computeIfAbsent(className, k -> new HashMap<>());
        if (!classMap.containsKey(reference))
            classMap.put(reference, fullClassName);
        else
            throw new RuntimeException("Attempt to add import twice for: " + className);
    }
}
