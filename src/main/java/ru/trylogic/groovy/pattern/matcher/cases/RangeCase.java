package ru.trylogic.groovy.pattern.matcher.cases;

import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.RangeExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;

import static org.codehaus.groovy.ast.tools.GeneralUtils.callX;

public class RangeCase extends AbstractMatchCase {
    
    protected final RangeExpression rangeException;
    
    public RangeCase(VariableExpression parameterExpression, RangeExpression rangeException, Expression valueExpression) {
        super(parameterExpression, valueExpression);
        this.rangeException = rangeException;
    }

    @Override
    public Expression getConditionExpression() {
        return callX(rangeException, "contains", parameterExpression);
    }
}
