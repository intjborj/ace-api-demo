package com.hisd3.hismk2.graphqlservices.hrm.dtotransformer

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hrm.dto.EmployeeDto
import com.hisd3.hismk2.domain.hrm.dto.EmployeeScheduleDto
import org.hibernate.transform.ResultTransformer
import org.springframework.beans.factory.annotation.Autowired

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

class EmployeeDtoTransformer implements ResultTransformer {

    @Autowired
    ObjectMapper objectMapper

    private Map<String, EmployeeDto> employeeDtoMap = new LinkedHashMap<>();

    @Override
    Object transformTuple(Object[] tuple, String[] aliases) {
        Map<String, Integer> aliasToIndexMap = aliasToIndexMap(aliases);

        String employeeId = tuple[aliasToIndexMap.get(EmployeeDto.ID_ALIAS)] as String;

        EmployeeDto employeeDto = employeeDtoMap.computeIfAbsent(
                employeeId,
                { id -> new EmployeeDto(tuple, aliasToIndexMap) }
        );

        EmployeeScheduleDto employeeScheduleDto = new EmployeeScheduleDto(tuple, aliasToIndexMap)

        if (employeeScheduleDto.getDateTimeStartRaw()) {
            Instant str = employeeScheduleDto.assignedDate ? employeeScheduleDto.assignedDate : employeeScheduleDto.getDateTimeStartRaw()
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM_dd_yyyy").withZone(ZoneId.systemDefault())
            String dateTime = formatter.format(str)
            if (employeeScheduleDto.isMultiDay) {
                List<Instant> days = daysScheduleHasPass(employeeScheduleDto.dateTimeStartRaw, employeeScheduleDto.dateTimeEndRaw)
                if (!days.isEmpty())
                    days.removeLast()
                days.eachWithIndex { Instant entry, int i ->
                    String dateFormat = formatter.format(entry)
                    EmployeeScheduleDto newSchedule = new EmployeeScheduleDto(tuple, aliasToIndexMap)
                    if (i == 0) newSchedule.isStart = true
                    else newSchedule.isContinuation = true
                    employeeDto.getEmployeeSchedule().put(dateFormat, newSchedule)
                }
            } else {
                if (employeeScheduleDto.isLeave) {
                    String key = "${dateTime}_LEAVE"
                    if (employeeScheduleDto.isRestDay)
                        key = "${dateTime}_LEAVE_REST"
                    employeeDto.getEmployeeSchedule().put(key, employeeScheduleDto)
                } else if (employeeScheduleDto.getIsOvertime() && !employeeScheduleDto.isOIC) {
                    if (!employeeDto.getEmployeeSchedule().get("${dateTime}_OVERTIME")) {
                        employeeDto.getEmployeeSchedule().put("${dateTime}_OVERTIME" as String, [employeeScheduleDto])
                    } else {
                        List<EmployeeScheduleDto> overtimeList = employeeDto.getEmployeeSchedule().get("${dateTime}_OVERTIME") as List<EmployeeScheduleDto>
                        overtimeList.add(employeeScheduleDto)
                    }
                } else if (employeeScheduleDto.getIsOvertime() && employeeScheduleDto.isOIC) {
                    if (!employeeDto.getEmployeeSchedule().get("${dateTime}_OVERTIME_OIC")) {
                        employeeDto.getEmployeeSchedule().put("${dateTime}_OVERTIME_OIC" as String, [employeeScheduleDto])
                    } else {
                        List<EmployeeScheduleDto> overtimeList = employeeDto.getEmployeeSchedule().get("${dateTime}_OVERTIME_OIC") as List<EmployeeScheduleDto>
                        overtimeList.add(employeeScheduleDto)
                    }
                } else if (employeeScheduleDto.isOIC) {
                    String key = "${dateTime}_OIC"
                    employeeDto.getEmployeeSchedule().put(key, employeeScheduleDto)
                } else {
                    employeeDto.getEmployeeSchedule().put(dateTime, employeeScheduleDto)
                }
            }
        }

        return employeeDto;
    }

    @Override
    List<EmployeeDto> transformList(List collection) {
        return new ArrayList<EmployeeDto>(employeeDtoMap.values())
    }

    List<Instant> daysScheduleHasPass(Instant start, Instant end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd").withZone(ZoneId.systemDefault())
        Instant startDay = start.atZone(ZoneId.systemDefault())
                .with(ChronoField.HOUR_OF_DAY, 0)
                .with(ChronoField.MINUTE_OF_HOUR, 0)
                .with(ChronoField.SECOND_OF_MINUTE, 0)
                .with(ChronoField.MILLI_OF_SECOND, 0)
                .toInstant()
        Instant endDate = end.atZone(ZoneId.systemDefault())
                .with(ChronoField.HOUR_OF_DAY, 0)
                .with(ChronoField.MINUTE_OF_HOUR, 0)
                .with(ChronoField.SECOND_OF_MINUTE, 0)
                .with(ChronoField.MILLI_OF_SECOND, 0)
                .toInstant()

        List<Instant> dates = []
        while (startDay <= endDate) {
            dates.add(startDay)
            startDay = startDay.plus(1, ChronoUnit.DAYS)
        }

        return dates
    }

    static Map<String, Integer> aliasToIndexMap(String[] aliases) {
        Map<String, Integer> aliasToIndexMap = new LinkedHashMap<>();
        for (int i = 0; i < aliases.length; i++) {
            aliasToIndexMap.put(aliases[i], i);
        }
        return aliasToIndexMap;
    }
}
