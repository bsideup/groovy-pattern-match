package ru.trylogic.groovy.pattern.matcher.cases;

import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.VariableExpression;

import static org.codehaus.groovy.ast.tools.GeneralUtils.eqX;

public class EqCase extends AbstractParametrizedMatchCase {
    
    protected final Expression expression;

    public EqCase(VariableExpression parameterExpression, Expression expression, Expression valueExpression) {
        super(parameterExpression, valueExpression);
        
        this.expression = expression;
    }

    @Override
    public Expression getConditionExpression() {
        return eqX(parameterExpression, expression);
    }
}
