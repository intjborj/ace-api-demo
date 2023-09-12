package com.hisd3.hismk2.domain.referential

import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*

@TypeChecked
@Entity
@Table(schema = "referential", name = "doh_surgical_codes")
class DohSurgeryCode {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "proccode", columnDefinition = "varchar")
	String proccode
	
	@GraphQLQuery
	@Column(name = "procdesc", columnDefinition = "varchar")
	String procdesc

	@GraphQLQuery
	@Column(name = "optycode", columnDefinition = "varchar")
	String optycode
	
	@GraphQLQuery
	@Column(name = "protcode", columnDefinition = "varchar")
	String protcode
	
	@GraphQLQuery
	@Column(name = "procuval", columnDefinition = "varchar")
	String procuval
	
	@GraphQLQuery
	@Column(name = "procrem", columnDefinition = "varchar")
	String procrem
	
	@GraphQLQuery
	@Column(name = "procstat", columnDefinition = "varchar")
	String procstat
	
	@GraphQLQuery
	@Column(name = "datemod", columnDefinition = "varchar")
	String datemod
	
	@GraphQLQuery
	@Column(name = "updsw", columnDefinition = "varchar")
	String updsw
	
	@GraphQLQuery
	@Column(name = "altpcode", columnDefinition = "varchar")
	String altpcode
	
	@GraphQLQuery
	@Column(name = "altpdesc", columnDefinition = "varchar")
	String altpdesc
	
	@GraphQLQuery
	@Column(name = "priden", columnDefinition = "varchar")
	String priden
	
	@GraphQLQuery
	@Column(name = "prmapto", columnDefinition = "varchar")
	String prmapto
	
	@GraphQLQuery
	@Column(name = "prvfa", columnDefinition = "varchar")
	String prvfa
	
	@GraphQLQuery
	@Column(name = "prdetsec", columnDefinition = "varchar")
	String prdetsec
	
	@GraphQLQuery
	@Column(name = "prregn", columnDefinition = "varchar")
	String prregn
	
	@GraphQLQuery
	@Column(name = "prextyp", columnDefinition = "varchar")
	String prextyp
	
	@GraphQLQuery
	@Column(name = "prspeco", columnDefinition = "varchar")
	String prspeco
	
	@GraphQLQuery
	@Column(name = "costcenter", columnDefinition = "varchar")
	String costcenter
	
	@GraphQLQuery
	@Column(name = "procreslt", columnDefinition = "varchar")
	String procreslt
	
	@GraphQLQuery
	@Column(name = "rvu", columnDefinition = "varchar")
	String rvu
	
	@GraphQLQuery
	@Column(name = "restemplate", columnDefinition = "varchar")
	String restemplate

	
}
