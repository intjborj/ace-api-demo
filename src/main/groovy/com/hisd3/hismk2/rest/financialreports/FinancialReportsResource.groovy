package com.hisd3.hismk2.rest.financialreports

import ar.com.fdvs.dj.domain.Style
import ar.com.fdvs.dj.domain.builders.ColumnBuilder
import ar.com.fdvs.dj.domain.constants.Border
import ar.com.fdvs.dj.domain.constants.Font
import ar.com.fdvs.dj.domain.constants.HorizontalAlign
import ar.com.fdvs.dj.domain.constants.Transparency
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn
import ar.com.fdvs.dj.domain.entities.conditionalStyle.ConditionStyleExpression
import ar.com.fdvs.dj.domain.entities.conditionalStyle.ConditionalStyle
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.accounting.ChartOfAccount
import com.hisd3.hismk2.domain.accounting.LineType
import com.hisd3.hismk2.domain.accounting.LineTypeOperationType
import com.hisd3.hismk2.domain.accounting.SourceLineType
import com.hisd3.hismk2.domain.accounting.SourceMotherAccount
import com.hisd3.hismk2.domain.accounting.SourceSubAccount
import com.hisd3.hismk2.domain.accounting.SourceType
import com.hisd3.hismk2.domain.accounting.SourceValueType
import com.hisd3.hismk2.domain.accounting.SubAccountSetup
import com.hisd3.hismk2.domain.accounting.SubAccountType
import com.hisd3.hismk2.graphqlservices.accounting.ChartOfAccountGenerate
import com.hisd3.hismk2.graphqlservices.accounting.CoaComponentContainer
import com.hisd3.hismk2.graphqlservices.accounting.FinancialReportServices
import com.hisd3.hismk2.graphqlservices.accounting.FiscalServices
import com.hisd3.hismk2.graphqlservices.accounting.GeneraLedgerServices
import com.hisd3.hismk2.graphqlservices.accounting.GeneralLedgerDtoContainer
import com.hisd3.hismk2.graphqlservices.accounting.LedgerServices
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.ReportTabularGeneratorService
import com.hisd3.hismk2.services.requestscope.ChartofAccountGenerator
import com.hisd3.hismk2.utils.ReportColumnUtils
import com.sun.org.apache.xpath.internal.operations.Bool
import groovy.transform.Canonical
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import javax.wsdl.OperationType
import java.awt.Color
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Canonical
class FinancialReportItemData {

    String accountCode
    BigDecimal current
    BigDecimal previous
    String normalSide
    Boolean isContra = false

}

@Canonical
class FinancialReportItem implements Serializable{

    String title
    BigDecimal prevMonthValue
    BigDecimal currentMonthValue
    BigDecimal increaseDecrease

    Boolean bold

    List<FinancialReportItemData> payloadData = []

    String getCodes(){

        payloadData.collect {
             it.accountCode
        }.join(",")
    }

    SourceValueType valueType
    Boolean alwaysShow
    FinancialReportItem parent

    LineType lineType
    Boolean boldUnderlined

    UUID rowId

    BigDecimal incDecPct

    List<FinancialReportItem> getChildren( List<FinancialReportItem> items){
        def self = this
        // println(self.title)


        //List<FinancialReportItem> results = []

        return items.findAll {
            it.parent == self && it.payloadData.size() > 0
        }

       /* for(i in items){
             if(i.parent == self && i.payloadData.size() > 0)
                 results << i
        }*/


       // return results
    }



}

@RestController
@RequestMapping("/accountingreports")
class FinancialReportsResource {

    @Autowired
    ReportTabularGeneratorService reportTabularGeneratorService

    @Autowired
    FinancialReportServices financialReportServices


    @Autowired
    LedgerServices ledgerServices

    @Autowired
    ChartofAccountGenerator chartofAccountGenerator


    @Autowired
    FiscalServices fiscalServices

    def processMotherAccountType(String prefix,LineType lineType, List<FinancialReportItem> financialReportItems,
                               FinancialReportItem parent){

        if(lineType.sourceMotherAccounts){
            if(lineType.sourceMotherAccounts.size() == 1){
                SourceMotherAccount sourceMotherAccount = lineType.sourceMotherAccounts.find()

                def matchAccount = getMatchPerSourceMotherAccount(sourceMotherAccount)

                if(matchAccount.size() == 1){
                    parent.payloadData << new FinancialReportItemData(matchAccount.find().code,0.0,0.0,matchAccount.find().motherAccount.normalSide,
                    sourceMotherAccount.chartOfAccount.isContra)
                    parent.valueType = sourceMotherAccount.valueType
                }
                else {
                    matchAccount.each {


                        String motherDesc =  it.motherAccount?.description?:""
                        String subAccount =  it.subAccount?.description?:"" // or department
                        String subSubAccount =  it.subSubAccount?.description?:""


                        Set<String> descList = []
                        // reverse ni
                        if(!lineType.showMotherAccount)
                            if(motherDesc)
                                descList << motherDesc


                        if(descList.size() ==0)
                            descList << it.description


                        def item = new FinancialReportItem(prefix  + (descList.join("-")),null,null,null,false)
                        item.parent = parent
                        item.lineType = lineType
                        item.valueType = sourceMotherAccount.valueType
                        item.boldUnderlined = lineType.boldUnderlined



                        item.payloadData << new FinancialReportItemData(it.code,0.0,0.0,
                                it.motherAccount.normalSide,
                                sourceMotherAccount.chartOfAccount.isContra)
                        financialReportItems << item
                    }
                }
            }
            else {

                lineType.sourceMotherAccounts.each {

                    def srcMother = it
                    def matchAccount = getMatchPerSourceMotherAccount(it)

                    if(matchAccount.size() == 1){
                        parent.payloadData << new FinancialReportItemData(matchAccount.find().code,0.0,0.0,matchAccount.find().motherAccount.normalSide,
                                srcMother.chartOfAccount.isContra)
                        parent.valueType = srcMother.valueType
                    }
                    else {
                        matchAccount.each {


                            String motherDesc =  it.motherAccount?.description?:""
                            String subAccount =  it.subAccount?.description?:"" // or department
                            String subSubAccount =  it.subSubAccount?.description?:""


                            Set<String> descList = []
                            // reverse ni
                            if(!lineType.showMotherAccount)
                                if(motherDesc)
                                    descList << motherDesc


                            if(descList.size() ==0)
                                descList << it.description


                            def item = new FinancialReportItem(prefix  + (descList.join("-")),null,null,null,false)
                            item.parent = parent
                            item.lineType = lineType
                            item.valueType = srcMother.valueType
                            item.boldUnderlined = lineType.boldUnderlined

                            item.payloadData << new FinancialReportItemData(it.code,0.0,0.0,
                                    it.motherAccount.normalSide,
                                    srcMother.chartOfAccount.isContra)
                            financialReportItems << item
                        }
                    }

                }
            }
        }
    }

    def processSubAccountsType(String prefix,LineType lineType, List<FinancialReportItem> financialReportItems,
                               FinancialReportItem parent) {




        if(lineType.sourceSubAccountList){
            // if only 1 child
             if(lineType.sourceSubAccountList.size() == 1){
                SourceSubAccount subAccountList = lineType.sourceSubAccountList.find()
                 def matchAccount = getMatchPerSubAccount(subAccountList)

                  // if only 1 match add to parent

                 if(matchAccount.size() == 1){
                     def mAct = matchAccount.find()
                     def coaParent = chartofAccountGenerator.getMemoizedCoa().find { it.id == mAct.motherAccount.id}
                     parent.payloadData << new FinancialReportItemData(mAct.code,0.0,0.0,mAct.motherAccount.normalSide,
                             coaParent?.isContra?:false)
                     parent.valueType = subAccountList.valueType
                 }
                 else {

                     matchAccount.each {

                         String motherDesc =  it.motherAccount?.description?:""
                         String subAccount =  it.subAccount?.description?:"" // or department
                         String subSubAccount =  it.subSubAccount?.description?:""


                         Set<String> descList = []
                         // reverse ni
                         if(!lineType.showMotherAccount)
                             if(motherDesc)
                                 descList << motherDesc

                         if(!lineType.showDepartment)
                             if(subAccount)
                                 descList << subAccount

                         if(!lineType.showSubSub)
                             if(subSubAccount)
                                 descList << subSubAccount

                         if(descList.size() ==0)
                             descList << it.description


                         def item = new FinancialReportItem(prefix  + (descList.join("-")),null,null,null,false)
                         item.valueType = subAccountList.valueType
                         item.lineType = lineType
                         item.parent = parent
                         item.boldUnderlined = lineType.boldUnderlined


                         def mAct = it
                         def coaParent = chartofAccountGenerator.getMemoizedCoa().find { it.id == mAct.motherAccount.id}

                         item.payloadData << new FinancialReportItemData(it.code,0.0,0.0,it.motherAccount.normalSide,
                         coaParent?.isContra?:false)
                         financialReportItems << item
                     }
                 }

             }
            else {

                 lineType.sourceSubAccountList.each {

                     def srcSub = it
                     def matchAccount = getMatchPerSubAccount(it)


                     matchAccount.each {
                         String motherDesc =  it.motherAccount?.description?:""
                         String subAccount =  it.subAccount?.description?:"" // or department
                         String subSubAccount =  it.subSubAccount?.description?:""

                          Set<String> descList = []
                         // reverse ni
                         if(!lineType.showMotherAccount)
                              if(motherDesc)
                                  descList << motherDesc

                         if(!lineType.showDepartment)
                             if(subAccount)
                                 descList << subAccount

                         if(!lineType.showSubSub)
                             if(subSubAccount)
                                 descList << subSubAccount

                         if(descList.size() ==0)
                             descList << it.description


                         def item = new FinancialReportItem(prefix  + (descList.join("-")),null,null,null,false)
                           item.valueType = srcSub.valueType
                           item.parent = parent
                           item.lineType = lineType
                           item.boldUnderlined = lineType.boldUnderlined


                          def mAct = it
                          def coaParent = chartofAccountGenerator.getMemoizedCoa().find { it.id == mAct.motherAccount.id}


                         item.payloadData << new FinancialReportItemData(it.code,0.0,0.0,it.motherAccount.normalSide,
                                 coaParent?.isContra?:false)
                          financialReportItems << item
                     }

                 }

             }
        }


    }

    def traverseFs(LineType lineType,List<FinancialReportItem> children,int level){


        if(lineType.sourceType != SourceType.HIDDEN)
        {
            def parentLine=  new FinancialReportItem(("     " * (level -1)) + lineType.caption,null,null,null,
                lineType.bold)
            parentLine.lineType = lineType

            parentLine.boldUnderlined = lineType.boldUnderlined

            //parentLine.alwaysShow = true
            children << parentLine

            if(lineType.sourceType in [SourceType.LINE_TYPE,SourceType.SUB_ACCOUNT,SourceType.MOTHER_ACCOUNT]){

                if(lineType.sourceType == SourceType.LINE_TYPE){
                    parentLine.alwaysShow = true
                }

                if(lineType.sourceType == SourceType.SUB_ACCOUNT){

                    processSubAccountsType(("     " * ((level -1)+1)),lineType,children,parentLine)
                }
                if(lineType.sourceType == SourceType.MOTHER_ACCOUNT){

                    processMotherAccountType(("     " * ((level -1)+1)),lineType,children,parentLine)
                }


                lineType.children.each {
                    // println(it.caption)
                    traverseFs(  it,children, level + 1)
                }
            }
            else {
                parentLine.alwaysShow = true

                lineType.children.each {
                    // println(it.caption)
                    traverseFs(  it,children, level + 1)
                }
            }

        }





    }


    def fillUpValues(List<FinancialReportItem> items,GeneralLedgerDtoContainer currentPreviousLedger,
                     GeneralLedgerDtoContainer currentMonthLedger ){


        items.each {fsItem->

         SourceValueType sourceValueType = fsItem.valueType
            fsItem.payloadData.each { payload->

                if(currentMonthLedger){


                    String accountCode = payload.accountCode
                    String normalSide = payload.normalSide

                   def matchValue = currentMonthLedger.payload.find { it.code == accountCode  }
              //      println(matchValue?.code + ":" + accountCode + ":" + sourceValueType?.name())

                    if(matchValue){
                        if(sourceValueType == SourceValueType.BALANCE){
                             if(normalSide == "DEBIT")
                                 {
                                     payload.current =  matchValue.endingDebit
                                     if(payload.current > 0)
                                         if(fsItem.parent)
                                            fsItem.parent.alwaysShow=true

                                     if(payload.isContra){
                                         payload.current *= -1
                                     }
                                 }
                            else
                                 {
                                     payload.current =  matchValue.endingCredit
                                     if(payload.current > 0)
                                         if(fsItem.parent)
                                             fsItem.parent.alwaysShow=true
                                     if(payload.isContra){
                                         payload.current *= -1
                                     }

                                 }

                        }

                        if(sourceValueType == SourceValueType.PERIODIC_BALANCE){
                            if(normalSide == "DEBIT")
                            {
                                payload.current =  matchValue.periodicDebit - matchValue.periodicCredit
                                if(payload.current > 0)
                                    if(fsItem.parent)
                                        fsItem.parent.alwaysShow=true

                                if(payload.isContra){
                                    payload.current *= -1
                                }
                            }
                            else
                            {
                                payload.current =  matchValue.periodicCredit - matchValue.periodicDebit
                                if(payload.current > 0)
                                    if(fsItem.parent)
                                        fsItem.parent.alwaysShow=true
                                if(payload.isContra){
                                    payload.current *= -1
                                }

                            }
                        }
                    }

                }

                if(currentPreviousLedger){

                    String accountCode = payload.accountCode
                    String normalSide = payload.normalSide

                    def matchValue = currentPreviousLedger.payload.find { it.code == accountCode  }

                    if(matchValue){
                        if(sourceValueType == SourceValueType.BALANCE){
                            if(normalSide == "DEBIT")
                            {
                                payload.previous =  matchValue.endingDebit

                                if(payload.previous > 0)
                                    if(fsItem.parent)
                                        fsItem.parent.alwaysShow=true
                                if(payload.isContra){
                                    payload.previous *= -1
                                }

                            }
                            else
                            {
                                payload.previous =  matchValue.endingCredit
                                if(payload.previous > 0)
                                    if(fsItem.parent)
                                        fsItem.parent.alwaysShow=true
                                if(payload.isContra){
                                    payload.previous *= -1
                                }
                            }

                        }

                        if(sourceValueType == SourceValueType.PERIODIC_BALANCE){
                            if(normalSide == "DEBIT")
                            {
                                payload.previous =   matchValue.periodicDebit - matchValue.periodicCredit

                                if(payload.previous > 0)
                                    if(fsItem.parent)
                                        fsItem.parent.alwaysShow=true
                                if(payload.isContra){
                                    payload.previous *= -1
                                }

                            }
                            else
                            {
                                payload.previous = matchValue.periodicCredit - matchValue.periodicDebit
                                if(payload.previous > 0)
                                    if(fsItem.parent)
                                        fsItem.parent.alwaysShow=true
                                if(payload.isContra){
                                    payload.previous *= -1
                                }
                            }

                        }
                    }

                }
            }



            def totalCurrent = 0.0
            def totalPrevious = 0.0


            fsItem.payloadData.each{

                 totalCurrent += it.current?:0.0
                 totalPrevious += it.previous?:0.0
            }




            fsItem.currentMonthValue = totalCurrent
            fsItem.prevMonthValue = totalPrevious
            fsItem.increaseDecrease = totalCurrent - totalPrevious
        }






    }


    BigDecimal processLineTypesChildrenCurrent(List<FinancialReportItem> items,LineType targetLineType,Boolean previous= false
    ){

        BigDecimal totals = 0.0

        // 1st get the totals of all sourceMotherAccounts
        if(targetLineType.sourceType == SourceType.MOTHER_ACCOUNT){
            items.findAll {
                it?.lineType?.id == targetLineType.id
            }.each {

                if(!previous)
                    totals += it.currentMonthValue
                else
                    totals += it.prevMonthValue

               /* it.payloadData.each {frid->
                    if(!previous)
                        totals +=  frid.current?:0.0
                    else
                        totals +=  frid.previous?:0.0
                }*/
            }

        }
        if(targetLineType.sourceType == SourceType.SUB_ACCOUNT){
            // 1st get the totals of all sourceSubAccounts

            items.findAll {
                (it?.parent?.lineType?.id?:it?.lineType?.id) == targetLineType.id
            }.each {

                if(!previous)
                    totals += it.currentMonthValue?:0.0
                else
                    totals += it.prevMonthValue?:0.0


                /* it.payloadData.each {frid->
                     if(!previous)
                         totals +=  frid.current?:0.0
                     else
                         totals +=  frid.previous?:0.0
                 }*/

            //    println("====== " + it.title + " : " +  new DecimalFormat("#,##0.00").format(totals))
            }

        }

        // There is also Fixed Chiled
        if(targetLineType.sourceType == SourceType.FIXED_VALUE && targetLineType.fixedValue > 0){
            totals +=  targetLineType?.fixedValue?:0.0
        }


        if(targetLineType.sourceType == SourceType.LINE_TYPE){
            items.findAll {
                (it?.parent?.lineType?.id?:it?.lineType?.id) == targetLineType.id
            }.each {
                if(!previous)
                    totals += it.currentMonthValue
                else
                    totals += it.prevMonthValue
            }
        }

        targetLineType.children.each {
            totals +=processLineTypesChildrenCurrent(items,it,previous)
        }



        return totals
    }


    def processLineTypes(List<FinancialReportItem> items){


      items.findAll {
              it?.lineType?.sourceType == SourceType.LINE_TYPE
        }.each {

          Map<BigDecimal, LineTypeOperationType> totalsPerLineTypeOperationCurrent = [:]
          Map<BigDecimal, LineTypeOperationType> totalsPerLineTypeOperationPrevious = [:]

          FinancialReportItem lineTypeCurrent = it
          // ==========================================================
           it.lineType.sourceLineTypes.each {sourceLineType->

               def operationCurrent = sourceLineType.operationType
               BigDecimal totalPerLineTypeCurrent = 0.0
               def operationPrevious = sourceLineType.operationType
               BigDecimal totalPerLineTypePrevious = 0.0

               def lineTypeToCheck = sourceLineType.lineType
               // get total of self
               totalPerLineTypeCurrent += processLineTypesChildrenCurrent(items,lineTypeToCheck)
               totalPerLineTypePrevious += processLineTypesChildrenCurrent(items,lineTypeToCheck,true)


               totalsPerLineTypeOperationCurrent.put(totalPerLineTypeCurrent,operationCurrent)
               totalsPerLineTypeOperationPrevious.put(totalPerLineTypePrevious,operationPrevious)
           }

          def totalCurrent = 0.0
          def totalPrevious = 0.0

          // Now Process Values

           // ADDITION
          totalsPerLineTypeOperationCurrent.each { k,v->
              if(v == LineTypeOperationType.ADD){
                  totalCurrent += k
              }

              if(v == LineTypeOperationType.SUBTRACT){
                  totalCurrent -= k
              }

              if(v == LineTypeOperationType.MULTIPLICATION){
                  totalCurrent =  (totalCurrent * k).setScale(2, RoundingMode.HALF_EVEN)
              }

              if(v == LineTypeOperationType.DIVISION){
                  totalCurrent =  (totalCurrent / k).setScale(2, RoundingMode.HALF_EVEN)
              }


          }

          totalsPerLineTypeOperationPrevious.each { k,v->
              if(v == LineTypeOperationType.ADD){
                  totalPrevious += k
              }

              if(v == LineTypeOperationType.SUBTRACT){
                  totalPrevious -= k
              }

              if(v == LineTypeOperationType.MULTIPLICATION){
                  totalPrevious =  (totalPrevious * k).setScale(2, RoundingMode.HALF_EVEN)
              }

              if(v == LineTypeOperationType.DIVISION){
                  totalPrevious =  (totalPrevious / k).setScale(2, RoundingMode.HALF_EVEN)
              }

          }

          lineTypeCurrent.currentMonthValue = totalCurrent
          lineTypeCurrent.prevMonthValue = totalPrevious
          lineTypeCurrent.increaseDecrease = totalCurrent - totalPrevious

           if(lineTypeCurrent.lineType.sourceType == SourceType.LINE_TYPE){
               if(lineTypeCurrent.currentMonthValue == 0 &&
                       lineTypeCurrent.prevMonthValue == 0
               )
                   lineTypeCurrent.alwaysShow = false
           }
          // =========================================================
        }

    }

    def mergeItems(List<FinancialReportItem> items){


        items.each {
            it.rowId = UUID.randomUUID()
        }


        List<FinancialReportItem>  forRemoval = []
        Map<UUID,List<FinancialReportItem>> newChildrenRowIdMap = [:]
        items.eachWithIndex { it, index ->

            def children = it.getChildren(items)

            if(children.size() > 0)
            {

                if(!newChildrenRowIdMap.containsKey(it.rowId))
                    newChildrenRowIdMap[it.rowId] = []



                Set<String> uniqueTitle = []

                // make sure to not merge other children at this point that has children
                def childrenForProcess = children.findAll {it.getChildren(items).size() == 0 }

                childrenForProcess.each {
                    uniqueTitle << it.title
                }

                uniqueTitle.each {un->

                    def matches = childrenForProcess.findAll { it.title  == un}
                    def firstMatch = matches.find()
                    FinancialReportItem newItem = new FinancialReportItem()
                    newItem.title = firstMatch.title
                    newItem.bold = firstMatch.bold
                    newItem.valueType = firstMatch.valueType
                    newItem.alwaysShow = firstMatch.alwaysShow
                    newItem.parent = firstMatch.parent
                    newItem.lineType = firstMatch.lineType
                    newItem.boldUnderlined =firstMatch.boldUnderlined


                    def totalCurrent = 0.0
                    def totalPrevious = 0.0

                    matches.each {
                        forRemoval  << it
                        it.payloadData.each {
                            totalCurrent += it.current
                            totalPrevious += it.previous
                            newItem.payloadData.add(it)
                        }
                    }


                    newItem.prevMonthValue = totalPrevious
                    newItem.currentMonthValue = totalCurrent
                    newItem.increaseDecrease = totalCurrent - totalPrevious
                    newChildrenRowIdMap[it.rowId] << newItem
                }
            }
        }

        items.removeAll(forRemoval)
        // insert new Items at index


        newChildrenRowIdMap.each { k, v->
            def indexRowId = items.findIndexOf {
                it.rowId == k
            }



            def parent = items.find {
                it.rowId == k
            }

            if(parent.lineType.showMotherAccount && parent.lineType.showDepartment && parent.lineType.showSubSub){
                def totalCurrent = 0.0
                def totalPrevious = 0.0


                parent.payloadData.clear()
                v.each {

                    it.payloadData.each {
                        totalCurrent += it.current
                        totalPrevious += it.previous
                        parent.payloadData.add(it)
                    }
                }

                parent.prevMonthValue = totalPrevious
                parent.currentMonthValue = totalCurrent
                parent.increaseDecrease = totalCurrent - totalPrevious

            }
            else {
                items.addAll(indexRowId +1 ,v)
            }

        }




    }

    @RequestMapping(value = "/printFs", produces = ["application/pdf"])
    ResponseEntity<byte[]> printFs(
            @RequestParam UUID financialReportId,
            @RequestParam(required = false) UUID departmentId,
            @RequestParam UUID fiscalId,
            @RequestParam Integer month
    ) {


        def fiscal = fiscalServices.findOne(fiscalId)
        LocalDateTime currentMonth = LocalDateTime.of(fiscal.toDate.getYear() ,month,1,0,0,0,0)

        LocalDateTime currentMonthLastDay = currentMonth.withDayOfMonth(currentMonth.month.length(currentMonth.toLocalDate().isLeapYear()))
        LocalDateTime previousMonth = null
        def financialReport = financialReportServices.findOne(financialReportId)
         if(month > 1 && financialReport.comparePrevMonth)
             previousMonth = LocalDateTime.of(fiscal.toDate.getYear() ,month-1,1,0,0,0,0)


        GeneralLedgerDtoContainer currentPreviousLedger  =  (month>1 &&  financialReport.comparePrevMonth) ?
                ledgerServices.getGeneralLedger(fiscalId,"","","","","",month-1):
                null

        GeneralLedgerDtoContainer currentMonthLedger  =   ledgerServices.getGeneralLedger(fiscalId,"","","","","",month)






        List<FinancialReportItem> items = []

        List<LineType> sortedParentType  = financialReport.lineTypes
                .findAll {it.parentLineType == null  }
                .toSorted { a,b ->
             a.orderLine <=> b.orderLine
         }

        sortedParentType.each {
            traverseFs(it,items,1)
        }


        fillUpValues(items,currentPreviousLedger,currentMonthLedger)
        // Remove no Values

        def forProcess = financialReport.showAll ? items :  items.findAll {
            if (it.alwaysShow) {

                if (!(it.prevMonthValue > 0 || it.currentMonthValue > 0 || it.increaseDecrease > 0)) {
                    it.currentMonthValue = null
                    it.prevMonthValue = null
                    it.increaseDecrease = null
                }

                return true
            }

            if(it?.valueType !=null){
                return (it.prevMonthValue || it.currentMonthValue || it.increaseDecrease)

            }

            return false


        }
        mergeItems(forProcess)
        processLineTypes(forProcess)

      /*  def onlyShowWithValue = financialReport.showAll ? forProcess :  forProcess.findAll {


            if(it.alwaysShow){

                if(!(it.prevMonthValue >0  || it.currentMonthValue> 0 || it.increaseDecrease> 0 )){
                    it.currentMonthValue = null
                    it.prevMonthValue = null
                    it.increaseDecrease = null
                }


                return true
            }



            if(it?.valueType !=null){
                return (it.prevMonthValue || it.currentMonthValue || it.increaseDecrease)

            }




                return false
        }*/


        String subTitle = ""

        if(financialReport.periodic){

            subTitle="For the period ended ${currentMonthLastDay.month.name()}, ${currentMonthLastDay.dayOfMonth} ${currentMonthLastDay.year}"
        }
        else {
            subTitle="As of ${currentMonthLastDay.month.name()}, ${currentMonthLastDay.dayOfMonth} ${currentMonthLastDay.year}"
        }

        return  reportTabularGeneratorService.generateReport(forProcess.collect{

            if(currentPreviousLedger){
                 if(it.prevMonthValue ==0){
                     it.incDecPct = 100
                 }
                else {
                     if(it.increaseDecrease)
                     it.incDecPct = (it.increaseDecrease / it.prevMonthValue) * 100
                 }


            }

            it
        },financialReport.title,subTitle ){ it,parameters->


            Style boldColumn = new Style()
            boldColumn.setTextColor(Color.BLACK);
            boldColumn.setFont(new Font(8,"DejaVu Sans",true,false,false))

            Style boldUnderlineColumn = new Style()
            boldUnderlineColumn.setTextColor(Color.BLACK);
            boldUnderlineColumn.setFont(new Font(14,"DejaVu Sans",true,false,true))



            Style headerStyle = new Style()
            Style amountStyle = new Style()
            amountStyle.setHorizontalAlign(HorizontalAlign.LEFT)

            Style rowSeparator = new Style()
            rowSeparator.setBorderBottom(Border.DASHED())
            rowSeparator.setBorderColor(Color.black)


          //  headerStyle.setBackgroundColor(Color.LIGHT_GRAY)
            headerStyle.setBorderBottom(Border.THIN())
            headerStyle.setBorderColor(Color.black)
            headerStyle.setHorizontalAlign(HorizontalAlign.LEFT)
            headerStyle.setTransparency(Transparency.OPAQUE)
            headerStyle.setFont(new Font(10,"DejaVu Sans",true,false,false))
             Style defaultColumnStyle = new Style()
            defaultColumnStyle.setTextColor(Color.BLACK);
            defaultColumnStyle.setFont(new Font(8,"DejaVu Sans",false,false,false))
            //defaultColumnStyle.setHorizontalAlign(HorizontalAlign.CENTER)

            Style amountColumnStyle = new Style()
            amountColumnStyle.setTextColor(Color.BLACK);
            amountColumnStyle.setFont(new Font(8,"DejaVu Sans",false,false,false))
            amountColumnStyle.setHorizontalAlign(HorizontalAlign.RIGHT)

            Style amountColumnStyleBold = new Style()
            amountColumnStyleBold.setTextColor(Color.BLACK);
            amountColumnStyleBold.setFont(new Font(14,"DejaVu Sans",true,false,true))
            amountColumnStyleBold.setHorizontalAlign(HorizontalAlign.RIGHT)
            Style amountColumnStyleNormalBold = new Style()
            amountColumnStyleNormalBold.setTextColor(Color.BLACK);
            amountColumnStyleNormalBold.setFont(new Font(8,"DejaVu Sans",true,false,false))
            amountColumnStyleNormalBold.setHorizontalAlign(HorizontalAlign.RIGHT)

            Style amountHeaderStyle = new Style()
            amountHeaderStyle.setTextColor(Color.BLACK);
            amountHeaderStyle.setFont(new Font(10,"DejaVu Sans",true,false,false))
            amountHeaderStyle.setHorizontalAlign(HorizontalAlign.RIGHT)
            amountHeaderStyle.setBorderBottom(Border.THIN())
            amountHeaderStyle.setBorderColor(Color.black)
            amountHeaderStyle.setTransparency(Transparency.OPAQUE)

            List<ConditionalStyle> conditionalStyles = []

            conditionalStyles << new ConditionalStyle(new IsBoldColumn(),boldColumn)
            conditionalStyles << new ConditionalStyle(new IsBoldUnderlinedColumn(),boldUnderlineColumn)


            AbstractColumn columnTitle = ColumnBuilder.getNew()
                    .setColumnProperty("title", String.class)
                    .setTitle("Account Title")
                    .setWidth(500)
                    .addConditionalStyles(conditionalStyles)
                    .build()
            it.addColumn(columnTitle)


          /*  AbstractColumn codesTitle = ColumnBuilder.getNew()
                    .setColumnProperty("codes", String.class)
                    .setTitle("Codes Debug")
                    .setWidth(100)
                    .addConditionalStyles(conditionalStyles)
                    .build()
            it.addColumn(codesTitle)*/



              AbstractColumn columnCurrentMonthValue = ColumnBuilder.getNew()
                      .setColumnProperty("currentMonthValue", BigDecimal.class)
                      .setPattern("#,##0.00;(#,##0.00)")
                      .setTitle(currentMonth.format(DateTimeFormatter.ofPattern("MMMM")))
                      .setWidth(150)
                      .setStyle(amountColumnStyle)
                      .setHeaderStyle(amountHeaderStyle)
                      .addConditionalStyles([ new ConditionalStyle(new IsBoldUnderlinedColumn(),amountColumnStyleBold),
                                              new ConditionalStyle(new IsBoldColumn(),amountColumnStyleNormalBold)
                      ])
                      .build()
              it.addColumn(columnCurrentMonthValue)



            if(currentPreviousLedger){
                AbstractColumn columnPrevMonthValue = ColumnBuilder.getNew()
                        .setColumnProperty("prevMonthValue", BigDecimal.class)
                        .setPattern("#,##0.00;(#,##0.00)")
                        .setTitle(previousMonth.format(DateTimeFormatter.ofPattern("MMMM")))
                        .setWidth(150)
                        .setStyle(amountColumnStyle)
                        .setHeaderStyle(amountHeaderStyle)
                        .addConditionalStyles([ new ConditionalStyle(new IsBoldUnderlinedColumn(),amountColumnStyleBold),
                                                new ConditionalStyle(new IsBoldColumn(),amountColumnStyleNormalBold)
                        ])
                        .build()
                it.addColumn(columnPrevMonthValue)

                AbstractColumn columnIncreaseDecrease = ColumnBuilder.getNew()
                        .setColumnProperty("increaseDecrease", BigDecimal.class)
                        .setPattern("#,##0.00;(#,##0.00)")
                        .setTitle("Inc/Dec")
                        .setWidth(150)
                        .setStyle(amountColumnStyle)
                        .setHeaderStyle(amountHeaderStyle)
                        .addConditionalStyles([ new ConditionalStyle(new IsBoldUnderlinedColumn(),amountColumnStyleBold),
                                                new ConditionalStyle(new IsBoldColumn(),amountColumnStyleNormalBold)
                        ])
                        .build()
                it.addColumn(columnIncreaseDecrease)

              /*  AbstractColumn incDecPct = ColumnBuilder.getNew()
                        .setColumnProperty("incDecPct", BigDecimal.class)
                        .setPattern("#,##0.00;(#,##0.00)")
                        .setTitle("%")
                        .setWidth(100)
                        .setStyle(amountColumnStyle)
                        .setHeaderStyle(amountHeaderStyle)
                        .addConditionalStyles([ new ConditionalStyle(new IsBoldUnderlinedColumn(),amountColumnStyleBold),
                                                new ConditionalStyle(new IsBoldColumn(),amountColumnStyleNormalBold)
                        ])
                        .build()
                it.addColumn(incDecPct)

*/


                def pctcol = ReportColumnUtils.createColumnToString(
                        BigDecimal.class,
                        "%",
                        "incDecPct",
                        100
                ) {
                    value, fields, variables, pmtrs ->
                        if (value) {
                            return  new DecimalFormat("#,##0.00").format(value) + " %"
                        }
                        return ""
                }
                pctcol.setStyle(amountColumnStyle)
                pctcol.setHeaderStyle(amountHeaderStyle)
              /*  pctcol.addConditionalStyles([ new ConditionalStyle(new IsBoldUnderlinedColumn(),amountColumnStyleBold),
                                                new ConditionalStyle(new IsBoldColumn(),amountColumnStyleNormalBold)
                        ])*/
                it.addColumn( pctcol)



                it.setTemplateFile("reports/accounting/financialreports_landscape.jrxml")
            }
            else{
                it.setTemplateFile("reports/accounting/financialreports.jrxml")

            }








            it.addField("bold",Boolean.class)
            it.addField("boldUnderlined",Boolean.class)





            it.setUseFullPageWidth(true)
            it.setDefaultStyles(null,null,headerStyle,defaultColumnStyle)//rowSeparator
            it.setWhenNoDataAllSectionNoDetail()
            it.build()
        }

    }

    // use to get Mother Accounts which has no child/subaccount setup
    List<ChartOfAccountGenerate> getMatchPerSourceMotherAccount(SourceMotherAccount sourceMotherAccount){
        ChartOfAccount chartOfAccount =  sourceMotherAccount.chartOfAccount


        def coaList = chartofAccountGenerator.getAllChartOfAccountGenerate("","","","","")


        return  coaList.findAll { gen ->
            boolean filter = true


            if(gen.motherAccount.code ==  chartOfAccount.accountCode){
                if(gen.subAccount || gen.subSubAccount){
                    filter = false
                }
            }
            else {
                filter = false
            }

            return  filter
        }

    }
    List<ChartOfAccountGenerate> getMatchPerSubAccount(SourceSubAccount sourceSubAccount) {

        def coaList = chartofAccountGenerator.getAllChartOfAccountGenerate("", "", "", "", "")


        SubAccountSetup setup = sourceSubAccount.subaccount

        Set<String> allowedMotherCodes = []

        setup.motherAccounts.each { it ->
            String accountCode = it.chartOfAccount.accountCode
            def m = sourceSubAccount.excludes.find {
                it.motherAccount.accountCode == accountCode
            }
            if (!m)
                allowedMotherCodes << accountCode
        }





            def forSorting = coaList.findAll { gen ->
                boolean filter = true

                String motherAccountCode = gen.motherAccount.code

                if (!allowedMotherCodes.contains(motherAccountCode))
                    filter = false


                CoaComponentContainer subAccount = gen.subAccount
                CoaComponentContainer subSubAccount = gen.subSubAccount
                if (!subAccount)
                    filter = false

                if (setup.subaccountType == SubAccountType.OTHERENTITIES) {
                    String hasDomain = setup.sourceDomain

                    if (!hasDomain) {
                        // subAccountOnly


                        if (setup.subaccountParent) {
                            if (subSubAccount == null)
                                filter = false

                            if (!StringUtils.equalsIgnoreCase(subAccount?.code, setup.subaccountParent?.subaccountCode))
                                filter = false

                            if (StringUtils.equalsIgnoreCase(subSubAccount?.domain, SubAccountSetup.class.name)) {
                                if (!StringUtils.equalsIgnoreCase(subSubAccount?.code, setup.subaccountCode))
                                    filter = false
                            } else
                                filter = false
                        } else {
                            // println("${subAccount.domain} ${subAccount.code} ${subAccountCode}")
                            if (StringUtils.equalsIgnoreCase(subAccount?.domain, SubAccountSetup.class.name)) {
                                if (!StringUtils.equalsIgnoreCase(subAccount?.code, setup.subaccountCode))
                                    filter = false
                            } else
                                filter = false
                        }


                    } else {


                        if (setup.subaccountParent) {
                            // domain entities with parents
                            if (subSubAccount == null)
                                filter = false

                            if (!StringUtils.equalsIgnoreCase(subAccount?.code, setup.subaccountParent?.subaccountCode))
                                filter = false


                            if (!StringUtils.equalsIgnoreCase(subSubAccount?.domain, hasDomain)) {
                                filter = false
                            }
                         }
                           else if (setup.includeDepartment) {
                                if (subAccount) {
                                    if (!StringUtils.equalsIgnoreCase(subAccount.domain, Department.class.name)) {
                                        filter = false
                                    }
                                }
                           }
                            else {
                            if (subAccount) {
                                if (!StringUtils.equalsIgnoreCase(subAccount.domain, hasDomain)) {
                                    filter = false
                                }
                            }
                        }


                    }

                } else {
                    //==============================Non Entities==============================================
                    SubAccountSetup parent = setup.subaccountParent

                    if (parent) {
                        if (subSubAccount) {
                            if (StringUtils.equalsIgnoreCase(subSubAccount.domain, SubAccountSetup.class.name)) {
                                if (!StringUtils.equalsIgnoreCase(subSubAccount.code, setup.subaccountCode))
                                    filter = false
                            } else {
                                filter = false
                            }
                        } else {
                            filter = false
                        }
                    } else if (setup.includeDepartment) {


                        if (subSubAccount) {

                            if (StringUtils.equalsIgnoreCase(subSubAccount.domain, SubAccountSetup.class.name)) {

                                if (!StringUtils.equalsIgnoreCase(subSubAccount.code, setup.subaccountCode))
                                    filter = false

                            } else {
                                filter = false
                            }
                        } else {
                            filter = false
                        }
                    } else {
                        if (subAccount) {

                            if (StringUtils.equalsIgnoreCase(subAccount.domain, SubAccountSetup.class.name)) {


                                if (!StringUtils.equalsIgnoreCase(subAccount.code, setup.subaccountCode)) {
                                    filter = false

                                }
                            } else {
                                filter = false
                            }
                        }
                    }

                }


                return filter

            }


      return  forSorting.toSorted {a,b ->

            Set<String> leftSide = []
            Set<String> rightSide = []

            if(a.subSubAccount)
            leftSide << a.subSubAccount.code

            if(a.subAccount)
                leftSide << a.subAccount.code


            if(a.motherAccount)
                leftSide << a.motherAccount.code


            if(b.subSubAccount)
                rightSide << b.subSubAccount.code

            if(b.subAccount)
                rightSide << b.subAccount.code

            if(b.motherAccount)
                rightSide << b.motherAccount.code

            return leftSide.join("-") <=> rightSide.join("-")

        }

    }




}



class IsBoldColumn extends ConditionStyleExpression{

    @Override
    Object evaluate(Map fields, Map variables, Map parameters) {
        Object value = fields.get("bold")
        if (value == null)
            return null

        return value == true
    }

    @Override
    String getClassName() {
        return Boolean.class.name
    }
}

class IsBoldUnderlinedColumn extends ConditionStyleExpression{

    @Override
    Object evaluate(Map fields, Map variables, Map parameters) {
        Object value = fields.get("boldUnderlined")
        if (value == null)
            return null

        return value == true
    }

    @Override
    String getClassName() {
        return Boolean.class.name
    }
}