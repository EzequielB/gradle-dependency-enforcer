package io.github.ezequielb.gradle.dependencyenforcer.rule.applier

import groovy.mock.interceptor.MockFor
import io.github.ezequielb.gradle.dependencyenforcer.util.ArtifactResolver
import org.junit.Assert
import org.junit.Test

class DependencyRuleApplierBuilderTest {

    @Test
    public void testBuildSetBasedDependencyApplier() {
        def mock = new MockFor(ArtifactResolver)
        def dependencyNames = ["abc"] as Set
        mock.demand.resolveClasses { dependencyNames }
        DependencyRuleApplier applier = DependencyRuleApplierBuilder.newBuilder(mock.proxyInstance()).expression("a:a")
                .initialWeight(1).usageEnforcement(UsageEnforcement.EXPLICITLY_ALLOWED).build()
        Assert.assertNotNull(applier)
        Assert.assertTrue(applier instanceof SetBasedDependencyApplier)
        SetBasedDependencyApplier setBasedDependencyApplier = (applier as SetBasedDependencyApplier)
        Assert.assertEquals(dependencyNames, setBasedDependencyApplier.dependencyNames)
        Assert.assertEquals(1, setBasedDependencyApplier.initialWeight)
        Assert.assertEquals(UsageEnforcement.EXPLICITLY_ALLOWED, setBasedDependencyApplier.usageEnforcement)
    }

    @Test
    public void testBuildPatternBasedDependencyRuleApplier() {
        def packagePattern = "com.example"
        DependencyRuleApplier applier = DependencyRuleApplierBuilder.newBuilder().expression(packagePattern)
                .initialWeight(1).usageEnforcement(UsageEnforcement.EXPLICITLY_ALLOWED).build()
        Assert.assertNotNull(applier)
        Assert.assertTrue(applier instanceof PatternBasedDependencyRuleApplier)
        PatternBasedDependencyRuleApplier patternBasedDependencyRuleApplier = (applier as PatternBasedDependencyRuleApplier)
        Assert.assertEquals(packagePattern.size() + 1, patternBasedDependencyRuleApplier.matchingWeight)
        Assert.assertEquals(UsageEnforcement.EXPLICITLY_ALLOWED, patternBasedDependencyRuleApplier.usageEnforcement)
    }

    @Test
    public void testBuildPatternBasedDependencyRuleApplierWithCaptures() {
        def packagePattern = "com.example.{0}"
        DependencyRuleApplier applier = DependencyRuleApplierBuilder.newBuilder().expression(packagePattern)
                .initialWeight(1).usageEnforcement(UsageEnforcement.EXPLICITLY_ALLOWED).captures(['abcd']).build()
        Assert.assertNotNull(applier)
        Assert.assertTrue(applier instanceof PatternBasedDependencyRuleApplier)
        PatternBasedDependencyRuleApplier patternBasedDependencyRuleApplier = (applier as PatternBasedDependencyRuleApplier)
        Assert.assertEquals(1 + 16, patternBasedDependencyRuleApplier.matchingWeight)
        Assert.assertEquals(UsageEnforcement.EXPLICITLY_ALLOWED, patternBasedDependencyRuleApplier.usageEnforcement)
    }

    @Test
    public void testIsStaticExpression() {
        Assert.assertTrue(DependencyRuleApplierBuilder.isStaticExpression("com.example"))
        Assert.assertFalse(DependencyRuleApplierBuilder.isStaticExpression("com.example.{0}"))
        Assert.assertFalse(DependencyRuleApplierBuilder.isStaticExpression(DependencyRuleApplierBuilder.SAME_PACKAGE_RULE_NAME))
    }

}
