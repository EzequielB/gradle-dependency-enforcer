package io.github.ezequielb.gradle.dependencyenforcer.rule.applier

import groovy.transform.ToString

public interface DependencyRuleApplier {

    ApplyResult apply(String dependencyName)

}

@ToString(includeFields = true, includeNames = true)
public class ApplyResult implements Comparable<ApplyResult>{

    final String dependencyName
    final UsageEnforcement usageEnforcement
    final int weight

    ApplyResult(String dependencyName, UsageEnforcement usageEnforcement, int weight) {
        this.dependencyName = dependencyName
        this.usageEnforcement = usageEnforcement
        this.weight = weight
    }

    @Override
    int compareTo(ApplyResult other) {
        int compare = weight.compareTo(other.weight)
        if (compare != 0) {
            return compare
        }
        return usageEnforcement.compareTo(other.usageEnforcement)
    }

    public static ApplyResult resolveConflictingResults(ApplyResult result1, ApplyResult result2) {
        if (result1 == null) {
            return result2
        }
        if (result2 == null) {
            return result1

        }
        if (result1 > result2) {
            return result1
        } else {
            return result2
        }
    }

}

public enum UsageEnforcement {
    EXPLICITLY_DISALLOWED, EXPLICITLY_ALLOWED
}