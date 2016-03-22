import api.*;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class RestApplication extends Application {
    @Override
    public Set<Object> getSingletons() {
        final HashSet<Object> objects = new HashSet<>();
        objects.add(new General());
        objects.add(new Forums());
        objects.add(new Users());
        objects.add(new Posts());
        objects.add(new Threads());
        return objects;
    }
}
