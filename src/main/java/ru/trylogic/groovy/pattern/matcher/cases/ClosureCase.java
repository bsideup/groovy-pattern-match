package ru.trylogic.groovy.pattern.matcher.cases;

import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import ru.trylogic.groovy.pattern.matcher.runtime.MatchCaseClosureCaller;

import static org.codehaus.groovy.ast.tools.GeneralUtils.callX;

public class ClosureCase extends AbstractParametrizedMatchCase {
    
    public static final ClassNode MATCH_CASE_CLOSURE_CALLER_NODE = ClassHelper.makeWithoutCaching(MatchCaseClosureCaller.class);
    
    protected final ClosureExpression closureExpression;
    
    public ClosureCase(VariableExpression parameterExpression, ClosureExpression closureExpression, Expression valueExpression) {
        super(parameterExpression, valueExpression);
        this.closureExpression = closureExpression;
    }

    @Override
    public Expression getConditionExpression() {
        //return callX(closureExpression, "call", parameterExpression);
        
        return callX(
                MATCH_CASE_CLOSURE_CALLER_NODE,
                "call",
                new ArgumentListExpression(
                        closureExpression,
                        parameterExpression
                )
        );
    }
}
