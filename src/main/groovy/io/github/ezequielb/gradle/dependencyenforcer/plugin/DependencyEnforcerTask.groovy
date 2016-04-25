package io.github.ezequielb.gradle.dependencyenforcer.plugin

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import io.github.ezequielb.gradle.dependencyenforcer.enforcer.DependencyEnforcerResult
import io.github.ezequielb.gradle.dependencyenforcer.enforcer.DependencyRuleEnforcer
import io.github.ezequielb.gradle.dependencyenforcer.rule.DependencyRule
import io.github.ezequielb.gradle.dependencyenforcer.rule.DependencyRuleBuilder
import io.github.ezequielb.gradle.dependencyenforcer.util.ArtifactResolver
import io.github.ezequielb.gradle.dependencyenforcer.util.MavenSharedDependencyAnalysis

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class DependencyEnforcerTask extends DefaultTask {

    @Input
    List<Map<String, Object>> rulesDescriptor

    @Input
    File classesDir

    @OutputFile
    File resultOut

    @Input
    boolean failOnDisallowedUsage

    ArtifactResolver resolver

    DependencyEnforcerResult enforcerResult

    public static class PackageDependencyEnforcerException extends GradleException {
        PackageDependencyEnforcerException(final String message) {
            super(message)
        }
    }

    @TaskAction
    void runEnforceTask() {
        DependencyRuleEnforcer enforcer =
                new DependencyRuleEnforcer(new MavenSharedDependencyAnalysis(classesDir), buildDependencyRules())
        enforcerResult = enforcer.enforceRules()
        resultOut.withWriter { writer ->
            writer.write(JsonOutput.prettyPrint(JsonOutput.toJson(enforcerResult)))
        }
        if (failOnDisallowedUsage && !enforcerResult.successful) {
            throw new PackageDependencyEnforcerException("Unallowed dependencies usage found");
        }
    }

    List<DependencyRule> buildDependencyRules() {
        return rulesDescriptor.collect {
            DependencyRuleBuilder.forPackage(it.pkg)
                    .allowed(it.allowed)
                    .disallowed(it.disallowed)
                    .resolver(resolver)
                    .build()
        }
    }

    void printResults() {
        if (!resultOut.exists() && enforcerResult == null) {
            return;
        }
        Map<String, Set<String>> disallowed
        if (enforcerResult == null) {
            Map resultOutMap = new JsonSlurper().parse(resultOut) as Map
            disallowed = resultOutMap.disallowed
        } else {
            disallowed = enforcerResult.disallowed
        }
        if (!disallowed.isEmpty()) {
            print 'Package dependency enforcer found not allowed uses:\n'
        }
        disallowed.sort().each { k, v ->
            println "class $k not allowed uses\n    - ${v.sort()}\n"
        }
    }

}
