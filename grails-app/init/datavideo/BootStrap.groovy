package datavideo

import grails.core.GrailsApplication
import org.apache.poi.ss.usermodel.WorkbookFactory

class BootStrap {
GrailsApplication grailsApplication
    def init = { servletContext ->
        if(User.count==0){
            Map map=grailsApplication.config.user.admin
            map.values().each {
                String email=it.email
                String name=it.name
                User user=new User(email: email,name: name)
                user.save(flush:true)
            }
        }

    }
    def destroy = {
    }
}
