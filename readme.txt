Release Notes:-

v1
=================================
1. PRODUCTION TAB FOR ROLES EBI_OPS AND EBI_ADMIN
2. DEV/LAB TAB FOR ROLES EBI_DEV AND EBI_ADMIN
3. PRODUCTION TAB:-
	1. PROMOTE KEY/VALUE FROM DEV/LAB
	2. ADD/MODIFY DIRECTLY TO PRODUCTION VALUES
	3. TAG TO FILE WHICH WILL VERSION THE FILES
	4. VIEW ALL VERSIONS OF FILES
	5. COMPARE BETWEEN FILES
	6. DOWNLOAD ANY VERSION 
	7. FULL AUDIT TRAIL OF EACH KEY
	
4. DEV/LAB TAB:-
	1. DISPLAY RALLY FEATURES(IMPLEMENTING OR ACCEPTING STATE) IN FEATURE SELECTION
	2. ADD/EDIT/DELETE KEYS LINKED TO RALLY FEATURES
	3. FULL AUDIT TRAIL OF EACH KEY
	4. DOWNLOAD PRODUCTION FILE MERGED WITH NEW/MODIFIED/DELETED KEYS
	5. DOWNLOAD ZIP
	
===========================================================
V1.1
===========================================================
1. FULL FLOWDOC INTEGRATION
2. MINOR BUG FIXES
3. VIEW ALL VERSIONS OF FILES NOW ALSO AVAILABLE FOR DEV/LAB
4. COMPARE BETWEEN FILES NOW ALSO AVAILABLE IN DEV/LAB
5. EXPORT TO EXCEL FOR ALL NEW/MODIFIED/DELETED KEYS IN DEV/LAB 
6. BETTER FILE COMPARISON VIEW



=======================================================================
V2.0.0
=======================================================================
US5015
1. Added new view for Marking Current Rally Milestone in tool
2. Added new API for Jenkins Regression process.

Fixed 2 Defects
1. Wrong version no being sent in flowdoc after Production Tagging
2. Historical File comparison LHS and RHS issue has been fixed. 

=========================
Jenkis API Samples
=========================

All of the Apis follow Basic Authentication Standards.

===================================================================================
Integrate compilation

http://10.107.138.50:8080/ebiutil/rest/rsrc/selfcare/release
{"appName":"SELFCARE","buildVersion":"130.2.000-INT","buildType":"INTEGRATE"}
===========================================================================================

============================================================================================
Release Branch compilation

http://10.107.138.50:8080/ebiutil/rest/rsrc/selfcare/release
{"appName":"SELFCARE","buildType":"RELEASE","buildVersion":"131.0.0-REL"}
=======================================================================

======================================================================================================
Fetch Integrate Milestone files

http://10.107.138.50:8080/ebiutil/rest/rsrc/milestone/files?langType=english&releaseId=130.2.000-INT
======================================================================================================

======================================================================================================
Fetch Release Milestone files

http://10.107.138.50:8080/ebiutil/rest/rsrc/selfcare/release/file/{buildId}/{langType}

/ebiutil/rest/rsrc/selfcare/release/file/131.0.0-REL/spanish
======================================================================================================







===============================================================================================

INSTALLATION NOTES

===============================================================================================