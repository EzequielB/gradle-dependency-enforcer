package io.github.ezequielb.gradle.dependencyenforcer.util

import java.util.regex.MatchResult
import java.util.regex.Pattern

abstract class PatternUtils {

    private static final String IDENTIFIER_REGEX = '[a-zA-Z$_][a-zA-Z0-9$_]*'
    private static final String CAPTURING_IDENTIFIER_REGEX = "($IDENTIFIER_REGEX)"
    private static final String WILDCARD = '*'

    /**
     * Given a package expression it creates a corresponding pattern that matches any class
     * name within that package
     * @param pkg the package
     * @return the pattern for the given package
     */
    static Pattern buildPackagePattern(String pkg) {
        if (WILDCARD.equals(pkg)) {
            return Pattern.compile("(?:$IDENTIFIER_REGEX)(?:\\.$IDENTIFIER_REGEX)*")
        }

        if (!pkg.contains("{")) {
            return buildStatic(pkg)
        }

        return buildWithCapture(pkg)
    }

    static Pattern buildStrictPackagePattern(String pkg) {
        Pattern.compile(Pattern.quote(pkg) + "\\.$IDENTIFIER_REGEX")
    }

    static private Pattern buildStatic(String pkg) {
        Pattern.compile(Pattern.quote(pkg) + "(?:\\.$IDENTIFIER_REGEX)+")
    }

    static private Pattern buildWithCapture(String pkg) {
        String pkgPatternString = pkg.split('\\{\\d+\\}').collect({
            Pattern.quote(it)
        }).join(CAPTURING_IDENTIFIER_REGEX)
        if (pkg.endsWith("}")) {
            pkgPatternString += CAPTURING_IDENTIFIER_REGEX
        }
        return Pattern.compile(pkgPatternString + "(?:\\.$IDENTIFIER_REGEX)+")
    }

    /*static Pattern buildWithEvaluatedCaptures(String pattern, List<String> captures) {
        return buildStatic(getPackageWithEvaluatedCaptures(pattern, captures))
    }*/

    static String getClassPackage(String className) {
        int lastPackage = className.lastIndexOf(".")
        if (lastPackage == -1) {
            return ''
        }
        return className.substring(0, lastPackage)
    }

    static String getPackageWithEvaluatedCaptures(String pattern, List<String> captures) {
        String pkg = pattern
        for (int i = 0; i < captures.size(); i++) {
            pkg = pkg.replace("{$i}", captures.get(i))
        }
        return pkg
    }

    static int getResolvedExpressionWeight(String expression) {
        return WILDCARD.equals(expression) ? 0 : expression.size()
    }

}
