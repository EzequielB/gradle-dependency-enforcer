package io.github.ezequielb.gradle.dependencyenforcer.rule

import io.github.ezequielb.gradle.dependencyenforcer.util.ArtifactResolver


public class DependencyRuleBuilder {

    private String pkg
    private List<String> allowed
    private List<String> disallowed
    private ArtifactResolver resolver

    private DependencyRuleBuilder() {
    }

    public static DependencyRuleBuilder forPackage(String pkg) {
        def builder = new DependencyRuleBuilder()
        builder.pkg = pkg
        return builder
    }

    public DependencyRuleBuilder allowed(List<String> allowed) {
        this.allowed = allowed
        return this
    }

    public DependencyRuleBuilder disallowed(List<String> disallowed) {
        this.disallowed = disallowed
        return this
    }

    public DependencyRuleBuilder resolver(ArtifactResolver resolver) {
        this.resolver = resolver
        return this
    }

    public DependencyRule build() {
        return new PatternBasedDependencyRule(pkg, allowed, disallowed, resolver)
    }

}
