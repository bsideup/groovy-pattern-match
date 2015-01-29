package ru.trylogic.groovy.pattern.matcher.runtime;

import groovy.lang.Closure;
import org.codehaus.groovy.runtime.InvokerHelper;

public class MatchCaseClosureCaller {
    public static <T> T call(Closure<T> cl, Object delegate) {
        cl.setDelegate(delegate);
        cl.setResolveStrategy(Closure.DELEGATE_FIRST);
        
        return (T) InvokerHelper.invokeClosure(cl, delegate);
    }
}
