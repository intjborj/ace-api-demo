package com.hisd3.hismk2

import com.google.common.base.Joiner
import com.hisd3.hismk2.config.Constants
import org.springframework.boot.SpringApplication
import org.springframework.core.env.SimpleCommandLinePropertySource

//@Slf4j
//@SpringBootApplication
class TestSoapClient {
	
	/*static void main(String[] args) {
		
		println("Starting HISD3 Application...")
		def start = System.currentTimeMillis()
		
		def app = new SpringApplication(TestSoapClient)
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

//	@Bean
//	CommandLineRunner lookup(SOAPConnector soapConnector) {
//		return { args ->
//
////            AuthenticationTest request = new AuthenticationTest()
////            request.username = "DOHTRAINING"
////            request.password = "123456"
////            AuthenticationTestResponse response =
////                    (AuthenticationTestResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/authenticationTest", request)
////            println("Got Response As below ========= : ")
////            println("Name : "+response.return )
//
//		}
//	}
	
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

/*
static def usingApacheAxis2(){

    println("Soap Client Example using Apache Axis 2")

    try{

        def soapService = new Online_Health_Facility_Statistical_Report_SystemStub(
                "http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php"
        )

        soapService._getServiceClient().getOptions().setProperty(HTTPConstants.SO_TIMEOUT, new Integer(360000))
        soapService._getServiceClient().getOptions().setProperty(HTTPConstants.CONNECTION_TIMEOUT, new Integer(360000))

        def result =  soapService.authenticationTest(new AuthenticationTest().tap {
            username = "NEHEHRSV201900114"
            password = "Mahiraphulaan1"
        })

        println("Output of Authentication")
        println(result.return)




    }catch(Exception e){


        e.printStackTrace()
    }*/
	
}




