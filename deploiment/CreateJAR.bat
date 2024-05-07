

SET APP_DIR=%~dp0
SET LIB_DIR=%APP_DIR%lib

REM Copier les *.java dans un dossier temporaire tempjava
MKDIR "%APP_DIR%tempjava"
for /R "%APP_DIR%" %%G IN (*.java) DO (
    XCOPY /Y "%%G" "%APP_DIR%\tempjava"
)

MKDIR "%APP_DIR%\classes"

REM Compiler les classes Java
javac -cp "%LIB_DIR%\*" -d "%APP_DIR%\classes"  %APP_DIR%tempjava\*.java

REM
    @REM jar cvf sprint0.jar -C %APP_DIR%classes\*
    jar cvf sprint0.jar -C "%APP_DIR%\classes" .


RD /S /Q "%APP_DIR%\classes"
RD /S /Q "%APP_DIR%\tempjava"