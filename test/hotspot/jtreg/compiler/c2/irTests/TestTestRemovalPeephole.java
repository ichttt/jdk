/*
 * Copyright (c) 2023, 2024, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package compiler.c2.irTests;

import jdk.test.lib.Asserts;
import compiler.lib.ir_framework.*;
import java.util.Random;
import jdk.test.lib.Utils;

/*
 * @test
 * @summary Test that unnessercary test instructions are not present in the final code
 * @bug 8312213
 * @library /test/lib /
 * @requires vm.compiler2.enabled
 * @requires os.arch == "x86_64" | os.arch == "amd64"
 * @run driver compiler.c2.irTests.TestTestRemovalPeephole
 */
public class TestTestRemovalPeephole {
    static volatile boolean field;
    static int sinkInt;
    static long sinkLong;
    public static void main(String[] args) {
        TestFramework.run();
    }

    @Test
    @Arguments(values = {Argument.RANDOM_EACH, Argument.RANDOM_EACH})
    @IR(failOn = {IRNode.X86_TESTI_REG, IRNode.X86_TESTL_REG}, phase = CompilePhase.FINAL_CODE)
    public boolean testIntAddtionEquals0(int x, int y) {
        int result = x + y;
        if (result == 0) {
            field = true;
            return true;
        }
        return false;
    }

    @Test
    @Arguments(values = {Argument.RANDOM_EACH, Argument.RANDOM_EACH})
    @IR(failOn = {IRNode.X86_TESTI_REG, IRNode.X86_TESTL_REG}, phase = CompilePhase.FINAL_CODE)
    public boolean testIntAddtionNotEquals0(int x, int y) {
        int result = x + y;
        if (result != 0) {
            field = true;
            return true;
        }
        return false;
    }

    @Test
    @Arguments(values = {Argument.RANDOM_EACH, Argument.RANDOM_EACH})
    @IR(failOn = {IRNode.X86_TESTI_REG, IRNode.X86_TESTL_REG}, phase = CompilePhase.FINAL_CODE)
    public boolean testLongAddtionEquals0(long x, long y) {
        long result = x + y;
        if (result == 0) {
            field = true;
            return true;
        }
        return false;
    }

    @Test
    @Arguments(values = {Argument.RANDOM_EACH, Argument.RANDOM_EACH})
    @IR(failOn = {IRNode.X86_TESTI_REG, IRNode.X86_TESTL_REG}, phase = CompilePhase.FINAL_CODE)
    public boolean testLongAddtionNotEquals0(long x, long y) {
        long result = x + y;
        if (result != 0) {
            field = true;
            return true;
        }
        return false;
    }

    @Test
    @Arguments(values = {Argument.RANDOM_EACH, Argument.RANDOM_EACH})
    @IR(failOn = {IRNode.X86_TESTI_REG, IRNode.X86_TESTL_REG}, phase = CompilePhase.FINAL_CODE)
    public boolean testIntOrEquals0(int x, int y) {
        int result = x | y;
        if (result == 0) {
            field = true;
            return true;
        }
        return false;
    }

    @Test
    @Arguments(values = {Argument.RANDOM_EACH, Argument.RANDOM_EACH})
    @IR(failOn = {IRNode.X86_TESTI_REG, IRNode.X86_TESTL_REG}, phase = CompilePhase.FINAL_CODE)
    public boolean testIntOrNotEquals0(int x, int y) {
        int result = x | y;
        if (result != 0) {
            field = true;
            return true;
        }
        return false;
    }

    @Test
    @Arguments(values = {Argument.RANDOM_EACH, Argument.RANDOM_EACH})
    @IR(failOn = {IRNode.X86_TESTI_REG, IRNode.X86_TESTL_REG}, phase = CompilePhase.FINAL_CODE)
    public boolean testLongOrEquals0(long x, long y) {
        long result = x | y;
        if (result == 0) {
            field = true;
            return true;
        }
        return false;
    }

    @Test
    @Arguments(values = {Argument.RANDOM_EACH, Argument.RANDOM_EACH})
    @IR(failOn = {IRNode.X86_TESTI_REG, IRNode.X86_TESTL_REG}, phase = CompilePhase.FINAL_CODE)
    public boolean testLongOrNotEquals0(long x, long y) {
        long result = x | y;
        if (result != 0) {
            field = true;
            return true;
        }
        return false;
    }

    @Test
    @Arguments(values = {Argument.RANDOM_EACH, Argument.RANDOM_EACH})
    @IR(failOn = {IRNode.X86_TESTI_REG, IRNode.X86_TESTL_REG}, phase = CompilePhase.FINAL_CODE)
    public boolean testIntOrGreater0(int x, int y) {
        int result = x | y;
        if (result > 0) {
            field = true;
            return true;
        }
        return false;
    }

    @Test
    @Arguments(values = {Argument.RANDOM_EACH, Argument.RANDOM_EACH})
    @IR(failOn = {IRNode.X86_TESTI_REG, IRNode.X86_TESTL_REG}, phase = CompilePhase.FINAL_CODE)
    public boolean testLongOrGreater0(long x, long y) {
        long result = x | y;
        if (result > 0) {
            field = true;
            return true;
        }
        return false;
    }

    // The following tests check that the test is also removed if the instruction that sets the
    // flags is not placed directly in front of it. The store in between only emits a mov, which
    // leaves the flags alone. It uses the result of the addition/or, so it is guaranteed to be
    // scheduled after it and before the test, which is always scheduled next to the branch.

    @Test
    @Arguments(values = {Argument.RANDOM_EACH, Argument.RANDOM_EACH})
    @IR(failOn = {IRNode.X86_TESTI_REG, IRNode.X86_TESTL_REG}, phase = CompilePhase.FINAL_CODE)
    public boolean testIntAddtionEquals0StoreInBetween(int x, int y) {
        int result = x + y;
        sinkInt = result;
        if (result == 0) {
            field = true;
            return true;
        }
        return false;
    }

    @Test
    @Arguments(values = {Argument.RANDOM_EACH, Argument.RANDOM_EACH})
    @IR(failOn = {IRNode.X86_TESTI_REG, IRNode.X86_TESTL_REG}, phase = CompilePhase.FINAL_CODE)
    public boolean testLongAddtionEquals0StoreInBetween(long x, long y) {
        long result = x + y;
        sinkLong = result;
        if (result == 0) {
            field = true;
            return true;
        }
        return false;
    }

    @Test
    @Arguments(values = {Argument.RANDOM_EACH, Argument.RANDOM_EACH})
    @IR(failOn = {IRNode.X86_TESTI_REG, IRNode.X86_TESTL_REG}, phase = CompilePhase.FINAL_CODE)
    public boolean testIntOrGreater0StoreInBetween(int x, int y) {
        int result = x | y;
        sinkInt = result;
        if (result > 0) {
            field = true;
            return true;
        }
        return false;
    }

    @Test
    @Arguments(values = {Argument.RANDOM_EACH, Argument.RANDOM_EACH})
    @IR(failOn = {IRNode.X86_TESTI_REG, IRNode.X86_TESTL_REG}, phase = CompilePhase.FINAL_CODE)
    public boolean testLongOrNotEquals0StoreInBetween(long x, long y) {
        long result = x | y;
        sinkLong = result;
        if (result != 0) {
            field = true;
            return true;
        }
        return false;
    }

    // The following tests check that the test is kept if an instruction in between overwrites the
    // flags. The multiplication depends on the result of the addition, so it is scheduled between
    // the addition and the test, and imul modifies the overflow and the carry flag.

    @Test
    @Arguments(values = {Argument.RANDOM_EACH, Argument.RANDOM_EACH, Argument.RANDOM_EACH})
    @IR(counts = {IRNode.X86_TESTI_REG, "1"}, phase = CompilePhase.FINAL_CODE)
    public boolean testIntAddtionEquals0FlagsOverwritten(int x, int y, int z) {
        int result = x + y;
        sinkInt = result * z;
        if (result == 0) {
            field = true;
            return true;
        }
        return false;
    }

    @Test
    @Arguments(values = {Argument.RANDOM_EACH, Argument.RANDOM_EACH, Argument.RANDOM_EACH})
    @IR(counts = {IRNode.X86_TESTL_REG, "1"}, phase = CompilePhase.FINAL_CODE)
    public boolean testLongAddtionEquals0FlagsOverwritten(long x, long y, long z) {
        long result = x + y;
        sinkLong = result * z;
        if (result == 0) {
            field = true;
            return true;
        }
        return false;
    }

    @DontCompile
    public void assertResult(int x, int y) {
        Asserts.assertEQ((x + y) == 0, testIntAddtionEquals0(x, y));
        Asserts.assertEQ((x + y) != 0, testIntAddtionNotEquals0(x, y));
        Asserts.assertEQ((x | y) == 0, testIntOrEquals0(x, y));
        Asserts.assertEQ((x | y) != 0, testIntOrNotEquals0(x, y));
        Asserts.assertEQ((x | y) > 0, testIntOrGreater0(x, y));
        Asserts.assertEQ((x + y) == 0, testIntAddtionEquals0StoreInBetween(x, y));
        Asserts.assertEQ((x | y) > 0, testIntOrGreater0StoreInBetween(x, y));
        Asserts.assertEQ((x + y) == 0, testIntAddtionEquals0FlagsOverwritten(x, y, x));
    }

    @DontCompile
    public void assertResult(long x, long y) {
        Asserts.assertEQ((x + y) == 0, testLongAddtionEquals0(x, y));
        Asserts.assertEQ((x + y) != 0, testLongAddtionNotEquals0(x, y));
        Asserts.assertEQ((x | y) == 0, testLongOrEquals0(x, y));
        Asserts.assertEQ((x | y) != 0, testLongOrNotEquals0(x, y));
        Asserts.assertEQ((x | y) > 0, testLongOrGreater0(x, y));
        Asserts.assertEQ((x + y) == 0, testLongAddtionEquals0StoreInBetween(x, y));
        Asserts.assertEQ((x | y) != 0, testLongOrNotEquals0StoreInBetween(x, y));
        Asserts.assertEQ((x + y) == 0, testLongAddtionEquals0FlagsOverwritten(x, y, x));
    }
}
