package com.hisd3.hismk2.domain.accounting.dto

import io.leangen.graphql.annotations.GraphQLQuery

// This Billing Item Dto is for the Account Receivable Billing Schedule module
class CustomBillingItemNativeDto {

    public static final String ROW_NUM_ALIAS = "rowNum"
    public static final String ID_ALIAS = "id"
    public static final String RECORD_NO_ALIAS = "recordNo"
    public static final String TRANSACTION_DATE = "transactionDate"
    public static final String ITEM_TYPE_ALIAS = "itemType"
    public static final String DESCRIPTION_ALIAS = "description"
    public static final String BILLING_ALIAS = "billing"
    public static final String CREDIT_ALIAS = "credit"
    public static final String FULL_COUNT_ALIAS = "fullCount"

    CustomBillingItemNativeDto (Object[] tuple, Map<String, Integer> aliasToIndexMap) {
        this.id = UUID.fromString(tuple[aliasToIndexMap.get(ID_ALIAS)] as String)
        this.rowNum = tuple[aliasToIndexMap.get(ROW_NUM_ALIAS)] as Integer
        this.transactionDate = tuple[aliasToIndexMap.get(TRANSACTION_DATE)]
        this.recordNo = tuple[aliasToIndexMap.get(RECORD_NO_ALIAS)]
        this.itemType = tuple[aliasToIndexMap.get(ITEM_TYPE_ALIAS)]
        this.description = tuple[aliasToIndexMap.get(DESCRIPTION_ALIAS)]
        this.billingStr = tuple[aliasToIndexMap.get(BILLING_ALIAS)] ? tuple[aliasToIndexMap.get(BILLING_ALIAS)] : null
        this.credit = tuple[aliasToIndexMap.get(CREDIT_ALIAS)] as BigDecimal
        this.fullCount = tuple[aliasToIndexMap.get(FULL_COUNT_ALIAS)] as Integer
    }

    CustomBillingItemNativeDto(){}

    @GraphQLQuery
    private UUID id

    @GraphQLQuery
    private Integer rowNum

    @GraphQLQuery
    private String recordNo

    @GraphQLQuery
    private  String itemType

    @GraphQLQuery
    private String description

    @GraphQLQuery
    public BillingDto billing

    @GraphQLQuery
    private String billingStr

    @GraphQLQuery
    private BigDecimal credit

    @GraphQLQuery
    private Integer fullCount

    @GraphQLQuery
    private String transactionDate

    UUID getId() {
        return id
    }

    Integer getRowNum(){
        return rowNum
    }

    String getRecordNo(){
        return  recordNo
    }

    String getItemType(){
        return itemType
    }

    String getDescription(){
        return description
    }

    String getBillingStr(){
        return billingStr
    }

    BillingDto getBilling(){
        return billing
    }

    BigDecimal getCredit(){
        return credit
    }

    Integer getFullCount(){
        return fullCount
    }

    String getTransactionDate(){
        return transactionDate
    }
}
