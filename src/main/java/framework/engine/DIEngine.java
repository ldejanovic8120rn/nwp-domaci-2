package framework.engine;

import java.io.File;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class DIEngine {
    private Set<Class<?>> allClasses = new HashSet<>();

    public Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loadAllClasses(File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                loadAllClasses(Objects.requireNonNull(file.listFiles()));
            } else if(file.getName().endsWith(".class")){
                String userDir = System.getProperty("user.dir");

                String className = file.getAbsolutePath().replace(userDir + "/target/classes/", "");
                className = className.replaceAll("/", ".");
                className = className.replace(".class", "");

                allClasses.add(getClass(className));
            }
        }
    }

    public Set<Class<?>> getAllClasses() {
        return allClasses;
    }
}
