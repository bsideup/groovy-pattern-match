package ru.trylogic.groovy.pattern.matcher.cases;

import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.VariableExpression;

import static org.codehaus.groovy.ast.tools.GeneralUtils.*;

public class ClassCase extends EqCase {
    
    private final ClassExpression classExpression;

    public ClassCase(VariableExpression parameterExpression, ClassExpression classExpression, Expression valueExpression) {
        super(parameterExpression, classExpression, valueExpression);
        
        this.classExpression = classExpression;
    }

    @Override
    public Expression getConditionExpression() {
        return andX(
                notNullX(parameterExpression),
                orX(
                        super.getConditionExpression(),
                        eqX(callX(parameterExpression, "getClass"), classExpression)
                )
        );
    }
}
