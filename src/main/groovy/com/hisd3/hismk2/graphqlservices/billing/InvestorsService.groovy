package com.hisd3.hismk2.graphqlservices.billing

import com.hisd3.hismk2.domain.billing.Investor
import com.hisd3.hismk2.domain.billing.InvestorDependent
import com.hisd3.hismk2.domain.billing.MoreInformation
import com.hisd3.hismk2.graphqlservices.billing.dto.InvestorIdFullNameDto
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.billing.InvestorDependentRepository
import com.hisd3.hismk2.repository.billing.InvestorsRepository
import com.hisd3.hismk2.services.EntityObjectMapperService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.hibernate.annotations.QueryHints
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Component
@GraphQLApi
class InvestorsService {

    @Autowired
    EntityObjectMapperService entityObjectMapperService

    @Autowired
    InvestorsRepository investorsRepository

    @Autowired
    InvestorDependentRepository investorDependentRepository

    @PersistenceContext
    EntityManager entityManager

    @GraphQLQuery
    List<InvestorIdFullNameDto> investorIdFullnameDtoList(
            @GraphQLArgument(name = "id") UUID id
    ) {
        List<InvestorIdFullNameDto> list = []
        InvestorIdFullNameDto investor = investorsRepository.findInvestorById(id)
        List<InvestorIdFullNameDto> dependents = investorDependentRepository.findInvestorById(id)
        if (!investor) throw new Exception("No investor found.")
        list.add(investor)
        list.addAll(dependents)
        return list
    }

    @GraphQLQuery(name = "investorById")
    Investor investorById(
            @GraphQLArgument(name = "id") UUID id
    ) {
        return investorsRepository.findById(id).get()
    }

    @GraphQLQuery(name = "investors")
    Page<Investor> getInvestors(
            @GraphQLArgument(name = "filter") String filter,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "size") Integer size
    ) {

        return investorsRepository.getInvestors(filter, new PageRequest(page, size, Sort.Direction.ASC, "fullName"))

    }

    @GraphQLQuery(name = "investorSubscriptions")
    Investor getInvestorSubscriptions(
            @GraphQLArgument(name = "id") UUID id
    ) {
        if (!id) throw new Exception("ID of the investor is required.")

        Investor investor = entityManager.createQuery("""
            Select distinct i from Investor i
            left join fetch i.subscriptions s
            where i.id = :id
            order by s.createdDate DESC
        """, Investor.class).setHint(QueryHints.PASS_DISTINCT_THROUGH, false).setParameter("id", id).getSingleResult()

        return investor
    }

    @GraphQLMutation
    Investor upsertInvestors(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {
        List<MoreInformation> newIdentifications = []
        List<MoreInformation> newContactNumbers = []

        if (fields.containsKey("identifications")) {
            if (fields.get("identifications")) {
                def identifications = fields.get("identifications")
                newIdentifications = (identifications as List<Map<String, Object>>).stream().map { new MoreInformation(it.get("title") as Map<String, String>, it.get("value").toString()) }.collect() ?: []
            }

        }
        if (fields.containsKey("contactNumbers")) {
            if (fields.get("contactNumbers")) {
                def contactNumbers = fields.get("contactNumbers")
                newContactNumbers = (contactNumbers as List<Map<String, Object>>).stream().map { new MoreInformation(it.get("title") as Map<String, String>, it.get("value").toString()) }.collect() ?: []
            }
        }

        if (id) {
            def item = investorsRepository.findById(id).get()
            entityObjectMapperService.updateFromMap(item, fields)

            item.lastname = item.lastname?.toUpperCase()
            item.firstname = item.firstname?.toUpperCase()
            item.middlename = item.middlename?.toUpperCase()
            if (fields.containsKey("identifications"))
                item.identifications = newIdentifications
            if (fields.containsKey("contactNumbers"))
                item.contactNumbers = newContactNumbers

            investorsRepository.save(item)

        } else {
            def item = new Investor()
            entityObjectMapperService.updateFromMap(item, fields)

            item.lastname = item.lastname?.toUpperCase()
            item.firstname = item.firstname?.toUpperCase()
            item.middlename = item.middlename?.toUpperCase()
            item.identifications = newIdentifications
            item.contactNumbers = newContactNumbers

            investorsRepository.save(item)
        }
    }


    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation
    GraphQLRetVal<String> importInvestorProfile(
            @GraphQLArgument(name = "attachment") List<MultipartFile> attachment
    ) {
            attachment.each {

                BufferedReader fileReader = new BufferedReader(new InputStreamReader(it.inputStream, 'UTF-8'))
                CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withHeader("INVSTORS ID", "AR NUMBER", "LAST NAME", "FIRST NAME", "MIDDLE NAME", "SUFFIX"))
                Iterable<CSVRecord> csvRecord = csvParser.getRecords()

                csvRecord.each { CSVRecord record ->
                    String investorId = record.get("INVSTORS ID")
                    String arno = record.get("AR NUMBER")
                    String lastname = record.get("LAST NAME")
                    String firstname = record.get("FIRST NAME")
                    String middlename = record.get("MIDDLE NAME")
                    String suffix = record.get("SUFFIX")

                    investorId = investorId.size() > 6 ? investorId: investorId.padLeft(6, "0")
                    println(investorId)

                    List<Investor> foundInvestor = []
                    foundInvestor = investorsRepository.findByInvestorNo(investorId)

                    if (foundInvestor.size()>0) {
                        foundInvestor.each {

                            it.investorNo = investorId
                            it.arno = arno
                            it.lastname = lastname.toUpperCase()
                            it.firstname = firstname.toUpperCase()
                            it.middlename = middlename.toUpperCase()
                            it.suffix = suffix.toUpperCase()
                            investorsRepository.save(it)
                        }
                    } else {
                        Investor newInvestor = new Investor()
                        newInvestor.investorNo = investorId
                        newInvestor.arno = arno
                        newInvestor.lastname = lastname.toUpperCase()
                        newInvestor.firstname = firstname.toUpperCase()
                        newInvestor.middlename = middlename.toUpperCase()
                        newInvestor.suffix = suffix.toUpperCase()
                        investorsRepository.save(newInvestor)
                    }
                }
            }

        return new GraphQLRetVal<String>(null, true, "Haaayy salamat mana jd.")

    }

    @GraphQLMutation
    InvestorDependent upsertDependents(
            @GraphQLArgument(name = "investorId") UUID investorId,
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {

        def investor = investorsRepository.findById(investorId).get()
        def dependent = id ? investorDependentRepository.findById(id).get() : new InvestorDependent()
        dependent.investor = investor
        entityObjectMapperService.updateFromMap(dependent, fields)
        dependent.lastname = dependent.lastname?.toUpperCase()
        dependent.firstname = dependent.firstname?.toUpperCase()
        dependent.middlename = dependent.middlename?.toUpperCase()

        investorDependentRepository.save(dependent)

    }

}
