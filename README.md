# gradle-dependency-enforcer
[![Build Status](https://travis-ci.org/EzequielB/gradle-dependency-enforcer.svg?branch=master)](https://travis-ci.org/EzequielB/gradle-dependency-enforcer)
[![codecov](https://codecov.io/gh/EzequielB/gradle-dependency-enforcer/branch/master/graph/badge.svg)](https://codecov.io/gh/EzequielB/gradle-dependency-enforcer)

Gradle plugin to enforce dependencies in a finer way within a project artifact.

## Motivation
Restrict how your artifcat components interacts in compile time, limiting new components dependencies and old components coupling increase.

## Installation
```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'io.github.ezequielb:gradle-dependency-enforcer:1.0.0'
    }
}

apply plugin: 'io.github.ezequielb.gradle-dependency-enforcer'
```


## Tasks
The plugin has single task named ``enforceDependency`` that performs a dependency check within the project using the given configuration.

## Configuration
When enabled, the plugin adds an extension to the ``project`` named ``enforceDependency``. Using this extension rules can be set to restrict compile time components interaction.

`````groovy
enforceDependency {
  // run this task as part of the check task
  enableOnCheck true 
  
  // make the task fail if disallowed dependencies uses are found
  failOnDisallowedUsage true 
  
  rules {
    // rules that applies to classes in com.example package
    pkg('com.example') { 
      // classes in this two packages (and sub packages) are allowed
      allow 'com.example.package1', 'com.example.package2' 
      
      // classes in this package (and sub packages) are disallowed
      disallow 'com.example.package3'
      
      // classes in this artifact are disallowed
      disallow 'group1:moduleA'
    }
    
    // rules that applies to classes in com.otherexample.{0} packages
    // where {0} is a capture (zero-based)
    pkg('com.otherexample.{0}') { 
      // classes in this two packages patterns (and sub packages) are allowed 
      // (using the capture in the rule declaration)
      allow 'com.anotherexample.{0}', 'com.yetanotherexample.{0}' 
    }    
    
    all {
      // classes in this two packages (and sub packages) are allowed for all
      allow 'com.example.x', 'com.example.y' 
      
      // classes in this package (and sub packages) are disallowed for all
      disallow 'com.example.z' 
    }
  }
}
`````

