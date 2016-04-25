package io.github.ezequielb.gradle.dependencyenforcer.rule.applier

import org.junit.Assert
import org.junit.Test

class SetBasedDependencyApplierTest {

    @Test
    public void testApply() {
        SetBasedDependencyApplier applier =
                new SetBasedDependencyApplier(["com.example.Class1", "com.example.Class2"] as Set, UsageEnforcement.EXPLICITLY_ALLOWED, 5)
        Assert.assertNull(applier.apply("com.example"))
        def className = "com.example.Class1"
        def result = applier.apply(className)
        Assert.assertNotNull(result)
        Assert.assertEquals(UsageEnforcement.EXPLICITLY_ALLOWED, result.usageEnforcement)
        Assert.assertEquals(className.size() + 5, result.weight)
    }

}
