/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
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

package compiler.c2.x86;

import jdk.test.lib.Asserts;
import compiler.lib.ir_framework.*;
import java.util.Random;
import jdk.test.lib.Utils;

/*
 * @test
 * @summary Test that the checks for the div special case are omitted where possible
 * @bug 8312213
 * @library /test/lib /
 * @requires os.arch == "x86_64" | os.arch == "amd64"
 * @run driver compiler.c2.x86.TestDivSmallPath
 */
// TODO bugid
public class TestDivSmallPath {
    public static void main(String[] args) {
        TestFramework.run();
    }

    public static final int DIVIDEND = 500;
    public static final long DIVIDEND_LONG = 500L;

    @DontInline
    private void consume(int i) {}
    @DontInline
    private void consume(long i) {}

    @Test
    @Arguments(values = {Argument.RANDOM_EACH})
    @IR(counts = { IRNode.X86_DIV_REG_FAST, "1" }, phase = CompilePhase.FINAL_CODE)
    public int testDivIntFast(int val) {
        return DIVIDEND / val;
    }

    @Test
    @Arguments(values = {Argument.RANDOM_EACH})
    @IR(counts = { IRNode.X86_DIVMOD_REG_FAST, "1" }, phase = CompilePhase.FINAL_CODE)
    public int testDivModIntFast(int val) {
        int div = DIVIDEND / val;
        int mod = DIVIDEND % val;
        consume(mod);
        return div;
    }

    @Test
    @Arguments(values = {Argument.RANDOM_EACH})
    @IR(counts = { IRNode.X86_MOD_REG_FAST, "1" }, phase = CompilePhase.FINAL_CODE)
    public int testModIntFast(int val) {
        return DIVIDEND % val;
    }

    @Test
    @Arguments(values = {Argument.RANDOM_EACH})
    @IR(counts = { IRNode.X86_DIV_REG_FAST, "1" }, phase = CompilePhase.FINAL_CODE)
    public long testDivLongFast(long val) {
        return DIVIDEND_LONG / val;
    }

    @Test
    @Arguments(values = {Argument.RANDOM_EACH})
    @IR(counts = { IRNode.X86_DIVMOD_REG_FAST, "1" }, phase = CompilePhase.FINAL_CODE)
    public long testDivModLongFast(long val) {
        long div = DIVIDEND_LONG / val;
        long mod = DIVIDEND_LONG % val;
        consume(mod);
        return div;
    }

    @Test
    @Arguments(values = {Argument.RANDOM_EACH})
    @IR(counts = { IRNode.X86_MOD_REG_FAST, "1" }, phase = CompilePhase.FINAL_CODE)
    public long testModLongFast(long val) {
        return DIVIDEND_LONG % val;
    }

    @Test
    @Arguments(values = {Argument.RANDOM_EACH, Argument.RANDOM_EACH})
    @IR(failOn = {IRNode.X86_MOD_REG_FAST}, phase = CompilePhase.FINAL_CODE)
    public int testDivIntNormal(int divident, int val) {
        return divident / val;
    }

    @Test
    @Arguments(values = {Argument.RANDOM_EACH, Argument.RANDOM_EACH})
    @IR(failOn = {IRNode.X86_MOD_REG_FAST}, phase = CompilePhase.FINAL_CODE)
    public int testDivModIntNormal(int divident, int val) {
        int div = divident / val;
        int mod = divident % val;
        consume(mod);
        return div;
    }

    @Test
    @Arguments(values = {Argument.RANDOM_EACH, Argument.RANDOM_EACH})
    @IR(failOn = {IRNode.X86_MOD_REG_FAST}, phase = CompilePhase.FINAL_CODE)
    public int testModIntNormal(int divident, int val) {
        return divident % val;
    }

    @Test
    @Arguments(values = {Argument.RANDOM_EACH, Argument.RANDOM_EACH})
    @IR(failOn = {IRNode.X86_MOD_REG_FAST}, phase = CompilePhase.FINAL_CODE)
    public long testDivLongNormal(long divident, long val) {
        return divident / val;
    }

    @Test
    @Arguments(values = {Argument.RANDOM_EACH, Argument.RANDOM_EACH})
    @IR(failOn = {IRNode.X86_MOD_REG_FAST}, phase = CompilePhase.FINAL_CODE)
    public long testDivModLongNormal(long divident, long val) {
        long div = divident / val;
        long mod = divident % val;
        consume(mod);
        return div;
    }

    @Test
    @Arguments(values = {Argument.RANDOM_EACH, Argument.RANDOM_EACH})
    @IR(failOn = {IRNode.X86_MOD_REG_FAST}, phase = CompilePhase.FINAL_CODE)
    public long testModLongNormal(long divident, long val) {
        return divident % val;
    }
}
