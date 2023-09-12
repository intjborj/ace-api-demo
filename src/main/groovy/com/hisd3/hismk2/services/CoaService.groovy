package com.hisd3.hismk2.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.hisd3.hismk2.domain.accounting.ChartOfAccount
import com.hisd3.hismk2.graphqlservices.accounting.ChartOfAccountServices
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct
import javax.transaction.Transactional

class CoaItemContainer {
	ArrayList<CoaItem> items
}

class CoaItem {
	String code
	String description
	ArrayList<String> tags
	ArrayList<CoaItem> subledger
	
}

@Service
@TypeChecked
class CoaService {
	
	@Autowired
	ApplicationContext applicationContext
	
	@Autowired
	ChartOfAccountServices chartOfAccountServices
	
	@PostConstruct
	@Transactional(rollbackOn = Exception)
	void runCOA() {
		def objectMapper = new ObjectMapper(new YAMLFactory())
		def container = objectMapper.readValue(applicationContext.getResource("classpath:/coa/acechartofaccounts.yml").inputStream, CoaItemContainer)
		//println("==========="+container.items.size())
		List<ChartOfAccount> check = chartOfAccountServices.findAll()
		//1st layer
		//subLedger(container.items, null)
		if (!check || check.size() <= 0) {
			container.items.each {
				it ->
					String tagging = ''
					if (it.tags) {
						Integer c = it.tags.size(); Integer counter = 1
						it.tags.each {
							e ->
								if (c > 1) {
									if (counter == c) {
										tagging = tagging + e
									} else {
										tagging = tagging + e + ","
									}
								} else {
									tagging = e
								}
								counter = counter + 1
						}
					}
					Boolean cat = false
					ChartOfAccount insert = new ChartOfAccount()
					def subledgers = it.subledger
					if (it.code.length() <= 2) {
						cat = true
					}
					insert.accountCode = it.code
					insert.description = it.description
					insert.category = cat
					insert.tags = tagging
					insert.deprecated = false
					insert = chartOfAccountServices.save(insert)
					subledgers.each {
						item ->
							traverse(item, insert.id)
					}
			}
		}
		
	}
	
	void traverse(CoaItem it, UUID parent) {
		String tagging = ''
		if (it.tags) {
			Integer c = it.tags.size(); Integer counter = 1
			it.tags.each {
				e ->
					if (c > 1) {
						if (counter == c) {
							tagging = tagging + e
						} else {
							tagging = tagging + e + ","
						}
					} else {
						tagging = e
					}
					counter = counter + 1
			}
		}
		Boolean cat = false
		ChartOfAccount insert = new ChartOfAccount()
		def subledgers = it.subledger
		if (it.code.length() <= 2) {
			cat = true
		}
		insert.accountCode = it.code
		insert.description = it.description
		insert.category = cat
		insert.tags = tagging
		insert.deprecated = false
		insert.parent = parent
		insert = chartOfAccountServices.save(insert)
		
		subledgers.each {
			item ->
				traverse(item, insert.id)
		}
	}
	
}

