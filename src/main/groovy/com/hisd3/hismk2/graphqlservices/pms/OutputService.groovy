package com.hisd3.hismk2.graphqlservices.pms

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.hrm.Shift
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.Intake
import com.hisd3.hismk2.domain.pms.Output
import com.hisd3.hismk2.repository.hrm.ShiftRepository
import com.hisd3.hismk2.repository.pms.OutputRepository
import groovy.transform.Canonical
import groovy.transform.TupleConstructor
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

import java.math.RoundingMode
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit



@Canonical
class ShiftAndOutputs {
    String fromTime
    String toTime
    String shiftName
    BigDecimal totalMl
    List<Output> outputList
}

@Canonical
class ShiftOutputsDTO {
    String outputsDate
    BigDecimal totalMl
    List<ShiftAndOutputs> shiftAndOutputs = []
}


@TypeChecked
@Component
@GraphQLApi
class OutputService {

    @Autowired
    private OutputRepository outputRepository

    @Autowired
    private ShiftRepository shiftRepository

    @Autowired
    ObjectMapper objectMapper

    //============== All Queries ====================

    @GraphQLQuery(name = "outputs", description = "Get all Outputs")
    List<Output> findAll() {
        return outputRepository.findAll().sort { it.entryDateTime }
    }

    @GraphQLQuery(name = "output", description = "Get Output By Id")
    Output findById(@GraphQLArgument(name = "id") UUID id) {
        return outputRepository.findById(id).get()
    }

    @GraphQLQuery(name = "outputsByCase", description = "Get all Outputs by Case Id")
    List<Output> getOutputsByCase(@GraphQLArgument(name = "caseId") UUID caseId) {
        return outputRepository.getOutputsByCase(caseId)
    }

    @GraphQLQuery(name = "outputsByCasePageable", description = "Get all Outputs by Case Id")
    Page<Output> getOutputsByCasePageable(
            @GraphQLArgument(name = "caseId") UUID caseId,
            @GraphQLArgument(name = 'date') Instant[] date,
            @GraphQLArgument(name = 'page') Integer page,
            @GraphQLArgument(name = 'pageSize') Integer pageSize
    ) {
        if (date) {
            return outputRepository.getOutputsByCaseAndDatePageable(caseId, date[0], date[1], PageRequest.of(page, pageSize, Sort.Direction.ASC, 'entryDateTime'))
        }

        return outputRepository.getOutputsByCasePageable(caseId, PageRequest.of(page, pageSize, Sort.Direction.ASC, 'entryDateTime'))
    }

    @GraphQLQuery(name = 'totalOutputs', description = 'total all intakes')
    Output getTotalOuput(
            @GraphQLArgument(name = "caseId") UUID caseId,
            @GraphQLArgument(name = 'date') Instant[] date
    ) {
        List<Output> outputs = outputRepository.getOutputsByCase(caseId).toList()

        if (date) {
            outputs = outputRepository.getOutputsByCaseAndDate(caseId, date[0], date[1])
        }

        return new Output().tap {
            voidedOutput = outputs.voidedOutput.sum(0) { it ?: 0 } as Float
            catheterOutput = outputs.catheterOutput.sum(0) { it ?: 0 } as Float
            ngOutput = outputs.ngOutput.sum(0) { it ?: 0 } as Float
            insensibleLossOutput = outputs.insensibleLossOutput.sum(0) { it ?: 0 } as Float
            bloodLoss = outputs.bloodLoss.sum(0) { it ?: 0 } as Float
            stoolOutput = outputs.stoolOutput.sum(0) { it ?: 0 } as Integer
            emesisOutput = outputs.emesisOutput.sum(0) { it ?: 0 } as Float
        }
    }

    @GraphQLQuery(name = "outputsToday", description = "Get all today Outputs")
    Output getOutputsToday(@GraphQLArgument(name = "caseId") UUID caseId) {
        List<Output> outputs = outputRepository.getOutputsToday(caseId, Instant.now().truncatedTo(ChronoUnit.DAYS)).sort {
            it.entryDateTime
        }

        return new Output().tap {
            voidedOutput = outputs.voidedOutput.sum(0) { it ?: 0 } as Float
            catheterOutput = outputs.catheterOutput.sum(0) { it ?: 0 } as Float
            ngOutput = outputs.ngOutput.sum(0) { it ?: 0 } as Float
            insensibleLossOutput = outputs.insensibleLossOutput.sum(0) { it ?: 0 } as Float
            bloodLoss = outputs.bloodLoss.sum(0) { it ?: 0 } as Float
            stoolOutput = outputs.stoolOutput.sum(0) { it ?: 0 } as Integer
            emesisOutput = outputs.emesisOutput.sum(0) { it ?: 0 } as Float
        }
    }

    @GraphQLQuery(name = "outputsWithin24hrs", description = "Get all 24hrs Outputs")
    Output outputsWithin24hrs(@GraphQLArgument(name = "caseId") UUID caseId) {
        List<Output> outputs = outputRepository.outputsWithin24hrs(caseId, Instant.now().minus(1, ChronoUnit.DAYS), Instant.now()).sort {
            it.entryDateTime
        }

        Output output = new Output().tap {
            voidedOutput = outputs.voidedOutput.sum(0) { it ?: 0 } as Float
            catheterOutput = outputs.catheterOutput.sum(0) { it ?: 0 } as Float
            ngOutput = outputs.ngOutput.sum(0) { it ?: 0 } as Float
            insensibleLossOutput = outputs.insensibleLossOutput.sum(0) { it ?: 0 } as Float
            bloodLoss = outputs.bloodLoss.sum(0) { it ?: 0 } as Float
            stoolOutput = outputs.stoolOutput.sum(0) { it ?: 0 } as Integer
            emesisOutput = outputs.emesisOutput.sum(0) { it ?: 0 } as Float
        }

        output.total = output.voidedOutput + output.catheterOutput + output.ngOutput + output.insensibleLossOutput + output.bloodLoss + output.stoolOutput + output.emesisOutput as Float

        return output
    }

    @GraphQLMutation
    Output addOutputsForFlutter(
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {
        return outputRepository.save(
                new Output().tap {
                    entryDateTime = Timestamp.valueOf(fields.get("entryDateTime") as String).toInstant()
                    voidedOutput = (fields.get("voidedOutput") != '' ? fields.get("voidedOutput") : 0) as Float
                    catheterOutput = (fields.get("catheterOutput") != '' ? fields.get("catheterOutput") : 0) as Float
                    ngOutput = (fields.get("ngOutput") != '' ? fields.get("ngOutput") : 0) as Float
                    insensibleLossOutput = (fields.get("insensibleLossOutput") != '' ? fields.get("insensibleLossOutput") : 0) as Float
                    bloodLoss = (fields.get("bloodLoss") != '' ? fields.get("bloodLoss") : 0) as Float
                    stoolOutput = (fields.get("stoolOutput") != '' ? fields.get("stoolOutput") : 0) as Integer
                    emesisOutput = (fields.get("emesisOutput") != '' ? fields.get("emesisOutput") : 0) as Float
                    remarks = fields.get("remarks") != '' ? fields.get("remarks") : null
                    drainage = fields.get("drainage") != '' ? fields.get("drainage") : null
                    employee = objectMapper.convertValue(fields.get("employee"), Employee)
                    parentCase = objectMapper.convertValue(fields.get("parentCase"), Case)
                }
        )
    }

    @TupleConstructor
    class DateOutput {
        Instant date
        Instant newDate
    }

    @TupleConstructor
    class GroupedOutputs {
        LocalDate date;
        List<GroupedShiftOutput> groupedList
        BigDecimal totalOutputs
    }

    @TupleConstructor
    class GroupedShiftOutput {
        Instant fromTime
        Instant toTime
        String shiftName
        List<Output> list
        BigDecimal shiftTotalOutputs;
    }

    @TupleConstructor
    static class OtherValue {
        String name
        String value
    }


    BigDecimal getTotalOutputsPerOutputs(Output it){

        BigDecimal total = 0.0
        BigDecimal otherTotals = 0.0


            if(it.drainage) {
                List<OtherValue> otherValues = objectMapper.readValue(it.drainage, new TypeReference<List<OtherValue>>() {})

                otherValues.each {
                    otherTotals += it.value ? new BigDecimal(it.value) : 0.0
                }
            }

        BigDecimal voidedOutput = it.voidedOutput? it.voidedOutput : 0
        BigDecimal catheterOutput = it.catheterOutput? it.catheterOutput : 0
        BigDecimal ngOutput = it.ngOutput? it.ngOutput : 0
        BigDecimal insensibleLossOutput = it.insensibleLossOutput? it.insensibleLossOutput : 0
        BigDecimal bloodLoss = it.bloodLoss? it.bloodLoss : 0
        BigDecimal stoolOutput = it.stoolOutput? it.stoolOutput : 0
        BigDecimal emesisOutput = it.emesisOutput? it.emesisOutput : 0

        return  voidedOutput + catheterOutput + ngOutput + insensibleLossOutput + bloodLoss + stoolOutput + emesisOutput + otherTotals as Float
    }


    @GraphQLQuery(name = "outputsGroupedByDate", description = "Get all outputs grouped by date")
    List<ShiftOutputsDTO> outputsGroupedByDate(@GraphQLArgument(name = "caseId") UUID caseId) {

        List<Output> outputs = outputRepository.getOutputsByCase(caseId).sort {
            it.entryDateTime
        }

        List<Shift> allShifts = shiftRepository.findAll()

        Map<String,Map<Shift,List<Output>>> outputMap = [:]
        Map<String,Map<Shift,BigDecimal>> outputMapTotal = [:]

        List<ShiftOutputsDTO> shiftDTOList = []

        outputs.each {

            output->

                //ENTRY DATE
                LocalDateTime entryDT = LocalDateTime.ofInstant(output.entryDateTime, ZoneOffset.UTC).plusHours(8)

                //ENTRY TIME
                LocalTime entryT = entryDT.truncatedTo(ChronoUnit.SECONDS).toLocalTime()

                //SHIFTS (PM, AM, NOC)
                allShifts.each {

                    //SHIFT FROM TIME
                    LocalTime fromT = LocalTime.parse(it.fromTime)
                    //SHIFT TO TIME
                    LocalTime toT = LocalTime.parse(it.toTime)
                    //ENTRY DATETIME IS BEFORE SHIFT FROM TIME
                    def after = entryT.isAfter(fromT)

                    //ENTRY DATETIME IS BEFORE SHIFT FROM TIME
                    def before = entryT.isBefore(toT)
                    def checkReverse = fromT.isAfter(toT)

                    if(
                            (checkReverse && ( after || before))
                                    ||
                                    (after && before)
                                    ||
                                    (entryT == fromT || entryT == toT)
                    ){

                        if(checkReverse && after) {
                            entryDT = entryDT.plus(1, ChronoUnit.DAYS)
                        }

                        //Need Clarification from dev for moveBackDays

//                        if(it.moveBackDays > 0) {
//                            entryDT = entryDT.minus(1, ChronoUnit.DAYS)
//                        }

                        String formatDate = DateTimeFormatter.ofPattern("MM-dd-yyyy").format(entryDT);

                        if(!outputMap[formatDate]){
                            ShiftOutputsDTO shiftDTO = new ShiftOutputsDTO()
                            shiftDTO.outputsDate = formatDate
                            shiftDTO.shiftAndOutputs = []
                            shiftDTO.totalMl = 0.00
                            shiftDTOList.push(shiftDTO)

                            Map<Shift,List<Output>> shiftListMap = [:]
                            shiftListMap[it] = []
                            shiftListMap[it].push(output)
                            outputMap[formatDate] = shiftListMap

                            Map<Shift,BigDecimal> shiftListTotal = [:]
                            shiftListTotal[it] = getTotalOutputsPerOutputs(output)
                            outputMapTotal[formatDate] = shiftListTotal
                        }
                        else {
                            BigDecimal shiftTotal = outputMapTotal[formatDate][it] ? outputMapTotal[formatDate][it] : 0.00

                            if(!outputMap[formatDate][it]) {
                                outputMap[formatDate][it] = []
                                outputMap[formatDate][it].push(output)
                                outputMapTotal[formatDate][it] = (shiftTotal + getTotalOutputsPerOutputs(output)).setScale(2, RoundingMode.HALF_EVEN)
                            }
                            else {
                                outputMap[formatDate][it].push(output)
                                outputMapTotal[formatDate][it] = (shiftTotal + getTotalOutputsPerOutputs(output)).setScale(2, RoundingMode.HALF_EVEN)

                            }
                        }
                    }
                }
        }


        shiftDTOList.each {
            et->
                allShifts.each {
                    shiftIndex->
                        ShiftAndOutputs shiftAndO = new ShiftAndOutputs()
                        shiftAndO.fromTime = shiftIndex.fromTime
                        shiftAndO.toTime = shiftIndex.toTime
                        shiftAndO.shiftName = shiftIndex.description
                        if(outputMap[et.outputsDate][shiftIndex]){
                            shiftAndO.outputList = outputMap[et.outputsDate][shiftIndex]
                            shiftAndO.totalMl = outputMapTotal[et.outputsDate][shiftIndex].setScale(2, RoundingMode.HALF_EVEN)
                            et.totalMl = (et.totalMl + outputMapTotal[et.outputsDate][shiftIndex]).setScale(2, RoundingMode.HALF_EVEN)
                        }
                        else {
                            shiftAndO.totalMl = 0.00
                            shiftAndO.outputList = []
                        }
                        et.shiftAndOutputs.push(shiftAndO)

                }

        }

        return  shiftDTOList

    }


    @GraphQLQuery(name = "outputsGroupedByDateV2", description = "Get all outputs grouped by date")
    List<GroupedOutputs> outputsGroupedByDateV2(@GraphQLArgument(name = "caseId") UUID caseId) {
        List<Output> outputs = outputRepository.getOutputsByCase(caseId).sort {
            it.entryDateTime
        }

        List<Shift> allShifts = shiftRepository.findAll()
        List<String> datesOnly = []
        List<DateOutput> di = []

        if(outputs) {

            //list of dates from outputs
            outputs.each {
                LocalDateTime datetime = LocalDateTime.ofInstant(it.entryDateTime, ZoneOffset.UTC).plus(8, ChronoUnit.HOURS);;
                String formatted = DateTimeFormatter.ofPattern("MM-dd-yyyy").format(datetime);
                datesOnly.add(formatted)
            }

            //remove all duplicate dates
            datesOnly = datesOnly.unique()

            List<GroupedOutputs> gOutputs = []
            datesOnly.each {dateOnly ->

                List<GroupedShiftOutput> gsOutputs = []

                BigDecimal overallTotal = 0.0

                allShifts.each {
                    DateTimeFormatter format = DateTimeFormatter.ofPattern("MM-dd-yyyy'T'HH:mm:ss")

                    Instant fromDT = LocalDateTime.parse(dateOnly + "T" + it.fromTime + ":00", format).toInstant(ZoneOffset.UTC).minus(8, ChronoUnit.HOURS)
                    Instant toDT = LocalDateTime.parse(dateOnly + "T" + it.toTime + ":00", format).toInstant(ZoneOffset.UTC).minus(8, ChronoUnit.HOURS)

                    if(it.moveBackDays)
                        fromDT = fromDT.minus(1, ChronoUnit.DAYS)

                    List<Output> fDTList = outputRepository.getOutputsByCaseAndDate(caseId, fromDT, toDT)

                    BigDecimal total = 0.0
                    BigDecimal otherTotals = 0.0

                    fDTList.each {
                        if(it.drainage) {
                            List<OtherValue> otherValues = objectMapper.readValue(it.drainage, new TypeReference<List<OtherValue>>() {})

                            otherValues.each {
                                otherTotals += it.value ? new BigDecimal(it.value) : 0.0
                            }
                        }
                    }

                    Output output = new Output().tap {
                        voidedOutput = fDTList.voidedOutput.sum(0) { it ?: 0 } as Float
                        catheterOutput = fDTList.catheterOutput.sum(0) { it ?: 0 } as Float
                        ngOutput = fDTList.ngOutput.sum(0) { it ?: 0 } as Float
                        insensibleLossOutput = fDTList.insensibleLossOutput.sum(0) { it ?: 0 } as Float
                        bloodLoss = fDTList.bloodLoss.sum(0) { it ?: 0 } as Float
                        stoolOutput = fDTList.stoolOutput.sum(0) { it ?: 0 } as Integer
                        emesisOutput = fDTList.emesisOutput.sum(0) { it ?: 0 } as Float
                    }

                    total = output.voidedOutput + output.catheterOutput + output.ngOutput + output.insensibleLossOutput + output.bloodLoss + output.emesisOutput + otherTotals as Float

                    overallTotal += total

                    gsOutputs.add(
                            new GroupedShiftOutput(
                                    fromDT,
                                    toDT,
                                    it.description,
                                    fDTList,
                                    total
                            )
                    )
                }

                gOutputs.add(
                        new GroupedOutputs(
                                LocalDate.parse(dateOnly, DateTimeFormatter.ofPattern("MM-dd-yyyy")),
                                gsOutputs,
                                overallTotal
                        )
                )
            }

            return gOutputs.sort{ it.date}.reverse(true)
        }


        return []
    }
}
