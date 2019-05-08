package two.ways

import grails.core.GrailsApplication
import groovy.sql.Sql

import javax.servlet.ServletContext

class BootStrap {
	GrailsApplication grailsApplication
	def dataSource


	def init = { ServletContext ctx ->
		
		Sql sql = new Sql(dataSource)
		println sql.execute("CREATE TABLE user (id  VARCHAR(255) PRIMARY KEY, username VARCHAR(255), version int)")
	}
	def destroy = {
	}
}
