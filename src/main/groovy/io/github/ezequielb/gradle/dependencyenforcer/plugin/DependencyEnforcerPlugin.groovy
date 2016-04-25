package io.github.ezequielb.gradle.dependencyenforcer.plugin

import io.github.ezequielb.gradle.dependencyenforcer.rule.applier.DependencyRuleApplierBuilder
import io.github.ezequielb.gradle.dependencyenforcer.util.GradleArtifactResolver
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskState

public class DependencyEnforcerPlugin implements Plugin<Project> {


    public static final String ENFORCE_DEPENDENCY_TASK_NAME = 'enforceDependency'

    public void apply(Project project) {
        project.extensions.create(ENFORCE_DEPENDENCY_TASK_NAME, EnforceDependencyExtension)

        project.afterEvaluate {
            project.task(ENFORCE_DEPENDENCY_TASK_NAME, dependsOn: 'classes', type: DependencyEnforcerTask) {
                def extension = project.enforceDependency as EnforceDependencyExtension
                def descriptor = extension.toRulesDescriptor()
                descriptor.add([pkg: '*', allowed: [DependencyRuleApplierBuilder.SAME_PACKAGE_RULE_NAME, 'java'], disallowed: []])
                rulesDescriptor = descriptor
                classesDir = project.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).output.classesDir
                resultOut = new File("${project.buildDir}/enforcer.dat")
                failOnDisallowedUsage = extension.failOnDisallowedUsage
                resolver = new GradleArtifactResolver(project.configurations.compile
                        .resolvedConfiguration
                        .firstLevelModuleDependencies*.allModuleArtifacts.flatten() as Set)
            }

            if (project.enforceDependency.enableOnCheck) {
                project.check.dependsOn(ENFORCE_DEPENDENCY_TASK_NAME)
            }

            project.gradle.taskGraph.addTaskExecutionListener(new TaskExecutionListener() {
                @Override
                void beforeExecute(Task task) {}

                @Override
                void afterExecute(Task task, TaskState taskState) {
                    if (task instanceof DependencyEnforcerTask && (taskState.executed || taskState.upToDate)) {
                        task.printResults()
                    }
                }
            })
        }
    }

}
