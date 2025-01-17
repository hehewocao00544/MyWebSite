package xyz.lirui123.mywebsite.manager;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
@SpringBootApplication
@ComponentScan(basePackages = {"xyz.lirui123.mywebsite.manager"})
@MapperScan({"xyz.lirui123.mywebsite.manager.mapper"})
/*public class MyWebSiteManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyWebSiteManagerApplication.class, args);
    }
}*/
public class MyWebSiteManagerApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(MyWebSiteManagerApplication.class, args);
    }
    //重写 configure
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MyWebSiteManagerApplication.class);
    }

}