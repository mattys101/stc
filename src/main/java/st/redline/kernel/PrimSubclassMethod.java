package st.redline.kernel;

import st.redline.Smalltalk;

public class PrimSubclassMethod extends PrimMethod {

    PrimSubclassMethod() {
        this.javaValue("Method PrimSubclass");
        this.function((receiver, context) -> {
            String className = String.valueOf(context.argumentAt(0).javaValue());
            PrimObject newClass = createClass(receiver, className);
            registerNewClass(context, newClass, className);
            return newClass;
        });
    }

    private void registerNewClass(PrimContext context, PrimObject newClass, String className) {
        Smalltalk smalltalk = context.smalltalk();
        smalltalk.register(newClass, className);
    }

    private PrimObject createClass(PrimObject receiver, String className) {
        PrimClass newClass = new PrimClass(className, false);
        PrimClass newMeta = new PrimClass(className, true);
        newClass.clazz(newMeta);
        newClass.superclass(receiver);
        newMeta.superclass(receiver.clazz());
        return newClass;
    }
}