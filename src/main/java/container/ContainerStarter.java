package container;

import config.AppConfig;
import komponenten.console.impl.ConsoleImpl;
import model.exceptions.TechnischeException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ContainerStarter {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context;
        try {
           context = new AnnotationConfigApplicationContext(AppConfig.class);
        } catch (Exception e) {
            throw new TechnischeException("Container konnte nicht hochgefahren werden");
        }

        ConsoleImpl consoleImpl = context.getBean(ConsoleImpl.class);

        consoleImpl.run();

    }
}
