package datavideo

import grails.core.GrailsApplication
import grails.transaction.Transactional
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory

@Transactional
class UploadSheetService {
GrailsApplication grailsApplication

    String upload(Map params){
        String filePath=grailsApplication.config.video.path
        String message="File already exist"
        def f = params.file
        String year=params.year
        String electionType=params.electionType
        String channelId=params.channelId
        String fileName="election_"+year+"_"+electionType+".xls"
        File folder=new File(filePath+"/"+channelId)
        if(!folder.exists()){
            folder.mkdirs()
        }


        if(!DataFileEntry.findByFileName(fileName) && f){
            File fileDest=new File(folder,fileName)
            f.transferTo(fileDest)
            message=checkFileFormat(fileDest.path)
            if(message.equals("SUCCESS")){
                Channel ownerChannel=Channel.findByChannelId(channelId)
                new DataFileEntry(fileName:fileName,filePath: fileDest.path,ownerChannel:ownerChannel ).save(flush:true)
                message="File SuccessFully Uploaded"
            }
        }
        message

    }

    String checkFileFormat(String path){
        String message="File format is wrong"
        FileInputStream fis = new FileInputStream(path);
        Workbook wb = WorkbookFactory.create(fis);
        Sheet sh1 = wb.getSheet("electors");
        Sheet sh2 = wb.getSheet("Cand_Wise");

        String state=String.valueOf(sh1.getRow(3).getCell(1)).toLowerCase()
        String constituency=String.valueOf(sh1.getRow(3).getCell(3)).toLowerCase()
        String voters=String.valueOf(sh1.getRow(3).getCell(4)).toLowerCase()
        String electors=String.valueOf(sh1.getRow(3).getCell(5)).toLowerCase()
        String percentage=String.valueOf(sh1.getRow(3).getCell(6)).toLowerCase()

        String pcName = String.valueOf(sh2.getRow(3).getCell(5)).toLowerCase()
        String candidateName = String.valueOf(sh2.getRow(3).getCell(7)).toLowerCase()
        String partySign = String.valueOf(sh2.getRow(3).getCell(11))toLowerCase()
        String totalVotePolled = String.valueOf(sh2.getRow(3).getCell(12)).toLowerCase();
        String position = String.valueOf(sh2.getRow(3).getCell(13)).toLowerCase();
        println state
        println constituency
        println voters
        println(electors)
        println(percentage)
        println(pcName)
        println(candidateName)
        println(partySign)
        println(totalVotePolled)
        println(position)
        if(state.equals("state")&&
            constituency.equals("parliamentary constituency")&&
            voters.equals("total voters")&&
            electors.equals("total_electors")&&
                percentage.equals("poll percentage")&&
                pcName.equals("pc name")&&
                candidateName.equals("candidate name")&&
                partySign.equals("party abbreviation")&&
                totalVotePolled.equals("total votes polled")&&
                position.equals("position")
            ){
            message="SUCCESS"
        }else{
            File file=new File(path)
            file.delete()
        }
        message

    }
}
