@echo off
set /p dir1="test"
set /p dir2="../"
xcopy "%dir1%" "%dir2%" /e /i /y
@pause