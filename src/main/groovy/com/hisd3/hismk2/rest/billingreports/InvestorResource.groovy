package com.hisd3.hismk2.rest.billingreports

import com.hisd3.hismk2.domain.ancillary.IntegrationConfig
import com.hisd3.hismk2.domain.billing.InvestorAttachment
import com.hisd3.hismk2.domain.pms.FileAttachment
import com.hisd3.hismk2.repository.ancillary.IntegrationConfigRepository
import com.hisd3.hismk2.repository.billing.InvestorAttachmentRepository
import groovy.transform.TypeChecked
import jcifs.smb.NtlmPasswordAuthentication
import jcifs.smb.SmbFile
import jcifs.smb.SmbFileInputStream
import org.apache.tika.Tika
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@TypeChecked
@RestController
class InvestorResource {

    @Autowired
    InvestorAttachmentRepository investorAttachmentRepository

    @Autowired
    IntegrationConfigRepository integrationConfigRepository

//    @GetMapping("/api/investors/attachmetns/{id}/download")
//    ResponseEntity<byte[]> downloadAttachment(
//            @PathVariable(value = "id")String id
//    )throws IOException{
//
//
//
//        LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
//        extHeaders.add("Content-Disposition", "inline;filename=Ancillary-Report.csv")
//
//        return new ResponseEntity(String.valueOf(mainBuffer).getBytes(), extHeaders, HttpStatus.OK)
//    }

    @GetMapping("/api/investors/attachment/{id}")
    ResponseEntity<byte[]> getAttachment(
            @PathVariable(value = "id") String id
    ) throws IOException {

        InvestorAttachment resultPhoto = null
        investorAttachmentRepository.findById(UUID.fromString(id)).ifPresent { resultPhoto = it }

        if (resultPhoto != null) {
            if (resultPhoto.urlPath != null) {
                try {
                    //NtlmPasswordAuthentication ntlmPasswordAuthentication = new NtlmPasswordAuthentication(null, "hisd3", "xsXY4;")
                    IntegrationConfig integrationConfig = integrationConfigRepository.findAll().first()

                    NtlmPasswordAuthentication ntlmPasswordAuthentication = new NtlmPasswordAuthentication(null, integrationConfig.smbUser, integrationConfig.smbPass)

                    SmbFile attachmentFile = new SmbFile(resultPhoto.urlPath, ntlmPasswordAuthentication)

                    SmbFileInputStream fInput = new SmbFileInputStream(attachmentFile)

                    ByteArrayOutputStream buffer = new ByteArrayOutputStream()
                    int nRead

                    byte[] data = new byte[(int) attachmentFile.length()]

                    while ((nRead = fInput.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead)
                    }

                    def mime = new Tika().detect(data)

                    LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
                    extHeaders.add("Content-Type", mime)
//                    extHeaders.add("Cache-Control", "max-age=3600")
                    extHeaders.add("Content-Disposition", "inline;filename=\"attachmentFile.getName()\"")
//                    extHeaders.add("ETag", "${resultPhoto.id}${resultPhoto.createdDate.toString()}${resultPhoto.lastModifiedDate.toString()}".md5())

                    return new ResponseEntity(buffer.toByteArray(),
                            extHeaders, HttpStatus.OK)

                } catch (Exception e) {
                    throw new IOException("Problem on file server " + e)
                }

            }
        }
        throw new IOException("File not Found")
    }


    @GetMapping("/api/investors/attachment/{id}/download")
    ResponseEntity<byte[]> downloadAttachment(
            @PathVariable(value = "id") String id
    ) throws IOException {

        InvestorAttachment resultPhoto = null
        investorAttachmentRepository.findById(UUID.fromString(id)).ifPresent { resultPhoto = it }

        if (resultPhoto != null) {
            if (resultPhoto.urlPath != null) {
                try {
                    //NtlmPasswordAuthentication ntlmPasswordAuthentication = new NtlmPasswordAuthentication(null, "hisd3", "xsXY4;")
                    IntegrationConfig integrationConfig = integrationConfigRepository.findAll().first()

                    NtlmPasswordAuthentication ntlmPasswordAuthentication = new NtlmPasswordAuthentication(null, integrationConfig.smbUser, integrationConfig.smbPass)

                    SmbFile attachmentFile = new SmbFile(resultPhoto.urlPath, ntlmPasswordAuthentication)

                    SmbFileInputStream fInput = new SmbFileInputStream(attachmentFile)

                    ByteArrayOutputStream buffer = new ByteArrayOutputStream()
                    int nRead

                    byte[] data = new byte[(int) attachmentFile.length()]

                    while ((nRead = fInput.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead)
                    }

                    def mime = new Tika().detect(data)

                    LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
//                    extHeaders.add("Content-Type", "application/octet-stream")
                    extHeaders.add("Cache-Control", "max-age=3600")
                    extHeaders.add("Content-Disposition", "inline;filename=" + attachmentFile.getName())
                    extHeaders.add("ETag", "${resultPhoto.id}${resultPhoto.createdDate.toString()}${resultPhoto.lastModifiedDate.toString()}".md5())

                    return new ResponseEntity(buffer.toByteArray(),
                            extHeaders, HttpStatus.OK)

                } catch (Exception e) {
                    throw new IOException("Problem on file server " + e)
                }

            }
        }
        throw new IOException("File not Found")
    }
}
