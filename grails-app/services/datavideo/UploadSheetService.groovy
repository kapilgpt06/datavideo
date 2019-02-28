package datavideo

import grails.core.GrailsApplication
import grails.transaction.Transactional
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory

@Transactional
class UploadSheetService {
    GrailsApplication grailsApplication

    List dataFileList(){
        List<DataFileEntry> dataFileEntryList=DataFileEntry.list()
        List<DataFileEntryDTO> dataFileEntryDTOList=[]
        dataFileEntryList.each {
            DataFileEntryDTO dataFileEntryDTO=new DataFileEntryDTO()
            dataFileEntryDTO.id=it.id
            dataFileEntryDTO.fileName=it.fileName.capitalize()
            dataFileEntryDTO.dateCreated=it.dateCreated
            dataFileEntryDTO.channelName=it.channel.channelName
            dataFileEntryDTO.status=it.status
            dataFileEntryDTO.fileUploadBy=it.fileUploadBy.email
            dataFileEntryDTOList<<dataFileEntryDTO
        }
        dataFileEntryDTOList
    }

    List<VideoDataEntryDTO> videoDataList(DataFileEntry dataFileEntry){
        List<VideoDataEntry> videoDataEntryList=VideoDataEntry.findAllByDataFile(dataFileEntry)
        List<VideoDataEntryDTO> videoDataEntryDTOList=[]
        videoDataEntryList.each {
            VideoDataEntryDTO videoDataEntryDTO=new VideoDataEntryDTO()
            videoDataEntryDTO.videoName=it.videoName
            videoDataEntryDTO.videocreated=it.videoPath.equals("NULL")?"NULL":"CREATED"
            videoDataEntryDTO.videoUploaded=it.videoId.equals("NULL")?"NULL":"UPLOADED"
            videoDataEntryDTO.channelName=it.channel.channelName
            videoDataEntryDTO.uploadBy=it.dataFile.fileUploadBy.email
            videoDataEntryDTO.videoCreatedDate=it.videoCreatedDate
            videoDataEntryDTO.videoUploadDate=it.videoUploadDate
            videoDataEntryDTO.videoURL=it.videoId.equals("NULL")?"NULL":"https://www.youtube.com/watch?v="+it.videoId
            videoDataEntryDTOList<<videoDataEntryDTO
        }
        videoDataEntryDTOList
    }

    String upload(Map params,String userEmail) {
        String filePath = grailsApplication.config.video.path
        String message = "File already exist"
        def f = params.file
        String year = params.year
        String electionType = params.electionType
        String channelId = params.channelId
        String fileName = "election_" + year + "_" + electionType + ".xls"
        File folder = new File(filePath + "/" + channelId)
        if (!folder.exists()) {
            folder.mkdirs()
        }


        if (!DataFileEntry.findByFileName(fileName) && f) {
            File fileDest = new File(folder, fileName)
            f.transferTo(fileDest)
            message = checkFileFormat(fileDest.path,year)
            if (message.equals("SUCCESS")) {
                Channel ownerChannel = Channel.findByChannelId(channelId)
                User user=User.findByEmail(userEmail)
                new DataFileEntry(fileName: fileName, filePath: fileDest.path, channel: ownerChannel,fileUploadBy: user,year: year,electionType: electionType).save(flush: true)
                message = "File SuccessFully Uploaded"
            }
        }
        message

    }

    String checkFileFormat(String path,String year) {
        String message = "File format is wrong"
        FileInputStream fis = new FileInputStream(path);
        Workbook wb = WorkbookFactory.create(fis);
        Sheet sh1 = wb.getSheet("electors");
        Sheet sh2 = wb.getSheet("Cand_Wise");

        String state = String.valueOf(sh1.getRow(3).getCell(1)).toLowerCase()
        String constituency = String.valueOf(sh1.getRow(3).getCell(3)).toLowerCase()
        String voters = String.valueOf(sh1.getRow(3).getCell(4)).toLowerCase()
        String electors = String.valueOf(sh1.getRow(3).getCell(5)).toLowerCase()
        String percentage = String.valueOf(sh1.getRow(3).getCell(6)).toLowerCase()

        String pcName = String.valueOf(sh2.getRow(3).getCell(5)).toLowerCase()
        String candidateName = String.valueOf(sh2.getRow(3).getCell(7)).toLowerCase()
        String partySign = String.valueOf(sh2.getRow(3).getCell(11)) toLowerCase()
        String totalVotePolled = String.valueOf(sh2.getRow(3).getCell(12)).toLowerCase();
        String position = String.valueOf(sh2.getRow(3).getCell(13)).toLowerCase();

        String sheetYear=String.valueOf(sh2.getRow(4).getCell(3)).split("\\.")[0]
        println sheetYear+" "+year
        if(!sheetYear.equals(year)){
            message="sheet year is not match with your input year"
            File file = new File(path)
            file.delete()
        }
        else if (state.equals("state") &&
                constituency.equals("parliamentary constituency") &&
                voters.equals("total voters") &&
                electors.equals("total_electors") &&
                percentage.equals("poll percentage") &&
                pcName.equals("pc name") &&
                candidateName.equals("candidate name") &&
                partySign.equals("party abbreviation") &&
                totalVotePolled.equals("total votes polled") &&
                position.equals("position")
        ) {
            message = "SUCCESS"
        } else {
            File file = new File(path)
            file.delete()
        }
        message

    }
}
