package ru.trylogic.groovy.pattern.matcher.cases;

import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.Statement;

public interface MatchCase {
    
    Expression getConditionExpression();
    
    Statement getCaseStatement();

    int getPriority();
}
