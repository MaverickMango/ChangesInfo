/*
 * Copyright 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;

/**
 * Unit tests for {@link FlowSensitiveInlineVariables}.
 *
 */
public class FlowSensitiveInlineVariablesTest extends CompilerTestCase  {

  public static final String EXTERN_FUNCTIONS = "" +
      "/** @nosideeffects */ function noSFX() {} \n" +
      "                      function hasSFX() {} \n";

  @Override
  public int getNumRepetitions() {
    // Test repeatedly inline.
    return 3;
  }

  @Override
  protected CompilerPass getProcessor(final Compiler compiler) {
    //return new FlowSensitiveInlineVariables(compiler);
    return new CompilerPass() {
      @Override
      public void process(Node externs, Node root) {
        (new MarkNoSideEffectCalls(compiler)).process(externs, root);
        (new FlowSensitiveInlineVariables(compiler)).process(externs, root);
      }
    };
  }

  public void testSimpleAssign() {
    inline("var x; x = 1; print(x)", "var x; print(1)");
    inline("var x; x = 1; x", "var x; 1");
    inline("var x; x = 1; var a = x", "var x; var a = 1");
    inline("var x; x = 1; x = x + 1", "var x; x = 1 + 1");
  }

  public void testSimpleVar() {
    inline("var x = 1; print(x)", "var x; print(1)");
    inline("var x = 1; x", "var x; 1");
    inline("var x = 1; var a = x", "var x; var a = 1");
    inline("var x = 1; x = x + 1", "var x; x = 1 + 1");
  }

  public void testExported() {
    noInline("var _x = 1; print(_x)");
  }

  public void testDoNotInlineIncrement() {
    noInline("var x = 1; x++;");
    noInline("var x = 1; x--;");
  }

  public void testDoNotInlineAssignmentOp() {
    noInline("var x = 1; x += 1;");
    noInline("var x = 1; x -= 1;");
  }

  public void testDoNotInlineIntoLhsOfAssign() {
    noInline("var x = 1; x += 3;");
  }

  public void testMultiUse() {
    noInline("var x; x = 1; print(x); print (x);");
  }

  public void testMultiUseInSameCfgNode() {
    noInline("var x; x = 1; print(x) || print (x);");
  }

  public void testMultiUseInTwoDifferentPath() {
    noInline("var x = 1; if (print) { print(x) } else { alert(x) }");
  }

  public void testAssignmentBeforeDefinition() {
    inline("x = 1; var x = 0; print(x)","x = 1; var x; print(0)" );
  }

  public void testVarInConditionPath() {
    noInline("if (foo) { var x = 0 } print(x)");
  }

  public void testMultiDefinitionsBeforeUse() {
    inline("var x = 0; x = 1; print(x)", "var x = 0; print(1)");
  }

  public void testMultiDefinitionsInSameCfgNode() {
    noInline("var x; x = 1 || x = 2; print(x)");
    noInline("var x; x = 1 && x = 2; print(x)");
    noInline("var x; x = 1 , x = 2; print(x)");
  }

  public void testNotReachingDefinitions() {
    noInline("var x; if (foo) { x = 0 } print (x)");
  }

  public void testNoInlineLoopCarriedDefinition() {
    // First print is undefined instead.
    noInline("var x; while(true) { print(x); x = 1; }");

    // Prints 0 1 1 1 1....
    noInline("var x = 0; while(true) { print(x); x = 1; }");
  }

  public void testDoNotExitLoop() {
    noInline("while (z) { var x = 3; } var y = x;");
  }

  public void testDoNotInlineWithinLoop() {
    noInline("var y = noSFX(); do { var z = y.foo(); } while (true);");
  }

  public void testDefinitionAfterUse() {
    inline("var x = 0; print(x); x = 1", "var x; print(0); x = 1");
  }

  public void testInlineSameVariableInStraightLine() {
    inline("var x; x = 1; print(x); x = 2; print(x)",
        "var x; print(1); print(2)");
  }

  public void testInlineInDifferentPaths() {
    inline("var x; if (print) {x = 1; print(x)} else {x = 2; print(x)}",
        "var x; if (print) {print(1)} else {print(2)}");
  }

  public void testNoInlineInMergedPath() {
    noInline(
        "var x,y;x = 1;while(y) { if(y){ print(x) } else { x = 1 } } print(x)");
  }

  public void testInlineIntoExpressions() {
    inline("var x = 1; print(x + 1);", "var x; print(1 + 1)");
  }

  public void testInlineExpressions1() {
    inline("var a, b; var x = a+b; print(x)", "var a, b; var x; print(a+b)");
  }

  public void testInlineExpressions2() {
    // We can't inline because of the redefinition of "a".
    noInline("var a, b; var x = a + b; a = 1; print(x)");
  }

  public void testInlineExpressions3() {
    inline("var a,b,x; x=a+b; x=a-b ; print(x)",
           "var a,b,x; x=a+b; print(a-b)");
  }

  public void testInlineExpressions4() {
    // Precision is lost due to comma's.
    noInline("var a,b,x; x=a+b, x=a-b; print(x)");
  }

  public void testInlineExpressions5() {
    noInline("var a; var x = a = 1; print(x)");
  }

  public void testInlineExpressions6() {
    noInline("var a, x; a = 1 + (x = 1); print(x)");
  }

  public void testInlineExpression7() {
    // Possible side effects in foo() that might conflict with bar();
    noInline("var x = foo() + 1; bar(); print(x)");

    // This is a possible case but we don't have analysis to prove this yet.
    // TODO(user): It is possible to cover this case with the same algorithm
    //                as the missing return check.
    noInline("var x = foo() + 1; print(x)");
  }

  public void testInlineExpression8() {
    // The same variable inlined twice.
    inline("var x = a + b; print(x);      x = a - b; print(x)",
           "var x;         print(a + b);             print(a - b)");
  }

  public void testInlineExpression9() {
    // Check for actual control flow sensitivity.
    inline("var x; if (g) { x= a + b; print(x)    }  x = a - b; print(x)",
           "var x; if (g) {           print(a + b)}             print(a - b)");
  }

  public void testInlineExpression10() {
    // The DFA is not fine grain enough for this.
    noInline("var x, y; x = ((y = 1), print(y))");
  }

  public void testInlineExpressions11() {
    inline("var x; x = x + 1; print(x)", "var x; print(x + 1)");
    noInline("var x; x = x + 1; print(x); print(x)");
  }

  public void testInlineExpressions12() {
    // ++ is an assignment and considered to modify state so it will not be
    // inlined.
    noInline("var x = 10; x = c++; print(x)");
  }

  public void testInlineExpressions13() {
    inline("var a = 1, b = 2;" +
           "var x = a;" +
           "var y = b;" +
           "var z = x + y;" +
           "var i = z;" +
           "var j = z + y;" +
           "var k = i;",

           "var a, b;" +
           "var x;" +
           "var y = 2;" +
           "var z = 1 + y;" +
           "var i;" +
           "var j = z + y;" +
           "var k = z;");
  }

  public void testNoInlineIfDefinitionMayNotReach() {
    noInline("var x; if (x=1) {} x;");
  }

  public void testNoInlineEscapedToInnerFunction() {
    noInline("var x = 1; function foo() { x = 2 }; print(x)");
  }

  public void testNoInlineLValue() {
    noInline("var x; if (x = 1) { print(x) }");
  }

  public void testSwitchCase() {
    inline("var x = 1; switch(x) { }", "var x; switch(1) { }");
  }

  public void testShadowedVariableInnerFunction() {
    inline("var x = 1; print(x) || (function() {  var x; x = 1; print(x)})()",
        "var x; print(1) || (function() {  var x; print(1)})()");
  }

  public void testCatch() {
    noInline("var x = 0; try { } catch (x) { }");
    noInline("try { } catch (x) { print(x) }");
  }

  public void testNoInlineGetProp() {
    // We don't know if j alias a.b
    noInline("var x = a.b.c; j.c = 1; print(x);");
  }

  public void testNoInlineGetProp2() {
    noInline("var x = 1 * a.b.c; j.c = 1; print(x);");
  }

  public void testNoInlineGetProp3() {
    // Anything inside a function is fine.
    inline("var x = function(){1 * a.b.c}; print(x);",
           "var x; print(function(){1 * a.b.c});");
  }

  public void testNoInlineGetEle() {
    // Again we don't know if i = j
    noInline("var x = a[i]; a[j] = 2; print(x); ");
  }

  // TODO(user): These should be inlinable.
  public void testNoInlineConstructors() {
    noInline("var x = new Iterator(); x.next();");
  }

  // TODO(user): These should be inlinable.
  public void testNoInlineArrayLits() {
    noInline("var x = []; print(x)");
  }

  // TODO(user): These should be inlinable.
  public void testNoInlineObjectLits() {
    noInline("var x = {}; print(x)");
  }

  // TODO(user): These should be inlinable after the REGEX checks.
  public void testNoInlineRegExpLits() {
    noInline("var x = /y/; print(x)");
  }

  public void testInlineConstructorCallsIntoLoop() {
    // Don't inline construction into loops.
    noInline("var x = new Iterator();" +
             "for(i = 0; i < 10; i++) {j = x.next()}");
  }

  public void testRemoveWithLabels() {
    inline("var x = 1; L: x = 2; print(x)", "var x = 1; print(2)");
    inline("var x = 1; L: M: x = 2; print(x)", "var x = 1; print(2)");
    inline("var x = 1; L: M: N: x = 2; print(x)", "var x = 1; print(2)");
  }

  public void testInlineAcrossSideEffect1() {
    inline("var y; var x = noSFX(y); print(x)", "var y;var x;print(noSFX(y))");
  }

  public void testInlineAcrossSideEffect2() {
    // Think noSFX() as a function that reads y.foo and return it
    // and SFX() write some new value of y.foo. If that's the case,
    // inlining across hasSFX() is not valid.

    // This is a case where hasSFX is right of the source of the inlining.
    noInline("var y; var x = noSFX(y), z = hasSFX(y); print(x)");
    noInline("var y; var x = noSFX(y), z = new hasSFX(y); print(x)");
    noInline("var y; var x = new noSFX(y), z = new hasSFX(y); print(x)");
  }

  public void testInlineAcrossSideEffect3() {
    // This is a case where hasSFX is left of the destination of the inlining.
    noInline("var y; var x = noSFX(y); hasSFX(y), print(x)");
    noInline("var y; var x = noSFX(y); new hasSFX(y), print(x)");
    noInline("var y; var x = new noSFX(y); new hasSFX(y), print(x)");
  }

  public void testInlineAcrossSideEffect4() {
    // This is a case where hasSFX is some control flow path between the
    // source and its destination.
    noInline("var y; var x = noSFX(y); hasSFX(y); print(x)");
    noInline("var y; var x = noSFX(y); new hasSFX(y); print(x)");
    noInline("var y; var x = new noSFX(y); new hasSFX(y); print(x)");
  }

  public void testCanInlineAcrossNoSideEffect() {
    inline("var y; var x = noSFX(Y), z = noSFX(); noSFX(); noSFX(), print(x)",
           "var y; var x, z = noSFX(); noSFX(); noSFX(), print(noSFX(Y))");
  }

  public void testDependOnOuterScopeVariables() {
    noInline("var x; function foo() { var y = x; x = 0; print(y) }");
    noInline("var x; function foo() { var y = x; x++; print(y) }");

    // Sadly, we don't understand the data flow of outer scoped variables as
    // it can be modified by code outside of this scope. We can't inline
    // at all if the definition has dependence on such variable.
    noInline("var x; function foo() { var y = x; print(y) }");
  }

  public void testInlineIfNameIsLeftSideOfAssign() {
    inline("var x = 1; x = print(x) + 1", "var x; x = print(1) + 1");
    inline("var x = 1; L: x = x + 2", "var x; L: x = 1 + 2");
    inline("var x = 1; x = (x = x + 1)", "var x; x = (x = 1 + 1)");

    noInline("var x = 1; x = (x = (x = 10) + x)");
    noInline("var x = 1; x = (f(x) = (x = 10) + x);");
    noInline("var x = 1; x=-1,foo(x)");
    noInline("var x = 1; x-=1,foo(x)");
  }

  public void testInlineArguments() {
    testSame("function _func(x) { print(x) }");
    testSame("function _func(x,y) { if(y) { x = 1 }; print(x) }");

    test("function(x, y) { x = 1; print(x) }",
         "function(x, y) { print(1) }");

    test("function(x, y) { if (y) { x = 1; print(x) }}",
         "function(x, y) { if (y) { print(1) }}");
  }

  public void testVarAssinInsideHookIssue965() {
    noInline("var i = 0; return 1 ? (i = 5) : 0, i;");
    noInline("var i = 0; return (1 ? (i = 5) : 0) ? i : 0;");
    noInline("var i = 0; return (1 ? (i = 5) : 0) || i;");
    noInline("var i = 0; return (1 ? (i = 5) : 0) * i;");
  }

  private void noInline(String input) {
    inline(input, input);
  }

  private void inline(String input, String expected) {
    test(EXTERN_FUNCTIONS, "function _func() {" + input + "}",
        "function _func() {" + expected + "}", null, null);
  }
}
