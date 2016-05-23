package io.github.ezequielb.gradle.dependencyenforcer.util

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MavenSharedDependencyAnalysisTest {

    private static MavenSharedDependencyAnalysis dependencyAnalysis

    @Before
    public void init() {
        final File currentClassPath = new File(MavenSharedDependencyAnalysisTest.class.getProtectionDomain().getCodeSource().getLocation().getPath())
        dependencyAnalysis = new MavenSharedDependencyAnalysis(currentClassPath)

    }

    @Test
    public void getAnalyzedClassesTest() {
        Assert.assertTrue(dependencyAnalysis.getAnalyzedClasses().contains(MavenSharedDependencyAnalysisTest.class.getName()))
    }

    @Test
    public void getClassDependenciesTest() {
        Assert.assertTrue(dependencyAnalysis.getClassDependencies(MavenSharedDependencyAnalysisTest.class.getName()).contains(MavenSharedDependencyAnalysis.class.getName()))
    }

}
