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
    public void simpleSuccessfulBuildTest() throws IOException {
        String enforceDescriptor = """
        enforceDependency {
          enableOnCheck true
          failOnDisallowedUsage true

          rules {
            pkg('com.example.x') {
              allow 'com.example.y'
            }

            all {
              allow 'com.example.'
            }
          }
        }
        """
        GradleRunner runner = buildRunner(enforceDescriptor)
        BuildResult result = runner.build()
        Assert.assertEquals(TaskOutcome.SUCCESS,
                result.task(":${DependencyEnforcerPlugin.ENFORCE_DEPENDENCY_TASK_NAME}").getOutcome());
    }

    @Test
    public void simpleFailingBuildTest() throws IOException {
        String enforceDescriptor = """
        enforceDependency {
          enableOnCheck true
          failOnDisallowedUsage true

          rules {
            pkg('com.example.x') {
              allow 'com.example.'
            }

            all {
              allow 'com.example.'
            }
          }
        }
        """
        GradleRunner runner = buildRunner(enforceDescriptor)
        BuildResult result = runner.buildAndFail()
        Assert.assertEquals(TaskOutcome.FAILED,
                result.task(":${DependencyEnforcerPlugin.ENFORCE_DEPENDENCY_TASK_NAME}").getOutcome());
    }

    private GradleRunner buildRunner(String enforceDescriptor) {
        String testSrcDir = System.getenv().get("testProjectSrcDir")
        String buildFileContent = """
        plugins {
            id 'java'
            id 'io.github.ezequielb.gradle-dependency-enforcer'
        }

        sourceSets {
            main {
                java {
                    srcDir '$testSrcDir'
                }
            }
        }

        $enforceDescriptor
        """
        buildFile.text = buildFileContent

        String agenPath = System.getenv().get("jacocoAgenPath")
        String destinationFile = System.getenv().get("jacocoDestinationFile")
        String jvmArg = "-javaagent:$agenPath=" +
                "includes=io/github/ezequielb/gradle/dependencyenforcer/**," +
                "excludes=io/github/ezequielb/gradle/dependencyenforcer/**/*Test," +
                "destfile=$destinationFile,append=true"

        return ((DefaultGradleRunner) GradleRunner.create()
                .withProjectDir(testProjectDir.getRoot())
                .withPluginClasspath())
                .withJvmArguments(jvmArg)
                .withArguments("check")
    }

}