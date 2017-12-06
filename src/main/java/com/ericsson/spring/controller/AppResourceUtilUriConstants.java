package com.ericsson.spring.controller;

public class AppResourceUtilUriConstants {

	public static final String AppRsrcAll = "/rest/rsrc/all";
	public static final String AppRsrcDirectUpdateProduction = "/rest/rsrc/directUpdateProductionRsrc";
	public static final String AppRsrcPromoteDevToProd = "/rest/rsrc/promoteDevToProd";
	public static final String AppRsrcDevUpdate = "/rest/rsrc/updateDevRsrc";
	public static final String AppRsrcFilter = "/rest/rsrc/filter";
	public static final String AppRsrcDeleteRsrcFromDev = "/rest/rsrc/deleteRsrcFromDev";
	public static final String AppRsrcFetchFiltersFromDev = "/rest/rsrc/fetchFiltersDev";
	public static final String AppRsrcGenerateFilter = "/rest/rsrc/generate";
	public static final String AppRsrcGenerateAudit = "/rest/rsrc/audit";
	public static final String AppRsrcBatchUpdate = "/rest/rsrc/batchUpdate";
	public static final String AppRsrcLogin = "/rest/rsrc/authenticate";
	public static final String AppRsrcPublish = "/rest/rsrc/publish";
	public static final String AppRsrcPublishReport = "/rest/rsrc/publishReport";
	public static final String AppRsrcDownloadPublishReport = "/rest/rsrc/downloadPublishedReport";
	public static final String AppRsrcFetchRallyFeatures = "/rest/rsrc/rallyFeatures";
	public static final String AppRsrcFetchRallyMilestone = "/rest/rsrc/rallyMilestones";
	public static final String AppRsrcFetchRallyMilestoneArtifacts = "/rest/rsrc/fetchMlStone/artifacts";
	public static final String AppRsrcFetchRallyDefects = "/rest/rsrc/rally/defects";
	public static final String AppRsrcReconcileProd = "/rest/rsrc/reconcile";
	public static final String AppRsrcSaveReconcileProd = "rest/rsrc/saveReconcileToProd";
	public static final String AppRsrcFetchPublishStatus = "rest/rsrc/fetchPublishStats";
	public static final String AppRsrcFileDiff = "/rest/rsrc/fileDiff";
	public static final String AppRsrcFetchCustomKeysList = "rest/rsrc/fetchCustomKeys";
	public static final String AppRsrcExportToExcel = "exportToexcel";
	
	public static final String AppRsrcUpdateCurrentMlStone = "rest/rsrc/milestone/update";
	
	public static final String AppRsrcFetchCurrentMlStoneFiles = "rest/rsrc/milestone/files";
	public static final String AppRsrcFetchCurrentMlStone = "rest/rsrc/milestone/current";
	
	public static final String SelfcareSWRelease = "rest/rsrc/selfcare/release";
	public static final String FetchSelfcareReleaseFiles = "rest/rsrc/selfcare/release/file/{buildId}/{langType}";
	
}
