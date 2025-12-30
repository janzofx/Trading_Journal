$files = Get-ChildItem -Path "src\main\java" -Filter "*.java" -Recurse
$files | ForEach-Object { "`"$($_.FullName.Replace('\', '/'))`"" } | Out-File -Encoding ASCII sources_quoted.txt
