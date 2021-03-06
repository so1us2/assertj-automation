/*
 * (c) Copyright 2020 Palantir Technologies Inc. All rights reserved.
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

package com.palantir.assertj.errorprone;

import com.google.errorprone.BugCheckerRefactoringTestHelper;
import org.junit.jupiter.api.Test;

public class AssertjAssertThatThrownByTest {

    @Test
    public void fix_with_single_throwing_statement() {
        fix().addInputLines(
                        "MyClass.java",
                        "import static org.junit.Assert.fail;",
                        "",
                        "import org.junit.jupiter.api.Test;",
                        "",
                        "class MyClass {",
                        "  @Test",
                        "  void foo() {",
                        "    try {",
                        "      System.out.println();",
                        "      fail(\"fail\");",
                        "    } catch (RuntimeException expected) {}",
                        "  }",
                        "}")
                .addOutputLines(
                        "MyClass.java",
                        "import static org.assertj.core.api.Assertions.assertThatThrownBy;",
                        "import static org.junit.Assert.fail;",
                        "",
                        "import org.junit.jupiter.api.Test;",
                        "",
                        "class MyClass {",
                        "  @Test",
                        "  void foo() {",
                        "    assertThatThrownBy(() -> System.out.println()).isInstanceOf(RuntimeException.class);",
                        "  }",
                        "}")
                .doTest(BugCheckerRefactoringTestHelper.TestMode.TEXT_MATCH);
    }

    @Test
    public void fix_with_multiple_throwing_statements() {
        fix().addInputLines(
                        "MyClass.java",
                        "import static org.junit.Assert.fail;",
                        "",
                        "import org.junit.jupiter.api.Test;",
                        "",
                        "class MyClass {",
                        "  @Test",
                        "  void foo() {",
                        "    try {",
                        "      System.out.println(\"1\");",
                        "      System.out.println(\"2\");",
                        "      System.out.println(\"3\");",
                        "      fail(\"fail\");",
                        "    } catch (RuntimeException expected) {}",
                        "  }",
                        "}")
                .addOutputLines(
                        "MyClass.java",
                        "import static org.assertj.core.api.Assertions.assertThatThrownBy;",
                        "import static org.junit.Assert.fail;",
                        "",
                        "import org.junit.jupiter.api.Test;",
                        "",
                        "class MyClass {",
                        "  @Test",
                        "  void foo() {",
                        "    assertThatThrownBy(() -> {",
                        "      System.out.println(\"1\");",
                        "      System.out.println(\"2\");",
                        "      System.out.println(\"3\");",
                        "    }).isInstanceOf(RuntimeException.class);",
                        "  }",
                        "}")
                .doTest(BugCheckerRefactoringTestHelper.TestMode.TEXT_MATCH);
    }

    @Test
    public void fix_while_preserving_fail_message() {
        fix().addInputLines(
                        "MyClass.java",
                        "import static org.junit.Assert.fail;",
                        "",
                        "import org.junit.jupiter.api.Test;",
                        "",
                        "class MyClass {",
                        "  @Test",
                        "  void foo() {",
                        "    try {",
                        "      System.out.println();",
                        "      fail(\"My error message.\");",
                        "    } catch (RuntimeException expected) {}",
                        "  }",
                        "}")
                .addOutputLines(
                        "MyClass.java",
                        "import static org.assertj.core.api.Assertions.assertThatThrownBy;",
                        "import static org.junit.Assert.fail;",
                        "",
                        "import org.junit.jupiter.api.Test;",
                        "",
                        "class MyClass {",
                        "  @Test",
                        "  void foo() {",
                        "    assertThatThrownBy(() -> System.out.println()).describedAs(\"My error message.\")"
                                + ".isInstanceOf(RuntimeException.class);",
                        "  }",
                        "}")
                .doTest(BugCheckerRefactoringTestHelper.TestMode.TEXT_MATCH);
    }

    @Test
    public void fix_with_comments_in_catch() {
        fix().addInputLines(
                        "MyClass.java",
                        "import static org.junit.Assert.fail;",
                        "",
                        "import org.junit.jupiter.api.Test;",
                        "",
                        "class MyClass {",
                        "  @Test",
                        "  void foo() {",
                        "    try {",
                        "      System.out.println();",
                        "      fail(\"My error message.\");",
                        "    } catch (RuntimeException expected) {",
                        "        // expected",
                        "    }",
                        "  }",
                        "}")
                .addOutputLines(
                        "MyClass.java",
                        "import static org.assertj.core.api.Assertions.assertThatThrownBy;",
                        "import static org.junit.Assert.fail;",
                        "",
                        "import org.junit.jupiter.api.Test;",
                        "",
                        "class MyClass {",
                        "  @Test",
                        "  void foo() {",
                        "    assertThatThrownBy(() -> System.out.println()).describedAs(\"My error message.\")"
                                + ".isInstanceOf(RuntimeException.class);",
                        "  }",
                        "}")
                .doTest(BugCheckerRefactoringTestHelper.TestMode.TEXT_MATCH);
    }

    @Test
    public void fix_with_comments_in_try() {
        fix().addInputLines(
                        "MyClass.java",
                        "import static org.junit.Assert.fail;",
                        "",
                        "import org.junit.jupiter.api.Test;",
                        "",
                        "class MyClass {",
                        "  @Test",
                        "  void foo() {",
                        "    try {",
                        "      // out is closed",
                        "      System.out.println();",
                        "      fail(\"My error message.\");",
                        "    } catch (RuntimeException expected) {",
                        "        // expected",
                        "    }",
                        "  }",
                        "}")
                .addOutputLines(
                        "MyClass.java",
                        "import static org.assertj.core.api.Assertions.assertThatThrownBy;",
                        "import static org.junit.Assert.fail;",
                        "",
                        "import org.junit.jupiter.api.Test;",
                        "",
                        "class MyClass {",
                        "  @Test",
                        "  void foo() {",
                        "      // out is closed",
                        "    assertThatThrownBy(() -> System.out.println()).describedAs(\"My error message.\")"
                                + ".isInstanceOf(RuntimeException.class);",
                        "  }",
                        "}")
                .doTest(BugCheckerRefactoringTestHelper.TestMode.TEXT_MATCH);
    }

    @Test
    public void skip_empty_try() {
        fix().addInputLines(
                        "MyClass.java",
                        "import static org.junit.Assert.fail;",
                        "",
                        "import org.junit.jupiter.api.Test;",
                        "",
                        "class MyClass {",
                        "  @Test",
                        "  void foo() {",
                        "    try {",
                        "    } catch (IllegalArgumentException expected) {}",
                        "  }",
                        "}")
                .expectUnchanged()
                .doTest(BugCheckerRefactoringTestHelper.TestMode.TEXT_MATCH);
    }

    @Test
    public void skip_multiple_catches() {
        fix().addInputLines(
                        "MyClass.java",
                        "import static org.junit.Assert.fail;",
                        "",
                        "import org.junit.jupiter.api.Test;",
                        "",
                        "class MyClass {",
                        "  @Test",
                        "  void foo() {",
                        "    try {",
                        "      System.out.println();",
                        "      fail(\"fail\");",
                        "    } catch (IllegalArgumentException expected) {",
                        "    } catch (NullPointerException expected) {",
                        "    }",
                        "  }",
                        "}")
                .expectUnchanged()
                .doTest(BugCheckerRefactoringTestHelper.TestMode.TEXT_MATCH);
    }

    @Test
    public void skip_missing_fail_statement() {
        fix().addInputLines(
                        "MyClass.java",
                        "import static org.junit.Assert.fail;",
                        "",
                        "import org.junit.jupiter.api.Test;",
                        "",
                        "class MyClass {",
                        "  @Test",
                        "  void foo() {",
                        "    try {",
                        "      System.out.println();",
                        "    } catch (IllegalArgumentException expected) {}",
                        "  }",
                        "}")
                .expectUnchanged()
                .doTest(BugCheckerRefactoringTestHelper.TestMode.TEXT_MATCH);
    }

    @Test
    public void skip_only_fail_in_try() {
        fix().addInputLines(
                        "MyClass.java",
                        "import static org.junit.Assert.fail;",
                        "",
                        "import org.junit.jupiter.api.Test;",
                        "",
                        "class MyClass {",
                        "  @Test",
                        "  void foo() {",
                        "    try {",
                        "      fail(\"fail\");",
                        "    } catch (IllegalArgumentException expected) {}",
                        "  }",
                        "}")
                .expectUnchanged()
                .doTest(BugCheckerRefactoringTestHelper.TestMode.TEXT_MATCH);
    }

    @Test
    public void ignore_with_non_final_arguments() {
        fix().addInputLines(
                        "MyClass.java",
                        "import static org.junit.Assert.fail;",
                        "",
                        "import org.junit.jupiter.api.Test;",
                        "",
                        "class MyClass {",
                        "  @Test",
                        "  void foo(boolean value) {",
                        "    String st = \"foo\";",
                        "    if (value) {",
                        "        st = \"bar\";",
                        "    }",
                        "    // BUG: Diagnostic contains:",
                        "    try {",
                        "      System.out.println(st);",
                        "      fail(\"fail\");",
                        "    } catch (RuntimeException expected) {}",
                        "  }",
                        "}")
                .expectUnchanged()
                .doTest();
    }

    private RefactoringValidator fix() {
        return RefactoringValidator.of(new AssertjRefactoring(new AssertjAssertThatThrownBy()), getClass());
    }
}
