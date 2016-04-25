package io.github.ezequielb.gradle.dependencyenforcer.rule

import io.github.ezequielb.gradle.dependencyenforcer.rule.applier.DependencyRuleApplier
import io.github.ezequielb.gradle.dependencyenforcer.rule.applier.DependencyRuleApplierBuilder
import io.github.ezequielb.gradle.dependencyenforcer.rule.applier.MultiDependencyRuleApplier
import io.github.ezequielb.gradle.dependencyenforcer.rule.applier.UsageEnforcement
import io.github.ezequielb.gradle.dependencyenforcer.util.ArtifactResolver
import io.github.ezequielb.gradle.dependencyenforcer.util.PatternUtils

import java.util.regex.Matcher
import java.util.regex.Pattern

public class PatternBasedDependencyRule implements DependencyRule {

    private String pkg;
    private List<String> allowed
    private List<String> disallowed
    private ArtifactResolver artifactResolver

    private Pattern pkgPattern
    private List<DependencyRuleApplier> staticAppliers = []
    private List<DependencyRuleApplierBuilder> applierBuilders = []

    public PatternBasedDependencyRule(String pkg, List<String> allowed, List<String> disallowed, ArtifactResolver artifactResolver) {
        this.pkg = pkg;
        this.allowed = Collections.unmodifiableList(allowed)
        this.disallowed = Collections.unmodifiableList(disallowed)
        this.artifactResolver = artifactResolver

        this.pkgPattern = PatternUtils.buildPackagePattern(pkg)
        allowed.each { initDependencyExpression(it, UsageEnforcement.EXPLICITLY_ALLOWED) }
        disallowed.each { initDependencyExpression(it, UsageEnforcement.EXPLICITLY_DISALLOWED) }
    }

    private void initDependencyExpression(String expression, UsageEnforcement usageEnforcement) {
        def builder = DependencyRuleApplierBuilder.newBuilder(artifactResolver).expression(expression)
                .usageEnforcement(usageEnforcement)
        if (DependencyRuleApplierBuilder.isStaticExpression(expression)) {
            staticAppliers.add(builder.initialWeight(PatternUtils.getResolvedExpressionWeight(pkg)).build())
        } else {
            applierBuilders.add(builder)
        }
    }

    @Override
    boolean canApply(String className) {
        return this.pkgPattern.matcher(className).matches()
    }

    @Override
    DependencyRuleApplier getApplier(String className) {
        Matcher matcher = this.pkgPattern.matcher(className)
        if (!matcher.matches()) {
            return null
        }

        List<String> captures = new ArrayList<>(matcher.groupCount())
        for (int i = 1; i <= matcher.groupCount(); i++) {
            captures.add(matcher.group(i))
        }

        List<DependencyRuleApplier> appliers = new ArrayList<>(staticAppliers.size() + applierBuilders.size())
        appliers.addAll(staticAppliers)
        appliers.addAll(applierBuilders.collect {
            String resolvedPkg = PatternUtils.getPackageWithEvaluatedCaptures(pkg, captures)
            int initialWeight = PatternUtils.getResolvedExpressionWeight(resolvedPkg)
            return it.className(className).captures(captures).initialWeight(initialWeight).build()
        })

        return new MultiDependencyRuleApplier(appliers)
    }
}
