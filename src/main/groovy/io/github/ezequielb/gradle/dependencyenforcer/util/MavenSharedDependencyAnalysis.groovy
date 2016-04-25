package io.github.ezequielb.gradle.dependencyenforcer.util

import org.apache.maven.shared.dependency.analyzer.DefaultClassAnalyzer
import org.apache.maven.shared.dependency.analyzer.asm.DependencyClassFileVisitor

public class MavenSharedDependencyAnalysis implements DependencyAnalysis {

    final File classesDir;

    MavenSharedDependencyAnalysis(File classesDir) {
        this.classesDir = classesDir
    }

    public Set<String> getAnalyzedClasses() {
        DefaultClassAnalyzer classAnalyzer = new DefaultClassAnalyzer()
        Set<String> classes = classAnalyzer.analyze(classesDir.toURI().toURL())
        classes
    }

    public Set<String> getClassDependencies(String className) {
        String relativePath = className.replace(".", File.separator) + ".class";
        File classFile = new File(classesDir, relativePath)
        DependencyClassFileVisitor visitor = new DependencyClassFileVisitor()
        visitor.visitClass(className, new FileInputStream(classFile))
        Set<String> dependencies = new HashSet<>(visitor.dependencies)
        dependencies.remove(className)
        dependencies
    }

}
