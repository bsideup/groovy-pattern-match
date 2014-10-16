package ru.trylogic.groovy.pattern;

import groovy.lang.Closure;
import groovy.lang.GroovyRuntimeException;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;
import ru.trylogic.groovy.macro.runtime.Macro;
import ru.trylogic.groovy.macro.runtime.MacroContext;
import ru.trylogic.groovy.pattern.matcher.MatchCaseFactory;
import ru.trylogic.groovy.pattern.matcher.cases.MatchCase;

import java.util.*;

import static org.codehaus.groovy.ast.tools.GeneralUtils.*;

public class PatternMatchingMacroMethods {

    public static final String MATCH_PARAMETER_NAME = "_";
    
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
        for (Statement statement : statements) {
            if (!(statement instanceof ExpressionStatement)) {
                addErrorAndContinue(sourceUnit, "only ExpressionStatement is allowed as case", statement);
                continue;
            }

            Expression caseExpression = ((ExpressionStatement) statement).getExpression();

            if (!(caseExpression instanceof BinaryExpression)) {
                addErrorAndContinue(sourceUnit, "case should be BinaryExpression", caseExpression);
                continue;
            }

            BinaryExpression binaryExpression = (BinaryExpression) caseExpression;

            Token operation = binaryExpression.getOperation();
            if (!operation.isA(Types.RIGHT_SHIFT)) {
                addErrorAndContinue(sourceUnit, "case expressions should be divided by >>", operation);
                continue;
            }

            MatchCase matchCaseProvider = matchCaseFactory.getCaseConditionProvider(
                    parameterExpression,
                    binaryExpression.getLeftExpression(),
                    binaryExpression.getRightExpression());
            
            conditions.add(matchCaseProvider);
        }

        Collections.sort(conditions, new Comparator<MatchCase>() {
            @Override
            public int compare(MatchCase o1, MatchCase o2) {
                return o1.getPriority() - o2.getPriority();
            }
        });

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

    public static void addErrorAndContinue(SourceUnit sourceUnit, String message, ASTNode node) {
        SyntaxException syntaxException = new SyntaxException(message,
                node.getLineNumber(),
                node.getColumnNumber(),
                node.getLastLineNumber(),
                node.getLastColumnNumber()
        );
        sourceUnit.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(syntaxException, sourceUnit));
    }

    public static void addErrorAndContinue(SourceUnit sourceUnit, String message, Token token) {
        SyntaxException syntaxException = new SyntaxException(message,
                token.getStartLine(),
                token.getStartColumn()
        );
        sourceUnit.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(syntaxException, sourceUnit));
    }
}
