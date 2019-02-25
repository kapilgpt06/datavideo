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

    def entryToDB(String excelSheetFilePath){
        fis = new FileInputStream(excelSheetFilePath);
        wb = WorkbookFactory.create(fis);
        sh1 = wb.getSheet("electors");
        sh2 = wb.getSheet("Cand_Wise");

        int j=4
        for(int i=4;i<7;i++){

            String state =capitalize( String.valueOf(sh1.getRow(i).getCell(1)).toLowerCase());
            String cons = capitalize(String.valueOf(sh1.getRow(i).getCell(3)).toLowerCase())
            String voters = indiaFormatNumber(sh1.getRow(i).getCell(4));
            String electors = indiaFormatNumber(sh1.getRow(i).getCell(5));
            String percentage = indiaFormatNumber(sh1.getRow(i).getCell(6));


            Constituency constituency=new Constituency(stateName: state,constituencyName: cons,totalVoters: voters,totalElectors: electors,percentage: percentage)
            String currentCons = String.valueOf(sh1.getRow(i).getCell(3));


            while (cons.equals(currentCons)) {
                String candidateName = capitalize(String.valueOf(sh2.getRow(j).getCell(7)).toLowerCase())
                String partySign = String.valueOf(sh2.getRow(j).getCell(11)).toLowerCase().toUpperCase();
                String totalVotePolled = indiaFormatNumber(sh2.getRow(j).getCell(12));
                String position = indiaFormatNumber(sh2.getRow(j).getCell(13));

                Candidate candidate=new Candidate(stateName: state,candidateName: candidateName,partySign: partySign,totalVotesPolled: totalVotePolled,position: position)

                constituency.addToCandidates(candidate).save(flush:true)
                currentCons = String.valueOf(sh2.getRow(j+1).getCell(5));
                j++
            }
        }
    }

    String indiaFormatNumber(Object object){
        Double Double=Double.parseDouble(String.valueOf(object))
        String integerFormat=String.format("%,.2f", Double)
        integerFormat.split("\\.")[0]

    }
}
