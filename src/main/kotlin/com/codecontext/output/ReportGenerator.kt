package com.codecontext.output

import com.codecontext.core.generator.LearningStep
import com.codecontext.core.graph.DependencyGraph
import java.io.File
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class GraphNode(
        val id: String,
        val label: String,
        val score: Double,
        val group: Int = 1,
        // Git Metadata Fields
        val authors: String = "",
        val churn: Int = 0,
        val lastMod: String = ""
)

@Serializable data class GraphLink(val source: String, val target: String, val value: Int = 1)

@Serializable data class GraphData(val nodes: List<GraphNode>, val links: List<GraphLink>)

class ReportGenerator {
    fun generate(
            graph: DependencyGraph,
            outputPath: String,
            parsedFiles: List<com.codecontext.core.parser.ParsedFile>,
            learningPath: List<LearningStep>
    ) {
        val hotspots = graph.getTopHotspots(15)

        // Create map for easy lookup of Git data
        val fileMap = parsedFiles.associateBy { it.file.absolutePath }

        // Convert graph data to JSON for visualization
        val nodes =
                graph.graph.vertexSet().map { id ->
                    val meta = fileMap[id]?.gitMetadata
                    val authors = meta?.topAuthors?.joinToString(", ") ?: "Unknown"
                    val churn = meta?.changeFrequency ?: 0
                    val lastMod =
                            if (meta != null && meta.lastModified > 0)
                                    java.util.Date(meta.lastModified).toString()
                            else "Never"

                    GraphNode(
                            id = id,
                            label = File(id).name,
                            score = (graph.pageRankScores[id] ?: 0.0),
                            authors = authors,
                            churn = churn,
                            lastMod = lastMod
                    )
                }
        val links =
                graph.graph.edgeSet().map {
                    GraphLink(
                            source = graph.graph.getEdgeSource(it),
                            target = graph.graph.getEdgeTarget(it)
                    )
                }

        val graphData = GraphData(nodes, links)
        val jsonGraph = Json.encodeToString(graphData)

        val htmlContent =
                createHTML().html {
                    head {
                        title("CodeContext Analysis Report")
                        style {
                            unsafe {
                                +"""
                        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 20px; background: #f4f4f4; }
                        .container { max-width: 1200px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                        h1 { color: #333; }
                        h2 { color: #555; border-bottom: 2px solid #eee; padding-bottom: 10px; }
                        .hotspot-list { list-style: none; padding: 0; }
                        .hotspot-item { padding: 10px; border-bottom: 1px solid #eee; display: flex; justify-content: space-between; }
                        .hotspot-score { font-weight: bold; color: #e74c3c; }
                        #graph-container { width: 100%; height: 600px; border: 1px solid #ddd; margin-top: 20px; }
                        """
                            }
                        }
                        script(src = "https://unpkg.com/force-graph") {}
                    }
                    body {
                        div("container") {
                            h1 { +"CodeContext Analysis Report" }

                            div {
                                h2 { +"🎓 Recommended Learning PAth" }
                                p { +"Start from these fundamental files and work your way up:" }
                                ul("hotspot-list") {
                                    learningPath.forEach { step ->
                                        li("hotspot-item") {
                                            div {
                                                strong { +File(step.file).name }
                                                span { +"  [${step.description}]" }
                                                br {}
                                                small { +step.reason }
                                            }
                                        }
                                    }
                                }
                            }

                            div {
                                h2 { +"🔥 Knowledge Hotspots (Top Critical Files)" }
                                ul("hotspot-list") {
                                    hotspots.forEach { (path, score) ->
                                        li("hotspot-item") {
                                            span { +File(path).name }
                                            span("hotspot-score") { +String.format("%.4f", score) }
                                        }
                                    }
                                }
                            }

                            div {
                                h2 { +"🗺️ Codebase Map (Hover for Context)" }
                                div { id = "graph-container" }
                            }
                        }

                        script {
                            unsafe {
                                +"""
                        const gData = $jsonGraph;
                        
                        const Graph = ForceGraph()
                          (document.getElementById('graph-container'))
                            .graphData(gData)
                            .nodeLabel(node => `${'$'}{node.label} \nScore: ${'$'}{node.score.toFixed(4)}\nAuthors: ${'$'}{node.authors}\nChanges: ${'$'}{node.churn}\nLast Mod: ${'$'}{node.lastMod}`)
                            .nodeVal('score')
                            .nodeAutoColorBy('group')
                            .linkDirectionalParticles(2)
                            .linkDirectionalParticleWidth(2);
                        """
                            }
                        }
                    }
                }

        File(outputPath).writeText(htmlContent)
    }
}
