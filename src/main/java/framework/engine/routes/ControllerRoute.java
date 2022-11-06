package framework.engine.routes;

import java.lang.reflect.Method;

public class ControllerRoute {
    private Object controller;
    private Method method;

    public ControllerRoute(Object controller, Method method) {
        this.controller = controller;
        this.method = method;
    }

    public Object getController() {
        return controller;
    }

    public Method getMethod() {
        return method;
    }

}
