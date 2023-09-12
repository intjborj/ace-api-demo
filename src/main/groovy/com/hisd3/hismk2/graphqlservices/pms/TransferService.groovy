package com.hisd3.hismk2.graphqlservices.pms

import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.bms.Room
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.Transfer
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.bms.RoomRepository
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.repository.pms.TransferRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

import java.time.LocalDateTime


class TransferDto {
    UUID id
    String registryType
    UUID room
    UUID department

}


@TypeChecked
@Component
@GraphQLApi
class TransferService {

    @Autowired
    private TransferRepository transferRepository

    @Autowired
    private CaseRepository caseRepository

    @Autowired
    private RoomRepository roomRepository

    @Autowired
    private DepartmentRepository departmentRepository

    @Autowired
    private JdbcTemplate jdbcTemplate
    //============== All Queries ====================

    @GraphQLQuery(name = "transfers", description = "Get All Transfers")
    List<Transfer> findAll() {
        return transferRepository.findAll()
    }

    @GraphQLQuery(name = "transfer", description = "Get Transfer By Id")
    Transfer findById(@GraphQLArgument(name = "id") UUID id) {
        return transferRepository.findById(id).get()
    }

    @GraphQLQuery(name = "searchTransfers", description = "Search Transfers")
    List<Transfer> searchTransfers(@GraphQLArgument(name = "filter") String filter) {
        return transferRepository.searchTransfers(filter)
    }

    @GraphQLQuery(name = "searchTransfersByPatient", description = "Search Transfers by patient")
    List<Transfer> searchTransfersByPatient(@GraphQLArgument(name = "filter") String filter) {
        return transferRepository.searchTransfersByPatient(filter)
    }

    @GraphQLQuery(name = "transfersByCase", description = "Transfers by case ID")
    List<Transfer> getTransfersByCase(@GraphQLArgument(name = "id") UUID id) {
        return transferRepository.getTransfersByCase(id)
    }

    @GraphQLQuery(name = "transfersByCaseWithRooms", description = "Transfers by case ID")
    List<Transfer> transfersByCaseWithRooms(@GraphQLArgument(name = "id") UUID id) {

        if (id)
            return transferRepository.getTransfersByCaseWithRooms(id)
        else
            []
    }

    @GraphQLQuery(name = "census", description = "Get Transfers by Date range")
    List<Transfer> getTransfersByDateRange(@GraphQLArgument(name = "fields") Map<String, Object> fields) {
        def fromDate = fields["fromDate"] as LocalDateTime
        def toDate = fields["toDate"] as LocalDateTime
        def registryType = fields["registryType"] as String

        return transferRepository.getTransfersByDateRange(fromDate, toDate, registryType)
    }

    @GraphQLQuery(name = "transfersDepartmentsByCase", description = "Transfers by case ID")
    List<Department> transfersDepartmentsByCase(@GraphQLArgument(name = "caseId") UUID caseId) {
        return transferRepository.transfersDepartmentsByCase(caseId).sort { it.departmentName }
    }

    @GraphQLMutation
    Transfer toggleActiveTransfer(
            @GraphQLArgument(name = "caseId") UUID caseId,
            @GraphQLArgument(name = "transferId") UUID transferId
    ) {
        Transfer newTransfer = transferRepository.findById(transferId).get()
        //newTransfer.active = true

        if (!transferRepository.getCurrentActiveTransfer(caseId).isEmpty()) {
            Transfer activeTransfer = transferRepository.getCurrentActiveTransfer(caseId).first()
            //activeTransfer.active = false
            transferRepository.save(activeTransfer)
        }

        return transferRepository.save(newTransfer)
    }

    @GraphQLMutation
    Transfer toggleVoidTransfer(
            @GraphQLArgument(name = "caseId") UUID caseId,
            @GraphQLArgument(name = "transferId") UUID transferId
    ) {
        Transfer newTransfer = transferRepository.findById(transferId).get()
        newTransfer.voided = !newTransfer.voided

        newTransfer = transferRepository.save(newTransfer)

        Case patientCase = caseRepository.findById(caseId).get()
        TransferDto prevTransfer = new TransferDto()

        try{
            prevTransfer = jdbcTemplate.queryForObject("""
						select id,registry_type as registryType, room, department from pms.transfers p where p.case = '${caseId}'
						and p.voided is not true order by p.created_date desc limit 1;
  		""", new BeanPropertyRowMapper(TransferDto.class))
        }catch(ignored){
            prevTransfer.room = null
            prevTransfer.department = null
        }



        Room oldRoom = new Room()

        if (patientCase.room) {
            oldRoom = roomRepository.findById(patientCase.room.id).get()
            oldRoom.status = 'AVAILABLE'
            oldRoom.notes = null
            roomRepository.save(oldRoom)
        }
        Room newRoom = new Room()
        if (prevTransfer.room) {
            newRoom = roomRepository.findById(prevTransfer.room).get()
            oldRoom.status = 'OCCUPIED'
            oldRoom.notes = patientCase.patient.fullName
            roomRepository.save(newRoom)
            patientCase.room = newRoom
        } else {
            patientCase.room = null
        }


        patientCase.registryType = prevTransfer.registryType
        patientCase.department = departmentRepository.findById(prevTransfer.department).get()
        caseRepository.save(patientCase)


        return newTransfer
    }
}
