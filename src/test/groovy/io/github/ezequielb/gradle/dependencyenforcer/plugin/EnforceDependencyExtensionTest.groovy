package io.github.ezequielb.gradle.dependencyenforcer.plugin

import org.junit.Assert
import org.junit.Test

class EnforceDependencyExtensionTest {

    @Test
    public void testExtension() {
        Closure closure = {
            enableOnCheck true
            failOnDisallowedUsage true
            rules {
                pkg('a') {
                    allow 'b', 'c'
                    disallow 'd'
                }
                pkg('x') {
                    allow 'z', 'y'
                    disallow 't'
                }
                all {
                    allow '1', '2'
                    disallow '3'
                }
            }

        }

        def extension = new EnforceDependencyExtension()
        closure.delegate = extension
        closure()

        Assert.assertNotNull(extension)
        Assert.assertTrue(extension.enableOnCheck)
        Assert.assertTrue(extension.failOnDisallowedUsage)

        Assert.assertEquals([[pkg       : 'a',
                              allowed   : ['b', 'c'],
                              disallowed: ['d']],
                             [pkg       : 'x',
                              allowed   : ['z', 'y'],
                              disallowed: ['t']],
                             [pkg       : '*',
                              allowed   : ['1', '2'],
                              disallowed: ['3']]], extension.toRulesDescriptor())
    }

}
