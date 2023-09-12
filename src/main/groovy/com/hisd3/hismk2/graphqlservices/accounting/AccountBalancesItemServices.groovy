package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.AccountBalancesItem
import com.hisd3.hismk2.domain.accounting.HeaderLedger
import com.hisd3.hismk2.domain.accounting.Ledger
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLResVal
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.accounting.LedgerRepository
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional


@GraphQLApi
@Service
class AccountBalancesItemServices extends AbstractDaoService<AccountBalancesItem> {

    AccountBalancesItemServices(){
        super(AccountBalancesItem.class)
    }

    @Autowired
    LedgerServices ledgerServices

    @Autowired
    SubAccountSetupService subAccountSetupService

    @Autowired
    AccountBalancesServices accountBalancesServices

    @Autowired
    LedgerRepository ledgerRepository

    @Transactional
    @GraphQLMutation(name="saveAccountBalancesItem")
    GraphQLResVal<Boolean> saveAccountBalancesItem(
            @GraphQLArgument(name="entries") List<Map<String,Object>> entries,
            @GraphQLArgument(name="headerId") UUID headerId,
            @GraphQLArgument(name="accountBalanceId") UUID accountBalanceId
    ){
        try{
            def header = ledgerServices.findOne(headerId)

            def coa =  subAccountSetupService.getAllChartOfAccountGenerate("","","","","")
            List<EntryFull> entriesTarget = []
            entries.each {
                en ->
                    Ledger ledger = new Ledger()
                    ledger.header = header
                    ledger.debit = 0
                    ledger.credit = 0
                    def match =  coa.find {
                        it.code == en.get('code')
                    }

                    if(!match){
                        return   new GraphQLResVal<Boolean>(false,false,"${code}-${description} is not found in Chart of accounts")
                    }
                    ledger.journalAccount = match
                    def newLedger = ledgerRepository.save(ledger)

                    AccountBalancesItem abItem = new AccountBalancesItem()
                    abItem.journalAccount = newLedger.journalAccount
                    abItem.debit = 0
                    abItem.credit = 0
                    abItem.accountBalances = accountBalancesServices.findOne(accountBalanceId)
                    abItem.headerId = header.id
                    abItem.ledgerId = newLedger.id
                    save(abItem)
            }

           return  new GraphQLResVal<Boolean>(true,true,"OK")
        }
        catch (e){
            return new GraphQLResVal<Boolean>(false, false , e.message)
        }
    }

    @GraphQLQuery(name="findOneAccountBalancesItem")
    List<AccountBalancesItem> findOneAccountBalancesItem(
            @GraphQLArgument(name="accountBalanceId") UUID accountBalanceId
    ){
        def accountBalance = createQuery("""
            Select a from AccountBalancesItem a where a.accountBalances.id = :accountBalanceId
        """,[accountBalanceId:accountBalanceId]).resultList

        if(!accountBalance)
            return []

        return accountBalance
    }

    @Transactional
    @GraphQLMutation(name = "updateLedger")
    GraphQLResVal<Boolean> updateLedger(
            @GraphQLArgument(name="updatedFields") List<Map<String,Object>> updatedFields,
            @GraphQLArgument(name="deletedFields") List<UUID> deletedFields
    ){
        try {
            if (updatedFields.size() > 0) {
                updatedFields.each {
                    AccountBalancesItem abItem = findOne(UUID.fromString(it.get('id').toString()))
                    entityObjectMapperService.updateFromMap(abItem, it)
                    save(abItem)

                    Map<String,Object> ledgerObj = it
                    ledgerObj['id'] = abItem.ledgerId
                    Ledger ledger = ledgerRepository.findById(abItem.ledgerId).get()
                    entityObjectMapperService.updateFromMap(ledger, ledgerObj)
                    ledgerRepository.save(ledger)
                }
            }

            if (deletedFields.size() > 0) {
                deletedFields.each {
                    deletedID ->
                        AccountBalancesItem abItem = findOne(deletedID)
                        ledgerRepository.deleteLedger(abItem.ledgerId)
                        delete(abItem)
                }
            }

            return new GraphQLResVal<Boolean>(true, true, 'Success')
        }catch(Exception e){
            return  new GraphQLResVal<Boolean>(false,false,e.message)
        }
    }

}
