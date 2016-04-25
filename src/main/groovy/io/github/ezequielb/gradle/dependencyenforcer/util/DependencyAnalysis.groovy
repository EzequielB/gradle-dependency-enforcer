package io.github.ezequielb.gradle.dependencyenforcer.util


public interface DependencyAnalysis {

    Set<String> getAnalyzedClasses()

    Set<String> getClassDependencies(String className)

}
