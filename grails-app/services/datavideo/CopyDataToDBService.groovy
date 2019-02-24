package datavideo

import grails.transaction.Transactional
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory

@Transactional
class CopyDataToDBService {
    private static FileInputStream fis;
    private static Workbook wb;
    private static Sheet sh1;
    private static Sheet sh2;

    def copyToDB(String excelSheetFilePath){
        fis = new FileInputStream(excelSheetFilePath);
        wb = WorkbookFactory.create(fis);
        sh1 = wb.getSheet("electors");
        sh2 = wb.getSheet("Cand_Wise");

        int j=4
        for(int i=4;i<6;i++){

            String state = String.valueOf(sh1.getRow(i).getCell(1)).toLowerCase().capitalize();
            String cons = String.valueOf(sh1.getRow(i).getCell(3)).toLowerCase().capitalize();
            String voters = String.valueOf((int)Double.parseDouble(String.valueOf(sh1.getRow(i).getCell(4))));
            String electors = String.valueOf((int)Double.parseDouble(String.valueOf(sh1.getRow(i).getCell(5))));
            String percentage = String.valueOf((int)Double.parseDouble(String.valueOf(sh1.getRow(i).getCell(6))));


            Constituency constituency=new Constituency(stateName: state,constituencyName: cons,totalVoters: voters,totalElectors: electors,percentage: percentage)
            String currentCons = String.valueOf(sh1.getRow(i).getCell(3));


            while (cons.equals(currentCons)) {
                String candidateName = String.valueOf(sh2.getRow(j).getCell(7)).toLowerCase().capitalize();
                String partySign = String.valueOf(sh2.getRow(j).getCell(11)).toLowerCase().toUpperCase();
                String totalVotePolled = String.valueOf((int)Double.parseDouble(String.valueOf(sh2.getRow(j).getCell(12))));
                String position = String.valueOf((int)Double.parseDouble(String.valueOf(sh2.getRow(j).getCell(13))));
                Candidate candidate=new Candidate(stateName: state,candidateName: candidateName,partySign: partySign,totalVotesPolled: totalVotePolled,position: position)

                constituency.addToCandidates(candidate).save(flush:true)
                currentCons = String.valueOf(sh2.getRow(j).getCell(5));
                j++
            }
        }
    }
}
