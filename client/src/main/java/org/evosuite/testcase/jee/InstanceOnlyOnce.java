package org.evosuite.testcase.jee;

import org.evosuite.runtime.util.Inputs;

import javax.servlet.http.HttpServlet;
import java.util.*;

/**
 * In some cases, there are classes we want to instantiate only once, eg an HTTP Servlet,
 * where having more than one of it would make no sense from a unit testing point of view.
 * Note, this is independent on whether it is the CUT or not.
 *
 * Created by Andrea Arcuri on 29/06/15.
 */
public class InstanceOnlyOnce {

    private static final Set<Class<?>> classes = Collections.unmodifiableSet(
            new HashSet<Class<?>>(){{
                add(HttpServlet.class);
            }}
    );

    public static boolean canInstantiateOnlyOnce(Class<?> klass) throws IllegalArgumentException{
        Inputs.checkNull(klass);
        for(Class<?> c : classes) {
            if(c.isAssignableFrom(klass)){
                return true;
            }
        }
        return false;
    }
}