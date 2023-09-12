package com.hisd3.hismk2.graphqlservices.hrm.dtotransformer

import org.hibernate.transform.ResultTransformer

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class EmployeeAttendanceDtoTransformer implements ResultTransformer {

    Map<String, List<Map<String, Instant>>> logs = new TreeMap<String, List<Map<String, Instant>>>()

    @Override
    Object transformTuple(Object[] tuple, String[] aliases) {
        Instant dateTime = tuple[0] as Instant
        String type = tuple[1]

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd").withZone(ZoneId.systemDefault())
        DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH").withZone(ZoneId.systemDefault())
        String date = dateFormatter.format(dateTime)
        String hour = hourFormatter.format(dateTime)
        if (type == "IN") {
            def logsDate = logs.get(date)
            if (!logsDate) {
                logsDate = []
                logsDate.add(["IN": dateTime])
                logs.put(date, logsDate)
            } else {
                //get the last added
                Map<String, Instant> lastLog = logsDate.get(logsDate.size() - 1)
                Instant lastLogIn = lastLog.get("IN")
                if (lastLogIn) {
                    String lastLogDate = dateFormatter.format(lastLogIn)
                    String lastLogHour = hourFormatter.format(lastLogIn)
                    long seconds = Duration.between(lastLogIn, dateTime).seconds
                    if (!(lastLogDate == date && lastLogHour == hour)) {
                        if (lastLog.get("OUT") && seconds > 60 * 5) {
                            logsDate.add(["IN": dateTime])
                            logs.put(date, logsDate)
                        }
                    }
                }
            }
        } else if (type == "OUT") {
            Map.Entry lastEntry = logs.lastEntry()
            if (!lastEntry) {
                def logsDate = []
                logsDate.add([
                        "OUT": dateTime
                ])
                logs.put(date, logsDate)
            } else {
                String lastEntryKey = lastEntry.key
                List<Map<String, Instant>> lastEntryValue = lastEntry.value
                Map<String, Instant> lastLog = lastEntryValue.get(lastEntryValue.size() - 1)
                lastLog.put("OUT", dateTime)
                lastEntryValue.set(lastEntryValue.size() - 1, lastLog)
                logs.put(lastEntryKey, lastEntryValue)
            }
        }
        return null
    }

    @Override
    List transformList(List collection) {
        def finalList = new ArrayList()
        finalList.add(logs)
        return finalList
    }
}
