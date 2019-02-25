package datavideo

import org.apache.poi.ss.usermodel.WorkbookFactory

class BootStrap {

    def init = { servletContext ->
        if(User.count==0){
            User user=new User(email: "kapilgpt06@gmail.com",name: "kapil")
            user.save(flush:true)
        }
    }
    def destroy = {
    }
}
