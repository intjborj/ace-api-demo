package com.hisd3.hismk2.domain.hrm.dto

import io.leangen.graphql.annotations.GraphQLQuery

import java.time.Instant

class EmployeeScheduleDto {

    public static final String ID_ALIAS = "e_s_id"
    public static final String DATE_TIME_START_ALIAS = "e_s_date_time_start"
    public static final String TIME_START_ALIAS = "e_s_time_start"
    public static final String DATE_TIME_START_RAW_ALIAS = "e_s_date_time_start_raw"
    public static final String DATE_TIME_END_ALIAS = "e_s_date_time_end"
    public static final String TIME_END_ALIAS = "e_s_time_end"
    public static final String DATE_TIME_END_RAW_ALIAS = "e_s_date_time_end_raw"
    public static final String COLOR_ALIAS = "e_s_color"
    public static final String IS_REST_DAY_ALIAS = "e_s_is_rest_day"
    public static final String IS_OVERTIME_ALIAS = "e_s_is_overtime"
    public static final String IS_CUSTOM_ALIAS = "e_s_is_custom"
    public static final String IS_OIC_ALIAS = "e_s_is_oic"
    public static final String IS_MULTI_DAY_ALIAS = "e_s_is_multi_day"
    public static final String IS_LEAVE_ALIAS = "e_s_is_leave"
    public static final String WITH_NSD_ALIAS = "e_s_with_nsd"
    public static final String WITH_HOLIDAY_ALIAS = "e_s_with_holiday"
    public static final String WITH_PAY_ALIAS = "e_s_with_pay"
    public static final String ASSIGNED_DATE_ALIAS = "e_s_assigned_date"
    public static final String TITLE_ALIAS = "e_s_title"
    public static final String LABEL_ALIAS = "e_s_label"
    public static final String LOCKED_ALIAS = "e_s_locked"
    public static final String MEAL_BREAK_START_ALIAS = "e_s_meal_break_start"
    public static final String MEAL_BREAK_END_ALIAS = "e_s_meal_break_end"
    public static final String REQUEST_ID_ALIAS = "r_request_id"
    public static final String DEPARTMENT_ALIAS = "esd_department_of_duty_id"
    public static final String DEPARTMENT_NAME_ALIAS = "esd_department_name"

    EmployeeScheduleDto(Object[] tuples, Map<String, Integer> aliasToIndexMap) {
        this.id = tuples[aliasToIndexMap.get(ID_ALIAS)] ? UUID.fromString(tuples[aliasToIndexMap.get(ID_ALIAS)] as String) : null
        this.dateTimeStart = tuples[aliasToIndexMap.get(DATE_TIME_START_ALIAS)]
        this.timeStart = tuples[aliasToIndexMap.get(TIME_START_ALIAS)]
        this.dateTimeStartRaw = tuples[aliasToIndexMap.get(DATE_TIME_START_RAW_ALIAS)] ? Instant.parse(tuples[aliasToIndexMap.get(DATE_TIME_START_RAW_ALIAS)] as String) : null
        this.dateTimeEnd = tuples[aliasToIndexMap.get(DATE_TIME_END_ALIAS)]
        this.timeEnd = tuples[aliasToIndexMap.get(TIME_END_ALIAS)]
        this.dateTimeEndRaw = tuples[aliasToIndexMap.get(DATE_TIME_END_RAW_ALIAS)] ? Instant.parse(tuples[aliasToIndexMap.get(DATE_TIME_END_RAW_ALIAS)] as String) : null
        this.color = tuples[aliasToIndexMap.get(COLOR_ALIAS)]
        this.isRestDay = tuples[aliasToIndexMap.get(IS_REST_DAY_ALIAS)]
        this.isOvertime = tuples[aliasToIndexMap.get(IS_OVERTIME_ALIAS)]
        this.isCustom = tuples[aliasToIndexMap.get(IS_CUSTOM_ALIAS)]
        this.isOIC = tuples[aliasToIndexMap.get(IS_OIC_ALIAS)]
        this.isLeave = tuples[aliasToIndexMap.get(IS_LEAVE_ALIAS)] ?: false
        this.isMultiDay = tuples[aliasToIndexMap.get(IS_MULTI_DAY_ALIAS)] ?: false
        this.withNSD = tuples[aliasToIndexMap.get(WITH_NSD_ALIAS)] ?: false
        this.withHoliday = tuples[aliasToIndexMap.get(WITH_HOLIDAY_ALIAS)] ?: false
        this.withPay = tuples[aliasToIndexMap.get(WITH_PAY_ALIAS)] ?: false
        this.assignedDate = tuples[aliasToIndexMap.get(ASSIGNED_DATE_ALIAS)] ? Instant.parse(tuples[aliasToIndexMap.get(ASSIGNED_DATE_ALIAS)] as String) : null
        this.title = tuples[aliasToIndexMap.get(TITLE_ALIAS)]
        this.label = tuples[aliasToIndexMap.get(LABEL_ALIAS)]
        this.locked = tuples[aliasToIndexMap.get(LOCKED_ALIAS)]
        this.mealBreakStart = tuples[aliasToIndexMap.get(MEAL_BREAK_START_ALIAS)] ? Instant.parse(tuples[aliasToIndexMap.get(MEAL_BREAK_START_ALIAS)] as String) : null
        this.mealBreakEnd = tuples[aliasToIndexMap.get(MEAL_BREAK_END_ALIAS)] ? Instant.parse(tuples[aliasToIndexMap.get(MEAL_BREAK_END_ALIAS)] as String) : null
        this.requestId = tuples[aliasToIndexMap.get(REQUEST_ID_ALIAS)] ? UUID.fromString(tuples[aliasToIndexMap.get(REQUEST_ID_ALIAS)] as String) : null
        this.department = tuples[aliasToIndexMap.get(DEPARTMENT_ALIAS)] ? UUID.fromString(tuples[aliasToIndexMap.get(DEPARTMENT_ALIAS)] as String) : null
        this.departmentName = tuples[aliasToIndexMap.get(DEPARTMENT_NAME_ALIAS)]
    }

    EmployeeScheduleDto() {}

    EmployeeScheduleDto(String color, String title, String label) {
        this.color = color
        this.title = title
        this.label = label
    }
    @GraphQLQuery
    private UUID id

    @GraphQLQuery
    private String dateTimeStart

    @GraphQLQuery
    private String timeStart

    @GraphQLQuery
    public Instant dateTimeStartRaw

    @GraphQLQuery
    private String dateTimeEnd

    @GraphQLQuery
    private String timeEnd

    @GraphQLQuery
    public Instant dateTimeEndRaw

    @GraphQLQuery
    private String color

    @GraphQLQuery
    public Boolean isRestDay

    @GraphQLQuery
    public Boolean isOvertime

    @GraphQLQuery
    public Boolean isCustom

    @GraphQLQuery
    public Boolean isOIC

    @GraphQLQuery
    public Boolean isMultiDay

    @GraphQLQuery
    public Boolean isLeave

    @GraphQLQuery
    public Boolean withNSD

    @GraphQLQuery
    public Boolean withHoliday

    @GraphQLQuery
    public Boolean withPay

    @GraphQLQuery
    public Instant assignedDate

    @GraphQLQuery
    public String title

    @GraphQLQuery
    public String label

    @GraphQLQuery
    private String locked

    @GraphQLQuery
    public Instant mealBreakStart

    @GraphQLQuery
    public Instant mealBreakEnd

    @GraphQLQuery
    public UUID requestId

    @GraphQLQuery
    public UUID department

    @GraphQLQuery
    public String departmentName

    public Boolean isStart = false

    public Boolean isContinuation = false

    Instant getDateTimeStartRaw() {
        return dateTimeStartRaw
    }

    UUID getId() {
        return id
    }

    String getDateTimeStart() {
        return dateTimeStart
    }

    String getDateTimeEnd() {
        return dateTimeEnd
    }

    Instant getDateTimeEndRaw() {
        return dateTimeEndRaw
    }

    String getColor() {
        return color
    }

    Boolean getIsRestDay() {
        return isRestDay
    }

    Boolean getIsOvertime() {
        return isOvertime
    }

    String getTitle() {
        return title
    }

    String getLabel() {
        return label
    }

    String getLocked() {
        return locked
    }

    String getTimeStart() {
        return timeStart
    }

    String getTimeEnd() {
        return timeEnd
    }

    Instant getMealBreakStart() {
        return mealBreakStart
    }

    void setMealBreakStart(Instant mealBreakStart) {
        this.mealBreakStart = mealBreakStart
    }

    Instant getMealBreakEnd() {
        return mealBreakEnd
    }

    void setMealBreakEnd(Instant mealBreakEnd) {
        this.mealBreakEnd = mealBreakEnd
    }
}
