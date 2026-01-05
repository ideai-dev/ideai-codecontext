# Turborepo Migration - Quick Start Guide

## TL;DR - Where Everything Goes

```
codecontext-monorepo/
├── apps/
│   ├── web/              → Next.js frontend (uses packages below)
│   └── cli/              → CLI tool (uses packages below)
│
└── packages/
    ├── core/             → ⭐ MAIN ENGINE (scanner, parser, graph, generator)
    ├── server/           → API routes/logic
    ├── output/           → Report generation
    ├── config/           → Configuration
    ├── ai/               → AI integration
    ├── enterprise/       → Enterprise features
    └── shared/           → Types & utilities (used by all)
```

## Key Points

### 1. **Core Package** (`packages/core/`)
This is where **ALL** your Kotlin core logic goes:
- `scanner/` → RepositoryScanner, GitAnalyzer
- `parser/` → JavaRealParser, KotlinRegexParser, ParserFactory
- `graph/` → RobustDependencyGraph, PageRank
- `generator/` → LearningPathGenerator
- `analyzer/` → Analyzer
- `cache/` → CacheManager
- `temporal/` → TemporalAnalyzer

**This becomes an npm package:** `@codecontext/core`

### 2. **Usage as Package**
Once migrated, anyone can use it:

```bash
npm install @codecontext/core
```

```typescript
import { analyzeRepository } from '@codecontext/core';

const result = await analyzeRepository('./my-project');
const hotspots = result.graph.getTopHotspots(10);
```

### 3. **Next.js Integration**
The Next.js app (`apps/web/`) uses the packages:

```typescript
// apps/web/app/api/analyze/route.ts
import { analyzeRepository } from '@codecontext/core';
import { generateReport } from '@codecontext/output';

export async function POST(request: Request) {
  const result = await analyzeRepository(repoPath);
  return Response.json(result);
}
```

### 4. **CLI Tool**
The CLI (`apps/cli/`) also uses the same packages:

```typescript
// apps/cli/src/commands/analyze.ts
import { analyzeRepository } from '@codecontext/core';

// CLI command implementation
```

## Migration Path

1. **Start with `packages/shared`** - Define TypeScript types
2. **Migrate `packages/core`** - Convert Kotlin → TypeScript (most work)
3. **Create supporting packages** - config, output, ai, enterprise
4. **Build apps** - web (Next.js) and cli
5. **Test & iterate**

## Technology Mapping

| Kotlin | TypeScript/Node.js |
|--------|-------------------|
| JavaParser | tree-sitter or @typescript-eslint/parser |
| JGraphT | graphology |
| JGit | simple-git |
| kotlinx.html | React or template strings |
| Ktor | Next.js API routes |
| Clikt | commander.js |

## Quick Setup Commands

```bash
# 1. Create Turborepo
npx create-turbo@latest codecontext-monorepo

# 2. Add packages
cd codecontext-monorepo
mkdir -p packages/core packages/shared packages/server packages/output
mkdir -p apps/web apps/cli

# 3. Install dependencies
pnpm install

# 4. Start migrating code from Kotlin → TypeScript
# Start with packages/shared, then packages/core
```

## Package Dependencies Flow

```
apps/web
  └─→ @codecontext/core
      └─→ @codecontext/shared

apps/cli
  └─→ @codecontext/core
      └─→ @codecontext/shared

packages/server
  └─→ @codecontext/core
      └─→ @codecontext/shared
```

All packages depend on `@codecontext/shared` for types.

## Benefits

✅ **Reusable**: Install `@codecontext/core` in any project  
✅ **Modular**: Each package has single responsibility  
✅ **Type-safe**: Shared TypeScript types  
✅ **Fast builds**: Turborepo caching  
✅ **Scalable**: Easy to add new packages/apps  

## Next Steps

1. Read `TURBOREPO_MIGRATION.md` for detailed architecture
2. Read `TURBOREPO_EXAMPLE_STRUCTURE.md` for code examples
3. Set up Turborepo scaffold
4. Begin migration starting with `packages/shared`

