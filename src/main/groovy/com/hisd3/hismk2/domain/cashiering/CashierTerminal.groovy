package com.hisd3.hismk2.domain.cashiering

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.domain.types.Subaccountable
import com.hisd3.hismk2.rest.dto.CoaConfig
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(name = "cashierterminals", schema = "cashiering")
class CashierTerminal extends AbstractAuditingEntity implements Serializable, Subaccountable {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "terminal_id", columnDefinition = "varchar")
	String terminalId
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "remarks", columnDefinition = "varchar")
	String remarks
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "ipaddresses", columnDefinition = "varchar")
	String macAddress

	@GraphQLQuery
	@UpperCase
	@Formula("concat(terminal_id,' - ',remarks)")
	String terminalName

	@Override
	String getDomain() {
		return CashierTerminal.class.name
	}

	@Override
	String getCode() {
		return terminalId
	}

	@Override
	String getDescription() {
		return remarks
	}

	@Override
	List<UUID> getDepartment() {
		return null
	}

	@Override
	CoaConfig getConfig() {
		new CoaConfig(show: true, showDepartments: true)
	}
}
