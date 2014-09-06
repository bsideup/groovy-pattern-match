package ru.trylogic.groovy.pattern.matcher.cases;

import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.VariableExpression;

import static org.codehaus.groovy.ast.tools.GeneralUtils.callX;

public class ClosureCase extends AbstractMatchCase {
    
    protected final ClosureExpression closureExpression;
    
    public ClosureCase(VariableExpression parameterExpression, ClosureExpression closureExpression, Expression valueExpression) {
        super(parameterExpression, valueExpression);
        this.closureExpression = closureExpression;
    }

    @Override
    public Expression getConditionExpression() {
        return callX(closureExpression, "call", parameterExpression);
    }
}
