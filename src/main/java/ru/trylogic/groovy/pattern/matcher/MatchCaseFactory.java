package ru.trylogic.groovy.pattern.matcher;

import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.syntax.Types;
import ru.trylogic.groovy.pattern.matcher.cases.*;

public class MatchCaseFactory {

    public MatchCase getCaseConditionProvider(VariableExpression parameterExpression, Expression expression, Expression valueExpression) {
        if (expression instanceof BinaryExpression) {
            BinaryExpression binaryCaseExpression = (BinaryExpression) expression;

            if (binaryCaseExpression.getOperation().isA(Types.BITWISE_OR)) {
                return new MultiCase(parameterExpression, binaryCaseExpression, this, valueExpression);
            }
        }

        if (expression instanceof ClassExpression) {
            return new ClassCase(parameterExpression, (ClassExpression) expression, valueExpression);
        }

        if (expression instanceof RangeExpression) {
            return new RangeCase(parameterExpression, (RangeExpression) expression, valueExpression);
        }

        if (expression instanceof ClosureExpression) {
            return new ClosureCase(parameterExpression, (ClosureExpression) expression, valueExpression);
        }
        
        if(expression instanceof VariableExpression) {
            VariableExpression variableExpression = (VariableExpression) expression;
            
            if(variableExpression.getName().equals(parameterExpression.getName())) {
                return new AnyCase(parameterExpression, valueExpression);
            }
        }

        return new EqCase(parameterExpression, expression, valueExpression);
    }
}
