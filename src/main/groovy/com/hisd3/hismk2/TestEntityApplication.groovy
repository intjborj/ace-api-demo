package com.hisd3.hismk2

import com.google.common.base.Joiner
import com.hisd3.hismk2.config.Constants
import com.hisd3.hismk2.domain.ancillary.Service
import com.hisd3.hismk2.repository.ancillary.ServiceRepository
import com.hisd3.hismk2.services.EntityObjectMapperService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.context.ApplicationContext
import org.springframework.core.env.SimpleCommandLinePropertySource

import javax.annotation.PostConstruct

//@SpringBootApplication
@Slf4j
class TestEntityApplication {
	
	@Autowired
	ApplicationContext applicationContext
	
	@Autowired
	EntityObjectMapperService entityObjectMapperService
	
	@Autowired
	ServiceRepository serviceRepository
	
	@PostConstruct
	def testService() {
		
		//def department = departmentRepository.findById(UUID.fromString("f255c5a2-9baf-45da-b9a0-85288a8b07ad"))
		
		def service = new Service()
		
		def mapToAssign = [
				department : "f255c5a2-9baf-45da-b9a0-85288a8b07ad", // this is id of the Department record
				// or also a map [id:"f255c5a2-9baf-45da-b9a0-85288a8b07ad"]
				serviceName: "XXXXXX",
				serviceCode: "999999"
		]
		
		service = entityObjectMapperService.updateFromMap(service, mapToAssign)
		
		serviceRepository.save(service)
		
	}
	
	/*static void main(String[] args) {
		
		println("Starting HISD3 Application...")
		def start = System.currentTimeMillis()
		
		def app = new SpringApplication(TestEntityApplication)
		def source = new SimpleCommandLinePropertySource(args)
		
		addDefaultProfile(app, source)
		addLiquibaseScanPackages()
		
		def env = app.run(args).environment
		log.warn("Access URLs:\n----------------------------------------------------------\n\t" +
				"Local: \t\thttp://127.0.0.1:{}\n\t" +
				"External: \thttp://{}:{}\n----------------------------------------------------------",
				env.getProperty("server.port"),
				InetAddress.getLocalHost().hostAddress,
				env.getProperty("server.port"))
		
		def end = System.currentTimeMillis()
		log.warn("HISMK2 Started in : ${(end - start) / 1000} seconds")
		
	}*/
	
	static def addDefaultProfile(SpringApplication app,
	                             SimpleCommandLinePropertySource propertySource) {
		
		if (!propertySource.containsProperty("spring.profiles.active") && !System.getenv().containsKey("SPRING_PROFILES_ACTIVE")) {
			app.setAdditionalProfiles(Constants.SPRING_PROFILE_DEVELOPMENT)
		}
		
	}
	
	static def addLiquibaseScanPackages() {
		
		System.setProperty("liquibase.scan.packages", Joiner.on(",").join(
				"liquibase.change", "liquibase.database", "liquibase.parser",
				"liquibase.precondition", "liquibase.datatype",
				"liquibase.serializer", "liquibase.sqlgenerator", "liquibase.executor",
				"liquibase.snapshot", "liquibase.logging", "liquibase.diff",
				"liquibase.structure", "liquibase.structurecompare", "liquibase.lockservice",
				"liquibase.ext", "liquibase.changelog"))
		
	}
	
}
