package framework.engine;

import framework.annotations.Qualifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DependencyContainer {
    private static DependencyContainer instance;

    private final Map<String, Class<?>> qualifierClasses;

    private DependencyContainer() {
        this.qualifierClasses = new HashMap<>();
    }

    public static DependencyContainer getInstance() {
        if (instance == null)
            instance = new DependencyContainer();
        return instance;
    }

    public void initQualifiers(Set<Class<?>> qualifiers) {
        for (Class<?> q: qualifiers) {
            if (q.isAnnotationPresent(Qualifier.class)) {
                String value = q.getAnnotation(Qualifier.class).value();

                if(getByKey(value) != null)
                    throw new RuntimeException("Class annotated with @Qualifier must have unique value!");

                addInMap(value, q);
            }
        }
    }

    public void addInMap(String key, Class<?> cl) {
        qualifierClasses.put(key, cl);
    }

    public Class<?> getByKey(String key) {
        return qualifierClasses.get(key);
    }
}
