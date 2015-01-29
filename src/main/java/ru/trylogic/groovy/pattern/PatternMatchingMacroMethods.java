package ru.trylogic.groovy.pattern;

import groovy.lang.Closure;
import groovy.lang.GroovyRuntimeException;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.classgen.asm.InvocationWriter;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.syntax.SyntaxException;
import ru.trylogic.groovy.macro.runtime.Macro;
import ru.trylogic.groovy.macro.runtime.MacroContext;
import ru.trylogic.groovy.pattern.matcher.MatchCaseFactory;
import ru.trylogic.groovy.pattern.matcher.cases.AnyCase;
import ru.trylogic.groovy.pattern.matcher.cases.MatchCase;

import java.util.*;

import static org.codehaus.groovy.ast.tools.GeneralUtils.*;

public class PatternMatchingMacroMethods {
    
    private static class MatchCaseSyntaxException extends Exception {
        
        private final ASTNode node;

        public MatchCaseSyntaxException(ASTNode node) {
            this.node = node;
        }

        public ASTNode getNode() {
            return node;
        }
    }

    public static final String MATCH_PARAMETER_NAME = "it";
    
    //For auto-complete, can be removed
    public static <T> T match(Object self, Object it, Closure cl) {
        throw new GroovyRuntimeException("not available at runtime");
    }

    public static <T> T match(Object self, Closure cl) {
        throw new GroovyRuntimeException("not available at runtime");
    }

    @Macro
    public static Expression match(MacroContext context, ClosureExpression cl) {
        return match(context, context.getCall().getObjectExpression(), cl);
    }

    @Macro
    public static Expression match(MacroContext context, Expression it, ClosureExpression cl) {
        SourceUnit sourceUnit = context.getSourceUnit();

        List<Statement> statements;

        Statement originalCode = cl.getCode();
        if (originalCode instanceof BlockStatement) {
            statements = ((BlockStatement) originalCode).getStatements();
        } else {
            statements = Arrays.asList(originalCode);
        }

        List<MatchCase> conditions = new ArrayList<MatchCase>();

        VariableExpression parameterExpression = varX(PatternMatchingMacroMethods.MATCH_PARAMETER_NAME);

        MatchCaseFactory matchCaseFactory = new MatchCaseFactory();

        Iterator<Statement> statementIterator = statements.iterator();
        while(statementIterator.hasNext()) {
            Statement statement = statementIterator.next();
            
            try {
                if (!(statement instanceof ExpressionStatement)) {
                    addErrorAndContinue(sourceUnit, "only ExpressionStatement is allowed as case", statement);
                    continue;
                }

                Expression caseExpression = ((ExpressionStatement) statement).getExpression();

                if (!(caseExpression instanceof MethodCallExpression)) {
                    addErrorAndContinue(sourceUnit, "case should be MethodCallExpression", caseExpression);
                    continue;
                }

                MethodCallExpression caseMethodCallExpression = (MethodCallExpression) caseExpression;

                ArgumentListExpression thenArguments = InvocationWriter.makeArgumentList(caseMethodCallExpression.getArguments());

                List<Expression> thenArgumentsExpressions = thenArguments.getExpressions();

                if (thenArgumentsExpressions.size() != 1) {
                    throw new MatchCaseSyntaxException(caseExpression);
                }

                Expression resultExpression = thenArgumentsExpressions.get(0);

                if ("then".equals(caseMethodCallExpression.getMethodAsString())) {

                    Expression conditionExpression = getMatchConditionExpression(caseMethodCallExpression);

                    MatchCase matchCaseProvider = matchCaseFactory.getCaseConditionProvider(
                            parameterExpression,
                            conditionExpression,
                            resultExpression
                    );

                    conditions.add(matchCaseProvider);
                } else if ("orElse".equals(caseMethodCallExpression.getMethodAsString())) {

                    if (statementIterator.hasNext()) {
                        addErrorAndContinue(sourceUnit, "orElse should be last match statement", caseMethodCallExpression);
                    }
                    conditions.add(new AnyCase(resultExpression));
                } else {
                    throw new MatchCaseSyntaxException(caseMethodCallExpression);
                }
            } catch (MatchCaseSyntaxException e) {
                addErrorAndContinue(sourceUnit, "please use 'when ... then ... or ...' form", e.getNode());
                continue;
            }
        }

        BlockStatement resultBlock = block();
        
        for (MatchCase matchCase : conditions) {
            resultBlock.addStatement(ifS(
                    matchCase.getConditionExpression(),
                    matchCase.getCaseStatement()
            ));
        }

        ClosureExpression closureExpression = closureX(
                params(param(ClassHelper.OBJECT_TYPE, MATCH_PARAMETER_NAME)),
                resultBlock
        );

        closureExpression.setVariableScope(cl.getVariableScope());

        return callX(closureExpression, "call", it);
    }
    
    protected static Expression getMatchConditionExpression(MethodCallExpression caseMethodCallExpression) throws MatchCaseSyntaxException {
        Expression thenObjectExpression = caseMethodCallExpression.getObjectExpression();

        if (!(thenObjectExpression instanceof MethodCallExpression)) {
            throw new MatchCaseSyntaxException(thenObjectExpression);
        }

        MethodCallExpression whenMethodCallExpression = (MethodCallExpression) thenObjectExpression;

        if (!"when".equals(whenMethodCallExpression.getMethodAsString())) {
            throw new MatchCaseSyntaxException(thenObjectExpression);
        }

        ArgumentListExpression whenArguments = InvocationWriter.makeArgumentList(whenMethodCallExpression.getArguments());

        List<Expression> whenArgumentsExpressions = whenArguments.getExpressions();

        if (whenArgumentsExpressions.size() != 1) {
            throw new MatchCaseSyntaxException(thenObjectExpression);
        }

        return whenArgumentsExpressions.get(0);
    }

    public static void addErrorAndContinue(SourceUnit sourceUnit, String message, ASTNode node) {
        SyntaxException syntaxException = new SyntaxException(message,
                node.getLineNumber(),
                node.getColumnNumber(),
                node.getLastLineNumber(),
                node.getLastColumnNumber()
        );
        sourceUnit.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(syntaxException, sourceUnit));
    }
}
