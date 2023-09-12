package com.hisd3.hismk2.graphqlservices.billing

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.ancillary.IntegrationConfig
import com.hisd3.hismk2.domain.billing.Investor
import com.hisd3.hismk2.domain.billing.InvestorAttachment
import com.hisd3.hismk2.domain.billing.InvestorDependent
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.ancillary.IntegrationConfigRepository
import com.hisd3.hismk2.repository.billing.InvestorAttachmentRepository
import com.hisd3.hismk2.repository.billing.InvestorDependentRepository
import com.hisd3.hismk2.repository.billing.InvestorsRepository
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import jcifs.smb.NtlmPasswordAuthentication
import jcifs.smb.SmbFile
import jcifs.smb.SmbFileOutputStream
import org.apache.commons.compress.compressors.FileNameUtil
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang3.StringUtils
import org.hibernate.annotations.QueryHints
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Component
@GraphQLApi
class InvestorAttachmentService {

    @Autowired
    InvestorsRepository investorsRepository

    @Autowired
    InvestorDependentRepository investorDependentRepository

    @Autowired
    InvestorAttachmentRepository investorAttachmentRepository

    @Autowired
    IntegrationConfigRepository integrationConfigRepository

    @Autowired
    ObjectMapper objectMapper

    @PersistenceContext
    EntityManager entityManager

    @GraphQLQuery
    InvestorAttachment getOneInvestorAttachments(
            @GraphQLArgument(name = "id") UUID id
    ) {
        InvestorAttachment attachment = null
        if (!id) throw new Exception("Investor Attachment id must not be null.")
        investorAttachmentRepository.findOneAttachment(id).ifPresent { attachment = it }
        if (!attachment) throw new Exception("No attachment found!")
        return attachment
    }

    @GraphQLQuery
    List<InvestorAttachment> getInvestorAttachments(
            @GraphQLArgument(name = "investor") UUID investor,
            @GraphQLArgument(name = "dependent") UUID dependent,
            @GraphQLArgument(name = "type") String type,
            @GraphQLArgument(name = "withHidden") Boolean withHidden
    ) {
        if (type) {
            if (type.equalsIgnoreCase("ALL")) {
                String query = """
                    Select distinct a from InvestorAttachment a
                    left join fetch a.investor i
                    left join fetch a.dependent d 
                    where i.id = :investor
                    and a.hide = :withHidden
                    order by a.createdDate desc
                """

                def attachmentQuery = entityManager
                        .createQuery(query, InvestorAttachment.class)
                        .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
                        .setParameter("investor", investor)
                        .setParameter("withHidden", withHidden)

                return attachmentQuery.resultList
            } else return null
        } else {
            String query = """
                Select distinct a from InvestorAttachment a
                left join fetch a.investor i
                left join fetch a.dependent d 
                where i.id = :investor and a.hide = :withHidden
            """
            if (dependent) {
                query += " and d.id = :dependent"
            } else {
                query += " and d is null"
            }
            query += " order by a.createdDate desc"

            def attachmentQuery = entityManager
                    .createQuery(query, InvestorAttachment.class)
                    .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
                    .setParameter("investor", investor)
                    .setParameter("withHidden", withHidden)
            if (dependent)
                attachmentQuery.setParameter("dependent", dependent)

            return attachmentQuery.resultList
        }
    }

    @GraphQLMutation
    GraphQLRetVal<InvestorAttachment> hideInvestorAttachment(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "hide") Boolean hide
    ) {
        String message = hide ? "hidden" : "unhidden"
        if (!id) return new GraphQLRetVal<InvestorAttachment>(null, false, "Failed to ${message} investor attachment.")
        InvestorAttachment attachment = null
        investorAttachmentRepository.findById(id).ifPresent { attachment = it }
        if (!attachment) return new GraphQLRetVal<InvestorAttachment>(null, false, "Failed to ${message} investor attachment.")
        attachment.hide = hide
        investorAttachmentRepository.save(attachment)
        return new GraphQLRetVal<InvestorAttachment>(attachment, true, "Successfully ${message} investor attachment.")
    }

    @GraphQLMutation
    @Transactional
    GraphQLRetVal<InvestorAttachment> addInvestorAttachment(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "investorId") UUID investorId,
            @GraphQLArgument(name = "dependentId") UUID dependentId,
            @GraphQLArgument(name = "fields") Map<String, Object> fields,
            @GraphQLArgument(name = "request") MultipartFile request
    ) {
        if (id) {
            InvestorAttachment investorAttachment = null
            investorAttachmentRepository.findById(id).ifPresent { investorAttachment = it }
            if (!investorAttachment || (!investorId && dependentId)) return new GraphQLRetVal<InvestorAttachment>(null, false, "Failed to update investor attachment.")

            Investor investor = null
            InvestorDependent dependent = null

            investorsRepository.findById(investorId).ifPresent { investor = it }
            if (dependentId)
                investorDependentRepository.findById(dependentId).ifPresent { dependent = it }

            investorAttachment = objectMapper.updateValue(investorAttachment, fields)
            investorAttachment.investor = investor
            investorAttachment.dependent = dependent
            investorAttachmentRepository.save(investorAttachment)

            return new GraphQLRetVal<InvestorAttachment>(investorAttachment, true, "Successfully updated investor attachment.")
        } else {
            if (!investorId && dependentId) return new GraphQLRetVal<InvestorAttachment>(null, false, "Failed to upload investor attachment.")

            Investor investor = null
            InvestorDependent dependent = null

            investorsRepository.findById(investorId).ifPresent { investor = it }
            if (dependentId)
                investorDependentRepository.findById(dependentId).ifPresent { dependent = it }

            if (dependentId && !dependent) return new GraphQLRetVal<InvestorAttachment>(null, false, "Failed to upload investor attachment.")

            InvestorAttachment newAttachment = objectMapper.convertValue(fields, InvestorAttachment.class)
            try {
                newAttachment.investor = investor
                newAttachment.dependent = dependent
                newAttachment.filename = request.originalFilename
                newAttachment.mimetype = request.contentType
                try {
                    /*** ready for NAS***/
                    String origin = request.resource.filename
                    String extension = FilenameUtils.getExtension(origin)
                    String idfname = newAttachment.filename
                    String filenameWithoutExtension = FilenameUtils.removeExtension(idfname)
                    byte[] byteData = request.getBytes()
                    newAttachment.urlPath = uploadAttachments(newAttachment, byteData, filenameWithoutExtension, extension)
                    investorAttachmentRepository.save(newAttachment)
                } catch (Exception e) {
                    e.printStackTrace()
                    throw e
                }
            } catch (Exception e) {
                e.printStackTrace()
                throw e
            }
            return new GraphQLRetVal<InvestorAttachment>(newAttachment, true, "Successfully uploaded investor attachment.")
        }
    }

    String uploadAttachments(InvestorAttachment investorAttachment, byte[] byteData, String fname, String extension) {
        String url = null
        try {

            IntegrationConfig integrationConfig = integrationConfigRepository.findAll().first()
            NtlmPasswordAuthentication ntlmPasswordAuthentication = new NtlmPasswordAuthentication(null, integrationConfig.smbUser, integrationConfig.smbPass)

            def shared = integrationConfig.nasLocation

//            String pFolder = patientCase.patient.patientNo.toString() + "/"
            String pFolder = null
            if (investorAttachment.investor) {
                pFolder = "investors/" + investorAttachment.investor.id.toString()
            } else if (investorAttachment.dependent) {
                pFolder = "investors/${investorAttachment.dependent.investor.id.toString()}"
            }

            String finalName = StringUtils.trim(fname)
            SmbFile sFile = new SmbFile(shared + pFolder, ntlmPasswordAuthentication)
            SmbFile investorsFolder = new SmbFile(shared + "investors", ntlmPasswordAuthentication)

            //create /investors folder
            if (folderCreator(investorsFolder)) {
                //create /investors/${investorNo} folder
                if (folderCreator(sFile)) {
                    if (investorAttachment.dependent) {
                        SmbFile investorDependentFolder = new SmbFile(shared + pFolder + "/dependents/", ntlmPasswordAuthentication)
                        // create /investors/${investorNo}/dependents
                        pFolder += "/dependents/"
                        if (folderCreator(investorDependentFolder)) {
                            // create /investors/${investorNo}/dependents/${dependentNo}/ folder
                            SmbFile dependentFolder = new SmbFile(shared + pFolder + "${investorAttachment.dependent.id.toString()}/", ntlmPasswordAuthentication)
                            pFolder += "${investorAttachment.dependent.id.toString()}/"
                            if (folderCreator(dependentFolder)) {
                                SmbFile dependentAttachments = new SmbFile(shared + pFolder + "attachments/", ntlmPasswordAuthentication)
                                // create /investors/${investorNo}/dependents/${dependentNo}/attachment folder
                                if (folderCreator(dependentAttachments)) {
                                    pFolder += "attachments/"
                                }
                            }
                        }
                    } else if (investorAttachment.investor) {
                        // create /investors/${investorNo}/attachments folder
                        SmbFile sFile1 = new SmbFile(shared + pFolder + "/attachments/", ntlmPasswordAuthentication)
                        if (folderCreator(sFile1)) {
                            pFolder += "/attachments/"
                        }
                    }
                }
            }

            url = shared + pFolder + finalName + ".${extension}"
            SmbFile sFileFinal = new SmbFile(url, ntlmPasswordAuthentication)
            Integer count = 0
            while (sFileFinal.exists()) {
                count++
                url = "${shared}${pFolder}${finalName} (${count}).${extension}"
                sFileFinal = new SmbFile(url, ntlmPasswordAuthentication)
            }

            SmbFileOutputStream sfos = new SmbFileOutputStream(sFileFinal)
            sfos.write(byteData)
            sfos.flush()
            sfos.close()

        } catch (Exception e) {
            e.printStackTrace()
            throw e
        }

        return url
    }

    Boolean folderCreator(SmbFile sFile) {

        try {
            if (!sFile.exists()) {
                sFile.mkdir()
            }
        } catch (IOException e) {
            //throw IllegalArgumentException(e.message)
            e.printStackTrace()
        }
        return true
    }

    @GraphQLMutation
    GraphQLRetVal<InvestorAttachment> deleteInvestorAttachment(
            @GraphQLArgument(name = "id") UUID id
    ) {
        if (!id) return new GraphQLRetVal<InvestorAttachment>(null, false, "Failed to delete investor attachment.")

        InvestorAttachment attachment = null
        investorAttachmentRepository.findById(id).ifPresent { attachment = it }

        if (!attachment) return new GraphQLRetVal<InvestorAttachment>(null, false, "Failed to delete investor attachment.")
        investorAttachmentRepository.delete(attachment)

        return new GraphQLRetVal<InvestorAttachment>(attachment, true, "Successfully delete investor attachment.")
    }
}
