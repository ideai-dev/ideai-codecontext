package com.codecontext

import com.codecontext.cli.AnalyzeCommand
import com.codecontext.cli.MainCommand
import com.github.ajalt.clikt.core.subcommands

fun main(args: Array<String>) {
    MainCommand()
        .subcommands(AnalyzeCommand())
        .main(args)
}
