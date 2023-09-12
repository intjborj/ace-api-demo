package com.hisd3.hismk2.graphqlservices.cashiering

import com.hisd3.hismk2.domain.accounting.HeaderLedger
import com.hisd3.hismk2.domain.accounting.JournalType
import com.hisd3.hismk2.domain.accounting.Ledger
import com.hisd3.hismk2.domain.accounting.LedgerDocType
import com.hisd3.hismk2.domain.cashiering.CashierTerminal
import com.hisd3.hismk2.domain.cashiering.ChequeEncashment
import com.hisd3.hismk2.domain.cashiering.Shifting
import com.hisd3.hismk2.graphqlservices.UserService
import com.hisd3.hismk2.graphqlservices.accounting.IntegrationServices
import com.hisd3.hismk2.graphqlservices.accounting.LedgerServices
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.EntityObjectMapperService
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

import javax.persistence.EntityManager
import javax.transaction.Transactional
import java.time.Instant

enum CHEQUE_E_INTEGRATION {
    CHEQUE_ENCASHMENT
}

@Canonical
class CESupportingDocu {
    String referenceNo
    String description
    String fileName
}

@Canonical
class ChequeEncashmentCashier {
    CashierTerminal cashierTerminal
    Shifting shifting
}

@Canonical
class ChequeEncashmentResponse {
    ChequeEncashment chequeEncashment
    Shifting activeShift
}

@Transactional(rollbackOn = [Exception.class])
@Component
@GraphQLApi
class ChequeEncashmentServices extends AbstractDaoService<ChequeEncashment> {

    ChequeEncashmentServices() {
        super(ChequeEncashment.class)
    }

    @Autowired
    EntityObjectMapperService entityObjectMapperService

    @Autowired
    ChequeEncashmentSupportDocServices chequeEncashmentSupportDocServices

    @Autowired
    CashieringService cashieringService

    @Autowired
    ShiftingServices shiftingServices

    @Autowired
    GeneratorService generatorService

    @Autowired
    EmployeeRepository employeeRepository

    @Autowired
    IntegrationServices integrationServices

    @Autowired
    LedgerServices ledgerServices

    @Autowired
    EntityManager entityManager

    @Autowired
    UserService userService

    @GraphQLQuery(name = "getEncashmentById")
    ChequeEncashment getEncashmentById(
            @GraphQLArgument(name="id") UUID id
    ){
        def encash = findOne(id)
        if(encash)
            return  encash
        return new ChequeEncashment()
    }

    @GraphQLQuery(name = "getEncashmentWithActiveShift")
    ChequeEncashmentResponse getEncashmentWithActiveShift(
            @GraphQLArgument(name="id") UUID id
    ){
        ChequeEncashmentResponse chequeEncashmentResponse = new ChequeEncashmentResponse()
        if(id) {
            def chequeE  = findOne(id)
            if (chequeE) {
                chequeEncashmentResponse.chequeEncashment = chequeE
                def activeShift = shiftingServices.getActiveShift(chequeE.terminal)
                if(activeShift)
                    chequeEncashmentResponse.activeShift = activeShift
            }
        }

        return chequeEncashmentResponse
    }

    @Transactional
    @GraphQLMutation
    GraphQLRetVal<String> voidChequeEncashment(
            @GraphQLArgument(name="id") UUID id,
            @GraphQLArgument(name="remarks") String remarks
    ){
        try{
            if(id) {
                def chequeE  = findOne(id)
                if (chequeE && !chequeE.collectionDetail) {
                    def activeShift = shiftingServices.getActiveShift(chequeE.terminal)
                    if(activeShift){
                        chequeE.returnedDate = Instant.now()
                        chequeE.returnedShifting = activeShift

                        def currentLogin = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()
                        if(currentLogin)
                            chequeE.returnedPersonnel = currentLogin
                        else
                            return new GraphQLRetVal<String>('no_active_login',false,'No active login')

                        chequeE.returnRemarks = remarks
                        def saved = save(chequeE)
                        if(saved && saved.postedLedger && !saved.returnPostedLedger){
                            def postedLedger = ledgerServices.findOne(saved.postedLedger)
                            def ledger = ledgerServices.reverseEntries(postedLedger)
                            if(ledger){
                                saved.returnPostedLedger = ledger.id
                                save(saved)
                                return new GraphQLRetVal<String>('success',true,'Successfully voided')
                            }
                            else
                                return new GraphQLRetVal<String>('error_journal_entry',false,'Error journal entry')
                        }
                        else
                            return new GraphQLRetVal<String>('save_error',false,'Already voided')
                    }
                    else
                        return new GraphQLRetVal<String>('no_active_shift',false,'No active shift')
                }
                else
                    return new GraphQLRetVal<String>('no_records',false,'No records found')
            }
            else
                return new GraphQLRetVal<String>('no_parameter',false,'No parameter')

        }
        catch (e){
            return new GraphQLRetVal<String>('',false,e.message)
        }
    }

    @Transactional
    @GraphQLQuery(name = "createChequeEncasmentJournalEntry")
    HeaderLedger createChequeEncasmentJournalEntry(ChequeEncashment chequeEncashment){

        def headerLEdger = integrationServices.generateAutoEntries(chequeEncashment){it, nul ->
            it.flagValue = CHEQUE_E_INTEGRATION.CHEQUE_ENCASHMENT.name()
        }

        Map<String,String> details = [:]
        chequeEncashment.details.each {k,v ->
            details[k] = v
        }

        details['CHEQUE_ENCASMENT_ID'] = chequeEncashment.id.toString()

        def pHeader = ledgerServices.persistHeaderLedger(headerLEdger,
                "${chequeEncashment.recordNo}-${chequeEncashment.chequeNo}",
                "ENCASHMENT - ${chequeEncashment.chequeNo}-${chequeEncashment.bank.bankname}",
                "New Cheque Encashment Cheque No.${chequeEncashment.chequeNo}-${chequeEncashment.bank.bankname}",
                LedgerDocType.JV,
                JournalType.GENERAL,
                chequeEncashment.transactionDate,
                details
        )
        return  pHeader
    }

    @Transactional
    @GraphQLMutation
    GraphQLRetVal<String> addChequeEncashmentTransaction(
            @GraphQLArgument(name = "fields") Map<String, Object> fields,
            @GraphQLArgument(name = "supportingDocs") Map<String,CESupportingDocu> supportingDocs,
            @GraphQLArgument(name = "attachments") ArrayList<MultipartFile> attachments
    ){
        try {
            ChequeEncashment chequeEncashment = new ChequeEncashment()
            entityObjectMapperService.updateFromMap(chequeEncashment , fields)
            chequeEncashment.recordNo = generatorService.getNextValue(GeneratorType.CHEQUE_ENCASHMENT_RN, {
                return StringUtils.leftPad(it.toString(), 6, "0")
            })
            chequeEncashment.transactionDate = Instant.now()
            if(chequeEncashment.amount > 0) {
                def saved = save(chequeEncashment)
                if (saved) {
                    attachments.each {
                        def convertedName = it.getOriginalFilename().replaceAll(' ', '_')
                        if (supportingDocs[convertedName])
                            chequeEncashmentSupportDocServices.addChequeEncashmentSuppDoc(supportingDocs[convertedName], it, saved)
                    }

                    def ledger = createChequeEncasmentJournalEntry(saved)
                    if (ledger) {
                        saved.postedLedger = ledger.id
                        save(saved)
                    }
                }
                return new GraphQLRetVal<String>('Success',true,'Successfully saved')
            }
            return new GraphQLRetVal<String>('Error',false,'Invalid Amount')

        }
        catch (e){
            return new GraphQLRetVal<String>('Error',false,e.message)
        }

    }

    @GraphQLQuery(name = "getCashierDataByMacAddress")
    ChequeEncashmentCashier getCashierDataByMacAddress(
            @GraphQLArgument(name = "macAddress") String macAddress
    ) {
            def cashier = cashieringService.findByMacAddess(macAddress)
            if (!cashier) {
                return new ChequeEncashmentCashier(null,null)
            }

            def shift  = shiftingServices.getActiveShift(cashier)
            return new ChequeEncashmentCashier(cashier,shift)
    }

    @GraphQLQuery(name = 'cheqEncashmentTransPageable')
    Page<ChequeEncashment> cheqEncashmentTransPageable(
            @GraphQLArgument(name = "filterBank") ArrayList<UUID> filterBank,
            @GraphQLArgument(name = "filter") String filter,
            @GraphQLArgument(name = "macAddress") String macAddress,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "size") Integer size
    ){

        Page<ChequeEncashment> encashmentPage = Page.empty()
        def currentLogin = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()

        if(currentLogin){
            def found = null
            def roles = userService.getRoles(currentLogin.user)
            List<String> allowed = Arrays.asList('ROLE_ADMIN','ROLE_CASHIER_ADMIN')
            if(roles)
                allowed.each {all -> if(roles.contains(all)) found = true }

            String query = ''' Select c from ChequeEncashment c where c.returnedShifting.id is null and
                            (
                                lower(c.bank.bankname) like lower(concat('%',:filter,'%')) 
                                or lower(c.chequeNo) like lower(concat('%',:filter,'%'))
                                or lower(c.recordNo) like lower(concat('%',:filter,'%'))
                                or lower(c.shifting.shiftno) like lower(concat('%',:filter,'%'))
                                or lower(c.terminal.terminalId) like lower(concat('%',:filter,'%'))
                            )'''
            String countQuery = ''' Select count(c) from ChequeEncashment c where c.returnedShifting.id is null and
                            (
                                lower(c.bank.bankname) like lower(concat('%',:filter,'%')) 
                                or lower(c.chequeNo) like lower(concat('%',:filter,'%'))
                                or lower(c.recordNo) like lower(concat('%',:filter,'%'))
                                or lower(c.shifting.shiftno) like lower(concat('%',:filter,'%'))
                                or lower(c.terminal.terminalId) like lower(concat('%',:filter,'%'))
                            )'''

            Map<String,Object> params = new HashMap<>()
            params.put('filter',filter)

            if(found && macAddress){
                def shift = getCashierDataByMacAddress(macAddress)
                def shiftId = shift?.shifting?.id
                params.put('shift', shiftId)
                query += ''' AND c.shifting.id = :shift'''
                countQuery += ''' AND c.shifting.id = :shift'''
            }

            if(filterBank.size() > 0){
                params.put('filterBank',filterBank)
                query += ''' AND c.bank.id in (:filterBank) order by c.recordNo desc'''
                countQuery += ''' AND c.bank.id in (:filterBank)'''
            }
            else{
                query += ''' order by c.recordNo desc'''
            }

            encashmentPage = getPageable(query,countQuery,page,size,params)
        }

        return encashmentPage

    }

    @GraphQLQuery(name = "cheqEncashmentByShiftId")
    List<ChequeEncashment> cheqEncashmentByShiftId(
            @GraphQLArgument(name = "shiftIds") ArrayList<UUID> shiftIds
    ){
        createQuery("Select c from ChequeEncashment c where c.shifting.id in (:shiftIds) or c.returnedShifting.id in (:shiftIds)",
                [shiftIds: shiftIds]).resultList.sort { it.recordNo }
    }

    @GraphQLQuery(name = "cheqEncashmentByOneShiftId")
    List<ChequeEncashment> cheqEncashmentByOneShiftId(
            @GraphQLArgument(name = "shiftId") UUID shiftId
    ){
        createQuery("Select c from ChequeEncashment c where c.shifting.id = :shiftIds or c.returnedShifting.id = :shiftIds",
                [shiftIds: shiftId]).resultList.sort { it.recordNo }
    }

    @GraphQLQuery(name = "getActiveOnDueCheques")
    Page<ChequeEncashment> getActiveOnDueCheques(
            @GraphQLArgument(name = "filter") String filter,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "size") Integer size
    ){

        def records = entityManager.createQuery("""Select c from ChequeEncashment c 
                lEFT JOIN CollectionDetail cd on cd.id = c.collectionDetail.id
                where (lower(c.chequeNo) like lower(concat('%',:filter,'%'))) and 
                (cd.id is null) and c.chequeDate <= current_date() and c.returnedShifting.id is null """, ChequeEncashment.class)
                .setParameter("filter",filter)
                .setMaxResults(size)
                .setFirstResult(page * size)
                .resultList.sort{it.chequeDate}

        def count = entityManager.createQuery("""Select count(c) from ChequeEncashment c 
                lEFT JOIN CollectionDetail cd on cd.id = c.collectionDetail.id
                where (lower(c.chequeNo) like lower(concat('%',:filter,'%'))) and 
                (cd.id is null) and c.chequeDate <= current_date() and c.returnedShifting.id is null """, Long.class)
                .setParameter("filter",filter)
                .singleResult

        new PageImpl<ChequeEncashment>(records, PageRequest.of(page,size),count)
    }


}
