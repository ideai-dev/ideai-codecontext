package com.codecontext.core.parser

import java.io.File

data class GitMetadata(
        val lastModified: Long = 0,
        val changeFrequency: Int = 0,
        val topAuthors: List<String> = emptyList(),
        val recentMessages: List<String> = emptyList()
)

data class ParsedFile(
        val file: File,
        val packageName: String,
        val imports: List<String>,
        var gitMetadata: GitMetadata = GitMetadata() // Mutable or default for now
)

interface LanguageParser {
    fun parse(file: File): ParsedFile
}
