package com.codecontext.core.parser

import java.io.File

class KotlinRegexParser : LanguageParser {
    // Improved regex to handle:
    // packagecom.example (missing space - rare but possible in weak parsing?) No, sticking to
    // standard.
    // import com.example.Class as Alias
    // import `quoted.name`

    // Capture group 1 is the package name
    private val packageRegex = Regex("^\\s*package\\s+([`\\w.]+)")

    // Capture group 1 is the import path
    // Handles 'import foo.bar' and 'import foo.bar as baz' (we only want foo.bar)
    private val importRegex = Regex("^\\s*import\\s+([`\\w.*]+)(?:\\s+as\\s+[`\\w]+)?")

    override fun parse(file: File): ParsedFile {
        var packageName = ""
        val imports = mutableListOf<String>()

        file.forEachLine { line ->
            val trimmed = line.trim()

            // Skip comments (basic)
            if (trimmed.startsWith("//") || trimmed.startsWith("*")) return@forEachLine

            if (trimmed.startsWith("package ")) {
                packageRegex.find(trimmed)?.let {
                    packageName = it.groupValues[1].replace("`", "") // Remove backticks
                }
            } else if (trimmed.startsWith("import ")) {
                importRegex.find(trimmed)?.let { imports.add(it.groupValues[1].replace("`", "")) }
            }
        }

        return ParsedFile(file, packageName, imports)
    }
}
