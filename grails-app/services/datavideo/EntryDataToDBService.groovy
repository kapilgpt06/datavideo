package datavideo

import grails.transaction.Transactional
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory

@Transactional
class EntryDataToDBService {
    private static FileInputStream fis;
    private static Workbook wb;
    private static Sheet sh1;
    private static Sheet sh2;

    String capitalize(String string){
        String[] str=string.toLowerCase().split(" |\\.")
        String result=new String()
        str.each {
            if(it.length()==1){
                result+=it.toUpperCase()+". "
            }
            if(it.length()>1){
                result+=it.substring(0,1).toUpperCase()+it.substring(1)+" "
            }
        }
        result
    }

    def entryToDB(DataFileEntry dataFileEntry) {
        String excelSheetFilePath = dataFileEntry.filePath
        fis = new FileInputStream(excelSheetFilePath);
        wb = WorkbookFactory.create(fis);
        sh1 = wb.getSheet("electors");
        sh2 = wb.getSheet("Cand_Wise");

        int j = 4
        int i = 4
        boolean flag = true
        while (i<6  ) {


            String state = capitalize(String.valueOf(sh1.getRow(i).getCell(1)).toLowerCase());
            String constituencyName = capitalize(String.valueOf(sh1.getRow(i).getCell(3)).toLowerCase())
            String voters = indiaFormatNumber(sh1.getRow(i).getCell(4));
            String electors = indiaFormatNumber(sh1.getRow(i).getCell(5));
            String percentage = indiaFormatNumber(sh1.getRow(i).getCell(6));
            Constituency constituency = new Constituency(stateName: state, constituencyName: constituencyName, totalVoters: voters, totalElectors: electors, percentage: percentage)

            String constituencyNameCheck=filter(constituencyName)
            String currentCons = filter(String.valueOf(sh1.getRow(i).getCell(3)))

            println "pre ="+currentCons+" "+constituencyNameCheck
            while (constituencyNameCheck==currentCons) {
                String candidateName = capitalize(String.valueOf(sh2.getRow(j).getCell(7)).toLowerCase())
                String partySign = String.valueOf(sh2.getRow(j).getCell(11)).toLowerCase().toUpperCase();
                String totalVotePolled = indiaFormatNumber(sh2.getRow(j).getCell(12));
                int position = Integer.parseInt(String.valueOf(sh2.getRow(j).getCell(13)).split("\\.")[0]);

                Candidate candidate = new Candidate(stateName: state, candidateName: candidateName, partySign: partySign, totalVotesPolled: totalVotePolled, position: position,constituency: constituency)
                Constituency.withNewTransaction {
                    constituency.addToCandidates(candidate).save(flush:true)
                }

                j++
                currentCons = filter(String.valueOf(sh2.getRow(j)?.getCell(5)))
            }

            StringBuilder videoName = new StringBuilder(dataFileEntry.fileName)
            videoName = videoName.delete(videoName.length() - 4, videoName.length())

            String[] str = String.valueOf(videoName).split("_")
            String year = str[1]
            String electionType = str[2]
            VideoDataEntry.withNewTransaction {

                VideoDataEntry videoDataEntry = new VideoDataEntry(channel: dataFileEntry.channel, videoName: videoName + "_" + constituencyName, dataFile: dataFileEntry,
                        year: year, electionType: electionType, constituency: constituency)
                videoDataEntry.save(flush: true)
            }



            i++
            String nextRow = String.valueOf(sh1.getRow(i))
            if (nextRow.equals('null')) {
                flag = false
            }

        }
        dataFileEntry.status = "PROCESSED"
        dataFileEntry.save(flush: true)
    }

    String indiaFormatNumber(Object object){
        Double Double=Double.parseDouble(String.valueOf(object))
        String integerFormat=String.format("%,.2f", Double)
        integerFormat.split("\\.")[0]

    }
    String filter(String str){
        String[] splitString=str.toLowerCase().trim().split(" |\\-|\\.|\\&")
        String result=new String()
        splitString.each {
            if(it.length()>1){
                result+=it
            }
        }
        result
    }

}
