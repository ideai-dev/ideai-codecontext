# Example Turborepo Structure & Configuration

This file shows concrete examples of how the CodeContext project would be structured in a Turborepo monorepo.

## Directory Tree

```
codecontext-monorepo/
├── .gitignore
├── package.json                    # Root workspace config
├── pnpm-workspace.yaml            # pnpm workspace config
├── turbo.json                     # Turborepo config
├── tsconfig.json                  # Root TS config
│
├── apps/
│   ├── web/                       # Next.js 14+ App
│   │   ├── package.json
│   │   ├── next.config.js
│   │   ├── tsconfig.json
│   │   ├── app/
│   │   │   ├── layout.tsx
│   │   │   ├── page.tsx          # Landing page
│   │   │   ├── analyze/
│   │   │   │   └── page.tsx      # Analysis UI
│   │   │   └── api/
│   │   │       ├── analyze/
│   │   │       │   └── route.ts  # Uses @codecontext/server
│   │   │       └── ask/
│   │   │           └── route.ts
│   │   ├── components/
│   │   │   ├── DependencyGraph.tsx
│   │   │   ├── LearningPath.tsx
│   │   │   └── Hotspots.tsx
│   │   └── lib/
│   │       └── utils.ts
│   │
│   └── cli/                       # CLI Tool
│       ├── package.json
│       ├── tsconfig.json
│       ├── src/
│       │   ├── index.ts          # Entry: "codecontext"
│       │   ├── commands/
│       │   │   ├── analyze.ts    # analyze command
│       │   │   ├── server.ts     # server command
│       │   │   ├── ask.ts        # ask command
│       │   │   └── evolution.ts  # evolution command
│       │   └── utils/
│       └── bin/
│           └── codecontext       # Executable script
│
└── packages/
    ├── core/                      # ⭐ Main Analysis Engine
    │   ├── package.json
    │   ├── tsconfig.json
    │   ├── src/
    │   │   ├── index.ts          # Public API
    │   │   ├── scanner/
    │   │   │   ├── index.ts
    │   │   │   ├── RepositoryScanner.ts
    │   │   │   └── GitAnalyzer.ts
    │   │   ├── parser/
    │   │   │   ├── index.ts
    │   │   │   ├── ParserFactory.ts
    │   │   │   ├── JavaParser.ts
    │   │   │   ├── KotlinParser.ts
    │   │   │   └── TypeScriptParser.ts
    │   │   ├── graph/
    │   │   │   ├── index.ts
    │   │   │   ├── DependencyGraph.ts
    │   │   │   └── PageRank.ts
    │   │   ├── generator/
    │   │   │   ├── index.ts
    │   │   │   └── LearningPathGenerator.ts
    │   │   ├── analyzer/
    │   │   │   ├── index.ts
    │   │   │   └── Analyzer.ts
    │   │   ├── cache/
    │   │   │   ├── index.ts
    │   │   │   └── CacheManager.ts
    │   │   └── temporal/
    │   │       ├── index.ts
    │   │       └── TemporalAnalyzer.ts
    │   └── dist/                  # Built output
    │
    ├── server/                    # API Server Logic
    │   ├── package.json
    │   ├── tsconfig.json
    │   ├── src/
    │   │   ├── index.ts
    │   │   ├── routes/
    │   │   │   ├── analyze.ts
    │   │   │   ├── ask.ts
    │   │   │   └── health.ts
    │   │   ├── middleware/
    │   │   │   ├── rateLimiter.ts
    │   │   │   └── cors.ts
    │   │   └── utils/
    │   │       └── sanitizePath.ts
    │   └── dist/
    │
    ├── output/                    # Report Generation
    │   ├── package.json
    │   ├── tsconfig.json
    │   ├── src/
    │   │   ├── index.ts
    │   │   ├── ReportGenerator.ts
    │   │   └── templates/
    │   │       ├── index.html
    │   │       └── graph-template.html
    │   └── dist/
    │
    ├── config/                    # Configuration
    │   ├── package.json
    │   ├── tsconfig.json
    │   ├── src/
    │   │   ├── index.ts
    │   │   ├── CodeContextConfig.ts
    │   │   └── ConfigLoader.ts
    │   └── dist/
    │
    ├── ai/                        # AI Integration
    │   ├── package.json
    │   ├── tsconfig.json
    │   ├── src/
    │   │   ├── index.ts
    │   │   └── AICodeAnalyzer.ts
    │   └── dist/
    │
    ├── enterprise/                # Enterprise Features
    │   ├── package.json
    │   ├── tsconfig.json
    │   ├── src/
    │   │   ├── index.ts
    │   │   ├── LicenseManager.ts
    │   │   └── OrganizationAnalyzer.ts
    │   └── dist/
    │
    └── shared/                    # Shared Types & Utils
        ├── package.json
        ├── tsconfig.json
        ├── src/
        │   ├── index.ts
        │   ├── types/
        │   │   ├── ParsedFile.ts
        │   │   ├── Graph.ts
        │   │   ├── Config.ts
        │   │   └── index.ts
        │   └── utils/
        │       └── index.ts
        └── dist/
```

## Root Configuration Files

### package.json (Root)
```json
{
  "name": "codecontext-monorepo",
  "version": "0.1.0",
  "private": true,
  "workspaces": [
    "apps/*",
    "packages/*"
  ],
  "scripts": {
    "build": "turbo run build",
    "dev": "turbo run dev",
    "test": "turbo run test",
    "lint": "turbo run lint",
    "clean": "turbo run clean && rm -rf node_modules"
  },
  "devDependencies": {
    "turbo": "^1.11.0",
    "typescript": "^5.3.0"
  },
  "packageManager": "pnpm@8.15.0"
}
```

### pnpm-workspace.yaml
```yaml
packages:
  - 'apps/*'
  - 'packages/*'
```

### turbo.json
```json
{
  "$schema": "https://turbo.build/schema.json",
  "globalDependencies": ["**/.env.*local"],
  "pipeline": {
    "build": {
      "dependsOn": ["^build"],
      "outputs": ["dist/**", ".next/**", "!.next/cache/**"]
    },
    "dev": {
      "cache": false,
      "persistent": true
    },
    "test": {
      "dependsOn": ["build"],
      "outputs": ["coverage/**"]
    },
    "lint": {
      "outputs": []
    },
    "type-check": {
      "dependsOn": ["^build"],
      "outputs": []
    }
  }
}
```

### tsconfig.json (Root)
```json
{
  "compilerOptions": {
    "target": "ES2022",
    "module": "ESNext",
    "lib": ["ES2022"],
    "moduleResolution": "bundler",
    "strict": true,
    "esModuleInterop": true,
    "skipLibCheck": true,
    "forceConsistentCasingInFileNames": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "incremental": true,
    "composite": true
  },
  "exclude": ["node_modules", "dist"]
}
```

## Package Examples

### packages/core/package.json
```json
{
  "name": "@codecontext/core",
  "version": "0.1.0",
  "main": "./dist/index.js",
  "types": "./dist/index.d.ts",
  "exports": {
    ".": {
      "types": "./dist/index.d.ts",
      "default": "./dist/index.js"
    },
    "./scanner": {
      "types": "./dist/scanner/index.d.ts",
      "default": "./dist/scanner/index.js"
    },
    "./parser": {
      "types": "./dist/parser/index.d.ts",
      "default": "./dist/parser/index.js"
    },
    "./graph": {
      "types": "./dist/graph/index.d.ts",
      "default": "./dist/graph/index.js"
    }
  },
  "scripts": {
    "build": "tsc",
    "dev": "tsc --watch",
    "test": "vitest",
    "lint": "eslint src"
  },
  "dependencies": {
    "@codecontext/shared": "workspace:*",
    "graphology": "^0.25.4",
    "graphology-pagerank": "^0.1.0",
    "simple-git": "^3.20.0",
    "p-limit": "^5.0.0",
    "fast-glob": "^3.3.2"
  },
  "devDependencies": {
    "@types/node": "^20.11.0",
    "typescript": "^5.3.0",
    "vitest": "^1.2.0"
  }
}
```

### packages/core/src/index.ts
```typescript
// Public API - re-export everything
export * from './scanner';
export * from './parser';
export * from './graph';
export * from './generator';
export * from './analyzer';
export * from './cache';
export * from './temporal';

// Main entry point function
export { analyzeRepository } from './analyze';
```

### packages/core/src/analyze.ts
```typescript
import { RepositoryScanner } from './scanner';
import { ParserFactory } from './parser';
import { DependencyGraph } from './graph';
import { CacheManager } from './cache';
import type { ParsedFile } from '@codecontext/shared';

export interface AnalysisResult {
  graph: DependencyGraph;
  parsedFiles: ParsedFile[];
  cacheManager: CacheManager;
}

export async function analyzeRepository(
  repoPath: string
): Promise<AnalysisResult> {
  // 1. Scan repository
  const scanner = new RepositoryScanner();
  const files = await scanner.scan(repoPath);

  // 2. Parse files (with caching)
  const cacheManager = new CacheManager();
  const parser = ParserFactory.create();
  const parsedFiles = await parser.parseFiles(files, cacheManager);

  // 3. Build dependency graph
  const graph = new DependencyGraph();
  graph.build(parsedFiles);
  graph.analyze(); // Run PageRank

  return { graph, parsedFiles, cacheManager };
}
```

### apps/web/package.json
```json
{
  "name": "@codecontext/web",
  "version": "0.1.0",
  "private": true,
  "scripts": {
    "dev": "next dev",
    "build": "next build",
    "start": "next start",
    "lint": "next lint"
  },
  "dependencies": {
    "@codecontext/core": "workspace:*",
    "@codecontext/server": "workspace:*",
    "@codecontext/output": "workspace:*",
    "@codecontext/shared": "workspace:*",
    "next": "^14.1.0",
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "d3": "^7.8.5",
    "react-flow-renderer": "^11.10.0"
  },
  "devDependencies": {
    "@types/node": "^20.11.0",
    "@types/react": "^18.2.0",
    "@types/react-dom": "^18.2.0",
    "typescript": "^5.3.0"
  }
}
```

### apps/web/app/api/analyze/route.ts
```typescript
import { NextRequest, NextResponse } from 'next/server';
import { analyzeRepository } from '@codecontext/core';
import { generateReport } from '@codecontext/output';
import { sanitizePath } from '@codecontext/server';

export async function POST(request: NextRequest) {
  try {
    const { repoPath } = await request.json();

    // Security: Sanitize path
    const safePath = sanitizePath(repoPath);
    if (!safePath) {
      return NextResponse.json(
        { error: 'Invalid repository path' },
        { status: 400 }
      );
    }

    // Analyze repository
    const result = await analyzeRepository(safePath);

    // Generate report
    const reportPath = await generateReport(result);

    // Get hotspots
    const hotspots = result.graph.getTopHotspots(5);

    return NextResponse.json({
      fileCount: result.parsedFiles.length,
      hotspots: hotspots.map(([file, score]) => ({ file, score })),
      reportPath,
    });
  } catch (error) {
    return NextResponse.json(
      { error: error instanceof Error ? error.message : 'Unknown error' },
      { status: 500 }
    );
  }
}
```

### apps/cli/package.json
```json
{
  "name": "@codecontext/cli",
  "version": "0.1.0",
  "bin": {
    "codecontext": "./dist/bin/codecontext.js"
  },
  "scripts": {
    "build": "tsc",
    "dev": "tsc --watch"
  },
  "dependencies": {
    "@codecontext/core": "workspace:*",
    "@codecontext/output": "workspace:*",
    "@codecontext/config": "workspace:*",
    "commander": "^11.1.0"
  },
  "devDependencies": {
    "@types/node": "^20.11.0",
    "typescript": "^5.3.0"
  }
}
```

### apps/cli/src/index.ts
```typescript
#!/usr/bin/env node

import { Command } from 'commander';
import { analyzeCommand } from './commands/analyze';
import { serverCommand } from './commands/server';
import { askCommand } from './commands/ask';

const program = new Command();

program
  .name('codecontext')
  .description('Intelligent codebase context analyzer')
  .version('0.1.0');

program.addCommand(analyzeCommand);
program.addCommand(serverCommand);
program.addCommand(askCommand);

program.parse();
```

### apps/cli/src/commands/analyze.ts
```typescript
import { Command } from 'commander';
import { analyzeRepository } from '@codecontext/core';
import { generateReport } from '@codecontext/output';

export const analyzeCommand = new Command('analyze')
  .description('Analyze a codebase')
  .argument('<path>', 'Path to repository')
  .option('-o, --output <path>', 'Output directory', 'output')
  .action(async (path: string, options: { output: string }) => {
    console.log(`Starting CodeContext analysis for: ${path}`);

    const result = await analyzeRepository(path);
    const reportPath = await generateReport(result, options.output);

    console.log(`Report generated at: ${reportPath}`);
  });
```

## Usage Examples

### As a Package (in another project)
```typescript
// In any project
import { analyzeRepository } from '@codecontext/core';
import { DependencyGraph } from '@codecontext/core/graph';

const result = await analyzeRepository('./my-project');
const hotspots = result.graph.getTopHotspots(10);
```

### In Next.js App
```typescript
// apps/web/app/analyze/page.tsx
'use client';

import { useState } from 'react';
import { analyzeRepository } from '@codecontext/core';

export default function AnalyzePage() {
  const [result, setResult] = useState(null);

  const handleAnalyze = async () => {
    const data = await fetch('/api/analyze', {
      method: 'POST',
      body: JSON.stringify({ repoPath: '/path/to/repo' }),
    });
    setResult(await data.json());
  };

  return <button onClick={handleAnalyze}>Analyze</button>;
}
```

### CLI Usage
```bash
# After building
pnpm build

# Run CLI
pnpm --filter @codecontext/cli codecontext analyze ./my-project

# Or install globally
pnpm add -g @codecontext/cli
codecontext analyze ./my-project
```

## Build & Development

```bash
# Install dependencies
pnpm install

# Build all packages
pnpm build

# Develop with hot reload
pnpm dev

# Run specific app
pnpm --filter @codecontext/web dev
pnpm --filter @codecontext/cli dev

# Test
pnpm test

# Lint
pnpm lint
```

## Publishing Packages

You can publish individual packages to npm:

```bash
# Publish core package
pnpm --filter @codecontext/core publish

# Or publish all
pnpm -r publish
```

This structure allows:
- ✅ CodeContext to be used as npm packages
- ✅ Independent versioning per package
- ✅ Shared code via workspace protocol
- ✅ Fast builds with Turborepo caching
- ✅ Type safety across packages
- ✅ Easy integration into Next.js apps

