package com.hisd3.hismk2.rest.accounting

import ar.com.fdvs.dj.domain.Style
import ar.com.fdvs.dj.domain.builders.ColumnBuilder
import ar.com.fdvs.dj.domain.constants.Border
import ar.com.fdvs.dj.domain.constants.Font
import ar.com.fdvs.dj.domain.constants.HorizontalAlign
import ar.com.fdvs.dj.domain.constants.Transparency
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn
import ar.com.fdvs.dj.domain.entities.conditionalStyle.ConditionStyleExpression
import ar.com.fdvs.dj.domain.entities.conditionalStyle.ConditionalStyle
import com.hisd3.hismk2.domain.accounting.Ledger
import com.hisd3.hismk2.domain.accounting.LineType
import com.hisd3.hismk2.graphqlservices.accounting.GeneralLedgerDtoContainer
import com.hisd3.hismk2.graphqlservices.accounting.IncomeStatementPage
import com.hisd3.hismk2.graphqlservices.accounting.ReportService
import com.hisd3.hismk2.graphqlservices.accounting.TrialBalanceDto
import com.hisd3.hismk2.rest.financialreports.IsBoldColumn
import com.hisd3.hismk2.rest.financialreports.IsBoldUnderlinedColumn
import com.hisd3.hismk2.services.EntityObjectMapperService
import com.hisd3.hismk2.services.ReportTabularGeneratorService
import com.hisd3.hismk2.utils.ReportColumnUtils
import groovy.json.JsonSlurper
import groovy.transform.Canonical
import net.sf.jasperreports.engine.type.LineStyleEnum
import org.apache.tomcat.util.buf.UDecoder
import org.apache.woden.wsdl20.Interface
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.awt.Color
import java.nio.charset.StandardCharsets
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@RestController
@RequestMapping('accounting/reports')
class ReportsResource {

    @Autowired
    ReportService reportService

    @Autowired
    ReportTabularGeneratorService reportTabularGeneratorService

    @Autowired
    EntityObjectMapperService entityObjectMapperService

    @RequestMapping(value = "/trialbalancepdf", produces = ["application/pdf"])
    ResponseEntity<byte[]> trialbalancepdf(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam String option
    ) {
        def formatter = DateTimeFormatter.ofPattern("d MMMM YYYY")
        LocalDate localDateStart = LocalDate.parse(start)
        LocalDate localDateEnd = LocalDate.parse(end)

        def decodeOption =  new JsonSlurper().parseText(option)
        def rows = 1;
        List<TrialBalanceDto> ledgerList = reportService.trialBalanceAccounts(start,end)
        List<TrialBalanceDto> trialBalanceDToList = new ArrayList<TrialBalanceDto>()

        BigDecimal debitGrandTotal = 0
        BigDecimal creditGrandTotal = 0
        BigDecimal balanceGrandTotal = 0
        ledgerList.each {
            at->

                TrialBalanceDto trialBalanceDTo = new TrialBalanceDto()
                trialBalanceDTo.account = "${at.code} - ${at.account}"
                trialBalanceDTo.bold = true
                trialBalanceDTo.boldUnderlined = false
                trialBalanceDTo.italic = false
                trialBalanceDTo.rows = rows++

                def childCodes = decodeOption[at.code]
                if(!(at.subAccounts && childCodes != null)) {
                    trialBalanceDTo.debit = at.debit
                    trialBalanceDTo.credit = at.credit
                    trialBalanceDTo.balance = at.balance
                }
                trialBalanceDToList.push(trialBalanceDTo)

                debitGrandTotal  = debitGrandTotal + at.debit
                creditGrandTotal  = creditGrandTotal + at.credit
                balanceGrandTotal  = balanceGrandTotal + at.balance

                if(at.subAccounts && childCodes != null){
                    List<TrialBalanceDto> subs = new JsonSlurper().parseText(at.subAccounts) as List<TrialBalanceDto>
                    subs.each {
                        et ->
                            Boolean exist = false;
                            if(decodeOption[at.code]){
                                decodeOption[at.code].each {
                                    ot->
                                        if((ot as String).equalsIgnoreCase(et.code)){
                                            exist = true
                                        }
                                }
                            }

                            TrialBalanceDto trialBalanceDTo1 = new TrialBalanceDto()
                            trialBalanceDTo1.account = "${et.code} - ${et.account}"
                            trialBalanceDTo1.rows = rows++

                            if(!(et.subsubAccounts && exist)){
                                trialBalanceDTo1.debit = et.debit
                                trialBalanceDTo1.credit = et.credit
                                trialBalanceDTo1.balance = et.balance
                            }

                            trialBalanceDTo1.bold = false
                            trialBalanceDTo1.italic = true
                            trialBalanceDTo1.boldUnderlined = false
                            trialBalanceDTo1.alwaysShow = false
                            trialBalanceDToList.push(trialBalanceDTo1)

                            if(et.subsubAccounts && exist){
//                        List<TrialBalanceDto> subsSubs = new JsonSlurper().parseText(et.subAccounts) as List<TrialBalanceDto>
                                (et.subsubAccounts as List<TrialBalanceDto>).each {
                                    it ->
                                        TrialBalanceDto trialBalanceDTo2 = new TrialBalanceDto()
                                        trialBalanceDTo2.account = "\u2022 ${it.code} - ${it.account}"
                                        trialBalanceDTo2.debit = BigDecimal.valueOf(Double.valueOf(it.debit))
                                        trialBalanceDTo2.credit = BigDecimal.valueOf(Double.valueOf(it.credit))
                                        trialBalanceDTo2.balance = BigDecimal.valueOf(Double.valueOf(it.balance))
                                        trialBalanceDTo2.rows = rows++

                                        trialBalanceDTo2.italic = false
                                        trialBalanceDTo2.bold = false
                                        trialBalanceDTo2.boldUnderlined = false
                                        trialBalanceDTo2.alwaysShow = false
                                        trialBalanceDToList.push(trialBalanceDTo2)
                                }

                                TrialBalanceDto trialBalanceDTo3 = new TrialBalanceDto()
                                trialBalanceDTo3.account = "TOTAL"
                                trialBalanceDTo3.debit = et.debit
                                trialBalanceDTo3.credit = et.credit
                                trialBalanceDTo3.balance = et.balance
                                trialBalanceDTo3.bold = false
                                trialBalanceDTo3.italic = true
                                trialBalanceDTo3.boldUnderlined = false
                                trialBalanceDTo3.alwaysShow = false
                                trialBalanceDTo3.rows = rows++
                                trialBalanceDToList.push(trialBalanceDTo3)

                            }


                    }

                    TrialBalanceDto trialBalanceDTo4 = new TrialBalanceDto()
                    trialBalanceDTo4.account = "TOTAL"
                    trialBalanceDTo4.debit = at.debit
                    trialBalanceDTo4.credit = at.credit
                    trialBalanceDTo4.balance = at.balance
                    trialBalanceDTo4.bold = true
                    trialBalanceDTo4.boldUnderlined = false
                    trialBalanceDTo4.alwaysShow = false
                    trialBalanceDTo4.rows = rows++
                    trialBalanceDToList.push(trialBalanceDTo4)
                }

        }

        TrialBalanceDto trialBalanceDTo5 = new TrialBalanceDto()
        trialBalanceDTo5.account = "GRAND TOTAL"
        trialBalanceDTo5.debit = debitGrandTotal
        trialBalanceDTo5.credit = creditGrandTotal
        trialBalanceDTo5.balance = balanceGrandTotal
        trialBalanceDTo5.bold = true
        trialBalanceDTo5.boldUnderlined = false
        trialBalanceDTo5.alwaysShow = false
        trialBalanceDTo5.rows = rows++
        trialBalanceDToList.push(trialBalanceDTo5)

        return reportTabularGeneratorService.generateInterfaceReport(trialBalanceDToList.toSorted{a,b -> a.rows <=> b.rows} as List<TrialBalanceDto>,"Trial Balance","As of ${localDateStart.format(formatter)} to ${localDateEnd.format(formatter)}"){ it, parameters->

            Style boldColumn = new Style()
            boldColumn.setTextColor(Color.BLACK);
            boldColumn.setFont(new Font(8,"DejaVu Sans",true,false,false))

            Style boldUnderlineColumn = new Style()
            boldUnderlineColumn.setTextColor(Color.BLACK);
            boldUnderlineColumn.setFont(new Font(8,"DejaVu Sans",true,false,true))
            boldColumn.setBorderBottom(new Border(1))
            boldColumn.setBorderTop(new Border(1))

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
            defaultColumnStyle.setPaddingLeft(40)
            //defaultColumnStyle.setHorizontalAlign(HorizontalAlign.CENTER)

            Style amountColumnStyle = new Style()
            amountColumnStyle.setTextColor(Color.BLACK);
            amountColumnStyle.setFont(new Font(8,"DejaVu Sans",false,false,false))
            amountColumnStyle.setHorizontalAlign(HorizontalAlign.RIGHT)


            Style accountColumnStyleBold = new Style()
            accountColumnStyleBold.setTextColor(Color.BLACK);
            accountColumnStyleBold.setFont(new Font(10,"DejaVu Sans",true,false,false))
            accountColumnStyleBold.setBorderBottom(new Border(1).THIN())
            accountColumnStyleBold.setBorderTop(new Border(1).THIN())
            accountColumnStyleBold.setPaddingLeft(0)

            Style accountColumnStyleItalic = new Style()
            accountColumnStyleItalic.setTextColor(Color.BLACK);
            accountColumnStyleItalic.setFont(new Font(8,"DejaVu Sans",true,true,false))
            accountColumnStyleItalic.setBorderBottom(new Border(1).THIN())
            accountColumnStyleItalic.setBorderTop(new Border(1).THIN())
            accountColumnStyleItalic.setPaddingLeft(20)

            Style amountColumnStyleBold = new Style()
            amountColumnStyleBold.setTextColor(Color.BLACK);
            amountColumnStyleBold.setFont(new Font(9,"DejaVu Sans",true,false,false))
            amountColumnStyleBold.setHorizontalAlign(HorizontalAlign.RIGHT)
            amountColumnStyleBold.setBorderBottom(new Border(1).THIN())
            amountColumnStyleBold.setBorderTop(new Border(1).THIN())
            amountColumnStyleBold.setPaddingLeft(0)

            Style amountColumnStyleItalic = new Style()
            amountColumnStyleItalic.setTextColor(Color.BLACK);
            amountColumnStyleItalic.setFont(new Font(8,"DejaVu Sans",true,true,false))
            amountColumnStyleItalic.setBorderBottom(new Border(1).THIN())
            amountColumnStyleItalic.setHorizontalAlign(HorizontalAlign.RIGHT)
            amountColumnStyleItalic.setBorderTop(new Border(1).THIN())
            amountColumnStyleItalic.setPaddingLeft(20)

            Style amountHeaderStyle = new Style()
            amountHeaderStyle.setTextColor(Color.BLACK);
            amountHeaderStyle.setFont(new Font(9,"DejaVu Sans",true,false,false))
            amountHeaderStyle.setHorizontalAlign(HorizontalAlign.RIGHT)
            amountHeaderStyle.setBorderBottom(Border.THIN())
            amountHeaderStyle.setBorderColor(Color.black)
            amountHeaderStyle.setTransparency(Transparency.OPAQUE)

            List<ConditionalStyle> conditionalStyles = []

            conditionalStyles << new ConditionalStyle(new IsBoldColumn(),boldColumn)
            conditionalStyles << new ConditionalStyle(new IsBoldUnderlinedColumn(),boldUnderlineColumn)



            AbstractColumn columnAccount = ColumnBuilder.getNew()
                    .setColumnProperty("account", String.class)
                    .setTitle("Account List")
                    .setWidth(150)
                    .addConditionalStyles([ new ConditionalStyle(new IsBoldColumn(),accountColumnStyleBold),
                                            new ConditionalStyle(new IsItalicColumn(),accountColumnStyleItalic)
                    ])
                    .build()
            it.addColumn(columnAccount)

            AbstractColumn columnDebit = ColumnBuilder.getNew()
                    .setPattern("#,##0.00;(#,##0.00)")
                    .setColumnProperty("debit", BigDecimal.class)
                    .setTitle("Debit")
                    .setWidth(50)
                    .setStyle(amountColumnStyle)
                    .setHeaderStyle(amountHeaderStyle)
                    .addConditionalStyles([ new ConditionalStyle(new IsBoldColumn(),amountColumnStyleBold),
                                            new ConditionalStyle(new IsItalicColumn(),amountColumnStyleItalic)
                    ])
                    .build()
            it.addColumn(columnDebit)

            AbstractColumn columnCredit = ColumnBuilder.getNew()
                    .setPattern("#,##0.00;(#,##0.00)")
                    .setColumnProperty("credit", BigDecimal.class)
                    .setTitle("Credit")
                    .setWidth(50)
                    .setStyle(amountColumnStyle)
                    .setHeaderStyle(amountHeaderStyle)
                    .addConditionalStyles([ new ConditionalStyle(new IsBoldColumn(),amountColumnStyleBold),
                                            new ConditionalStyle(new IsItalicColumn(),amountColumnStyleItalic)
                    ])
                    .build()
            it.addColumn(columnCredit)

            AbstractColumn columnBalance = ColumnBuilder.getNew()
                    .setPattern("#,##0.00;(#,##0.00)")
                    .setColumnProperty("balance", BigDecimal.class)
                    .setTitle("Balance")
                    .setWidth(50)
                    .setStyle(amountColumnStyle)
                    .setHeaderStyle(amountHeaderStyle)
                    .addConditionalStyles([ new ConditionalStyle(new IsBoldColumn(),amountColumnStyleBold),
                                            new ConditionalStyle(new IsItalicColumn(),amountColumnStyleItalic)
                    ])
                    .build()
            it.addColumn(columnBalance)

            it.setTemplateFile("reports/accounting/trialbalancereports.jrxml")

            it.addField("italic",Boolean.class)
            it.addField("bold",Boolean.class)
            it.addField("boldUnderlined",Boolean.class)

            it.setUseFullPageWidth(true)
            it.setDefaultStyles(null,null,headerStyle,defaultColumnStyle)//rowSeparator
            it.setWhenNoDataAllSectionNoDetail()
            it.build()
        }


    }


    @RequestMapping(value = "/incomestatementpdf", produces = ["application/pdf"])
    ResponseEntity<byte[]> incomestatementpdf(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam Integer period
    ) {
        def formatter = DateTimeFormatter.ofPattern("d MMMM YYYY")
        LocalDate localDateStart = LocalDate.parse(start)
        LocalDate localDateEnd = LocalDate.parse(end)

        def rows = 1;
        IncomeStatementPage incomeStatements = reportService.getIncomeStatements(start, end, period)


        return reportTabularGeneratorService.generateInterfaceReport(incomeStatements ,"Trial Balance","As of ${localDateStart.format(formatter)} to ${localDateEnd.format(formatter)}"){ it, parameters->

            Style boldColumn = new Style()
            boldColumn.setTextColor(Color.BLACK);
            boldColumn.setFont(new Font(8,"DejaVu Sans",true,false,false))

            Style boldUnderlineColumn = new Style()
            boldUnderlineColumn.setTextColor(Color.BLACK);
            boldUnderlineColumn.setFont(new Font(8,"DejaVu Sans",true,false,true))
            boldColumn.setBorderBottom(new Border(1))
            boldColumn.setBorderTop(new Border(1))

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
            defaultColumnStyle.setPaddingLeft(40)
            //defaultColumnStyle.setHorizontalAlign(HorizontalAlign.CENTER)

            Style amountColumnStyle = new Style()
            amountColumnStyle.setTextColor(Color.BLACK);
            amountColumnStyle.setFont(new Font(8,"DejaVu Sans",false,false,false))
            amountColumnStyle.setHorizontalAlign(HorizontalAlign.RIGHT)


            Style accountColumnStyleBold = new Style()
            accountColumnStyleBold.setTextColor(Color.BLACK);
            accountColumnStyleBold.setFont(new Font(10,"DejaVu Sans",true,false,false))
            accountColumnStyleBold.setBorderBottom(new Border(1).THIN())
            accountColumnStyleBold.setBorderTop(new Border(1).THIN())
            accountColumnStyleBold.setPaddingLeft(0)

            Style accountColumnStyleItalic = new Style()
            accountColumnStyleItalic.setTextColor(Color.BLACK);
            accountColumnStyleItalic.setFont(new Font(8,"DejaVu Sans",true,true,false))
            accountColumnStyleItalic.setBorderBottom(new Border(1).THIN())
            accountColumnStyleItalic.setBorderTop(new Border(1).THIN())
            accountColumnStyleItalic.setPaddingLeft(20)

            Style amountColumnStyleBold = new Style()
            amountColumnStyleBold.setTextColor(Color.BLACK);
            amountColumnStyleBold.setFont(new Font(9,"DejaVu Sans",true,false,false))
            amountColumnStyleBold.setHorizontalAlign(HorizontalAlign.RIGHT)
            amountColumnStyleBold.setBorderBottom(new Border(1).THIN())
            amountColumnStyleBold.setBorderTop(new Border(1).THIN())
            amountColumnStyleBold.setPaddingLeft(0)

            Style amountColumnStyleItalic = new Style()
            amountColumnStyleItalic.setTextColor(Color.BLACK);
            amountColumnStyleItalic.setFont(new Font(8,"DejaVu Sans",true,true,false))
            amountColumnStyleItalic.setBorderBottom(new Border(1).THIN())
            amountColumnStyleItalic.setHorizontalAlign(HorizontalAlign.RIGHT)
            amountColumnStyleItalic.setBorderTop(new Border(1).THIN())
            amountColumnStyleItalic.setPaddingLeft(20)

            Style amountHeaderStyle = new Style()
            amountHeaderStyle.setTextColor(Color.BLACK);
            amountHeaderStyle.setFont(new Font(9,"DejaVu Sans",true,false,false))
            amountHeaderStyle.setHorizontalAlign(HorizontalAlign.RIGHT)
            amountHeaderStyle.setBorderBottom(Border.THIN())
            amountHeaderStyle.setBorderColor(Color.black)
            amountHeaderStyle.setTransparency(Transparency.OPAQUE)

            List<ConditionalStyle> conditionalStyles = []

            conditionalStyles << new ConditionalStyle(new IsBoldColumn(),boldColumn)
            conditionalStyles << new ConditionalStyle(new IsBoldUnderlinedColumn(),boldUnderlineColumn)



            AbstractColumn columnAccount = ColumnBuilder.getNew()
                    .setColumnProperty("account", String.class)
                    .setTitle("Account List")
                    .setWidth(150)
                    .addConditionalStyles([ new ConditionalStyle(new IsBoldColumn(),accountColumnStyleBold),
                                            new ConditionalStyle(new IsItalicColumn(),accountColumnStyleItalic)
                    ])
                    .build()
            it.addColumn(columnAccount)

            AbstractColumn columnDebit = ColumnBuilder.getNew()
                    .setPattern("#,##0.00;(#,##0.00)")
                    .setColumnProperty("debit", BigDecimal.class)
                    .setTitle("Debit")
                    .setWidth(50)
                    .setStyle(amountColumnStyle)
                    .setHeaderStyle(amountHeaderStyle)
                    .addConditionalStyles([ new ConditionalStyle(new IsBoldColumn(),amountColumnStyleBold),
                                            new ConditionalStyle(new IsItalicColumn(),amountColumnStyleItalic)
                    ])
                    .build()
            it.addColumn(columnDebit)

            AbstractColumn columnCredit = ColumnBuilder.getNew()
                    .setPattern("#,##0.00;(#,##0.00)")
                    .setColumnProperty("credit", BigDecimal.class)
                    .setTitle("Credit")
                    .setWidth(50)
                    .setStyle(amountColumnStyle)
                    .setHeaderStyle(amountHeaderStyle)
                    .addConditionalStyles([ new ConditionalStyle(new IsBoldColumn(),amountColumnStyleBold),
                                            new ConditionalStyle(new IsItalicColumn(),amountColumnStyleItalic)
                    ])
                    .build()
            it.addColumn(columnCredit)

            AbstractColumn columnBalance = ColumnBuilder.getNew()
                    .setPattern("#,##0.00;(#,##0.00)")
                    .setColumnProperty("balance", BigDecimal.class)
                    .setTitle("Balance")
                    .setWidth(50)
                    .setStyle(amountColumnStyle)
                    .setHeaderStyle(amountHeaderStyle)
                    .addConditionalStyles([ new ConditionalStyle(new IsBoldColumn(),amountColumnStyleBold),
                                            new ConditionalStyle(new IsItalicColumn(),amountColumnStyleItalic)
                    ])
                    .build()
            it.addColumn(columnBalance)

            it.setTemplateFile("reports/accounting/trialbalancereports.jrxml")

            it.addField("italic",Boolean.class)
            it.addField("bold",Boolean.class)
            it.addField("boldUnderlined",Boolean.class)

            it.setUseFullPageWidth(true)
            it.setDefaultStyles(null,null,headerStyle,defaultColumnStyle)//rowSeparator
            it.setWhenNoDataAllSectionNoDetail()
            it.build()
        }


    }


    class IsItalicColumn extends ConditionStyleExpression{

        @Override
        Object evaluate(Map fields, Map variables, Map parameters) {
            Object value = fields.get("italic")
            if (value == null)
                return null

            return value == true
        }

        @Override
        String getClassName() {
            return Boolean.class.name
        }
    }
}
