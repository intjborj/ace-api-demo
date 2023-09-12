package com.hisd3.hismk2.graphqlservices.billing

import com.hisd3.hismk2.domain.billing.Package
import com.hisd3.hismk2.domain.billing.PackageItem
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
@GraphQLApi
class PackageServices extends AbstractDaoService<Package> {
	
	@Autowired
	GeneratorService generatorService
	
	PackageServices() {
		super(Package.class)
	}
	
	@GraphQLQuery(name = "packageById")
	Package packageById(
			@GraphQLArgument(name = "id") UUID id
	) {
		findOne(id)
	}
	
	@GraphQLQuery(name = "packages")
	Page<Package> packages(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size,
			@GraphQLArgument(name = "includeInactive") Boolean includeInactive
	) {
		if(includeInactive){
				getPageable("""  Select c from Package c  where lower(c.description) like lower(concat('%',:filter,'%')) order by c.code
					 """,
						""" Select count(c) from Package c  where lower(c.description) like lower(concat('%',:filter,'%'))
				""",
						page,
						size,
						[filter: filter])
		}
		else {
			getPageable("""  Select c from Package c  where lower(c.description) like lower(concat('%',:filter,'%')) and coalesce(c.isActive, true) is true order by c.code
				 """,
					""" Select count(c) from Package c  where lower(c.description) like lower(concat('%',:filter,'%'))
			""",
					page,
					size,
					[filter: filter])
		}

	}
	
	@GraphQLMutation
	Package upsertPackage(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		
		upsertFromMap(id, fields, { Package entity, boolean forInsert ->
			if (forInsert) {
				entity.code = generatorService.getNextValue(GeneratorType.PKGID, {
					return "PKG-" + StringUtils.leftPad(it.toString(), 6, "0")
				})
			}
		})
		
	}
	
	@GraphQLMutation
	Package deletePackageItem(
			@GraphQLArgument(name = "packageId") UUID packageId,
			@GraphQLArgument(name = "packageItemId") UUID packageItemId
	) {
		def pkg = findOne(packageId)
		
		pkg.items?.removeAll {
			it.id.toString() == packageItemId.toString()
		}
		
		save(pkg)
	}
	
	@GraphQLMutation
	Package addPackageItem(
			@GraphQLArgument(name = "packageId") UUID packageId,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		def pkg = findOne(packageId)
		
		def id = fields.get("id") as String
		
		if (id) {
			
			def items = pkg.items
			
			items.find {
				it.id.toString() == id
			}?.tap {
				updateFromMap(it, fields)
				
			}
		} else {
			def item = new PackageItem()
			item.parent = pkg
			item.active = true
			updateFromMap(item, fields)
			
			pkg.items.add(item)
		}
		
		save(pkg)
	}
}
