// src/main/java/com/example/gymcrm/server/RestServer.java
package com.example.gymcrm.server;

import com.example.gymcrm.config.AppConfig;
import com.example.gymcrm.web.WebConfig;
import com.example.gymcrm.web.filter.TransactionIdFilter;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;

public class RestServer {

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getProperty("PORT", "8080"));

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);

        File base = new File(".");
        Context ctx = tomcat.addContext("", base.getAbsolutePath());

        // --- Register txId filter via Tomcat API (must be before start) ---
        FilterDef txDef = new FilterDef();
        txDef.setFilterName("transactionIdFilter");
        txDef.setFilterClass(TransactionIdFilter.class.getName()); // needs no-arg ctor
        ctx.addFilterDef(txDef);

        FilterMap txMap = new FilterMap();
        txMap.setFilterName("transactionIdFilter");
        txMap.addURLPattern("/*");
        ctx.addFilterMap(txMap);
        // -------------------------------------------------------------------

        // Spring WebApplicationContext
        AnnotationConfigWebApplicationContext appCtx = new AnnotationConfigWebApplicationContext();
        appCtx.register(AppConfig.class, WebConfig.class);

        // DispatcherServlet
        DispatcherServlet dispatcher = new DispatcherServlet(appCtx);

        var servlet = Tomcat.addServlet(ctx, "dispatcher", dispatcher);
        servlet.setLoadOnStartup(1);
        ctx.addServletMappingDecoded("/", "dispatcher");

        tomcat.start();
        System.out.println("REST server started on http://localhost:" + port);
        tomcat.getServer().await();
    }
}
