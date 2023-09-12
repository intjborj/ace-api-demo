/**
 * Online_Health_Facility_Statistical_Report_SystemCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.9  Built on : Nov 16, 2018 (12:05:37 GMT)
 */
package com.hisd3.hismk2.phic.wsdl;


/**
 *  Online_Health_Facility_Statistical_Report_SystemCallbackHandler Callback class, Users can extend this class and implement
 *  their own receiveResult and receiveError methods.
 */
public abstract class Online_Health_Facility_Statistical_Report_SystemCallbackHandler {
    protected Object clientData;

    /**
     * User can pass in any object that needs to be accessed once the NonBlocking
     * Web service call is finished and appropriate method of this CallBack is called.
     * @param clientData Object mechanism by which the user can pass in user data
     * that will be avilable at the time this callback is called.
     */
    public Online_Health_Facility_Statistical_Report_SystemCallbackHandler(
        Object clientData) {
        this.clientData = clientData;
    }

    /**
     * Please use this constructor if you don't want to set any clientData
     */
    public Online_Health_Facility_Statistical_Report_SystemCallbackHandler() {
        this.clientData = null;
    }

    /**
     * Get the client data
     */
    public Object getClientData() {
        return clientData;
    }

    /**
     * auto generated Axis2 call back method for hospOptDischargesMorbidity method
     * override this method for handling normal response from hospOptDischargesMorbidity operation
     */
    public void receiveResulthospOptDischargesMorbidity(
        ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesMorbidityResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from hospOptDischargesMorbidity operation
     */
    public void receiveErrorhospOptDischargesMorbidity(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for hospOptDischargesNumberDeliveries method
     * override this method for handling normal response from hospOptDischargesNumberDeliveries operation
     */
    public void receiveResulthospOptDischargesNumberDeliveries(
        ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesNumberDeliveriesResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from hospOptDischargesNumberDeliveries operation
     */
    public void receiveErrorhospOptDischargesNumberDeliveries(
        java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for hospitalOperationsMajorOpt method
     * override this method for handling normal response from hospitalOperationsMajorOpt operation
     */
    public void receiveResulthospitalOperationsMajorOpt(
        ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsMajorOptResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from hospitalOperationsMajorOpt operation
     */
    public void receiveErrorhospitalOperationsMajorOpt(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for hospitalOperationsMinorOpt method
     * override this method for handling normal response from hospitalOperationsMinorOpt operation
     */
    public void receiveResulthospitalOperationsMinorOpt(
        ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsMinorOptResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from hospitalOperationsMinorOpt operation
     */
    public void receiveErrorhospitalOperationsMinorOpt(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for hospitalOperationsDeaths method
     * override this method for handling normal response from hospitalOperationsDeaths operation
     */
    public void receiveResulthospitalOperationsDeaths(
        ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsDeathsResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from hospitalOperationsDeaths operation
     */
    public void receiveErrorhospitalOperationsDeaths(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for genInfoQualityManagement method
     * override this method for handling normal response from genInfoQualityManagement operation
     */
    public void receiveResultgenInfoQualityManagement(
        ph.gov.doh.uhmistrn.ahsr.webservice.index.GenInfoQualityManagementResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from genInfoQualityManagement operation
     */
    public void receiveErrorgenInfoQualityManagement(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for hospOptDischargesSpecialty method
     * override this method for handling normal response from hospOptDischargesSpecialty operation
     */
    public void receiveResulthospOptDischargesSpecialty(
        ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesSpecialtyResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from hospOptDischargesSpecialty operation
     */
    public void receiveErrorhospOptDischargesSpecialty(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for authenticationTest method
     * override this method for handling normal response from authenticationTest operation
     */
    public void receiveResultauthenticationTest(
        ph.gov.doh.uhmistrn.ahsr.webservice.index.AuthenticationTestResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from authenticationTest operation
     */
    public void receiveErrorauthenticationTest(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for hospOptSummaryOfPatients method
     * override this method for handling normal response from hospOptSummaryOfPatients operation
     */
    public void receiveResulthospOptSummaryOfPatients(
        ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptSummaryOfPatientsResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from hospOptSummaryOfPatients operation
     */
    public void receiveErrorhospOptSummaryOfPatients(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for expenses method
     * override this method for handling normal response from expenses operation
     */
    public void receiveResultexpenses(
        ph.gov.doh.uhmistrn.ahsr.webservice.index.ExpensesResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from expenses operation
     */
    public void receiveErrorexpenses(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for hospOptDischargesOPD method
     * override this method for handling normal response from hospOptDischargesOPD operation
     */
    public void receiveResulthospOptDischargesOPD(
        ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesOPDResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from hospOptDischargesOPD operation
     */
    public void receiveErrorhospOptDischargesOPD(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for hospOptDischargesSpecialtyOthers method
     * override this method for handling normal response from hospOptDischargesSpecialtyOthers operation
     */
    public void receiveResulthospOptDischargesSpecialtyOthers(
        ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesSpecialtyOthersResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from hospOptDischargesSpecialtyOthers operation
     */
    public void receiveErrorhospOptDischargesSpecialtyOthers(
        java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for hospitalOperationsMortalityDeaths method
     * override this method for handling normal response from hospitalOperationsMortalityDeaths operation
     */
    public void receiveResulthospitalOperationsMortalityDeaths(
        ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsMortalityDeathsResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from hospitalOperationsMortalityDeaths operation
     */
    public void receiveErrorhospitalOperationsMortalityDeaths(
        java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for submittedReports method
     * override this method for handling normal response from submittedReports operation
     */
    public void receiveResultsubmittedReports(
        ph.gov.doh.uhmistrn.ahsr.webservice.index.SubmittedReportsResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from submittedReports operation
     */
    public void receiveErrorsubmittedReports(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for hospOptDischargesTesting method
     * override this method for handling normal response from hospOptDischargesTesting operation
     */
    public void receiveResulthospOptDischargesTesting(
        ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesTestingResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from hospOptDischargesTesting operation
     */
    public void receiveErrorhospOptDischargesTesting(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for hospOptDischargesEV method
     * override this method for handling normal response from hospOptDischargesEV operation
     */
    public void receiveResulthospOptDischargesEV(
        ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesEVResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from hospOptDischargesEV operation
     */
    public void receiveErrorhospOptDischargesEV(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for staffingPatternOthers method
     * override this method for handling normal response from staffingPatternOthers operation
     */
    public void receiveResultstaffingPatternOthers(
        ph.gov.doh.uhmistrn.ahsr.webservice.index.StaffingPatternOthersResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from staffingPatternOthers operation
     */
    public void receiveErrorstaffingPatternOthers(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for getDataTable method
     * override this method for handling normal response from getDataTable operation
     */
    public void receiveResultgetDataTable(
        ph.gov.doh.uhmistrn.ahsr.webservice.index.GetDataTableResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getDataTable operation
     */
    public void receiveErrorgetDataTable(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for hospOptDischargesER method
     * override this method for handling normal response from hospOptDischargesER operation
     */
    public void receiveResulthospOptDischargesER(
        ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesERResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from hospOptDischargesER operation
     */
    public void receiveErrorhospOptDischargesER(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for revenues method
     * override this method for handling normal response from revenues operation
     */
    public void receiveResultrevenues(
        ph.gov.doh.uhmistrn.ahsr.webservice.index.RevenuesResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from revenues operation
     */
    public void receiveErrorrevenues(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for genInfoBedCapacity method
     * override this method for handling normal response from genInfoBedCapacity operation
     */
    public void receiveResultgenInfoBedCapacity(
        ph.gov.doh.uhmistrn.ahsr.webservice.index.GenInfoBedCapacityResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from genInfoBedCapacity operation
     */
    public void receiveErrorgenInfoBedCapacity(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for createNEHEHRSVaccount method
     * override this method for handling normal response from createNEHEHRSVaccount operation
     */
    public void receiveResultcreateNEHEHRSVaccount(
        ph.gov.doh.uhmistrn.ahsr.webservice.index.CreateNEHEHRSVaccountResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from createNEHEHRSVaccount operation
     */
    public void receiveErrorcreateNEHEHRSVaccount(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for genInfoClassification method
     * override this method for handling normal response from genInfoClassification operation
     */
    public void receiveResultgenInfoClassification(
        ph.gov.doh.uhmistrn.ahsr.webservice.index.GenInfoClassificationResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from genInfoClassification operation
     */
    public void receiveErrorgenInfoClassification(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for hospitalOperationsHAI method
     * override this method for handling normal response from hospitalOperationsHAI operation
     */
    public void receiveResulthospitalOperationsHAI(
        ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsHAIResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from hospitalOperationsHAI operation
     */
    public void receiveErrorhospitalOperationsHAI(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for hospOptDischargesOPV method
     * override this method for handling normal response from hospOptDischargesOPV operation
     */
    public void receiveResulthospOptDischargesOPV(
        ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesOPVResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from hospOptDischargesOPV operation
     */
    public void receiveErrorhospOptDischargesOPV(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for staffingPattern method
     * override this method for handling normal response from staffingPattern operation
     */
    public void receiveResultstaffingPattern(
        ph.gov.doh.uhmistrn.ahsr.webservice.index.StaffingPatternResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from staffingPattern operation
     */
    public void receiveErrorstaffingPattern(java.lang.Exception e) {
    }
}
