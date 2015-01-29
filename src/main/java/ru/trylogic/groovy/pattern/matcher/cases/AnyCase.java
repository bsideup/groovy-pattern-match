package ru.trylogic.groovy.pattern.matcher.cases;

import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.VariableExpression;

import static org.codehaus.groovy.ast.tools.GeneralUtils.*;

public class AnyCase extends AbstractMatchCase {
    
    public AnyCase(Expression valueExpression) {
        super(valueExpression);
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Expression getConditionExpression() {
        return constX(true);
    }
}
