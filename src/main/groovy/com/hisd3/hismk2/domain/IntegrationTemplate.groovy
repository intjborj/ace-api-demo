package com.hisd3.hismk2.domain

import com.hisd3.hismk2.domain.types.AutoIntegrateable
import com.hisd3.hismk2.domain.types.Subaccountable
import com.hisd3.hismk2.rest.dto.CoaConfig
import groovy.transform.builder.Builder

import javax.persistence.ManyToOne

class SubAccountHolder implements Subaccountable{
     SubAccountHolder() {
     }

     SubAccountHolder(Subaccountable subaccountable) {
          this.id = subaccountable.id
          this.domain = subaccountable.domain
          this.code = subaccountable.code
          this.description = subaccountable.description
          //added by wilson
          this.department = subaccountable.department
          this.config = subaccountable.config
     }
     UUID id
     String domain
     String code
     String description
     //added by wilson
     List<UUID> department
     CoaConfig config


}
@Builder
class IntegrationTemplate implements AutoIntegrateable{
     String domain = IntegrationTemplate.class.name
     String flagValue
     Map<String,String> details = [:]


     // Subaccountable
     SubAccountHolder sub_a
     SubAccountHolder sub_b
     SubAccountHolder sub_c
     SubAccountHolder sub_d
     SubAccountHolder sub_e
     SubAccountHolder sub_f
     SubAccountHolder sub_g
     SubAccountHolder sub_h
     SubAccountHolder sub_i
     SubAccountHolder sub_k
     SubAccountHolder sub_l
     SubAccountHolder sub_m
     SubAccountHolder sub_n
     SubAccountHolder sub_o
     SubAccountHolder sub_p



     BigDecimal value_a
     BigDecimal value_b
     BigDecimal value_c
     BigDecimal value_d
     BigDecimal value_e
     BigDecimal value_f
     BigDecimal value_g
     BigDecimal value_h
     BigDecimal value_i
     BigDecimal value_j
     BigDecimal value_k
     BigDecimal value_l
     BigDecimal value_m
     BigDecimal value_n
     BigDecimal value_o
     BigDecimal value_p


     @ManyToOne
     Department dept_a
     @ManyToOne
     Department dept_b
     @ManyToOne
     Department dept_c
     @ManyToOne
     Department dept_d
     @ManyToOne
     Department dept_e
     @ManyToOne
     Department dept_f
     @ManyToOne
     Department dept_g
     @ManyToOne
     Department dept_h
     @ManyToOne
     Department dept_i
     @ManyToOne
     Department dept_j
     @ManyToOne
     Department dept_k
     @ManyToOne
     Department dept_l
     @ManyToOne
     Department dept_m
     @ManyToOne
     Department dept_n
     @ManyToOne
     Department dept_o
     @ManyToOne
     Department dept_p








    // Add Your custom property here
}
