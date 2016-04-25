package io.github.ezequielb.gradle.dependencyenforcer.enforcer

import groovy.transform.ToString

@ToString(includeNames = true, includeFields = true)
public class DependencyEnforcerResult {

    final boolean successful;
    final Map<String, Set<String>> disallowed

    private DependencyEnforcerResult(Map<String, Set<String>> disallowed) {
        this.successful = disallowed.isEmpty()
        this.disallowed = Collections.unmodifiableMap(disallowed)
    }

    public static DependencyEnforceResultCollector newCollector() {
        return new DependencyEnforceResultCollector();
    }

    static class DependencyEnforceResultCollector {
        private Map<String, Set<String>> disallowed = new HashMap<>()

        public collectDisallowed(String className, Set<String> dependencies) {
            if (disallowed == null) {
                throw new IllegalStateException("Can't build")
            }
            disallowed.putAt(className, dependencies)
        }

        public DependencyEnforcerResult build() {
            return new DependencyEnforcerResult(disallowed)
        }
    }

}

