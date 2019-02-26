package datavideo

import grails.transaction.Transactional

@Transactional
class UploadSheetService {

    boolean upload(Map params){
        boolean message=false
        def f = params.file
        String year=params.year
        String electionType=params.electionType
        String channelId=params.channelId
        String fileName="election_"+year+"_"+electionType+".xls"
        File folder=new File("/home/kapil/opt/d2v/"+channelId)
        if(!folder.exists()){
            folder.mkdirs()
        }


        if(!DataFileEntry.findByFileName(fileName) && f){
            File fileDest=new File(folder,fileName)
            f.transferTo(fileDest)
            Channel ownerChannel=Channel.findByChannelId(channelId)
            new DataFileEntry(fileName:fileName,filePath: fileDest.path,ownerChannel:ownerChannel ).save(flush:true)
            message=true
        }
        message

    }
}
