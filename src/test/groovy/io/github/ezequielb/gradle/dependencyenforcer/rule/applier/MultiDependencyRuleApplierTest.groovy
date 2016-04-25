package io.github.ezequielb.gradle.dependencyenforcer.rule.applier

import groovy.mock.interceptor.MockFor
import org.junit.Assert
import org.junit.Test

class MultiDependencyRuleApplierTest {

    @Test
    public void testApplyNoCollision() {
        def mock = new MockFor(DependencyRuleApplier)
        mock.demand.apply({ return new ApplyResult(it, UsageEnforcement.EXPLICITLY_ALLOWED, 10) })
        DependencyRuleApplier applier1 = mock.proxyInstance()

        mock = new MockFor(DependencyRuleApplier)
        mock.demand.apply({ return null })
        DependencyRuleApplier applier2 = mock.proxyInstance()

        MultiDependencyRuleApplier applier = new MultiDependencyRuleApplier([applier2, applier1])
        ApplyResult result = applier.apply("somepackage")
        Assert.assertNotNull(result)
        Assert.assertEquals(10, result.weight)
        Assert.assertEquals(UsageEnforcement.EXPLICITLY_ALLOWED, result.usageEnforcement)
    }

    @Test
    public void testApplyWithCollisionAndDifferentWeight() {
        def mock = new MockFor(DependencyRuleApplier)
        mock.demand.apply({ return new ApplyResult(it, UsageEnforcement.EXPLICITLY_ALLOWED, 10) })
        DependencyRuleApplier applier1 = mock.proxyInstance()

        mock = new MockFor(DependencyRuleApplier)
        mock.demand.apply({ return new ApplyResult(it, UsageEnforcement.EXPLICITLY_DISALLOWED, 15) })
        DependencyRuleApplier applier2 = mock.proxyInstance()

        MultiDependencyRuleApplier applier = new MultiDependencyRuleApplier([applier1, applier2])
        ApplyResult result = applier.apply("somepackage")
        Assert.assertNotNull(result)
        Assert.assertEquals(15, result.weight)
        Assert.assertEquals(UsageEnforcement.EXPLICITLY_DISALLOWED, result.usageEnforcement)
    }

    @Test
    public void testApplyWithCollisionAndSameWeight() {
        def mock = new MockFor(DependencyRuleApplier)
        mock.demand.apply({ return new ApplyResult(it, UsageEnforcement.EXPLICITLY_ALLOWED, 10) })
        DependencyRuleApplier applier1 = mock.proxyInstance()

        mock = new MockFor(DependencyRuleApplier)
        mock.demand.apply({ return new ApplyResult(it, UsageEnforcement.EXPLICITLY_DISALLOWED, 10) })
        DependencyRuleApplier applier2 = mock.proxyInstance()

        MultiDependencyRuleApplier applier = new MultiDependencyRuleApplier([applier2, applier1])
        ApplyResult result = applier.apply("somepackage")
        Assert.assertNotNull(result)
        Assert.assertEquals(10, result.weight)
        Assert.assertEquals(UsageEnforcement.EXPLICITLY_ALLOWED, result.usageEnforcement)
    }

}
