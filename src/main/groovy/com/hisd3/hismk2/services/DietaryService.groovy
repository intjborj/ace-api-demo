package com.hisd3.hismk2.services

import com.hisd3.hismk2.domain.dietary.DietList
import com.hisd3.hismk2.domain.dietary.MealSeched
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.Patient
import com.hisd3.hismk2.repository.dietary.DietListRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.repository.pms.PatientRepository
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.sql.Timestamp
import java.time.Instant

@Service
@TypeChecked
class DietaryService {

    @Autowired
    PatientRepository patientRepository

    @Autowired
    CaseRepository caseRepository

    @Autowired
    DietListRepository dietListRepository

    @Autowired
    EmployeeRepository employeeRepository
    List<DietList> createPatientDietList(String meal,String start,String end){

        Timestamp startTimeS = Timestamp.valueOf(start)
        Timestamp endTimeS = Timestamp.valueOf(end)

        Instant startDate = startTimeS.toInstant()
        Instant endDate = endTimeS.toInstant()

         List<Patient> pList = new ArrayList<>()
         pList = patientRepository.listAllInpatient()

         List<DietList> res = new ArrayList<>()
        if(pList.size() > 0){
            pList.each { it ->
                Case pCase = caseRepository.getPatientActiveCase(it.id)

                List<DietList> activeDiet = dietListRepository.findDietByPatientAndMealtime(pCase.id,meal,startDate,endDate)
                if(activeDiet.size() == 0){
                    DietList dlist = new DietList()
                    dlist.patientCase = pCase
                    dlist.mealSched = meal
                    dlist.dietType = pCase?.diet?.dietName
                    dlist.status = "Acknowledge"
                    res.add(dlist)
                    dietListRepository.save(dlist)
                }

            }
            return res
        }
        throw new Exception("Error Listing of Patients")
    }

    Boolean addEmpMeal(String meal,UUID id){

        Employee rod = employeeRepository.findById(id).get()

        try{
            DietList dlist = new DietList()

            dlist.mealSched = meal
            dlist.employee = rod
            dlist.status = "Acknowledge"

            dietListRepository.save(dlist)
            return true
        }catch(Exception e){
            throw e
        }
    }


    Boolean addCompanionMeal(String meal,UUID patienCase,String alias){

        Case pCase = caseRepository.findById(patienCase).get()

        try{
            DietList dlist = new DietList()

            dlist.mealSched = meal
            dlist.patientCase = pCase
            dlist.alias = alias
            dlist.mealToCompanion = true
            dlist.status = "Acknowledge"

            dietListRepository.save(dlist)
            return true
        }catch(Exception e){
            throw e
        }
    }
}
