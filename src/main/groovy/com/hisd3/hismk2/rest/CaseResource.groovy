package com.hisd3.hismk2.rest

import com.hisd3.hismk2.domain.ancillary.DiagnosticResult
import com.hisd3.hismk2.domain.ancillary.IntegrationConfig
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.FileAttachment
import com.hisd3.hismk2.domain.pms.Patient
import com.hisd3.hismk2.repository.ancillary.DiagnosticsResultRepository
import com.hisd3.hismk2.repository.ancillary.IntegrationConfigRepository
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.repository.pms.FileAttachmentRepository
import com.hisd3.hismk2.repository.pms.PatientRepository
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import groovy.transform.TypeChecked
import jcifs.smb.NtlmPasswordAuthentication
import jcifs.smb.SmbFile
import jcifs.smb.SmbFileInputStream
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartRequest

@TypeChecked
@RestController
class CaseResource {
	
	@Autowired
	CaseRepository caseRepository

	@Autowired
    PatientRepository patientRepository

    @Autowired
    GeneratorService generatorService
	
	@Autowired
	FileAttachmentRepository fileAttachmentRepository
	
	@Autowired
	IntegrationConfigRepository integrationConfigRepository
	
	@Autowired
	DiagnosticsResultRepository diagnosticsResultRepository
	
	@Autowired
	OrderslipResource orderSlipResource
	
	@Transactional
	@RequestMapping(method = RequestMethod.POST, value = "/api/patient/addCasePhotos")
	ResponseEntity<String> addCasePhotos(@RequestParam String id, MultipartRequest request) {
		
		Case activeCase = caseRepository.findById(UUID.fromString(id)).get()
		Map files = request.getFileMap()
		
		try {
			files.values().forEach {
				file ->
					MultipartFile f = file
					FileAttachment newAttachment = new FileAttachment()
					newAttachment.patient = activeCase.patient
					newAttachment.patientCase = activeCase
					newAttachment.fileName = f.originalFilename
					newAttachment.mimetype = f.contentType
					newAttachment.desc = "Case photo"
					try {
						/*** ready for NAS***/
						String origin = f.resource.filename
						String extension = FilenameUtils.getExtension(origin)
						String fileName = newAttachment.fileName + "." + extension
						byte[] byteData = f.getBytes()
						newAttachment.urlPath = orderSlipResource.uploadPhotos(activeCase, byteData, fileName)
						fileAttachmentRepository.save(newAttachment)
					} catch (Exception e) {
						e.printStackTrace()
						throw e
					}
			}
		} catch (Exception ignored) {
			return new ResponseEntity<>(
					"Upload failed",
					HttpStatus.INTERNAL_SERVER_ERROR)
		}
		
		return new ResponseEntity<>(
				"Success Uploading Files",
				HttpStatus.OK)
		
	}
	@Transactional
	@RequestMapping(method = RequestMethod.GET, value = "/api/patient/updatePatientNo")
	Boolean updatePatientNo(@RequestParam String patientId) {
		Patient patient = patientRepository.getOne(UUID.fromString(patientId))
        patient.patientNo = generatorService.getNextValue(GeneratorType.PATIENT_NO) { Long no ->
            StringUtils.leftPad(no.toString(), 6, "0")
        }
        patientRepository.save(patient);
        return true;
	}
	
	@Transactional
	@RequestMapping(method = RequestMethod.POST, value = "/api/patient/addCasePhotosNew")
	ResponseEntity<String> addCasePhotosNew(@RequestParam String id, MultipartRequest request) {
		
		Case activeCase = caseRepository.findById(UUID.fromString(id)).get()
		Map files = request.getFileMap()
		
		try {
			files.values().forEach {
				file ->
					MultipartFile f = file
					FileAttachment newAttachment = new FileAttachment()
					newAttachment.patient = activeCase.patient
					newAttachment.patientCase = activeCase
					newAttachment.fileName = f.originalFilename
					newAttachment.mimetype = f.contentType
					newAttachment.desc = "Case photo"
					try {
						/*** ready for NAS***/
						String fileName = newAttachment.fileName
						byte[] byteData = f.getBytes()
						newAttachment.urlPath = orderSlipResource.uploadPhotos(activeCase, byteData, fileName)
						fileAttachmentRepository.save(newAttachment)
					} catch (Exception e) {
						e.printStackTrace()
						throw e
					}
			}
		} catch (Exception ignored) {
			return new ResponseEntity<>(
					"Upload failed",
					HttpStatus.INTERNAL_SERVER_ERROR)
		}
		
		return new ResponseEntity<>(
				"Success Uploading Files",
				HttpStatus.OK)
		
	}
	
	@RequestMapping(method = [RequestMethod.GET], value = "/api/patient/getCasePhoto/{id}")
	String getCasePhoto(@PathVariable(value = "id") String id) {
		
		FileAttachment resultPhoto = fileAttachmentRepository.findById(UUID.fromString(id)).get()
		if (resultPhoto != null) {
			if (resultPhoto.urlPath != null) {
				IntegrationConfig integrationConfig = integrationConfigRepository.findAll().first()
				
				NtlmPasswordAuthentication ntlmPasswordAuthentication = new NtlmPasswordAuthentication(null, integrationConfig.smbUser, integrationConfig.smbPass)
				
				SmbFile attachment = new SmbFile(resultPhoto.urlPath, ntlmPasswordAuthentication)
				
				SmbFileInputStream inputStream = new SmbFileInputStream(attachment)
				byte[] imageBytes = new byte[(int) attachment.length()]
				inputStream.read(imageBytes, 0, imageBytes.length)
				inputStream.close()
				
				return imageBytes.encodeBase64().toString()
			}
		}
		
		return new ResponseEntity<>(
				"File not found",
				HttpStatus.NOT_FOUND)
	}
	
	@RequestMapping(method = [RequestMethod.GET], value = "/api/patient/getDiagnosticResultImage/{id}")
	String getDiagnosticResultImage(@PathVariable(value = "id") String id) {
		
		DiagnosticResult resultImage = diagnosticsResultRepository.findById(UUID.fromString(id)).get()
		if (resultImage != null) {
			if (resultImage.url_path != null) {
				IntegrationConfig integrationConfig = integrationConfigRepository.findAll().first()
				
				NtlmPasswordAuthentication ntlmPasswordAuthentication = new NtlmPasswordAuthentication(null, integrationConfig.smbUser, integrationConfig.smbPass)
				
				SmbFile attachment = new SmbFile(resultImage.url_path, ntlmPasswordAuthentication)
				
				SmbFileInputStream inputStream = new SmbFileInputStream(attachment)
				byte[] imageBytes = new byte[(int) attachment.length()]
				inputStream.read(imageBytes, 0, imageBytes.length)
				inputStream.close()
				
				return imageBytes.encodeBase64().toString()
			}
		}
		
		return new ResponseEntity<>(
				"File not found",
				HttpStatus.NOT_FOUND)
	}
}
