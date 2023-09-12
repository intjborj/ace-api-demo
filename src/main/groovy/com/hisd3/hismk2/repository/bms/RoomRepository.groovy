package com.hisd3.hismk2.repository.bms

import com.hisd3.hismk2.domain.bms.Room
import groovy.transform.TypeChecked
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

@TypeChecked
interface RoomRepository extends JpaRepository<Room, UUID> {
	
	@Query(value = "Select room from Room room where room.status = 'AVAILABLE' and  (room.deleted = false or room.deleted is null)")
	List<Room> getAvailableRooms()
	
	@Query(value = "Select room from Room room where upper(room.roomName) like upper(concat('%',:filter,'%')) or upper(room.roomNo) like upper(concat('%',:filter,'%'))")
	List<Room> getRoomsByFilter(@Param("filter") String filter)
	
	@Query(value = '''
		Select room
			from Room room
			where
				(
					upper(room.roomName) like upper(concat('%',:filter,'%'))
					or upper(room.roomNo) like upper(concat('%',:filter,'%'))
				)
				and concat('', room.department.id, '') like concat('%',:departmentId,'%')
				and upper(room.type) like upper(concat('%',:type,'%'))
				and upper(room.status) like upper(concat('%',:status,'%'))
	''')
	List<Room> getRoomsByFilters(@Param("filter") String filter, @Param("departmentId") String departmentId, @Param("type") String type, @Param("status") String status)
	
	@Query(value = "Select room from Room room where room.department.id = :departmentId and room.status = 'AVAILABLE' and  (room.deleted = false or room.deleted is null)")
	List<Room> getAvailableRoomsByDepartment(@Param("departmentId") UUID departmentId)
	
	@Query(value = "Select room from Room room where room.department.id = :departmentId and (room.deleted = false or room.deleted is null)")
	List<Room> getRoomsByDepartment(@Param("departmentId") UUID departmentId)
}
