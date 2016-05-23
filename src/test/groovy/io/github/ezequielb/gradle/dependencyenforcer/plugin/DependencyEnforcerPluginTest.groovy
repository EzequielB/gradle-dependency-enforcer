package io.github.ezequielb.gradle.dependencyenforcer.plugin

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.gradle.testkit.runner.internal.DefaultGradleRunner
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

public class DependencyEnforcerPluginTest {
    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder();
    private File buildFile;

    @Before
    public void setup() throws IOException {
        buildFile = testProjectDir.newFile("build.gradle");
    }

    @Test
    public void simpleBuildTest() throws IOException {
        String buildFileContent = """
        plugins {
            id 'java'
            id 'io.github.ezequielb.gradle-dependency-enforcer'
        }

        enforceDependency {
          enableOnCheck true
          failOnDisallowedUsage true

          rules {
            pkg('com.example') {
              allow 'com.example.package1', 'com.example.package2'
              disallow 'com.example.package3'
              disallow 'group1:moduleA'
            }

            all {
              allow 'com.example.x', 'com.example.y'
              disallow 'com.example.z'
            }
          }
        }
        """
        buildFile.text = buildFileContent

        String agenPath = System.getenv().get("jacocoAgenPath")
        String destinationFile = System.getenv().get("jacocoDestinationFile")
        String jvmArg = "-javaagent:$agenPath=" +
                "includes=io/github/ezequielb/gradle/dependencyenforcer/**," +
                "excludes=io/github/ezequielb/gradle/dependencyenforcer/**/*Test," +
                "destfile=$destinationFile,append=true"
        BuildResult result = ((DefaultGradleRunner) GradleRunner.create()
                .withProjectDir(testProjectDir.getRoot())
                .withPluginClasspath())
                .withJvmArguments(jvmArg)
                .withArguments("check")
                .build();
        Assert.assertEquals(result.task(":${DependencyEnforcerPlugin.ENFORCE_DEPENDENCY_TASK_NAME}").getOutcome(),
                TaskOutcome.SUCCESS);
    }

}