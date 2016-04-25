package io.github.ezequielb.gradle.dependencyenforcer.rule.applier

public class MultiDependencyRuleApplier implements DependencyRuleApplier {

    private final List<DependencyRuleApplier> appliers

    public MultiDependencyRuleApplier(List<DependencyRuleApplier> appliers) {
        this.appliers = appliers
    }

    @Override
    ApplyResult apply(String dependencyName) {
        ApplyResult result = null
        appliers.each {
            result = ApplyResult.resolveConflictingResults(result, it.apply(dependencyName))
        }
        return result
    }

}
