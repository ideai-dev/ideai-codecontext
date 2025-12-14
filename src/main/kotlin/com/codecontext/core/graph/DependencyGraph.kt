package com.codecontext.core.graph

import com.codecontext.core.parser.ParsedFile
import org.jgrapht.alg.scoring.PageRank
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge

class DependencyGraph {
    val graph = DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)
    val pageRankScores = mutableMapOf<String, Double>()

    fun build(parsedFiles: List<ParsedFile>) {
        // map package.Class to filePath
        val classMap = mutableMapOf<String, String>()

        // 1. Add all files as nodes and build class map
        parsedFiles.forEach { parsed ->
            val filePath = parsed.file.absolutePath
            graph.addVertex(filePath)

            val className = parsed.file.nameWithoutExtension
            val fqcn =
                    if (parsed.packageName.isNotEmpty()) "${parsed.packageName}.$className"
                    else className
            classMap[fqcn] = filePath
        }

        // 2. Add edges based on imports
        parsedFiles.forEach { source ->
            source.imports.forEach { import ->
                // Handle Wildcard Imports
                if (import.endsWith(".*")) {
                    val packageName = import.removeSuffix(".*")
                    // Find all classes in this package
                    parsedFiles.filter { it.packageName == packageName }.forEach { target ->
                        val targetPath = target.file.absolutePath
                        if (targetPath != source.file.absolutePath) {
                            graph.addEdge(source.file.absolutePath, targetPath)
                        }
                    }
                } else {
                    // Exact match handling
                    if (classMap.containsKey(import)) {
                        val targetPath = classMap[import]!!
                        if (targetPath != source.file.absolutePath) {
                            graph.addEdge(source.file.absolutePath, targetPath)
                        }
                    }
                }
            }
        }
    }

    fun analyze() {
        if (graph.vertexSet().isEmpty()) return
        val pageRank = PageRank(graph)
        graph.vertexSet().forEach { vertex ->
            pageRankScores[vertex] = pageRank.getVertexScore(vertex)
        }
    }

    fun getTopHotspots(limit: Int = 10): List<Pair<String, Double>> {
        return pageRankScores.entries.sortedByDescending { it.value }.take(limit).map {
            it.key to it.value
        }
    }
}
