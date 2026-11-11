function Resolve-MavenCommand {
    param([string]$ProjectRoot, [string]$MavenHome)

    $wrapper = Join-Path $ProjectRoot "mvnw.cmd"
    if (Test-Path $wrapper) { return $wrapper }

    $command = Get-Command "mvn.cmd" -ErrorAction SilentlyContinue
    if ($command) { return $command.Source }

    foreach ($homePath in @($MavenHome, $env:MAVEN_HOME, $env:M2_HOME)) {
        if (-not [string]::IsNullOrWhiteSpace($homePath)) {
            $candidate = Join-Path $homePath "bin\mvn.cmd"
            if (Test-Path $candidate) { return $candidate }
        }
    }

    $patterns = @(
        "$ProjectRoot\.tools\apache-maven-*\bin\mvn.cmd",
        "C:\tools\apache-maven-*\bin\mvn.cmd",
        "C:\apache-maven-*\bin\mvn.cmd",
        "$env:ProgramFiles\Apache\apache-maven-*\bin\mvn.cmd",
        "$env:ProgramFiles\apache-maven-*\bin\mvn.cmd",
        "$env:USERPROFILE\scoop\apps\maven\current\bin\mvn.cmd",
        "$env:ProgramData\chocolatey\bin\mvn.cmd"
    )
    foreach ($pattern in $patterns) {
        $candidate = Get-ChildItem -Path $pattern -File -ErrorAction SilentlyContinue |
            Sort-Object FullName -Descending | Select-Object -First 1
        if ($candidate) { return $candidate.FullName }
    }

    throw 'Maven was not found. Install Maven or run with -MavenHome "C:\path\to\apache-maven". The running server has not been stopped.'
}

function Test-BuildTools {
    param([string]$MavenCommand)

    if (-not (Get-Command "java" -ErrorAction SilentlyContinue)) {
        throw "Java was not found on PATH. Configure Java 17 before deploying."
    }
    & $MavenCommand --version
    if ($LASTEXITCODE -ne 0) {
        throw "Maven could not start. Check JAVA_HOME and the Maven installation."
    }
}
