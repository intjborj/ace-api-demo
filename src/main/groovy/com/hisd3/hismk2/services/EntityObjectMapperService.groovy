package com.hisd3.hismk2.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.domain.types.Subaccountable
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.persistence.EntityManager
import javax.persistence.ManyToOne
import javax.persistence.OneToOne
import javax.persistence.PersistenceContext

/**
 * HISEntityObjectMapperService maps from a Map to Entity Object
 *
 *
 * @author Albert Oclarit
 * @version 1.0* @since 2020-2-11
 */
@Service
class EntityObjectMapperService {
	
	@Autowired
	ObjectMapper objectMapper
	
	@PersistenceContext
	EntityManager entityManager
	
	/**
	 *
	 * @param object
	 * @param fields
	 * @return object send
	 *
	 *
	 * Please ignore fields (meaning dont include them in the map if they are not @ManyToOne, @OneToOne to prevent error
	 *
	 * Feb. 23, 2020 . Now recognize custom UpperCase annotation that will autouppercase String prop
	 */
	
	def <T> T updateFromMap(T object, Map<String, Object> fieldsParam) {
		
		def fields = fieldsParam
		// id property should not be updated from a map. we will ignore it
				.findAll { !StringUtils.equalsIgnoreCase(it.key, "id") }
		// Array/List/Set are also ignored  and should be excluded in fields
				.findAll {
			!(it.value instanceof List || it.value instanceof Set || it.value instanceof Object[])
		}
		
		def objectClass = object.class
		
		def defferedValuesToBeMapped = [:]
		
		object.properties.each { property ->
			
			String key = property.key
			
			// check if key exists in fields
			if (fields.containsKey(key)) {
				// now process everything from here
				
				// Check if property is a @ManyToOne or @OneToOne
				
				def match = objectClass.declaredFields.find {
					def annotations = it.declaredAnnotations*.annotationType()
					it.name == key && (ManyToOne.class in annotations || OneToOne.class in annotations)
				}
				
				if (match) {
					
					def fValue = fields.get(key)
					if(fValue ==null){
						defferedValuesToBeMapped[key] = null
					}
					if (fValue instanceof Map) {
						fValue = fValue.id
					}
					
					if (fValue instanceof String && fValue) {
						
						// check if it is a valid uuid string
						try {
							UUID uuid = UUID.fromString(fValue)
							def type = match.type
							def entity = entityManager.find(type, uuid)
							
							object[key] = entity
							
						} catch (IllegalArgumentException exception) {
							exception.printStackTrace()
						}
					}
					
					if (fValue instanceof UUID) {
						property.value = entityManager.find(objectClass, fValue)
					}
					
				} else {
					
					def matchUpper = objectClass.declaredFields.find {
						def annotations = it.declaredAnnotations*.annotationType()
						it.name == key && (UpperCase.class in annotations)
					}
					
					if (matchUpper) {
						def fValue = fields.get(key)
						
						if (fValue instanceof String) {
							defferedValuesToBeMapped[key] = StringUtils.defaultString(fValue, "").toUpperCase()
						} else {
							// if non String with UpperCase Annotation
							defferedValuesToBeMapped[key] = fields.get(key)
						}
						
					} else {
						// Default
						defferedValuesToBeMapped[key] = fields.get(key)
					}
					
				}
			}
			
		}
		
		if (defferedValuesToBeMapped) {
			objectMapper.updateValue(object, defferedValuesToBeMapped)
		}
		
		object
	}
	
}
