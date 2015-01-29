package ru.trylogic.groovy.pattern.matcher.cases;

import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.Statement;

import static org.codehaus.groovy.ast.tools.GeneralUtils.returnS;

public abstract class AbstractParametrizedMatchCase extends AbstractMatchCase {

    protected final VariableExpression parameterExpression;

    public AbstractParametrizedMatchCase(VariableExpression parameterExpression, Expression valueExpression) {
        super(valueExpression);
        this.parameterExpression = parameterExpression;
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
