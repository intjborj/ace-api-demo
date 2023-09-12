package com.hisd3.hismk2.graphqlservices.hrm.dtotransformer

import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.hrm.dto.EmployeeScheduleDto
import org.hibernate.transform.ResultTransformer

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class EmployeeScheduleDtoTransformer implements ResultTransformer {

    private Map<String, Object> scheduleDtoMap = new LinkedHashMap<>()
    private Boolean isShort = false

    EmployeeScheduleDtoTransformer(Boolean isShort) {
        this.isShort = isShort
    }

    EmployeeScheduleDtoTransformer() {}

    @Override
    Object transformTuple(Object[] tuple, String[] aliases) {
        Map<String, Integer> aliasToIndexMap = aliasToIndexMap(aliases)

        EmployeeScheduleDto employeeScheduleDto = new EmployeeScheduleDto()
        employeeScheduleDto.dateTimeStartRaw = Instant.parse(tuple[0] as String)
        employeeScheduleDto.dateTimeEndRaw = Instant.parse(tuple[1] as String)
        employeeScheduleDto.isRestDay = tuple[2]
        employeeScheduleDto.isOvertime = tuple[3]
        employeeScheduleDto.mealBreakStart = tuple[4] ? Instant.parse(tuple[4] as String) : null
        employeeScheduleDto.mealBreakEnd = tuple[5] ? Instant.parse(tuple[5] as String) : null
        employeeScheduleDto.isOIC = tuple[6]
        employeeScheduleDto.withNSD = tuple[7]
        employeeScheduleDto.withHoliday = tuple[8]
        employeeScheduleDto.withPay = tuple[9]
        employeeScheduleDto.isLeave = tuple[10]
        employeeScheduleDto.assignedDate = tuple[11] ? Instant.parse(tuple[11] as String) : null
        employeeScheduleDto.department = tuple[12]['id'] ? UUID.fromString(tuple[12]['id'] as String) : null
        employeeScheduleDto.title = tuple[13]
        employeeScheduleDto.label = tuple[14] ? tuple[14] : null

        if (employeeScheduleDto.getDateTimeStartRaw()) {
            Instant str = employeeScheduleDto.assignedDate ? employeeScheduleDto.assignedDate : employeeScheduleDto.getDateTimeStartRaw()
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd").withZone(ZoneId.systemDefault())
            String dateTime = formatter.format(str)
//            if (employeeScheduleDto.getIsRestDay()) {
//                String key = "${dateTime}_REST"
//                scheduleDtoMap.computeIfAbsent(key, {
//                    it -> employeeScheduleDto
//                })
//            }
            if (employeeScheduleDto.isLeave && !employeeScheduleDto.isRestDay) {
                String key = "${dateTime}_LEAVE"
                scheduleDtoMap.put(key, employeeScheduleDto)
            } else if (employeeScheduleDto.getIsOvertime() && !employeeScheduleDto.isOIC) {
                String key = "${dateTime}_OVERTIME"
                if (!scheduleDtoMap.get(key)) {
                    scheduleDtoMap.put(key as String, [employeeScheduleDto])
                } else {
                    List<EmployeeScheduleDto> overtimeList = this.scheduleDtoMap[key] as List<EmployeeScheduleDto> ?: []
                    overtimeList.add(employeeScheduleDto)
                    scheduleDtoMap[key] = overtimeList
                }
            } else if (employeeScheduleDto.getIsOvertime() && employeeScheduleDto.isOIC) {
                String key = isShort ? "${dateTime}_OVERTIME" : "${dateTime}_OVERTIME_OIC"
                if (!scheduleDtoMap.get(key)) {
                    scheduleDtoMap.put(key as String, [employeeScheduleDto])
                } else {
                    List<EmployeeScheduleDto> overtimeList = scheduleDtoMap.get(key) as List<EmployeeScheduleDto>
                    overtimeList.add(employeeScheduleDto)
                }
            } else if (employeeScheduleDto.isOIC) {
                String key = "${dateTime}_OIC"
                scheduleDtoMap.computeIfAbsent(key, {
                    it -> employeeScheduleDto
                })
            } else {
                scheduleDtoMap.put(dateTime, employeeScheduleDto)
            }
        }

        return employeeScheduleDto
    }

    @Override
    List<Map<String, Object>> transformList(List collection) {
        List<Map<String, Object>> finalList = new ArrayList<Map<String, Object>>()
        finalList.add(scheduleDtoMap)
        return finalList
    }

    Map<String, Integer> aliasToIndexMap(String[] aliases) {
        Map<String, Integer> aliasToIndexMap = new LinkedHashMap<>()
        for (int i = 0; i < aliases.length; i++) {
            aliasToIndexMap.put(aliases[i], i)
        }
        return aliasToIndexMap
    }
}
