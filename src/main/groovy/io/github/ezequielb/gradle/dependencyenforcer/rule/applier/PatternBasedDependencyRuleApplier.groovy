package io.github.ezequielb.gradle.dependencyenforcer.rule.applier

import io.github.ezequielb.gradle.dependencyenforcer.util.PatternUtils

import java.util.regex.Matcher
import java.util.regex.Pattern

class PatternBasedDependencyRuleApplier implements DependencyRuleApplier {

    final Pattern pattern;
    final UsageEnforcement usageEnforcement
    final int matchingWeight

    PatternBasedDependencyRuleApplier(String packagePattern, int matchingWeight, UsageEnforcement usageEnforcement) {
        this.pattern = PatternUtils.buildPackagePattern(packagePattern)
        this.matchingWeight = matchingWeight
        this.usageEnforcement = usageEnforcement
    }

    @Override
    public ApplyResult apply(String dependencyName) {
        Matcher matcher = pattern.matcher(dependencyName)
        if (!matcher.matches()) {
            return null;
        }

        return buildResult(dependencyName, matcher)
    }

    private ApplyResult buildResult(String dependencyName, Matcher matcher) {
        return new ApplyResult(dependencyName, this.usageEnforcement, this.matchingWeight)
    }

}
