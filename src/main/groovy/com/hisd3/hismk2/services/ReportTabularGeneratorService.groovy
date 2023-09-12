package com.hisd3.hismk2.services

import ar.com.fdvs.dj.core.DynamicJasperHelper
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager
import ar.com.fdvs.dj.domain.DynamicReport
import ar.com.fdvs.dj.domain.Style
import ar.com.fdvs.dj.domain.builders.FastReportBuilder
import ar.com.fdvs.dj.domain.constants.Border
import ar.com.fdvs.dj.domain.constants.HorizontalAlign
import ar.com.fdvs.dj.domain.constants.Transparency
import com.hisd3.hismk2.graphqlservices.accounting.TrialBalanceDto
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import net.sf.jasperreports.engine.JRDataSource
import net.sf.jasperreports.engine.JRException
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.export.SimpleExporterInput
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput
import net.sf.jasperreports.export.SimplePdfExporterConfiguration
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap

import java.awt.Color
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


interface  ReportFiller{
    DynamicReport fill(FastReportBuilder fsb,Map<String,Object> parameters)
}


@Service
class ReportTabularGeneratorService {

    @Autowired
    HospitalConfigService hospitalConfigService

    @Autowired
    ApplicationContext applicationContext




    ResponseEntity<byte []> generateReport(
            List<Serializable> dataSource,
            String reportName,
            String subTitle,
            ReportFiller filler
    ){

        def logo = applicationContext?.getResource("classpath:/reports/logo.png")


        Style headerStyle = new Style()

        headerStyle.setBackgroundColor(Color.LIGHT_GRAY)
        headerStyle.setBorderBottom(Border.THIN())
        headerStyle.setBorderColor(Color.black)
        headerStyle.setHorizontalAlign(HorizontalAlign.LEFT)
        headerStyle.setTransparency(Transparency.OPAQUE)


        Map<String,Object> parameters = [:]

        def hospitalInfo = hospitalConfigService.hospitalInfo
        parameters.put("logo", logo?.inputStream)
        parameters["hospitalname"] = hospitalInfo.hospitalName
        parameters["reportTitle"] = reportName
        parameters["subTitle"] = subTitle
        parameters["dateprinted"] = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"))

        def fulladdress = (hospitalInfo?.address ?: "") + " " +
                (hospitalInfo?.addressLine2 ?: "") + "\n" +
                (hospitalInfo?.city ?: "") + " " +

                (hospitalInfo?.zip ?: "") + " " +
                (hospitalInfo?.country ?: "")


        parameters["hospitalfulladdress"] = fulladdress
        parameters.put("contactline",
                "Contact No: " + (hospitalInfo?.telNo ?: "No hospital contact") + " " +
                        "Email: " + (hospitalInfo?.email ?: "No hospital email")
        )


        FastReportBuilder drb = new FastReportBuilder()
        JRDataSource ds =  new JRBeanCollectionDataSource(dataSource)
        def os = new ByteArrayOutputStream()
        try {
            JasperPrint jrprint = DynamicJasperHelper.generateJasperPrint(filler.fill(drb,parameters), new ClassicLayoutManager(), ds,parameters)
            def pdfExporter = new JRPdfExporter()

            def outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(os)

            pdfExporter.setExporterInput(new SimpleExporterInput(jrprint))
            pdfExporter.setExporterOutput(outputStreamExporterOutput)
            def configuration = new SimplePdfExporterConfiguration()
            pdfExporter.setConfiguration(configuration)
            pdfExporter.exportReport()

        } catch (JRException e) {
            e.printStackTrace()
        } catch (IOException e) {
            e.printStackTrace()
        }

        def data = os.toByteArray()
        String fileName = StringUtils.replace(reportName," ","").toLowerCase()
        def params = new LinkedMultiValueMap<String, String>()
        String fname = "inline;filename=${fileName}.pdf"
        params.add("Content-Disposition",fname )
        return new ResponseEntity(data, params, HttpStatus.OK)

    }


    ResponseEntity<byte []> generateInterfaceReport(
            List<TrialBalanceDto> dataSource,
            String reportName,
            String subTitle,
            ReportFiller filler
    ){

        def logo = applicationContext?.getResource("classpath:/reports/logo.png")


        Style headerStyle = new Style()

        headerStyle.setBackgroundColor(Color.LIGHT_GRAY)
        headerStyle.setBorderBottom(Border.THIN())
        headerStyle.setBorderColor(Color.black)
        headerStyle.setHorizontalAlign(HorizontalAlign.LEFT)
        headerStyle.setTransparency(Transparency.OPAQUE)


        Map<String,Object> parameters = [:]

        def hospitalInfo = hospitalConfigService.hospitalInfo
        parameters.put("logo", logo?.inputStream)
        parameters["hospitalname"] = hospitalInfo.hospitalName
        parameters["reportTitle"] = reportName
        parameters["subTitle"] = subTitle
        parameters["dateprinted"] = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"))

        def fulladdress = (hospitalInfo?.address ?: "") + " " +
                (hospitalInfo?.addressLine2 ?: "") + "\n" +
                (hospitalInfo?.city ?: "") + " " +

                (hospitalInfo?.zip ?: "") + " " +
                (hospitalInfo?.country ?: "")


        parameters["hospitalfulladdress"] = fulladdress
        parameters.put("contactline",
                "Contact No: " + (hospitalInfo?.telNo ?: "No hospital contact") + " " +
                        "Email: " + (hospitalInfo?.email ?: "No hospital email")
        )


        FastReportBuilder drb = new FastReportBuilder()
        JRDataSource ds =  new JRBeanCollectionDataSource(dataSource)
        def os = new ByteArrayOutputStream()
        try {
            JasperPrint jrprint = DynamicJasperHelper.generateJasperPrint(filler.fill(drb,parameters), new ClassicLayoutManager(), ds,parameters)
            def pdfExporter = new JRPdfExporter()

            def outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(os)

            pdfExporter.setExporterInput(new SimpleExporterInput(jrprint))
            pdfExporter.setExporterOutput(outputStreamExporterOutput)
            def configuration = new SimplePdfExporterConfiguration()
            pdfExporter.setConfiguration(configuration)
            pdfExporter.exportReport()

        } catch (JRException e) {
            e.printStackTrace()
        } catch (IOException e) {
            e.printStackTrace()
        }

        def data = os.toByteArray()
        String fileName = StringUtils.replace(reportName," ","").toLowerCase()
        def params = new LinkedMultiValueMap<String, String>()
        String fname = "inline;filename=${fileName}.pdf"
        params.add("Content-Disposition",fname )
        return new ResponseEntity(data, params, HttpStatus.OK)

    }

    ResponseEntity<byte []> generateTrialReport(
            List<TrialBalanceDto> dataSource,
            String reportName,
            String subTitle,
            ReportFiller filler
    ){

        def logo = applicationContext?.getResource("classpath:/reports/logo.png")


        Style headerStyle = new Style()

        headerStyle.setBackgroundColor(Color.LIGHT_GRAY)
        headerStyle.setBorderBottom(Border.THIN())
        headerStyle.setBorderColor(Color.black)
        headerStyle.setHorizontalAlign(HorizontalAlign.LEFT)
        headerStyle.setTransparency(Transparency.OPAQUE)


        Map<String,Object> parameters = [:]

        def hospitalInfo = hospitalConfigService.hospitalInfo
        parameters.put("logo", logo?.inputStream)
        parameters["hospitalname"] = hospitalInfo.hospitalName
        parameters["reportTitle"] = reportName
        parameters["subTitle"] = subTitle
        parameters["dateprinted"] = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"))

        def fulladdress = (hospitalInfo?.address ?: "") + " " +
                (hospitalInfo?.addressLine2 ?: "") + "\n" +
                (hospitalInfo?.city ?: "") + " " +

                (hospitalInfo?.zip ?: "") + " " +
                (hospitalInfo?.country ?: "")


        parameters["hospitalfulladdress"] = fulladdress
        parameters.put("contactline",
                "Contact No: " + (hospitalInfo?.telNo ?: "No hospital contact") + " " +
                        "Email: " + (hospitalInfo?.email ?: "No hospital email")
        )


        FastReportBuilder drb = new FastReportBuilder()
        JRDataSource ds =  new JRBeanCollectionDataSource(dataSource)
        def os = new ByteArrayOutputStream()
        try {
            JasperPrint jrprint = DynamicJasperHelper.generateJasperPrint(filler.fill(drb,parameters), new ClassicLayoutManager(), ds,parameters)
            def pdfExporter = new JRPdfExporter()

            def outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(os)

            pdfExporter.setExporterInput(new SimpleExporterInput(jrprint))
            pdfExporter.setExporterOutput(outputStreamExporterOutput)
            def configuration = new SimplePdfExporterConfiguration()
            pdfExporter.setConfiguration(configuration)
            pdfExporter.exportReport()

        } catch (JRException e) {
            e.printStackTrace()
        } catch (IOException e) {
            e.printStackTrace()
        }

        def data = os.toByteArray()
        String fileName = StringUtils.replace(reportName," ","").toLowerCase()
        def params = new LinkedMultiValueMap<String, String>()
        String fname = "inline;filename=${fileName}.pdf"
        params.add("Content-Disposition",fname )
        return new ResponseEntity(data, params, HttpStatus.OK)

    }
}
