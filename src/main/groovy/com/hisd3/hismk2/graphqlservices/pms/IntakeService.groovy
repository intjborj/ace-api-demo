package com.hisd3.hismk2.graphqlservices.pms

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.hrm.Shift
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.Intake
import com.hisd3.hismk2.domain.pms.Output
import com.hisd3.hismk2.repository.hrm.ShiftRepository
import com.hisd3.hismk2.repository.pms.IntakeRepository
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
import java.text.DateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@TupleConstructor
class IntakeDTO {
    String shift
    Double value
}

@TupleConstructor
class OutputDTO {
    String shift
    Double value
}

@TupleConstructor
class IntakeOutputDTO {
    String title
    Double intakeTotal
    Double outputTotal
}

@Canonical
class ShiftAndIntakes {
    String fromTime
    String toTime
    String shiftName
    BigDecimal totalMl
    List<Intake> intakeList
}

@Canonical
class ShiftDTO {
    String intakesDate
    BigDecimal totalMl
    List<ShiftAndIntakes> shiftAndIntakes = []
}

@TypeChecked
@Component
@GraphQLApi
class IntakeService {

    @Autowired
    private IntakeRepository intakeRepository

    @Autowired
    private OutputRepository outputRepository

    @Autowired
    private ShiftRepository shiftRepository

    @Autowired
    ObjectMapper objectMapper

    //============== All Queries ====================

    @GraphQLQuery(name = "intakes", description = "Get all intakes")
    List<Intake> findAll() {
        return intakeRepository.findAll().sort { it.entryDateTime }
    }

    @GraphQLQuery(name = "intake", description = "Get Intake By Id")
    Intake findById(@GraphQLArgument(name = "id") UUID id) {
        return intakeRepository.findById(id).get()
    }

    @GraphQLQuery(name = "intakesByCase", description = "Get all Intakes by Case Id")
    List<Intake> getIntakesByCase(@GraphQLArgument(name = "caseId") UUID caseId) {
        return intakeRepository.getIntakesByCase(caseId)
    }

    @GraphQLQuery(name = "intakesByCasePageable", description = "Get all Intakes by Case Id")
    Page<Intake> getIntakesByCasePageable(
            @GraphQLArgument(name = "caseId") UUID caseId,
            @GraphQLArgument(name = 'date') Instant[] date,
            @GraphQLArgument(name = 'page') Integer page,
            @GraphQLArgument(name = 'pageSize') Integer pageSize
    ) {
        if (date) {
            return intakeRepository.getIntakesByCaseAndDatePageable(caseId, date[0], date[1], PageRequest.of(page, pageSize, Sort.Direction.ASC, 'entryDateTime'))
        }

        return intakeRepository.getIntakesByCasePageable(caseId, PageRequest.of(page, pageSize, Sort.Direction.ASC, 'entryDateTime'))
    }

    @GraphQLQuery(name = 'totalIntakes', description = 'total all intakes')
    Intake getTotalIntakes(
            @GraphQLArgument(name = "caseId") UUID caseId,
            @GraphQLArgument(name = 'date') Instant[] date
    ) {
        List<Intake> intakes = intakeRepository.getIntakesByCase(caseId).toList()

        if (date) {
            intakes = intakeRepository.getIntakesByCaseAndDate(caseId, date[0], date[1]).toList()
        }

        return new Intake().tap {
            poIntake = intakes.poIntake.sum(0) { it ?: 0 } as Float
            tubeIntake = intakes.tubeIntake.sum(0) { it ?: 0 } as Float
            ivfIntake = intakes.ivfIntake.sum(0) { it ?: 0 } as Float
            bloodIntake = intakes.bloodIntake.sum(0) { it ?: 0 } as Float
            tpnIntake = intakes.tpnIntake.sum(0) { it ?: 0 } as Float
            pbIntake = intakes.pbIntake.sum(0) { it ?: 0 } as Float
            medicationIntake = intakes.medicationIntake.sum(0) { it ?: 0 } as Float
        }
    }

    @GraphQLQuery(name = "intakesToday", description = "Get all today Intakes")
    Intake getIntakesToday(@GraphQLArgument(name = "caseId") UUID caseId) {
        List<Intake> intakes = intakeRepository.getIntakesToday(caseId, Instant.now().truncatedTo(ChronoUnit.DAYS))

        return new Intake().tap {
            poIntake = intakes.poIntake.sum(0) { it ?: 0 } as Float
            tubeIntake = intakes.tubeIntake.sum(0) { it ?: 0 } as Float
            ivfIntake = intakes.ivfIntake.sum(0) { it ?: 0 } as Float
            bloodIntake = intakes.bloodIntake.sum(0) { it ?: 0 } as Float
            tpnIntake = intakes.tpnIntake.sum(0) { it ?: 0 } as Float
            pbIntake = intakes.pbIntake.sum(0) { it ?: 0 } as Float
            medicationIntake = intakes.medicationIntake.sum(0) { it ?: 0 } as Float
        }
    }

    @GraphQLQuery(name = "ioByShifts", description = "Summary of intakes/ouputs by shifts")
    List<IntakeOutputDTO> ioByShifts(
            @GraphQLArgument(name = "caseId") UUID caseId,
            @GraphQLArgument(name = "fromDate") String fromDate,
            @GraphQLArgument(name = "toDate") String toDate) {

        List<IntakeOutputDTO> shiftValues = []

        List<Shift> shifts = shiftRepository.findAll().sort { a, b -> b.fromTime <=> a.fromTime }

        shifts.each {
            it ->
                LocalDateTime from = null
                LocalDateTime to = null
                /*
                  for all date to use slash instead of dash
                 */
                String[] fromStr = fromDate.split("/", 0)
                String[] toStr = toDate.split("/", 0)

                String fromStr1 = fromStr[2] + '-' + fromStr[0] + '-' + fromStr[1]
                String toStr1 = toStr[2] + '-' + toStr[0] + '-' + toStr[1]

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

                from = LocalDateTime.parse(fromStr1 + ' ' + it.fromTime + ':00', formatter)
                to = LocalDateTime.parse(toStr1 + ' ' + it.toTime + ':00', formatter)

                Instant iFrom = from.toInstant(ZoneOffset.UTC).minus(8, ChronoUnit.HOURS)

                if (it.moveBackDays > 0)
                    iFrom = from.toInstant(ZoneOffset.UTC).minus(8, ChronoUnit.HOURS).minus(it.moveBackDays, ChronoUnit.DAYS)

                Instant iTo = to.toInstant(ZoneOffset.UTC).minus(8, ChronoUnit.HOURS)

                List<Intake> intakes = intakeRepository.intakesByShifts(caseId, iFrom, iTo).sort {
                    it.entryDateTime
                }

                List<Output> outputs = outputRepository.outputsByShifts(caseId, iFrom, iTo).sort {
                    it.entryDateTime
                }

                Intake intake = new Intake().tap {
                    poIntake = intakes.poIntake.sum(0) { it ?: 0 } as Float
                    tubeIntake = intakes.tubeIntake.sum(0) { it ?: 0 } as Float
                    ivfIntake = intakes.ivfIntake.sum(0) { it ?: 0 } as Float
                    bloodIntake = intakes.bloodIntake.sum(0) { it ?: 0 } as Float
                    tpnIntake = intakes.tpnIntake.sum(0) { it ?: 0 } as Float
                    pbIntake = intakes.pbIntake.sum(0) { it ?: 0 } as Float
                    medicationIntake = intakes.medicationIntake.sum(0) { it ?: 0 } as Float
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

                Double totalIntake = intake.poIntake + intake.tubeIntake + intake.ivfIntake + intake.bloodIntake + intake.tpnIntake + intake.pbIntake + intake.medicationIntake
                Double totalOutput = output.voidedOutput + output.catheterOutput + output.ngOutput + output.insensibleLossOutput + output.bloodLoss + output.emesisOutput

                shiftValues.push(new IntakeOutputDTO(
                        it.description,
                        totalIntake,
                        totalOutput
                ))
        }

        return shiftValues

    }

    @GraphQLQuery(name = "intakesWithin24hrs", description = "Get all 24hrs Intakes")
    Intake intakesWithin24hrs(@GraphQLArgument(name = "caseId") UUID caseId) {
        List<Intake> intakes = intakeRepository.intakesWithin24hrs(caseId, Instant.now().minus(1, ChronoUnit.DAYS), Instant.now()).sort {
            it.entryDateTime
        }

        Intake intake = new Intake().tap {
            poIntake = intakes.poIntake.sum(0) { it ?: 0 } as Float
            tubeIntake = intakes.tubeIntake.sum(0) { it ?: 0 } as Float
            ivfIntake = intakes.ivfIntake.sum(0) { it ?: 0 } as Float
            bloodIntake = intakes.bloodIntake.sum(0) { it ?: 0 } as Float
            tpnIntake = intakes.tpnIntake.sum(0) { it ?: 0 } as Float
            pbIntake = intakes.pbIntake.sum(0) { it ?: 0 } as Float
            medicationIntake = intakes.medicationIntake.sum(0) { it ?: 0 } as Float
        }

        intake.total = intake.poIntake + intake.tubeIntake + intake.ivfIntake + intake.bloodIntake + intake.tpnIntake + intake.pbIntake + intake.medicationIntake as Float

        return intake
    }

    @GraphQLMutation
    Intake addIntakesForFlutter(
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {
        return intakeRepository.save(
            new Intake().tap {
                entryDateTime = Timestamp.valueOf(fields.get("entryDateTime") as String).toInstant()
                poIntake = (fields.get("poIntake") != '' ? fields.get("poIntake") : 0) as Float
                tubeIntake = (fields.get("tubeIntake") != '' ? fields.get("tubeIntake") : 0) as Float
                ivfIntake = (fields.get("ivfIntake") != '' ? fields.get("ivfIntake") : 0) as Float
                bloodIntake = (fields.get("bloodIntake") != '' ? fields.get("bloodIntake") : 0) as Float
                tpnIntake = (fields.get("tpnIntake") != '' ? fields.get("tpnIntake") : 0) as Float
                pbIntake = (fields.get("pbIntake") != '' ? fields.get("pbIntake") : 0) as Float
                remarks = fields.get("remarks") != '' ? fields.get("remarks") : null
                medicationIntake = (fields.get("medicationIntake") != '' ? fields.get("medicationIntake") : 0) as Float
                employee = objectMapper.convertValue(fields.get("employee"), Employee)
                parentCase = objectMapper.convertValue(fields.get("parentCase"), Case)
            }
        )
    }

    @TupleConstructor
    class DateIntakes {
        Instant date
        Instant newDate
    }

    @TupleConstructor
    class GroupedIntakes {
        LocalDate date;
        List<GroupedShiftIntake> groupedList
        BigDecimal totalIntakes
    }

    @TupleConstructor
    class GroupedShiftIntake {
        Instant fromTime
        Instant toTime
        String shiftName
        List<Intake> list
        BigDecimal shiftTotalIntakes;
    }

    @TupleConstructor
    static class OtherValue {
        String name
        String value
    }


    BigDecimal getTotalIntakesPerIntakes(Intake it){

        BigDecimal total = 0.0
        BigDecimal otherTotals = 0.0
        BigDecimal multiMedicationTotal = 0.0
        BigDecimal multiIVFTotal = 0.0

        if(it.others) {
            List<OtherValue> otherValues = objectMapper.readValue(it.others, new TypeReference<List<OtherValue>>() {})

            otherValues.each {
                otherTotals += it.value ? new BigDecimal(it.value) : 0.0
            }
        }

        if(it.multiMedication) {
            List<OtherValue> otherValues = objectMapper.readValue(it.multiMedication, new TypeReference<List<OtherValue>>() {})

            otherValues.each {
                multiMedicationTotal += it.value ? new BigDecimal(it.value) : 0.0
            }
        }

        if(it.multiIVF) {
            List<OtherValue> otherValues = objectMapper.readValue(it.multiIVF, new TypeReference<List<OtherValue>>() {})

            otherValues.each {
                multiIVFTotal += it.value ? new BigDecimal(it.value) : 0.0
            }
        }

        BigDecimal poIntake =  it.poIntake ? it.poIntake : 0
        BigDecimal tubeIntake = it.tubeIntake ? it.tubeIntake : 0
        BigDecimal ivfIntake = it.ivfIntake ? it.ivfIntake : 0
        BigDecimal bloodIntake = it.bloodIntake ? it.bloodIntake : 0
        BigDecimal tpnIntake = it.tpnIntake ? it.tpnIntake : 0
        BigDecimal pbIntake = it.pbIntake ? it.pbIntake : 0
        BigDecimal medicationIntake = it.medicationIntake ? it.medicationIntake : 0

        return  poIntake + tubeIntake + ivfIntake + bloodIntake + tpnIntake + pbIntake + medicationIntake + otherTotals + multiMedicationTotal + multiIVFTotal as Float
    }




    @GraphQLQuery(name = "intakesGroupedByDate", description = "Get all intakes grouped by date")
    List<ShiftDTO> intakesGroupedByDate(@GraphQLArgument(name = "caseId") UUID caseId) {

        List<Intake> intakes = intakeRepository.getIntakesByCase(caseId).sort {
            it.entryDateTime
        }

        List<Shift> allShifts = shiftRepository.findAll()


        Map<String,Map<Shift,List<Intake>>> intakeMap = [:]
        Map<String,Map<Shift,BigDecimal>> intakeMapTotal = [:]

        List<ShiftDTO> shiftDTOList = []

        intakes.each {

            intake->

                //ENTRY DATE
                LocalDateTime entryDT = LocalDateTime.ofInstant(intake.entryDateTime, ZoneOffset.UTC).plusHours(8)

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

                        if(!intakeMap[formatDate]){
                            ShiftDTO shiftDTO = new ShiftDTO()
                            shiftDTO.intakesDate = formatDate
                            shiftDTO.shiftAndIntakes = []
                            shiftDTO.totalMl = 0.00
                            shiftDTOList.push(shiftDTO)

                            Map<Shift,List<Intake>> shiftListMap = [:]
                            shiftListMap[it] = []
                            shiftListMap[it].push(intake)
                            intakeMap[formatDate] = shiftListMap

                            Map<Shift,BigDecimal> shiftListTotal = [:]
                            shiftListTotal[it] = getTotalIntakesPerIntakes(intake)
                            intakeMapTotal[formatDate] = shiftListTotal
                        }
                        else {
                            BigDecimal shiftTotal = intakeMapTotal[formatDate][it] ? intakeMapTotal[formatDate][it] : 0.00

                            if(!intakeMap[formatDate][it]) {
                                intakeMap[formatDate][it] = []
                                intakeMap[formatDate][it].push(intake)

                                intakeMapTotal[formatDate][it] =  (shiftTotal + getTotalIntakesPerIntakes(intake)).setScale(2, RoundingMode.HALF_EVEN)
                            }
                            else {
                                intakeMap[formatDate][it].push(intake)
                                intakeMapTotal[formatDate][it] = (shiftTotal + getTotalIntakesPerIntakes(intake)).setScale(2, RoundingMode.HALF_EVEN)

                            }
                        }
                    }
                }
        }


        shiftDTOList.each {
            et->
                allShifts.each {
                    shiftIndex->
                        ShiftAndIntakes shiftAndI = new ShiftAndIntakes()
                        shiftAndI.fromTime = shiftIndex.fromTime
                        shiftAndI.toTime = shiftIndex.toTime
                        shiftAndI.shiftName = shiftIndex.description
                        if(intakeMap[et.intakesDate][shiftIndex]){
                            shiftAndI.intakeList = intakeMap[et.intakesDate][shiftIndex]
                            shiftAndI.totalMl = intakeMapTotal[et.intakesDate][shiftIndex].setScale(2, RoundingMode.HALF_EVEN)
                            et.totalMl = (et.totalMl + intakeMapTotal[et.intakesDate][shiftIndex]).setScale(2, RoundingMode.HALF_EVEN)
                        }
                        else {
                            shiftAndI.totalMl = 0.00
                            shiftAndI.intakeList = []
                        }
                        et.shiftAndIntakes.push(shiftAndI)

                }

        }

        return  shiftDTOList
    }

    @GraphQLQuery(name = "intakesGroupedByDateV2", description = "Get all intakes grouped by date")
    List<GroupedIntakes> intakesGroupedByDateV2(@GraphQLArgument(name = "caseId") UUID caseId) {

        List<Intake> intakes = intakeRepository.getIntakesByCase(caseId).sort {
            it.entryDateTime
        }

        List<Shift> allShifts = shiftRepository.findAll()
        List<String> datesOnly = []
        List<DateIntakes> di = []
        def lastShift =  intakes.size() - 1

        if(intakes) {

            //list of dates from intakes
            intakes.each {
                LocalDateTime datetime = LocalDateTime.ofInstant(it.entryDateTime, ZoneOffset.UTC).plus(8, ChronoUnit.HOURS);
                String formatted = DateTimeFormatter.ofPattern("MM-dd-yyyy").format(datetime);
                datesOnly.add(formatted)
            }




//            String formatted = DateTimeFormatter.ofPattern("MM-dd-yyyy").format(datetime);
//            datesOnly.add(formatted)

            //remove all duplicate dates
            datesOnly = datesOnly.unique()

            List<GroupedIntakes> gIntakes = []
            datesOnly.each {dateOnly ->

                List<GroupedShiftIntake> gsIntakes = []

                BigDecimal overallTotal = 0.0

                allShifts.each {
                    DateTimeFormatter format = DateTimeFormatter.ofPattern("MM-dd-yyyy'T'HH:mm:ss")

                    Instant fromDT = LocalDateTime.parse(dateOnly + "T" + it.fromTime + ":00", format).toInstant(ZoneOffset.UTC).minus(8, ChronoUnit.HOURS)
                    Instant toDT = LocalDateTime.parse(dateOnly + "T" + it.toTime + ":00", format).toInstant(ZoneOffset.UTC).minus(8, ChronoUnit.HOURS)

                    if(it.moveBackDays > 0) {
                        fromDT = fromDT.minus(1, ChronoUnit.DAYS)
                    }
                    //toDT = toDT.plus(1, ChronoUnit.DAYS)

                    List<Intake> fDTList = intakeRepository.getIntakesByCaseAndDate(caseId, fromDT, toDT)

                    BigDecimal total = 0.0
                    BigDecimal otherTotals = 0.0
                    BigDecimal multiMedicationTotal = 0.0
                    BigDecimal multiIVFTotal = 0.0

                    fDTList.each {
                        if(it.others) {
                            List<OtherValue> otherValues = objectMapper.readValue(it.others, new TypeReference<List<OtherValue>>() {})

                            otherValues.each {
                                otherTotals += it.value ? new BigDecimal(it.value) : 0.0
                            }
                        }
                    }

                    fDTList.each {
                        if(it.multiMedication) {
                            List<OtherValue> otherValues = objectMapper.readValue(it.multiMedication, new TypeReference<List<OtherValue>>() {})

                            otherValues.each {
                                multiMedicationTotal += it.value ? new BigDecimal(it.value) : 0.0
                            }
                        }
                    }

                    fDTList.each {
                        if(it.multiIVF) {
                            List<OtherValue> otherValues = objectMapper.readValue(it.multiIVF, new TypeReference<List<OtherValue>>() {})

                            otherValues.each {
                                multiIVFTotal += it.value ? new BigDecimal(it.value) : 0.0
                            }
                        }
                    }

                    Intake intake = new Intake().tap {
                        poIntake = fDTList.poIntake.sum(0) { it ?: 0 } as Float
                        tubeIntake = fDTList.tubeIntake.sum(0) { it ?: 0 } as Float
                        ivfIntake = fDTList.ivfIntake.sum(0) { it ?: 0 } as Float
                        bloodIntake = fDTList.bloodIntake.sum(0) { it ?: 0 } as Float
                        tpnIntake = fDTList.tpnIntake.sum(0) { it ?: 0 } as Float
                        pbIntake = fDTList.pbIntake.sum(0) { it ?: 0 } as Float
                        medicationIntake = fDTList.medicationIntake.sum(0) { it ?: 0 } as Float
                    }

                    total = intake.poIntake + intake.tubeIntake + intake.ivfIntake + intake.bloodIntake + intake.tpnIntake + intake.pbIntake + intake.medicationIntake + otherTotals + multiMedicationTotal + multiIVFTotal as Float

                    overallTotal += total

                    gsIntakes.add(
                            new GroupedShiftIntake(
                                    fromDT,
                                    toDT,
                                    it.description,
                                    fDTList,
                                    total
                            )
                    )
                }

                gIntakes.add(
                        new GroupedIntakes(
                                LocalDate.parse(dateOnly, DateTimeFormatter.ofPattern("MM-dd-yyyy")),
                                gsIntakes,
                                overallTotal
                        )
                )
            }

            return gIntakes.sort{ it.date}.reverse(true)
        }


        return []
    }
}
