package com.hisd3.hismk2.graphqlservices.cashiering

import com.hisd3.hismk2.domain.cashiering.CashierTerminal
import com.hisd3.hismk2.domain.cashiering.Shifting
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

import java.time.Instant

@Service
@GraphQLApi
class ShiftingServices extends AbstractDaoService<Shifting> {
	
	ShiftingServices() {
		super(Shifting.class)
	}
	
	@Autowired
	GeneratorService generatorService
	
	@Autowired
	CashieringService cashieringService
	
	@Autowired
	UserRepository userRepository
	
	@Autowired
	EmployeeRepository employeeRepository
	
	Shifting getActiveShift(CashierTerminal terminal) {
		
		createQuery("from Shifting s where s.terminal=:terminal and s.active=true",
				[terminal: terminal])
				.resultList.find()
		
	}
	
	@GraphQLQuery(name = "getShiftById")
	Shifting getShiftById(
			@GraphQLArgument(name = "id") UUID id) {
		
		findOne(id)
		
	}
	
	@GraphQLQuery(name = "getShiftingRecords")
	Page<Shifting> getShiftingRecords(
			@GraphQLArgument(name = "shiftNo") String shiftNo,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {
		def username = SecurityUtils.currentLogin()
		//  def user = userRepository.findOneByLogin(username)
		//  def emp = employeeRepository.findOneByUser(user)
		
		getPageable("from Shifting s where lower(s.shiftno) like lower(concat('%',:shiftNo,'%')) and lower(s.createdBy) = lower(:username) order by s.startshift desc",
				"Select count(s)  from Shifting s where s.shiftno=:shiftNo and lower(s.createdBy) = lower(:username) ",
				page,
				size,
				[shiftNo : shiftNo,
				 username: username]
		)
	}
	
	@GraphQLQuery(name = "getAllShiftingRecords")
	Page<Shifting> getAllShiftingRecords(
			@GraphQLArgument(name = "shiftNo") String shiftNo,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {
		
		getPageable("from Shifting s where lower(s.shiftno) like lower(concat('%',:shiftNo,'%'))  order by s.startshift desc",
				"Select count(s)  from Shifting s where s.shiftno=:shiftNo   ",
				page,
				size,
				[shiftNo: shiftNo]
		)
	}
	
	@GraphQLQuery(name = "getAllShiftingRecordsForCdctr")
	Page<Shifting> getAllShiftingRecordsForCdctr(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "terminalId") List<UUID> terminalId,
			@GraphQLArgument(name = "shiftStartDate") String shiftStartDate,
			@GraphQLArgument(name = "shiftEndDate") String shiftEndDate,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {
		String queryStr = "from Shifting s where  s.cdctr is null "
		Map<String, Object> params = [:]

		if(terminalId){
			params['terminalId'] = terminalId.collect{it}
			queryStr +=" and s.terminal.id in :terminalId "
		}

		if(filter){
			params['filter'] = filter
			queryStr +=" and upper(s.shiftno) like upper(concat('%',:filter,'%')) "
		}

		if(shiftStartDate){
			params['shiftStartDate'] = shiftStartDate
			queryStr +=" and to_date(to_char(s.startshift, 'YYYY-MM-DD'),'YYYY-MM-DD') = to_date(:shiftStartDate,'YYYY-MM-DD') "
		}

        if(shiftEndDate){
            params['shiftEndDate'] = shiftEndDate
            queryStr +=" and to_date(to_char(s.endshift, 'YYYY-MM-DD'),'YYYY-MM-DD') = to_date(:shiftEndDate,'YYYY-MM-DD') "
        }

		getPageable("${queryStr} order by s.startshift desc",
				"Select count(s) ${queryStr} ",
				page,
				size,
				params
		)
	}

    @GraphQLQuery(name = "getAllShiftingRecordsArchive")
    Page<Shifting> getAllShiftingRecordsArchive(
            @GraphQLArgument(name = "filter") String filter,
            @GraphQLArgument(name = "terminalId") List<UUID> terminalId,
            @GraphQLArgument(name = "shiftStartDate") String shiftStartDate,
            @GraphQLArgument(name = "shiftEndDate") String shiftEndDate,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "size") Integer size
    ) {
        String queryStr = "from Shifting s where  s.cdctr is not null "
        Map<String, Object> params = [:]

        if(terminalId){
            params['terminalId'] = terminalId.collect{it}
            queryStr +=" and s.terminal.id in :terminalId "
        }

        if(filter){
            params['filter'] = filter
            queryStr +=" and upper(s.shiftno) like upper(concat('%',:filter,'%')) "
        }

        if(shiftStartDate){
            params['shiftStartDate'] = shiftStartDate
            queryStr +=" and to_date(to_char(s.startshift, 'YYYY-MM-DD'),'YYYY-MM-DD') = to_date(:shiftStartDate,'YYYY-MM-DD') "
        }

        if(shiftEndDate){
            params['shiftEndDate'] = shiftEndDate
            queryStr +=" and to_date(to_char(s.endshift, 'YYYY-MM-DD'),'YYYY-MM-DD') = to_date(:shiftEndDate,'YYYY-MM-DD') "
        }

        getPageable("${queryStr} order by s.startshift desc",
                "Select count(s) ${queryStr} ",
                page,
                size,
                params
        )
    }
	
	@GraphQLMutation
	Shifting addShiftingRecord(
			@GraphQLArgument(name = "macAddress") String macAddress
	) {
		
		def terminal = cashieringService.findByMacAddess(macAddress)
		
		if (terminal) {
			
			/*def username = SecurityUtils.currentLogin()
			def user = userRepository.findOneByLogin(username)
			def emp = employeeRepository.findOneByUser(user)
			def department = emp.departmentOfDuty
		*/
			def shifting = new Shifting()
			shifting.shiftno = generatorService.getNextValue(GeneratorType.SHIFTING_ID, {
				return "SFT-" + StringUtils.leftPad(it.toString(), 6, "0")
			})
			
			shifting.terminal = terminal
			shifting.active = true
			shifting.startshift = Instant.now()
			shifting = save(shifting)
			
			return shifting
		}
		
		null
	}
	
	@GraphQLMutation
	Shifting closeShiftingRecord(
			@GraphQLArgument(name = "shiftId") String shiftId
	) {
		
		def shift = createQuery("from Shifting s where s.shiftno=:shiftId",
				[shiftId: shiftId]).resultList.find()
		
		shift.active = false
		shift.endshift = Instant.now()
		
		shift = save(shift)
		
		shift
	}
	
}
