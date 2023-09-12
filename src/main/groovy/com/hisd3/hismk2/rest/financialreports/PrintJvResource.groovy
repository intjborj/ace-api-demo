package com.hisd3.hismk2.rest.financialreports

import ar.com.fdvs.dj.domain.Style
import ar.com.fdvs.dj.domain.builders.ColumnBuilder
import ar.com.fdvs.dj.domain.constants.Border
import ar.com.fdvs.dj.domain.constants.Font
import ar.com.fdvs.dj.domain.constants.HorizontalAlign
import ar.com.fdvs.dj.domain.constants.Transparency
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn
import ar.com.fdvs.dj.domain.entities.conditionalStyle.ConditionalStyle
import com.hisd3.hismk2.domain.accounting.HeaderLedger
import com.hisd3.hismk2.graphqlservices.accounting.LedgerServices
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.ReportTabularGeneratorService
import groovy.transform.Canonical
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import java.awt.Color
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Canonical
class PrintJVItem implements Serializable{

    String code
    String title

    BigDecimal debit // totals
    BigDecimal credit // totals
    Boolean bold

}


@RestController
@RequestMapping("/accountingreports")
class PrintJvResource {

    @Autowired
    ReportTabularGeneratorService reportTabularGeneratorService

    @Autowired
    LedgerServices ledgerServices

    @Autowired
    UserRepository userRepository

    @Autowired
    EmployeeRepository employeeRepository


    @RequestMapping(value = "/printJv", produces = ["application/pdf"])
    ResponseEntity<byte[]> printJv(
            @RequestParam UUID ledgerHeaderId
    ) {


        def header = ledgerServices.findOne(ledgerHeaderId)


        List<PrintJVItem> items = []
        BigDecimal totalDebit= 0.0
        BigDecimal totalCredit = 0.0

         header.ledger.each {
             items << new PrintJVItem(it.journalAccount.code,it.journalAccount.description,it.debit,it.credit)
             totalDebit += it.debit
             totalCredit += it.credit
         }
        items << new PrintJVItem("","Totals",totalDebit,totalCredit,true)


        return  reportTabularGeneratorService.generateReport(items,header.journalType.name(),"" ){it , parameters ->


           // def username = SecurityUtils.currentLogin()
            def user = userRepository.findOneByLogin(header.createdBy)
            def preparedBy = employeeRepository.findOneByUser(user)

            parameters.put("datetime",header.transactionDate.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")))
            parameters.put("entity",header.entityName)
            parameters.put("transaction",header.particulars)
            parameters.put("docType",header.docType.name())
            parameters.put("docnum",header.docnum)
            parameters.put("preparedby","")
            parameters.put("approveby","")

            if(preparedBy)
            parameters.put("preparedby",preparedBy?.fullName)



            if(StringUtils.isNotBlank(header.approvedBy)) {

                def usera = userRepository.findOneByLogin(header.approvedBy)
                def approvedBy = employeeRepository.findOneByUser(usera)

                if(approvedBy)
                    parameters.put("approveby",approvedBy?.fullName)
            }



            Style boldColumn = new Style()
            boldColumn.setTextColor(Color.BLACK);
            boldColumn.setFont(new Font(10,"DejaVu Sans",true,false,false))
            boldColumn.setHorizontalAlign(HorizontalAlign.RIGHT)

            Style defaultColumnStyle = new Style()
            defaultColumnStyle.setTextColor(Color.BLACK);
            defaultColumnStyle.setFont(new Font(8,"DejaVu Sans",false,false,false))




            Style headerStyle = new Style()
            Style amountStyle = new Style()
            amountStyle.setHorizontalAlign(HorizontalAlign.RIGHT)
            amountStyle.setFont(new Font(8,"DejaVu Sans",true,false,false))
            Style headerStyleAmount = new Style()

            Style rowSeparator = new Style()
            rowSeparator.setBorderBottom(Border.DASHED())
            rowSeparator.setBorderColor(Color.black)


            //  headerStyle.setBackgroundColor(Color.LIGHT_GRAY)
            headerStyle.setBorderBottom(Border.THIN())
            headerStyle.setBorderColor(Color.black)
            headerStyle.setHorizontalAlign(HorizontalAlign.LEFT)
            headerStyle.setTransparency(Transparency.OPAQUE)


            headerStyleAmount.setBorderBottom(Border.THIN())
            headerStyleAmount.setBorderColor(Color.black)
            headerStyleAmount.setHorizontalAlign(HorizontalAlign.RIGHT)
            headerStyleAmount.setTransparency(Transparency.OPAQUE)


            List<ConditionalStyle> conditionalStyles = []

            conditionalStyles << new ConditionalStyle(new IsBoldColumn(),boldColumn)


            AbstractColumn columnTitle = ColumnBuilder.getNew()
                    .setColumnProperty("code", String.class)
                    .setTitle("Code")
                    .setWidth(60)
                    .build()
            it.addColumn(columnTitle)


            AbstractColumn columnPrevMonthValue = ColumnBuilder.getNew()
                    .setColumnProperty("title", String.class)
                    .setTitle("Title")
                    .setWidth(150)
                    .addConditionalStyles(conditionalStyles)
                    .build()
            it.addColumn(columnPrevMonthValue)

            AbstractColumn columnCurrentMonthValue = ColumnBuilder.getNew()
                    .setPattern("#,##0.00;(-#,##0.00)")
                    .setColumnProperty("debit", BigDecimal.class)
                    .setTitle("Debit")
                    .setWidth(35)
                    .setStyle(amountStyle)
                    .setHeaderStyle(headerStyleAmount)
                    .addConditionalStyles(conditionalStyles)
                    .build()
            it.addColumn(columnCurrentMonthValue)


            AbstractColumn columnIncreaseDecrease = ColumnBuilder.getNew()
                    .setPattern("#,##0.00;(-#,##0.00)")
                    .setColumnProperty("credit", BigDecimal.class)
                    .setTitle("Credit")
                    .setWidth(35)
                    .setStyle(amountStyle)
                    .setHeaderStyle(headerStyleAmount)
                    .addConditionalStyles(conditionalStyles)
                    .build()
            it.addColumn(columnIncreaseDecrease)

            it.addField("bold",Boolean.class)
            it.setTemplateFile("reports/accounting/printjv.jrxml")
            it.setUseFullPageWidth(true)
            it.setDefaultStyles(null,null,headerStyle,defaultColumnStyle)//rowSeparator
            it.setWhenNoDataAllSectionNoDetail()
            it.build()
        }

    }


}
