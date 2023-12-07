package org.frozenarc.wsstream;

/*
 * Author: Manan
 * Date: 08-01-2018 14:39
 */
import java.net.URLConnection;

public interface URLConnConfigurator {

    static URLConnConfigurator accumulate(URLConnConfigurator... configurators) {
        return con -> {
            for (URLConnConfigurator configurator : configurators) {
                configurator.configure(con);
            }
        };
    }

    void configure(URLConnection con) throws WSStreamException;
}
