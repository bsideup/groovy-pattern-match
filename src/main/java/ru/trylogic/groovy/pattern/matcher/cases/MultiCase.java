package ru.trylogic.groovy.pattern.matcher.cases;

import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.syntax.Types;
import ru.trylogic.groovy.pattern.matcher.MatchCaseFactory;

import static org.codehaus.groovy.ast.tools.GeneralUtils.orX;

public class MultiCase extends AbstractParametrizedMatchCase {
    
    protected final BinaryExpression expression;
    
    protected final MatchCaseFactory matchCaseFactory;

    public MultiCase(VariableExpression parameterExpression, BinaryExpression expression, MatchCaseFactory matchCaseFactory, Expression valueExpression) {
        super(parameterExpression, valueExpression);
        this.expression = expression;
        this.matchCaseFactory = matchCaseFactory;

        if (!expression.getOperation().isA(Types.BITWISE_OR)) {
            throw new IllegalArgumentException("MultiCaseMatcher variants should be divided by |");
        }
    }

    @Override
    public Expression getConditionExpression() {
        return orX(
                matchCaseFactory.getCaseConditionProvider(parameterExpression, expression.getLeftExpression(), valueExpression).getConditionExpression(),
                matchCaseFactory.getCaseConditionProvider(parameterExpression, expression.getRightExpression(), valueExpression).getConditionExpression()
        );
    }
}
