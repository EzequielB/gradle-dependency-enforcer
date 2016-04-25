package io.github.ezequielb.gradle.dependencyenforcer.rule.applier

import io.github.ezequielb.gradle.dependencyenforcer.util.ArtifactResolver
import io.github.ezequielb.gradle.dependencyenforcer.util.PatternUtils

public class DependencyRuleApplierBuilder {

    public static final String SAME_PACKAGE_RULE_NAME = '%same-package-rule%'

    private ArtifactResolver artifactResolver

    private String className
    private String expression
    private List<String> captures = []
    private UsageEnforcement usageEnforcement
    private int initialWeight

    public static boolean isStaticExpression(String expression) {
        return !expression.contains("{") && !SAME_PACKAGE_RULE_NAME.equals(expression)
    }

    public static DependencyRuleApplierBuilder newBuilder(ArtifactResolver resolver) {
        return new DependencyRuleApplierBuilder(resolver)
    }

    public static DependencyRuleApplierBuilder newBuilder() {
        return new DependencyRuleApplierBuilder(null)
    }

    private DependencyRuleApplierBuilder(ArtifactResolver artifactResolver) {
        this.artifactResolver = artifactResolver
    }

    public DependencyRuleApplierBuilder className(String className) {
        this.className = className
        this
    }

    public DependencyRuleApplierBuilder expression(String expression) {
        this.expression = expression
        this
    }

    public DependencyRuleApplierBuilder captures(List<String> captures) {
        this.captures = captures
        this
    }

    public DependencyRuleApplierBuilder usageEnforcement(UsageEnforcement usageEnforcement) {
        this.usageEnforcement = usageEnforcement
        this
    }

    public DependencyRuleApplierBuilder initialWeight(int initialWeight) {
        this.initialWeight = initialWeight
        this
    }

    public DependencyRuleApplier build() {
        if (expression.contains(':')) {
            Set<String> dependencyNames = artifactResolver.resolveClasses(expression)
            return new SetBasedDependencyApplier(dependencyNames, usageEnforcement, initialWeight)
        }
        if (SAME_PACKAGE_RULE_NAME.equals(expression)) {
            def applier = new SamePackageDependencyRuleApplier(className, usageEnforcement, initialWeight)
            //println applier.toString()
            return applier
        }
        String packagePattern = PatternUtils.getPackageWithEvaluatedCaptures(expression, captures)
        int matchingWeight = initialWeight + PatternUtils.getResolvedExpressionWeight(packagePattern)
        return new PatternBasedDependencyRuleApplier(packagePattern, matchingWeight, usageEnforcement)
    }

}
