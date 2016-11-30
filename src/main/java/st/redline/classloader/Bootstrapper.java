package st.redline.classloader;

import st.redline.core.*;

public class Bootstrapper {

    public void bootstrap(SmalltalkClassLoader smalltalkClassLoader) {
        smalltalkClassLoader.cacheObject("st.redline.core.PrimObject", new PrimObject());
        loadProtoObject(smalltalkClassLoader);
    }

    private void loadProtoObject(ClassLoader classLoader) {
        try {
            classLoader.loadClass("st.redline.core.ProtoObject").newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
