package io.github.ezequielb.gradle.dependencyenforcer.util

import org.apache.maven.shared.dependency.analyzer.DefaultClassAnalyzer
import org.gradle.api.artifacts.ResolvedArtifact


public interface ArtifactResolver {

    Set<String> resolveClasses(String artifactExpression)

}

public class GradleArtifactResolver implements ArtifactResolver {

    private final Map<String, File> artifactFileMap

    public GradleArtifactResolver(Set<ResolvedArtifact> artifacts) {
        artifactFileMap = (artifacts.unique {it.file} as Set).collectEntries {
            [ ("$it.moduleVersion.id.group:$it.moduleVersion.id.name".toString()) : it.file]
        }
    }

    public Set<String> resolveClasses(String artifactExpression) {
        File artifactFile = artifactFileMap.get(artifactExpression)
        if (artifactFile == null) {
            return Collections.emptySet()
        }
        DefaultClassAnalyzer classAnalyzer = new DefaultClassAnalyzer()
        return classAnalyzer.analyze(artifactFile.toURI().toURL())
    }

}
