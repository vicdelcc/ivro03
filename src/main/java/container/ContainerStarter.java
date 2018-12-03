package container;

import config.AppConfig;
import komponenten.console.impl.ConsoleImpl;
import model.exceptions.MauMauException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ContainerStarter {

    public static void main(String[] args) throws MauMauException {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        ConsoleImpl consoleImpl = context.getBean(ConsoleImpl.class);

        consoleImpl.run();

    }
}
