# Turborepo Migration Guide

This document outlines how to port CodeContext from Kotlin to a Next.js Turborepo monorepo structure.

## Proposed Turborepo Structure

```
codecontext-monorepo/
├── apps/
│   ├── web/                    # Next.js frontend application
│   │   ├── app/               # Next.js 13+ app directory
│   │   │   ├── api/           # API routes (can use @codecontext/server)
│   │   │   ├── (routes)/      # Frontend pages
│   │   │   └── layout.tsx
│   │   ├── components/        # React components
│   │   ├── lib/               # App-specific utilities
│   │   └── package.json
│   │
│   └── cli/                   # CLI application (Node.js)
│       ├── src/
│       │   ├── commands/      # CLI commands (analyze, server, etc.)
│       │   └── index.ts      # Entry point
│       └── package.json
│
├── packages/
│   ├── core/                  # Core analysis engine (main package)
│   │   ├── src/
│   │   │   ├── scanner/       # RepositoryScanner → scanner/
│   │   │   ├── parser/        # JavaRealParser, KotlinRegexParser → parser/
│   │   │   ├── graph/         # RobustDependencyGraph → graph/
│   │   │   ├── analyzer/      # Analyzer → analyzer/
│   │   │   ├── generator/     # LearningPathGenerator → generator/
│   │   │   ├── cache/        # CacheManager → cache/
│   │   │   ├── temporal/     # TemporalAnalyzer → temporal/
│   │   │   └── index.ts      # Public API exports
│   │   ├── package.json
│   │   └── tsconfig.json
│   │
│   ├── server/                # API server package
│   │   ├── src/
│   │   │   ├── routes/       # API route handlers
│   │   │   │   ├── analyze.ts
│   │   │   │   ├── ask.ts
│   │   │   │   └── health.ts
│   │   │   ├── middleware/   # RateLimiter, CORS, etc.
│   │   │   └── index.ts      # Server setup
│   │   ├── package.json
│   │   └── tsconfig.json
│   │
│   ├── output/                # Report generation
│   │   ├── src/
│   │   │   ├── ReportGenerator.ts
│   │   │   └── templates/    # HTML templates
│   │   ├── package.json
│   │   └── tsconfig.json
│   │
│   ├── config/                # Configuration management
│   │   ├── src/
│   │   │   ├── CodeContextConfig.ts
│   │   │   └── ConfigLoader.ts
│   │   ├── package.json
│   │   └── tsconfig.json
│   │
│   ├── enterprise/            # Enterprise features
│   │   ├── src/
│   │   │   ├── LicenseManager.ts
│   │   │   └── OrganizationAnalyzer.ts
│   │   ├── package.json
│   │   └── tsconfig.json
│   │
│   ├── ai/                    # AI integration
│   │   ├── src/
│   │   │   └── AICodeAnalyzer.ts
│   │   ├── package.json
│   │   └── tsconfig.json
│   │
│   └── shared/                # Shared types, utilities
│       ├── src/
│       │   ├── types/        # TypeScript types/interfaces
│       │   │   ├── ParsedFile.ts
│       │   │   ├── Graph.ts
│       │   │   └── index.ts
│       │   └── utils/        # Shared utilities
│       ├── package.json
│       └── tsconfig.json
│
├── turbo.json                 # Turborepo configuration
├── package.json               # Root package.json
├── pnpm-workspace.yaml        # or package.json workspaces
└── tsconfig.json              # Root TypeScript config
```

## Package Dependencies

```
apps/web
  └── depends on: @codecontext/core, @codecontext/server, @codecontext/output

apps/cli
  └── depends on: @codecontext/core, @codecontext/output, @codecontext/config

packages/core
  └── depends on: @codecontext/shared

packages/server
  └── depends on: @codecontext/core, @codecontext/config, @codecontext/enterprise, @codecontext/ai

packages/output
  └── depends on: @codecontext/core, @codecontext/shared

packages/config
  └── depends on: @codecontext/shared

packages/enterprise
  └── depends on: @codecontext/core

packages/ai
  └── depends on: @codecontext/core, @codecontext/shared
```

## Technology Stack Migration

| Kotlin Component | TypeScript/Node.js Equivalent |
|-----------------|------------------------------|
| Kotlin 2.1.0 | TypeScript 5.x |
| Gradle | pnpm/npm workspaces + Turborepo |
| Clikt | Commander.js or yargs |
| JavaParser | @typescript-eslint/parser, babel-parser, or tree-sitter |
| JGraphT | graphology or cytoscape.js |
| JGit | simple-git or isomorphic-git |
| kotlinx.html | React Server Components or template strings |
| kotlinx.serialization | zod for validation, native JSON |
| Ktor | Next.js API routes or Express.js |
| kotlinx.coroutines | native async/await, p-limit for concurrency |
| Kotest | Jest or Vitest |

## Key Migration Considerations

### 1. Code Parsing
- **Java/Kotlin Parsers**: Use `@typescript-eslint/parser` for TypeScript/JavaScript, or `tree-sitter` for multi-language support
- **AST Traversal**: Use `@typescript-eslint/typescript-estree` or `babel` for JavaScript/TypeScript
- For Java/Kotlin parsing in Node.js, consider:
  - `tree-sitter-java` and `tree-sitter-kotlin`
  - Or call JavaParser via a Java subprocess
  - Or use a language server protocol (LSP) client

### 2. Graph Algorithms
- **PageRank**: Use `graphology` library (has PageRank implementation)
- **Topological Sort**: Use `graphology` or implement with Kahn's algorithm
- **Dependency Graph**: `graphology` provides directed graphs

### 3. Git Integration
- Use `simple-git` (most popular) or `isomorphic-git` (pure JS)
- Both support git history, blame, log operations

### 4. Parallel Processing
- Replace Kotlin coroutines with:
  - `Promise.all()` for parallel async operations
  - `p-limit` for concurrency control
  - Worker threads for CPU-intensive tasks

### 5. Caching
- Replace file-based cache with:
  - File system (same approach)
  - Or Redis for distributed caching
  - Or use `node-cache` for in-memory

### 6. CLI
- Replace Clikt with:
  - `commander.js` (most popular)
  - `yargs` (more features)
  - `oclif` (full framework)

## Implementation Steps

### Phase 1: Setup Turborepo
1. Initialize Turborepo: `npx create-turbo@latest`
2. Configure `turbo.json` with build pipelines
3. Set up package.json workspaces or pnpm-workspace.yaml
4. Configure TypeScript project references

### Phase 2: Create Shared Package
1. Create `packages/shared` with TypeScript types
2. Define interfaces: `ParsedFile`, `DependencyGraph`, etc.
3. Set up package exports

### Phase 3: Migrate Core Package
1. Start with `packages/core/scanner` (RepositoryScanner)
2. Migrate `packages/core/parser` (most complex)
3. Migrate `packages/core/graph` (RobustDependencyGraph)
4. Migrate `packages/core/generator` (LearningPathGenerator)
5. Migrate `packages/core/cache` (CacheManager)

### Phase 4: Migrate Supporting Packages
1. `packages/config` - Configuration management
2. `packages/output` - Report generation
3. `packages/ai` - AI integration
4. `packages/enterprise` - Enterprise features

### Phase 5: Create Applications
1. `apps/cli` - CLI tool using core packages
2. `apps/web` - Next.js frontend
3. `packages/server` - API server (or use Next.js API routes)

### Phase 6: Testing & Integration
1. Port tests to Jest/Vitest
2. Set up E2E tests
3. Integration testing across packages

## Example Package Structure

### packages/core/package.json
```json
{
  "name": "@codecontext/core",
  "version": "0.1.0",
  "main": "./dist/index.js",
  "types": "./dist/index.d.ts",
  "exports": {
    ".": "./dist/index.js",
    "./scanner": "./dist/scanner/index.js",
    "./parser": "./dist/parser/index.js",
    "./graph": "./dist/graph/index.js"
  },
  "dependencies": {
    "@codecontext/shared": "workspace:*",
    "graphology": "^0.25.4",
    "graphology-pagerank": "^0.1.0",
    "simple-git": "^3.20.0",
    "p-limit": "^5.0.0"
  },
  "devDependencies": {
    "@types/node": "^20.0.0",
    "typescript": "^5.3.0"
  }
}
```

### packages/core/src/index.ts
```typescript
// Public API exports
export * from './scanner';
export * from './parser';
export * from './graph';
export * from './generator';
export * from './analyzer';
export * from './cache';
```

## Turborepo Configuration

### turbo.json
```json
{
  "$schema": "https://turbo.build/schema.json",
  "pipeline": {
    "build": {
      "dependsOn": ["^build"],
      "outputs": ["dist/**", ".next/**"]
    },
    "dev": {
      "cache": false,
      "persistent": true
    },
    "test": {
      "dependsOn": ["build"],
      "outputs": []
    },
    "lint": {
      "outputs": []
    }
  }
}
```

## Usage in Next.js App

### apps/web/app/api/analyze/route.ts
```typescript
import { analyzeRepository } from '@codecontext/core';
import { generateReport } from '@codecontext/output';

export async function POST(request: Request) {
  const { repoPath } = await request.json();
  
  const result = await analyzeRepository(repoPath);
  const reportPath = await generateReport(result);
  
  return Response.json({ reportPath });
}
```

### apps/web/components/DependencyGraph.tsx
```typescript
'use client';

import { useEffect, useRef } from 'react';
import { DependencyGraph } from '@codecontext/core';

export function DependencyGraphView({ graph }: { graph: DependencyGraph }) {
  const containerRef = useRef<HTMLDivElement>(null);
  
  useEffect(() => {
    // Render D3.js or React Flow graph
  }, [graph]);
  
  return <div ref={containerRef} />;
}
```

## Benefits of This Structure

1. **Modularity**: Each package has a single responsibility
2. **Reusability**: Core logic can be used by CLI, web app, and API
3. **Type Safety**: Shared TypeScript types across packages
4. **Performance**: Turborepo caching speeds up builds
5. **Scalability**: Easy to add new packages (e.g., VS Code extension)
6. **Developer Experience**: Hot reload, shared tooling, monorepo benefits

## Next Steps

1. Review this structure and adjust based on your needs
2. Set up the Turborepo scaffold
3. Begin migrating packages one at a time, starting with `shared` and `core`
4. Test each package in isolation before integrating
5. Consider keeping Kotlin version running in parallel during migration

