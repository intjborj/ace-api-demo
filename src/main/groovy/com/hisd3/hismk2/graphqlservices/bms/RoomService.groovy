package com.hisd3.hismk2.graphqlservices.bms

import com.hisd3.hismk2.domain.bms.Room
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.repository.bms.RoomRepository
import com.hisd3.hismk2.repository.pms.CaseRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class RoomService {
	
	@Autowired
	private RoomRepository roomRepository
	
	@Autowired
	private CaseRepository caseRepository
	
	//============== All Queries ====================
	
	@GraphQLQuery(name = "rooms", description = "Get all Rooms")
	List<Room> findAll() {
		return roomRepository.findAll().sort { it.roomName }
	}
	
	@GraphQLQuery(name = "room", description = "Get Room By Id")
	Room findById(@GraphQLArgument(name = "id") UUID id) {
		return roomRepository.findById(id).get()
	}
	
	@GraphQLQuery(name = "availableRooms", description = "Get all available Rooms")
	List<Room> getAvailableRooms() {
		return roomRepository.getAvailableRooms().sort { it.roomName }
	}
	
	@GraphQLQuery(name = "roomsByFilters", description = "Get Rooms by filter")
	List<Room> getRoomsByFilters(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "departmentId") String departmentId,
			@GraphQLArgument(name = "type") String type,
			@GraphQLArgument(name = "status") String status) {
		return roomRepository.getRoomsByFilters(filter, departmentId, type, status).sort { it.roomName }
	}
	
	@GraphQLQuery(name = "availableRoomsByDepartment", description = "Get all available Rooms by Department")
	List<Room> getAvailableRoomsByDepartment(@GraphQLArgument(name = "departmentId") UUID departmentId) {
		return roomRepository.getAvailableRoomsByDepartment(departmentId).sort { it.roomName }
	}
	
	@GraphQLQuery(name = "roomsByDepartment", description = "Get all Rooms by Department")
	List<Room> getRoomsByDepartment(@GraphQLArgument(name = "departmentId") UUID departmentId) {
		return roomRepository.getRoomsByDepartment(departmentId).sort { it.roomName }
	}
	
	@GraphQLMutation
	Room updateRoom(
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		Case caseObj = caseRepository.findById(UUID.fromString(fields.get("caseId").toString())).get()
		Room room = roomRepository.findById(caseObj?.room?.id).get()
		
		return roomRepository.save(room.tap {
			status = fields.get("status")
		})
	}
}
