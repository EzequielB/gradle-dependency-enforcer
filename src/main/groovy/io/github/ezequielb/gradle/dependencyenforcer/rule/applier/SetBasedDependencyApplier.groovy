package io.github.ezequielb.gradle.dependencyenforcer.rule.applier

import io.github.ezequielb.gradle.dependencyenforcer.util.PatternUtils

public class SetBasedDependencyApplier implements DependencyRuleApplier {

    public final Set<String> dependencyNames
    public final int initialWeight
    public final UsageEnforcement usageEnforcement

    public SetBasedDependencyApplier(Set<String> dependencyNames, UsageEnforcement usageEnforcement, int initialWeight) {
        this.dependencyNames = dependencyNames
        this.usageEnforcement = usageEnforcement
        this.initialWeight = initialWeight
    }

    @Override
    public ApplyResult apply(String dependencyName) {
        if (!dependencyNames.contains(dependencyName)) {
            return null
        }
        return new ApplyResult(dependencyName, usageEnforcement, initialWeight + PatternUtils.getResolvedExpressionWeight(dependencyName))
    }

}



