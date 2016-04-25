package io.github.ezequielb.gradle.dependencyenforcer.rule

import groovy.mock.interceptor.MockFor
import io.github.ezequielb.gradle.dependencyenforcer.rule.applier.UsageEnforcement
import io.github.ezequielb.gradle.dependencyenforcer.util.ArtifactResolver
import org.junit.Assert
import org.junit.Test

class PatternBasedDependencyRuleTest {

    @Test
    public void testWithWildcard() {
        PatternBasedDependencyRule rule =
                new PatternBasedDependencyRule('*', [], ['*'], null)

        Assert.assertTrue(rule.canApply('com.example.b.Class1'))
        Assert.assertTrue(rule.canApply('com.example.c.Class1'))
        def className = 'com.example.a.Class1'
        Assert.assertTrue(rule.canApply(className))
        def applier = rule.getApplier(className)

        def applyResult = applier.apply('com.example.z.Class1')
        Assert.assertNotNull(applyResult)
        Assert.assertEquals(UsageEnforcement.EXPLICITLY_DISALLOWED, applyResult.usageEnforcement)
        Assert.assertEquals(0, applyResult.weight)

        applyResult = applier.apply('com.example.b.Class1')
        Assert.assertNotNull(applyResult)
        Assert.assertEquals(UsageEnforcement.EXPLICITLY_DISALLOWED, applyResult.usageEnforcement)
        Assert.assertEquals(0, applyResult.weight)

        applyResult = applier.apply('com.example.c.Class1')
        Assert.assertNotNull(applyResult)
        Assert.assertEquals(UsageEnforcement.EXPLICITLY_DISALLOWED, applyResult.usageEnforcement)
        Assert.assertEquals(0, applyResult.weight)

        applyResult = applier.apply('com.example.d.Class1')
        Assert.assertNotNull(applyResult)
        Assert.assertEquals(UsageEnforcement.EXPLICITLY_DISALLOWED, applyResult.usageEnforcement)
        Assert.assertEquals(0, applyResult.weight)
    }

    @Test
    public void testWithoutCaptures() {
        PatternBasedDependencyRule rule =
                new PatternBasedDependencyRule('com.example.a', ['com.example.b', 'com.example.c'], ['com.example.d'], null)

        Assert.assertFalse(rule.canApply('com.example.b.Class1'))
        Assert.assertFalse(rule.canApply('com.example.c.Class1'))
        def className = 'com.example.a.Class1'
        Assert.assertTrue(rule.canApply(className))
        def applier = rule.getApplier(className)

        def applyResult = applier.apply('com.example.z.Class1')
        Assert.assertNull(applyResult)

        applyResult = applier.apply('com.example.b.Class1')
        Assert.assertNotNull(applyResult)
        Assert.assertEquals(UsageEnforcement.EXPLICITLY_ALLOWED, applyResult.usageEnforcement)
        Assert.assertEquals(26, applyResult.weight)

        applyResult = applier.apply('com.example.c.Class1')
        Assert.assertNotNull(applyResult)
        Assert.assertEquals(UsageEnforcement.EXPLICITLY_ALLOWED, applyResult.usageEnforcement)
        Assert.assertEquals(26, applyResult.weight)

        applyResult = applier.apply('com.example.d.Class1')
        Assert.assertNotNull(applyResult)
        Assert.assertEquals(UsageEnforcement.EXPLICITLY_DISALLOWED, applyResult.usageEnforcement)
        Assert.assertEquals(26, applyResult.weight)
    }

    @Test
    public void testWithCaptures() {
        PatternBasedDependencyRule rule =
                new PatternBasedDependencyRule('com.example.{0}', ['com.example.pack1.{0}'], ['com.example.{0}.f'], null)

        Assert.assertTrue(rule.canApply('com.example.b.Class1'))
        Assert.assertTrue(rule.canApply('com.example.c.Class1'))
        def className = 'com.example.a.Class1'
        Assert.assertTrue(rule.canApply(className))
        def applier = rule.getApplier(className)

        def applyResult = applier.apply('com.example.z.Class1')
        Assert.assertNull(applyResult)

        applyResult = applier.apply('com.example.pack1.a.Class1')
        Assert.assertNotNull(applyResult)
        Assert.assertEquals(UsageEnforcement.EXPLICITLY_ALLOWED, applyResult.usageEnforcement)
        Assert.assertEquals('com.example.pack1.a'.size() + 'com.example.a'.size(), applyResult.weight)

        applyResult = applier.apply('com.example.a.f.Class1')
        Assert.assertNotNull(applyResult)
        Assert.assertEquals(UsageEnforcement.EXPLICITLY_DISALLOWED, applyResult.usageEnforcement)
        Assert.assertEquals(28, applyResult.weight)
    }

    @Test
    public void testWithArtifact() {
        def module1 = 'group:module1'
        def classesModule1 = ['com.allowed.pkg.class1', 'com.other.allowed.pkg.class2'] as Set

        def module2 = 'group:module2'
        def classesModule2 = ['com.disallowed.pkg.class1', 'com.other.disallowed.pkg.class2'] as Set

        ArtifactResolver artifactResolver = [resolveClasses: { artifactExpression ->
            if (module1.equals(artifactExpression)) {
                return classesModule1
            }
            if (module2.equals(artifactExpression)) {
                return classesModule2
            }
            return [] as Set
        }] as ArtifactResolver

        PatternBasedDependencyRule rule =
                new PatternBasedDependencyRule('com.example', [module1, 'b:b'], [module2, 'c:c'], artifactResolver)

        def className = 'com.example.a.Class1'
        Assert.assertTrue(rule.canApply(className))
        def applier = rule.getApplier(className)

        def applyResult = applier.apply('com.example.z.Class1')
        Assert.assertNull(applyResult)

        classesModule1.each {
            applyResult = applier.apply(it)
            Assert.assertNotNull(applyResult)
            Assert.assertEquals(UsageEnforcement.EXPLICITLY_ALLOWED, applyResult.usageEnforcement)
            Assert.assertEquals(it.size() + 'com.example'.size(), applyResult.weight)
        }

        classesModule2.each {
            applyResult = applier.apply(it)
            Assert.assertNotNull(applyResult)
            Assert.assertEquals(UsageEnforcement.EXPLICITLY_DISALLOWED, applyResult.usageEnforcement)
            Assert.assertEquals(it.size() + 'com.example'.size(), applyResult.weight)
        }
    }

}
