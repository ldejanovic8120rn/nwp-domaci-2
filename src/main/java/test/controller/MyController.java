package test.controller;

import framework.annotations.*;
import test.bean.Bean1;
import test.component.Component1;
import test.qualifier.Interface1;
import test.qualifier.Interface2;
import test.service.Service1;

@Controller
public class MyController {

    private String controllerName = "Controller";

    @Autowired(verbose = false)
    private Component1 component1;

    @Autowired(verbose = false)
    private Bean1 bean1;

    @Autowired(verbose = false)
    private Service1 service1;

    @Autowired(verbose = false)
    @Qualifier(value = "interface1")
    private Interface1 interface1;

    @Autowired(verbose = false)
    @Qualifier(value = "interface2")
    private Interface2 interface2;

    public MyController() {

    }

    @GET
    @Path(path = "/")
    public void root() {
        System.out.println("ROOT");
    }

    @GET
    @Path(path = "/method1")
    public void method1() {
        System.out.println("METHOD1 - GET");
    }

    @POST
    @Path(path = "/method2")
    public void method2() {
        System.out.println("METHOD2 - POST");
    }
}
