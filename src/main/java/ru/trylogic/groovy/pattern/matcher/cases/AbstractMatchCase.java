package ru.trylogic.groovy.pattern.matcher.cases;

import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.Statement;

import static org.codehaus.groovy.ast.tools.GeneralUtils.*;

public abstract class AbstractMatchCase implements MatchCase {

    protected final VariableExpression parameterExpression;

    protected final Expression valueExpression;

    public AbstractMatchCase(VariableExpression parameterExpression, Expression valueExpression) {
        this.parameterExpression = parameterExpression;
        this.valueExpression = valueExpression;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public abstract Expression getConditionExpression();

    @Override
    public Statement getCaseStatement() {
        return returnS(valueExpression);
    }
}
