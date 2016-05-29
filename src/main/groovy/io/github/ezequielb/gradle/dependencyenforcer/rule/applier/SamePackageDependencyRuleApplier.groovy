package io.github.ezequielb.gradle.dependencyenforcer.rule.applier

import io.github.ezequielb.gradle.dependencyenforcer.util.PatternUtils

import java.util.regex.Pattern

class SamePackageDependencyRuleApplier implements DependencyRuleApplier {

    public final Pattern samePackagePattern
    public final int matchingWeight
    public final UsageEnforcement usageEnforcement

    SamePackageDependencyRuleApplier(String className, UsageEnforcement usageEnforcement, int initialWeight) {
        def pkg = PatternUtils.getClassPackage(className)
        this.samePackagePattern = PatternUtils.buildPackagePattern("$pkg.")
        this.matchingWeight = initialWeight + PatternUtils.getResolvedExpressionWeight(pkg)
        this.usageEnforcement = usageEnforcement
    }

    @Override
    ApplyResult apply(String dependencyName) {
        if (samePackagePattern.matcher(dependencyName).matches()) {
            return new ApplyResult(dependencyName, usageEnforcement, matchingWeight)
        }
        return null
    }
}
