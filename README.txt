Get Eclipse Mars STS (you can download the varsion added to accurev in this folder)
Install Swing and WindowsBuild packages from this location: https://download.eclipse.org/windowbuilder/lastgoodbuild/ - this will already be installed on the eclipse provided
Follow these instructions to install these packages: https://help.eclipse.org/2019-12/index.jsp?topic=/org.eclipse.wb.doc.user/html/installation/index.html

Import \RPTValidator\
These are the steps you can follow:
1. Open eclipse
2. Use the workspace under the eclipse instalation
3. Import the RPTValidator project (Java- Existing Projects)
4. Click on the top menu item: Run -> Run Configuration
5. Under Java Application create a new configuration
6. Select guiPackage.cr_tester as Main class (keep the project name)
7. Hit Run


Exporting:
1. Go to File -> Export -> Java -> Runnable Jar File
2. Select the Launch Configuration we've used to run the project from Eclipse
3. Set an export destination
4. Select extract required libraries into generated jar
5. Hit Finish (ignore duplicate warnings)
6. Copy log4j.xml and run_rpt_validator.bat to the extract location
6. Test if the jar works by running run_rpt_validator.bat
