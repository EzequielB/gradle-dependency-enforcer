package io.github.ezequielb.gradle.dependencyenforcer.enforcer

import io.github.ezequielb.gradle.dependencyenforcer.rule.DependencyRule
import io.github.ezequielb.gradle.dependencyenforcer.rule.applier.ApplyResult
import io.github.ezequielb.gradle.dependencyenforcer.rule.applier.UsageEnforcement
import io.github.ezequielb.gradle.dependencyenforcer.util.DependencyAnalysis

public class DependencyRuleEnforcer {

    final DependencyAnalysis dependencyAnalysis
    final List<DependencyRule> dependencyRules
    public boolean strict = true

    public DependencyRuleEnforcer(DependencyAnalysis dependencyAnalysis, List<DependencyRule> dependencyRules) {
        this.dependencyAnalysis = dependencyAnalysis
        this.dependencyRules = dependencyRules
    }

    public DependencyEnforcerResult enforceRules() {
        DependencyEnforcerResult.DependencyEnforceResultCollector collector = DependencyEnforcerResult.newCollector()
        dependencyAnalysis.analyzedClasses.each { className ->
            Set<String> disallowed = findDisallowed(className)
            if (!disallowed.isEmpty()) {
                collector.collectDisallowed(className, disallowed)
            }
        }
        collector.build()
    }

    private Set<String> findDisallowed(String className) {
        Set<String> dependencies = dependencyAnalysis.getClassDependencies(className)
        Map<String, ApplyResult> applyResultMap = new HashMap<>()
        dependencyRules.each { dependencyRule ->
            if (dependencyRule.canApply(className)) {
                def applier = dependencyRule.getApplier(className)
                dependencies.each {
                    def result = ApplyResult.resolveConflictingResults(applier.apply(it), applyResultMap.get(it))
                    if (result != null) {
                        applyResultMap.put(it, result)
                    }
                }
            }
        }
        Set<String> disallowed = applyResultMap.entrySet()
                .findAll { it.value.usageEnforcement == UsageEnforcement.EXPLICITLY_DISALLOWED }
                .collect { it.key } as Set
        if (strict) {
            disallowed.addAll(dependencies - applyResultMap.keySet())

        }
        disallowed
    }

}
