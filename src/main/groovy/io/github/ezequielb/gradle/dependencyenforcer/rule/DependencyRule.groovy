package io.github.ezequielb.gradle.dependencyenforcer.rule

import io.github.ezequielb.gradle.dependencyenforcer.rule.applier.DependencyRuleApplier

/**
 * Abstraction for expressing dependencies rules among classes and packages.
 */
public interface DependencyRule {

    boolean canApply(String className)

    DependencyRuleApplier getApplier(String className)

}
