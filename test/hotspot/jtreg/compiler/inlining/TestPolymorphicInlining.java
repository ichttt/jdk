/*
 * Copyright (c) 2026, Oracle and/or its affiliates. All rights reserved.
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

package compiler.inlining;

import compiler.lib.ir_framework.*;
import jdk.test.lib.Asserts;

/*
 * @test
 * @summary Verify that C2 speculatively inlines call sites with more than two
 *          receivers when UsePolymorphicInlining is enabled, but not if the
 *          call site sits in an uncommon branch or has more receivers than
 *          PolymorphicInliningLimit allows.
 * @library /test/lib /
 * @run driver compiler.inlining.TestPolymorphicInlining
 */
public class TestPolymorphicInlining {
    interface I {
        int value();
    }

    static class A implements I { public int value() { return 1; } }
    static class B implements I { public int value() { return 2; } }
    static class C implements I { public int value() { return 3; } }
    static class D implements I { public int value() { return 4; } }
    static class E implements I { public int value() { return 5; } }

    static final I[] FOUR_RECEIVERS = { new A(), new B(), new C(), new D() };
    static final I[] FIVE_RECEIVERS = { new A(), new B(), new C(), new D(), new E() };

    // The cold call site below is only reached on every COLD_PERIOD-th invocation.
    static final int COLD_PERIOD = 64;

    public static void main(String[] args) {
        Scenario polymorphicOn = new Scenario(0, "-XX:+UsePolymorphicInlining", "-XX:PolymorphicInliningLimit=4");
        Scenario polymorphicOff = new Scenario(1, "-XX:-UsePolymorphicInlining");
        new TestFramework().addScenarios(polymorphicOn, polymorphicOff).start();
    }

    // Four receivers are within PolymorphicInliningLimit, so all of them are
    // guarded by a type check and the fall-through path can trap. Without
    // polymorphic inlining, the profile does not even know all receivers and the
    // call stays virtual.
    @Test
    @IR(applyIf = {"UsePolymorphicInlining", "true"},
        counts = {IRNode.BIMORPHIC_TRAP, "1"},
        failOn = {IRNode.DYNAMIC_CALL_OF_METHOD, "value"})
    @IR(applyIf = {"UsePolymorphicInlining", "false"},
        counts = {IRNode.DYNAMIC_CALL_OF_METHOD, "value", "1"})
    public static int testFourReceivers(int i) {
        return FOUR_RECEIVERS[i & 3].value();
    }

    @Run(test = "testFourReceivers")
    public void runFourReceivers() {
        for (int i = 0; i < 4; i++) {
            Asserts.assertEQ(i + 1, testFourReceivers(i));
        }
    }

    // Five receivers exceed both PolymorphicInliningLimit and the width of the
    // type profile, so the call site is megamorphic and stays virtual.
    @Test
    @IR(counts = {IRNode.DYNAMIC_CALL_OF_METHOD, "value", "1"},
        failOn = {IRNode.BIMORPHIC_TRAP})
    public static int testFiveReceivers(int i) {
        return FIVE_RECEIVERS[i % 5].value();
    }

    @Run(test = "testFiveReceivers")
    public void runFiveReceivers() {
        for (int i = 0; i < 5; i++) {
            Asserts.assertEQ(i + 1, testFiveReceivers(i));
        }
    }

    // The call site has four receivers as well, but it is part of a branch that
    // is rarely taken. Emitting a chain of type checks there is not worth the
    // code size, so the call is left virtual.
    @Test
    @IR(counts = {IRNode.DYNAMIC_CALL_OF_METHOD, "value", "1"},
        failOn = {IRNode.BIMORPHIC_TRAP})
    public static int testColdCallSite(int i) {
        if ((i & (COLD_PERIOD - 1)) == 0) {
            return FOUR_RECEIVERS[(i / COLD_PERIOD) & 3].value();
        }
        return 0;
    }

    @Run(test = "testColdCallSite")
    public void runColdCallSite() {
        for (int i = 0; i < 4 * COLD_PERIOD; i++) {
            int expected = (i % COLD_PERIOD == 0) ? (i / COLD_PERIOD) + 1 : 0;
            Asserts.assertEQ(expected, testColdCallSite(i));
        }
    }
}
