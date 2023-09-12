package com.hisd3.hismk2.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import liquibase.integration.spring.SpringLiquibase
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContextException
import org.springframework.context.EnvironmentAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

import javax.sql.DataSource

@Configuration
@EnableJpaRepositories("com.hisd3.hismk2.repository")
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
class DatabaseConfig implements EnvironmentAware {
	
	private def log = LoggerFactory.getLogger(DatabaseConfig)
	private Environment env
	
	@Override
	void setEnvironment(Environment environment) {
		this.env = environment
	}
	
	@Bean(destroyMethod = "close")
	DataSource dataSource() {
		
		log.debug("Configuring Datasource")
		println("Configuring Datasource")
		
		if (env.containsProperty("spring.datasource.url") && env.containsProperty("spring.datasource.databaseName")) {
			log.error("Your database connection pool configuration is incorrect! The application" + " cannot start. Please check your Spring profile, current profiles are: {}",
					Arrays.toString(env.activeProfiles))
			
			println("Database connection pool is not configured correctly")
			throw new ApplicationContextException("Database connection pool is not configured correctly")
		}
		
		println("Configuring HikariConfig")
		def config = new HikariConfig()
		config.dataSourceClassName = env.getProperty("spring.datasource.dataSourceClassName")
		if (StringUtils.isEmpty(env.getProperty("spring.datasource.url"))) {
			config.addDataSourceProperty("databaseName", env.getProperty("spring.datasource.databaseName"))
			config.addDataSourceProperty("serverName", env.getProperty("spring.datasource.serverName"))
		} else {
			config.addDataSourceProperty("url", env.getProperty("spring.datasource.url"))
		}
		config.addDataSourceProperty("user", env.getProperty("spring.datasource.username"))
		config.addDataSourceProperty("password", env.getProperty("spring.datasource.password"))
		
		println("Configuring HikariConfig Finished")
		return new HikariDataSource(config)
	}
	
	@Bean
	SpringLiquibase liquibase(DataSource dataSource) {
		def liquibase = new SpringLiquibase()
		liquibase.dataSource = dataSource
		liquibase.changeLog = "classpath:config/liquibase/master.xml"
		liquibase.contexts = env.getProperty("liquiBase.context")
		
		log.debug("Configuring Liquibase")
		println("Configuring Liquibase")
		return liquibase
	}
	
}
