package framework.engine;

import framework.annotations.*;
import framework.engine.routes.ControllerRoute;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ClassHelper {

    public static Map<String, ControllerRoute> initializeControllers(Set<Class<?>> controllers) {
        Map<String, ControllerRoute> routeMap = new HashMap<>();

        for(Class<?> cl: controllers) {
            Object controllerObj = instantiateClass(cl);

            Method[] methods = cl.getDeclaredMethods();
            for(Method method: methods) {

                if(method.isAnnotationPresent(Path.class) && method.isAnnotationPresent(GET.class)) {
                    String route = "GET:" + method.getAnnotation(Path.class).path();

                    ControllerRoute controllerRoute = new ControllerRoute(controllerObj, method);
                    routeMap.put(route, controllerRoute);
                }
                else if(method.isAnnotationPresent(Path.class) && method.isAnnotationPresent(POST.class)) {
                    String route = "POST:" + method.getAnnotation(Path.class).path();

                    ControllerRoute controllerRoute = new ControllerRoute(controllerObj, method);
                    routeMap.put(route, controllerRoute);
                }

            }
        }

        return routeMap;
    }

    public static Object instantiateClass(Class<?> cl) {
        try {
            Object object = cl.getDeclaredConstructor().newInstance();

            Field[] fields = cl.getDeclaredFields();
            for (Field field: fields) {
                field.setAccessible(true);
                Class<?> type = field.getType();

                if (field.isAnnotationPresent(Autowired.class)) {
                    if(type.isInterface() && !field.isAnnotationPresent(Qualifier.class))
                        throw new RuntimeException("Field: " + field.getName() + ", in class: " + cl.getName() + ", is an interface and must have @Qualifier annotation!");

                    Object objectField;
                    if(type.isInterface() && field.isAnnotationPresent(Qualifier.class)) {
                        String value = field.getAnnotation(Qualifier.class).value();

                        Class<?> classImplementation = DependencyContainer.getInstance().getByKey(value);
                        objectField = instantiateClass(classImplementation);
                    }
                    else if(type.isAnnotationPresent(Service.class)) {
                        objectField = DIEngine.getInstance().getServiceOrBeanByClass(type);

                        if (objectField == null) {
                            objectField = instantiateClass(type);
                            DIEngine.getInstance().addServiceOrBean(type, objectField);
                        }
                    }
                    else if(type.isAnnotationPresent(Component.class)) {
                        objectField = instantiateClass(type);
                    }
                    else if(type.isAnnotationPresent(Bean.class)) {
                        ScopeType scopeType = type.getAnnotation(Bean.class).scope();

                        if(scopeType == ScopeType.SINGLETON) {
                            objectField = DIEngine.getInstance().getServiceOrBeanByClass(type);

                            if (objectField == null) {
                                objectField = instantiateClass(type);
                                DIEngine.getInstance().addServiceOrBean(type, objectField);
                            }
                        }
                        else {
                            objectField = instantiateClass(type);
                        }
                    }
                    else {
                        throw new RuntimeException("Class: " + type.getName() + ", must have one of annotations [@Component, @Bean, @Service, @Qualifier]!");
                    }

                    field.set(object, objectField);

                    boolean verbose = field.getAnnotation(Autowired.class).verbose();
                    if (verbose) {
                        System.out.println("Initialized <" + type.getName() + "> <" + field.getName() +
                                "> in <" + cl.getName() + "> on <" + LocalDateTime.now() + ">" + "with <"
                                + objectField.hashCode() + ">"
                        );
                    }
                }
            }

            return object;
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
