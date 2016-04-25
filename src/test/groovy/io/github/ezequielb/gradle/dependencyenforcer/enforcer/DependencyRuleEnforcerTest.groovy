package io.github.ezequielb.gradle.dependencyenforcer.enforcer

import io.github.ezequielb.gradle.dependencyenforcer.rule.DependencyRule
import io.github.ezequielb.gradle.dependencyenforcer.rule.applier.ApplyResult
import io.github.ezequielb.gradle.dependencyenforcer.rule.applier.DependencyRuleApplier
import io.github.ezequielb.gradle.dependencyenforcer.rule.applier.UsageEnforcement
import io.github.ezequielb.gradle.dependencyenforcer.util.DependencyAnalysis
import org.junit.Assert
import org.junit.Test

class DependencyRuleEnforcerTest {

    static class MapBasedDependencyAnalisys implements DependencyAnalysis {

        public Map<String, Set<String>> dependencyMap

        @Override
        Set<String> getAnalyzedClasses() {
            return dependencyMap.keySet()
        }

        @Override
        Set<String> getClassDependencies(String className) {
            return dependencyMap.get(className)
        }
    }

    static class MapBasedDependencyRule implements DependencyRule {

        Set<String> applyToClasses
        Map<String, ApplyResult> applyResultMap
        DependencyRuleApplier applier = new DependencyRuleApplier() {
            @Override
            ApplyResult apply(String dependencyName) {
                return applyResultMap.get(dependencyName)
            }
        }

        @Override
        boolean canApply(String className) {
            return applyToClasses.contains(className)
        }

        @Override
        DependencyRuleApplier getApplier(String className) {
            return applier
        }
    }

    static final dependencyAnalisys1 = [
            'dependencyMap': [
                    'a': ['b', 'c', 'd'] as Set,
                    'b': ['d'] as Set,
                    'c': ['a'] as Set,
                    'd': ['a'] as Set
            ]] as MapBasedDependencyAnalisys

    static final def dependencyRule1 = [
            applyToClasses:
                    ['a', 'b'] as Set,
            applyResultMap:
                    [
                            'a': null,
                            'b': null,
                            'c': new ApplyResult('', UsageEnforcement.EXPLICITLY_ALLOWED, 10),
                            'd': new ApplyResult('', UsageEnforcement.EXPLICITLY_DISALLOWED, 10)
                    ]
    ] as MapBasedDependencyRule

    static final def dependencyRule2 = [
            applyToClasses:
                    ['c', 'd'] as Set,
            applyResultMap:
                    [
                            'a': new ApplyResult('', UsageEnforcement.EXPLICITLY_ALLOWED, 10),
                            'b': null,
                            'c': null,
                            'd': null
                    ]
    ] as MapBasedDependencyRule

    static final def dependencyRule3 = [
            applyToClasses:
                    ['d'] as Set,
            applyResultMap:
                    [
                            'a': new ApplyResult('', UsageEnforcement.EXPLICITLY_DISALLOWED, 11),
                            'b': null,
                            'c': null,
                            'd': null
                    ]] as MapBasedDependencyRule

    @Test
    public void enforceTest1() {
        DependencyRuleEnforcer enforcer =
                new DependencyRuleEnforcer(dependencyAnalisys1, [])
        DependencyEnforcerResult result = enforcer.enforceRules()
        Assert.assertNotNull(result)
        Assert.assertFalse(result.successful)
        Assert.assertEquals(dependencyAnalisys1.dependencyMap,
                result.disallowed)
    }

    @Test
    public void enforceTest2() {
        DependencyRuleEnforcer enforcer =
                new DependencyRuleEnforcer(dependencyAnalisys1, [dependencyRule1, dependencyRule2, dependencyRule3])
        DependencyEnforcerResult result = enforcer.enforceRules()
        Assert.assertNotNull(result)
        Assert.assertFalse(result.successful)
        Assert.assertEquals([
                'a': ['b', 'd'] as Set,
                'b': ['d'] as Set,
                'd': ['a'] as Set],
                result.disallowed)
    }

    @Test
    public void enforceTest3() {
        DependencyRuleEnforcer enforcer =
                new DependencyRuleEnforcer(dependencyAnalisys1, [dependencyRule1, dependencyRule2])
        DependencyEnforcerResult result = enforcer.enforceRules()
        Assert.assertNotNull(result)
        Assert.assertFalse(result.successful)
        Assert.assertEquals([
                'a': ['b', 'd'] as Set,
                'b': ['d'] as Set],
                result.disallowed)
    }

    @Test
    public void enforceTest4() {
        DependencyRuleEnforcer enforcer =
                new DependencyRuleEnforcer(dependencyAnalisys1, [dependencyRule2])
        DependencyEnforcerResult result = enforcer.enforceRules()
        Assert.assertNotNull(result)
        Assert.assertFalse(result.successful)
        Assert.assertEquals([
                'a': ['b', 'c', 'd'] as Set,
                'b': ['d'] as Set],
                result.disallowed)
    }

}
