package com.hisd3.hismk2.dao.price_tier

import com.hisd3.hismk2.domain.ancillary.Service
import com.hisd3.hismk2.domain.billing.ItemPriceControl
import com.hisd3.hismk2.domain.billing.PriceTierDetail
import com.hisd3.hismk2.domain.billing.ServicePriceControl
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.inventory.Markup
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.ancillary.ServiceRepository
import com.hisd3.hismk2.repository.billing.ItemPriceControlRepository
import com.hisd3.hismk2.repository.billing.PriceTierDetailRepository
import com.hisd3.hismk2.repository.billing.ServicePriceControlRepository
import com.hisd3.hismk2.repository.bms.RoomRepository
import com.hisd3.hismk2.repository.inventory.ItemRepository
import com.hisd3.hismk2.repository.inventory.MarkupRepository
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.rest.dto.TierDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

enum PriceTierItemType {
    MEDICINE,
    SUPPLIES,
    SERVICE
}

@Transactional
@org.springframework.stereotype.Service
class PriceTierDetailDao extends AbstractDaoService<PriceTierDetail> {
    PriceTierDetailDao() {
        super(PriceTierDetail.class)
    }
    @Autowired
    private PriceTierDetailRepository priceTierDetailRepository

    @Autowired
    private CaseRepository caseRepository

    @Autowired
    private RoomRepository roomRepository

    @Autowired
    private ItemRepository itemRepository

    @Autowired
    private MarkupRepository markupRepository

    @Autowired
    private ServiceRepository serviceRepository

    @Autowired
    private ItemPriceControlRepository itemPriceControlRepository

    @Autowired
    private ServicePriceControlRepository servicePriceControlRepository

    @PersistenceContext
    EntityManager entityManager

//Albert
    BigDecimal getItemPrice(UUID priceTierDetailId, UUID item) {
        PriceTierDetail priceTierDetail = priceTierDetailRepository.findById(priceTierDetailId).get()

        Item itemObj = itemRepository.findById(item).get()
        //Markup markupObj = markupRepository.findById(item).get()

        def basePrice = itemObj.actualUnitCost != null ? itemObj.actualUnitCost : 0.0
        def defaultMarkup = itemObj.item_markup != null ? itemObj.item_markup / 100 : 0.0
        basePrice = basePrice + (basePrice * defaultMarkup)

        return getContent(itemObj, priceTierDetail, basePrice).calculatedValue
    }

    TierDTO getItemTier(UUID caseId, UUID item) {
        PriceTierDetail priceTierDetail = getDetail(caseId)

        Item itemObj = itemRepository.findById(item).get()
        //Markup markupObj = markupRepository.findById(item).get()

        def basePrice = itemObj.actualUnitCost != null ? itemObj.actualUnitCost : 0.0
        def defaultMarkup = itemObj.item_markup != null ? itemObj.item_markup / 100 : 0.0
        basePrice = basePrice + (basePrice * defaultMarkup)

        return getContent(itemObj, priceTierDetail, basePrice)
    }

    TierDTO getContent(Item item, PriceTierDetail priceTierDetail, BigDecimal calculatedCost) {
        TierDTO tierDTO = new TierDTO()
        def theValue = 0.0

        tierDTO.tierDetail = priceTierDetail

        ItemPriceControl itemPriceControl = itemPriceControlRepository.getItemByIdAndTier(priceTierDetail.id, item.id)

        if (itemPriceControl)
            theValue = itemPriceControl.amountValue
        else {
            theValue = calculatedCost
        }

        //this section only applies to item that is marked as vatable on the masterfile.
        if (priceTierDetail.isVatable && item.vatable)
            theValue = theValue + (theValue * (priceTierDetail.vatRate / 100))

        tierDTO.calculatedValue = theValue

        return tierDTO
    }

    // --------------------- SERVICES ------------------------//
    // --------------------- SERVICES ------------------------//
    // --------------------- SERVICES ------------------------//
    // --------------------- SERVICES ------------------------//

    //Albert
    BigDecimal getServicePrice(UUID priceTierDetailId, UUID service) {
        PriceTierDetail priceTierDetail = priceTierDetailRepository.findById(priceTierDetailId).get()
        Service serviceObj = serviceRepository.findById(service).get()

        def cost = serviceObj?.basePrice ?: 0.0

        return getContent(serviceObj, priceTierDetail, cost).calculatedValue
    }

    TierDTO getServiceTier(UUID caseId, UUID service) {
        PriceTierDetail priceTierDetail = getDetail(caseId)
        Service serviceObj = serviceRepository.findById(service).get()

        def cost = serviceObj?.basePrice ?: 0.0
        return getContent(serviceObj, priceTierDetail, cost)
    }

    TierDTO getContent(Service serviceObj, PriceTierDetail priceTierDetail, BigDecimal calculatedCost) {
        TierDTO tierDTO = new TierDTO()
        def theValue = 0.0

        tierDTO.tierDetail = priceTierDetail

        ServicePriceControl servicePriceControl = servicePriceControlRepository.getServiceByIdAndTier(priceTierDetail.id, serviceObj.id)

        if (servicePriceControl)
            theValue = servicePriceControl.amountValue
        else {
            theValue = calculatedCost
        }

        tierDTO.calculatedValue = theValue

        return tierDTO
    }

    // --------------------- GENERAL ------------------------//
    // --------------------- GENERAL ------------------------//
    // --------------------- GENERAL ------------------------//
    // --------------------- GENERAL ------------------------//

    //Albert
    PriceTierDetail getDetail(UUID caseId) {
        if (!caseRepository.findById(caseId).present)
            return null

        Case c = caseRepository.findById(caseId).get()
        PriceTierDetail priceTierDetail = null // you should return null... Dont worry graphql respect null object
        List<PriceTierDetail> priceTierDetails = []
        Boolean hasSenior = Boolean.FALSE

        if (c.patient.oscaId) {
            hasSenior = true
        }

        Map<String, Object> params = new HashMap<>()

        String query = '''select tier from PriceTierDetail tier where 
							(tier.registryType = :registryType and 
							tier.accommodationType = :accommodationType)'''

        params.put("registryType", c.registryType)
        params.put("accommodationType", c.priceAccommodationType)

        //at this point in time, the special departments doesn't have to check
        //the patient's room type.
        //None of the price tier has been configured for it too.
        if (c.room && !c.department.hasSpecialPriceTier && c.registryType == "IPD") {
            query += " and upper(tier.roomTypes) like upper(concat('%',:roomType,'%'))"
            params.put("roomType", c.room.type)
        }
        String allowedDepartQuery = query
//
//        if (c.department.hasSpecialPriceTier) {
//            query += " and tier.department.id = :department"
//            params.put("department", c.department.id)
//        } else {
//            allowedDepartQuery += " and upper(tier.departments) LIKE upper(concat('%', :department, '%'))"
//            params.put("department", c.department.id)
//        }

        String seniorQuery = " and tier.forSenior = :hasSenior"

        if(!c.department.hasSpecialPriceTier){
            allowedDepartQuery += " and upper(tier.departments) LIKE upper(concat('%', :department, '%'))"
            String seniorAllowedDeptQuery = allowedDepartQuery + seniorQuery
            params.put("hasSenior", hasSenior)
            params.put("department", c.department.id)
            priceTierDetails = createQuery(seniorAllowedDeptQuery, params).resultList
        }

        //do allowed department query first
        if (priceTierDetails.size() == 0) {
            params.remove("hasSenior")
            if (!c.department.hasSpecialPriceTier) {
                params.put("department", c.department.id)
                priceTierDetails = createQuery(allowedDepartQuery, params).resultList
                if (priceTierDetails.size() == 0) {
                    params.remove("department")
                }
            }
        }

        if (priceTierDetails.size() == 0) {
            if(c.department.hasSpecialPriceTier){
                query += " and tier.department.id = :department"
                params.put("department", c.department.id)
            }
            String originalSeniorQuery = query + seniorQuery
            params.put("hasSenior", hasSenior)
            priceTierDetails = createQuery(originalSeniorQuery, params).resultList
        }

        if (priceTierDetails.size() == 0) {
            params.remove("hasSenior")
            priceTierDetails = createQuery(query, params).resultList
            priceTierDetail = priceTierDetails[0]
        } else {
            priceTierDetail = priceTierDetails[0]
        }

        return priceTierDetail
    }
}
