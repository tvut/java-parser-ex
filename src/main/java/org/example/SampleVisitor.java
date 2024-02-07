package org.example;

import com.github.javaparser.Range;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.body.CallableDeclaration;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import java.util.function.*;

public class SampleVisitor
{
    private static final String FILE_PATH = "src/main/java/ReversePolishNotation.java";

    public static void main(String[] args) throws Exception
    {
        CompilationUnit cu = StaticJavaParser.parse(Files.newInputStream(Paths.get(FILE_PATH)));

        VoidVisitor<PredicateRangePayload> visitor = new PredicateAndRangeVisitor();

        PredicateRangePayload payload = new PredicateRangePayload();
        payload.constraints.add(getNameMatch("memoryRecall"));

        visitor.visit(cu, payload);

        System.out.println(payload.matchingRanges.size());
        System.out.println(payload.matchingRanges.get(0).toString());
    }

    private static Predicate<CallableDeclaration> getNameMatch(String pMatch) {
        return cd -> cd.getName().asString().equals(pMatch);
    }

    private static Predicate<CallableDeclaration> getFirstTypeMatch(String pMatch) {
        return cd -> cd.getTypeParameter(0).asString().equals(pMatch);
    }

    private static class PredicateRangePayload {
        List<Predicate<CallableDeclaration>> constraints = new ArrayList<>();
        List<Range> matchingRanges = new ArrayList<>();
    }

    private static class PredicateAndRangeVisitor extends VoidVisitorAdapter<PredicateRangePayload> {
        @Override
        public void visit(MethodDeclaration md, PredicateRangePayload payload) {
            for(Predicate<CallableDeclaration> bp : payload.constraints) {
                if(!bp.test(md)) {
                    // failed a condition
                    return;
                }
            }
            if(md.getName().getRange().isPresent()){
                payload.matchingRanges.add(md.getName().getRange().get());
            }
        }
    }

}

