@echo off
setlocal

set MAVEN_VERSION=3.9.6
set MAVEN_DEST=%USERPROFILE%\.mvnw
set MVN=%MAVEN_DEST%\apache-maven-%MAVEN_VERSION%\bin\mvn.cmd

if not exist "%MVN%" (
    echo Maven nije pronaden. Preuzimam Maven %MAVEN_VERSION%...
    powershell -NoProfile -ExecutionPolicy Bypass -Command "$v='%MAVEN_VERSION%';$d='%MAVEN_DEST%';$url='https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/'+$v+'/apache-maven-'+$v+'-bin.zip';$zip=$env:TEMP+'\maven_dl.zip';Write-Host 'Preuzimanje...';Invoke-WebRequest -Uri $url -OutFile $zip;Write-Host 'Raspakivanje...';Expand-Archive -Path $zip -DestinationPath $d -Force;Remove-Item $zip;Write-Host 'Maven preuzet!'"
    if errorlevel 1 (
        echo.
        echo GRESKA: Nije moguce preuzeti Maven automatski.
        echo Instalirajte Maven rucno s: https://maven.apache.org/download.cgi
        exit /b 1
    )
)

"%MVN%" %*
endlocal
