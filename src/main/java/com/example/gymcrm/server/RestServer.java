package com.example.gymcrm.server;

import com.example.gymcrm.config.AppConfig;
import com.example.gymcrm.web.WebConfig;
import jakarta.servlet.ServletException;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;

public class RestServer {

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getProperty("PORT", "8080"));

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);

        // Minimal context; docBase must exist
        File base = new File(".");
        Context ctx = tomcat.addContext("", base.getAbsolutePath());

        // Spring application context (register Web + App configs)
        AnnotationConfigWebApplicationContext appCtx = new AnnotationConfigWebApplicationContext();
        appCtx.register(AppConfig.class, WebConfig.class);

        // Dispatcher servlet
        DispatcherServlet dispatcher = new DispatcherServlet(appCtx);
        var servlet = Tomcat.addServlet(ctx, "dispatcher", dispatcher);
        servlet.setLoadOnStartup(1);
        ctx.addServletMappingDecoded("/", "dispatcher");

        // Start
        tomcat.start();
        System.out.println("REST server started on http://localhost:" + port);
        tomcat.getServer().await();
    }
}
