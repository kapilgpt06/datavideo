package datavideo

class UploadSheetController {

    def index() { }

    def upload(){
        def f = request.getFile('file')
        String year=request.getParameter("year")
        String electionType=request.getParameter("electionType")
        String fileName="election_"+year+"_"+electionType+".xls"
        String filePath="/home/kapil/Project/datavideo/opt/d2v/data/"
        String message="already exited"

        if(!DataFileEntry.findByFileName(fileName) && f){

            File fileDest = new File(filePath+fileName)
            f.transferTo(fileDest)
            message="uploaded"
            new DataFileEntry(fileName:fileName,filePath: filePath ).save(flush:true)
        }
        render message
    }
}