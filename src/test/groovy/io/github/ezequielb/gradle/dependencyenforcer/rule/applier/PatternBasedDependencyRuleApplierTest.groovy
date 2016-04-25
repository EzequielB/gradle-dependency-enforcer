package io.github.ezequielb.gradle.dependencyenforcer.rule.applier

import org.junit.Assert;
import org.junit.Test;

class PatternBasedDependencyRuleApplierTest {

    @Test
    public void testApply() {
        PatternBasedDependencyRuleApplier ruleApplier =
                new PatternBasedDependencyRuleApplier("com.a.b", 5, UsageEnforcement.EXPLICITLY_ALLOWED)
        Assert.assertNull(ruleApplier.apply("com.b.c.Class1"))
        def result = ruleApplier.apply("com.a.b.Class1")
        Assert.assertNotNull(result)
        Assert.assertEquals(UsageEnforcement.EXPLICITLY_ALLOWED, result.usageEnforcement)
        Assert.assertEquals(5, result.weight)
    }

}
