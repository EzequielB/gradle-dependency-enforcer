package io.github.ezequielb.gradle.dependencyenforcer.plugin

class EnforceDependencyExtension {

    def boolean enableOnCheck
    def boolean failOnDisallowedUsage
    private RuleCollectionExtension ruleCollection = new RuleCollectionExtension()

    def enableOnCheck(boolean enableOnCheck) {
        this.enableOnCheck = enableOnCheck
    }

    def failOnDisallowedUsage(boolean failOnDisallowedUsage) {
        this.failOnDisallowedUsage = failOnDisallowedUsage
    }

    void rules(Closure closure) {
        closure.delegate = ruleCollection
        closure()
    }

    List<Map<String, Object>> toRulesDescriptor() {
        return ruleCollection.rules.collect {
            return [ pkg: it.pkg, allowed: it.allowed, disallowed: it.disallowed] as Map<String, Object>
        }
    }


}

class RuleCollectionExtension {
    final List<RuleExtension> rules = []

    void pkg(String name, Closure closure) {
        def rule = new RuleExtension(pkg: name)
        closure.delegate = rule
        closure()
        rules.add(rule)
    }

    void all(Closure closure) {
        this.pkg('*', closure)
    }

}

class RuleExtension {
    def String pkg
    def List<String> allowed = []
    def List<String> disallowed = []

    def allow(String... names) {
        allowed.addAll(names)
    }

    def disallow(String... names) {
        disallowed.addAll(names)
    }
}