package com.hisd3.hismk2.dao.ancillary

import com.hisd3.hismk2.domain.ancillary.PanelContent
import com.hisd3.hismk2.repository.ancillary.PanelContentRepository
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@TypeChecked
@Service
@Transactional
class PanelContentDao {
	
	@Autowired
	private PanelContentRepository panelContentRepository
	
	@PersistenceContext
	EntityManager entityManager
	
	PanelContent save(PanelContent child) {
		panelContentRepository.save(child)
	}
	
	List<PanelContent> addPanelComponents(List<PanelContent> children) {
		List<PanelContent> res = []
		children.each {
			it ->
				it.deleted = false
				res.add(panelContentRepository.save(it))
		}
		return res
	}
	
	List<PanelContent> searchHisServices(String id) {
		return panelContentRepository.findAllbyParentId(UUID.fromString(id))
	}
	
}
