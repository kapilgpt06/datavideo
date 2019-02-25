package datavideo

class CallbackController {

    EntryDataToDBService entryDataToDBService
    def index() {
        System.out.println(params)
        return "hello"
    }
    def check(){
        entryDataToDBService.entryToDB("/home/kapil/Project/datavideo/grails-app/2014.xls")
        render "done"
    }
}
