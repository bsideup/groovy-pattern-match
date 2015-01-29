package ru.trylogic.groovy.pattern.matcher.cases;

import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.syntax.Types;
import ru.trylogic.groovy.pattern.matcher.MatchCaseFactory;

import java.util.Iterator;
import java.util.List;

import static org.codehaus.groovy.ast.tools.GeneralUtils.orX;

public class MultiCase extends AbstractParametrizedMatchCase {
    
    protected final List<Expression> options;
    
    protected final MatchCaseFactory matchCaseFactory;

    public MultiCase(VariableExpression parameterExpression, List<Expression> options, MatchCaseFactory matchCaseFactory, Expression valueExpression) {
        super(parameterExpression, valueExpression);
        this.options = options;
        this.matchCaseFactory = matchCaseFactory;
    }

    @Override
    public Expression getConditionExpression() {
        

        Iterator<Expression> iterator = options.iterator();


        Expression result = orX(
                matchCaseFactory.getCaseConditionProvider(parameterExpression, iterator.next(), valueExpression).getConditionExpression(),
                matchCaseFactory.getCaseConditionProvider(parameterExpression, iterator.next(), valueExpression).getConditionExpression()
        );
        
        while(iterator.hasNext()) {
            result = orX(
                    result,
                    matchCaseFactory.getCaseConditionProvider(parameterExpression, iterator.next(), valueExpression).getConditionExpression()
            );
        }
        
        return result;

        /*
        return orX(
                matchCaseFactory.getCaseConditionProvider(parameterExpression, expression.getLeftExpression(), valueExpression).getConditionExpression(),
                matchCaseFactory.getCaseConditionProvider(parameterExpression, expression.getRightExpression(), valueExpression).getConditionExpression()
        );
        */
    }
}
