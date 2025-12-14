# CodeContext 🎯

> Intelligent codebase context analyzer for faster developer onboarding

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-blue.svg)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](LICENSE)
[![Status](https://img.shields.io/badge/Status-In%20Development-yellow.svg)]()

## 📋 Overview

CodeContext is an open-source CLI tool that analyzes codebases to generate interactive context maps, identify knowledge hotspots, and create personalized onboarding paths for developers.

**Problem:** New developers take 1-3 months to become productive due to lack of codebase understanding.

**Solution:** Automated codebase analysis that makes understanding large projects 10x faster.

## ✨ Features (Planned)

- 🗺️ **Interactive Dependency Maps** - Visualize your codebase structure
- 🔥 **Knowledge Hotspots** - Identify critical files everyone needs to know
- 🎓 **Learning Paths** - Personalized onboarding journeys by role
- 📊 **Comprehensive Reports** - HTML, Markdown, and JSON outputs
- 🔍 **Git History Analysis** - Understand architectural decisions through commits
- 🚀 **Multi-Language Support** - Java, Kotlin, and more

## 🚀 Quick Start

### Prerequisites

- JDK 21 or higher
- Git

### Installation

```bash
# Clone the repository
git clone https://github.com/yourusername/codecontext.git
cd codecontext

# Build the project
./gradlew build

# Run the CLI
./gradlew run --args="--help"
```

### Usage

```bash
# Analyze a codebase
codecontext analyze /path/to/your/project

# View generated report
open output/index.html
```

## 🏗️ Project Structure

```
codecontext/
├── src/main/kotlin/com/codecontext/
│   ├── cli/              # CLI commands
│   ├── core/             # Core analysis logic
│   │   ├── scanner/      # File scanning
│   │   ├── parser/       # Code parsing
│   │   ├── graph/        # Dependency graphs
│   │   ├── analyzer/     # Analysis algorithms
│   │   └── generator/    # Context generation
│   └── output/           # Report generation
└── src/test/             # Tests
```

## 🛠️ Tech Stack

- **Language:** Kotlin 1.9.22
- **Build:** Gradle 8.5+
- **CLI:** Clikt
- **Parsing:** JavaParser, KotlinPoet
- **Graphs:** JGraphT
- **Git:** JGit

## 📊 Development Status

- [x] Project initialization
- [ ] File scanner implementation
- [ ] Java/Kotlin parser
- [ ] Dependency graph builder
- [ ] HTML report generator
- [ ] CLI interface refinement

## 🤝 Contributing

Contributions are welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details.

## 📄 License

This project is licensed under the Apache License 2.0 - see [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

Built with ❤️ to solve real developer onboarding problems.
