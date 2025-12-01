$files = Get-ChildItem -Path "SYNC/src/main/java" -Recurse -Filter "*.java"

foreach ($file in $files) {
    $content = Get-Content $file.FullName -Raw
    # Remove single-line comments
    $content = $content -replace '(?m)^[ \t]*//.*$', ''
    # Remove multi-line comments
    $content = $content -replace '(?s)/\*.*?\*/', ''
    # Remove extra blank lines if any
    $content = $content -replace '(?m)^\s*$', ''
    Set-Content $file.FullName $content
}
