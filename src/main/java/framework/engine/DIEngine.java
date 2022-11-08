package framework.engine;

import framework.annotations.*;
import framework.engine.routes.ControllerRoute;

import java.io.File;
import java.util.*;

public class DIEngine {
    private static DIEngine instance;

    private final Set<Class<?>> controllers;
    private final Set<Class<?>> qualifiers;

    private Map<String, ControllerRoute> routes;
    private final Map<Class<?>, Object> servicesAndBeans;

    private DIEngine() {
        this.controllers = new HashSet<>();
        this.qualifiers = new HashSet<>();
        this.servicesAndBeans = new HashMap<>();
    }

    public static DIEngine getInstance() {
        if(instance == null)
            instance = new DIEngine();

        return instance;
    }

    public void setup() {
        File dir = new File(System.getProperty("user.dir"));
        loadAllClasses(Objects.requireNonNull(dir.listFiles()));

        DependencyContainer.getInstance().initQualifiers(qualifiers);
        routes = ClassHelper.initializeControllers(controllers);
    }

    public Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loadAllClasses(File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                loadAllClasses(Objects.requireNonNull(file.listFiles()));
            }
            else if(file.getName().endsWith(".class")){
                String userDir = System.getProperty("user.dir");

                String className = file.getAbsolutePath().replace(userDir + "/target/classes/", "");
                className = className.replaceAll("/", ".");
                className = className.replace(".class", "");

                Class<?> cl = getClass(className);
                if (cl.isAnnotationPresent(Controller.class))
                    controllers.add(cl);
                else if (cl.isAnnotationPresent(Qualifier.class))
                    qualifiers.add(cl);
            }
        }
    }

    public Object getServiceOrBeanByClass(Class<?> key) {
        return servicesAndBeans.get(key);
    }

    public void addServiceOrBean(Class<?> key, Object value) {
        servicesAndBeans.put(key, value);
    }

    public ControllerRoute getControllerRoutByPath(String path) {
        return routes.get(path);
    }

}
