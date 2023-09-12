package com.hisd3.hismk2.graphqlservices.accounting.transformers

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.accounting.dto.BillingDto
import com.hisd3.hismk2.domain.accounting.dto.CustomBillingItemNativeDto
import org.hibernate.transform.ResultTransformer
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

//bean
//singleton
//prototype
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
class BillingScheduleTransform implements ResultTransformer {


    private List<CustomBillingItemNativeDto> list = new ArrayList<CustomBillingItemNativeDto>()

    @Override
    Object transformTuple(Object[] tuple, String[] aliases) {
        Map<String, Integer> aliasToIndexMap = aliasToIndexMap(aliases);

        CustomBillingItemNativeDto customBillingItemNativeDto = new CustomBillingItemNativeDto(tuple,aliasToIndexMap)
        ObjectMapper obj = new ObjectMapper()
        customBillingItemNativeDto.billing = obj.readValue(customBillingItemNativeDto.billingStr,BillingDto.class)
        list.add(customBillingItemNativeDto)
        return customBillingItemNativeDto
    }

    @Override
    List<CustomBillingItemNativeDto> transformList(List collection) {
        return new ArrayList<CustomBillingItemNativeDto>(list)
    }

    static Map<String, Integer> aliasToIndexMap(String[] aliases) {
        Map<String, Integer> aliasToIndexMap = new LinkedHashMap<>();
        for (int i = 0; i < aliases.length; i++) {
            aliasToIndexMap.put(aliases[i], i);
        }
        return aliasToIndexMap;
    }

}
