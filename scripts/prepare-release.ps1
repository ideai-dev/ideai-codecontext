# CodeContext Release Preparation Script (Windows)
# Version: 0.1.0

$ErrorActionPreference = "Stop"

Write-Host "🚀 CodeContext Release Preparation" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

$VERSION = "v0.1.0"

# Step 1: Clean previous builds
Write-Host "📦 Step 1: Cleaning previous builds..." -ForegroundColor Blue
& .\gradlew.bat clean
Write-Host "✅ Clean complete" -ForegroundColor Green
Write-Host ""

# Step 2: Run tests
Write-Host "🧪 Step 2: Running tests..." -ForegroundColor Blue
& .\gradlew.bat test
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ All tests passed" -ForegroundColor Green
} else {
    Write-Host "⚠️  Some tests failed. Continue anyway? (y/n)" -ForegroundColor Yellow
    $response = Read-Host
    if ($response -notmatch '^[Yy]$') {
        Write-Host "Release cancelled." -ForegroundColor Red
        exit 1
    }
}
Write-Host ""

# Step 3: Build project
Write-Host "🔨 Step 3: Building project..." -ForegroundColor Blue
& .\gradlew.bat build
Write-Host "✅ Build complete" -ForegroundColor Green
Write-Host ""

# Step 4: Create distribution
Write-Host "📦 Step 4: Creating distribution..." -ForegroundColor Blue
& .\gradlew.bat installDist
Write-Host "✅ Distribution created" -ForegroundColor Green
Write-Host ""

# Step 5: Package release
Write-Host "📦 Step 5: Packaging release..." -ForegroundColor Blue
$RELEASE_NAME = "codecontext-$VERSION"
$RELEASE_DIR = "build\release"

# Create release directory
New-Item -ItemType Directory -Force -Path $RELEASE_DIR | Out-Null

# Create zip archive
Push-Location build\install
Compress-Archive -Path codecontext -DestinationPath "..\..\$RELEASE_DIR\$RELEASE_NAME.zip" -Force
Write-Host "✅ Created $RELEASE_NAME.zip" -ForegroundColor Green
Pop-Location
Write-Host ""

# Step 6: Generate checksums
Write-Host "🔐 Step 6: Generating checksums..." -ForegroundColor Blue
Push-Location $RELEASE_DIR
Get-FileHash -Algorithm SHA256 "$RELEASE_NAME.zip" | 
    Select-Object @{Name='Hash';Expression={$_.Hash.ToLower()}}, @{Name='File';Expression={Split-Path $_.Path -Leaf}} |
    ForEach-Object { "$($_.Hash)  $($_.File)" } |
    Out-File -FilePath checksums.txt -Encoding utf8
Write-Host "✅ Checksums generated" -ForegroundColor Green
Pop-Location
Write-Host ""

# Step 7: Create release notes
Write-Host "📝 Step 7: Creating release notes..." -ForegroundColor Blue
$releaseNotes = @"
# CodeContext $VERSION Release Notes

## 🎉 Features

### Core Analysis
- **Smart Git Analysis**: Single-pass commit analysis for optimal performance
- **Parallel Parsing**: Chunked file processing with intelligent caching
- **Cycle-Aware Dependency Graph**: Robust handling of circular dependencies
- **AI-Powered Insights**: Optional Gemini integration for code analysis

### Commands
- ``analyze``: Comprehensive codebase analysis with hotspot detection
- ``ai-assistant``: AI-powered code insights and recommendations
- ``evolution``: Track codebase changes over time
- ``server``: REST API server mode for programmatic access

### Output
- Interactive HTML reports with D3.js visualizations
- Learning path generation for new developers
- AI insights in markdown format
- Hotspot detection and ranking

## 📦 Installation

### From Release Archive

``````bash
# Extract archive
Expand-Archive codecontext-$VERSION.zip

# Add to PATH (PowerShell)
`$env:Path += ";`$(Get-Location)\codecontext\bin"

# Verify installation
codecontext --help
``````

### From Source

``````bash
git clone https://github.com/sonii-shivansh/CodeContext.git
cd CodeContext
.\gradlew.bat installDist
.\build\install\codecontext\bin\codecontext.bat --help
``````

## 🚀 Quick Start

``````bash
# Analyze current directory
codecontext analyze .

# View report
start output\index.html

# Enable AI insights (requires Gemini API key)
# Add to .codecontext.json:
{
  "ai": {
    "enabled": true,
    "apiKey": "your-api-key",
    "model": "gemini-1.5-flash"
  }
}
``````

## 🔧 Configuration

Create ``.codecontext.json`` in your project root:

``````json
{
  "maxFilesAnalyze": 10000,
  "hotspotCount": 10,
  "enableCache": true,
  "ai": {
    "enabled": false,
    "apiKey": "",
    "model": "gemini-1.5-flash"
  }
}
``````

## 📊 System Requirements

- **JVM**: Java 11 or higher
- **Memory**: 2GB RAM minimum, 4GB recommended
- **Disk**: 100MB for installation, additional space for cache

## 🐛 Known Issues

- Large repositories (>10,000 files) may require increased heap size
- Git analysis requires repository to be initialized
- AI features require valid Gemini API key

## 📚 Documentation

- [README](https://github.com/sonii-shivansh/CodeContext/blob/main/README.md)
- [API Documentation](https://github.com/sonii-shivansh/CodeContext/blob/main/docs/API.md)
- [Contributing Guide](https://github.com/sonii-shivansh/CodeContext/blob/main/CONTRIBUTING.md)

## 🙏 Acknowledgments

Built with:
- Kotlin
- JGit for Git analysis
- JGraphT for graph algorithms
- Ktor for REST API
- D3.js for visualizations

## 📧 Support

- Issues: https://github.com/sonii-shivansh/CodeContext/issues
- Email: shivanshsoni568@gmail.com
- Discussions: https://github.com/sonii-shivansh/CodeContext/discussions

## 📄 License

MIT License - See LICENSE file for details
"@

$releaseNotes | Out-File -FilePath "$RELEASE_DIR\RELEASE_NOTES.md" -Encoding utf8
Write-Host "✅ Release notes created" -ForegroundColor Green
Write-Host ""

# Summary
Write-Host "==================================" -ForegroundColor Cyan
Write-Host "✨ Release preparation complete!" -ForegroundColor Green
Write-Host ""
Write-Host "📦 Release artifacts:" -ForegroundColor Cyan
Write-Host "   - Location: $RELEASE_DIR\" -ForegroundColor White
Get-ChildItem $RELEASE_DIR | Format-Table Name, Length, LastWriteTime
Write-Host ""
Write-Host "📋 Next steps:" -ForegroundColor Cyan
Write-Host "   1. Review release notes: $RELEASE_DIR\RELEASE_NOTES.md" -ForegroundColor White
Write-Host "   2. Test the distribution: .\build\install\codecontext\bin\codecontext.bat --help" -ForegroundColor White
Write-Host "   3. Create GitHub release with artifacts from $RELEASE_DIR\" -ForegroundColor White
Write-Host "   4. Update CHANGELOG.md" -ForegroundColor White
Write-Host "   5. Tag release: git tag $VERSION && git push origin $VERSION" -ForegroundColor White
Write-Host ""
Write-Host "🎉 Ready to release $VERSION!" -ForegroundColor Blue
