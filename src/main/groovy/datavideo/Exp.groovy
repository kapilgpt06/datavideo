package datavideo

import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory

class Exp {
static void main(String[] args)throws Exception{
    String str="/home/kapil/opt/d2v/UCKl5pBcN9bl0cyrzaHHYtKA/election_2014_loksabha.xls"

    FileInputStream fis = new FileInputStream(str);

    Workbook wb = WorkbookFactory.create(fis);
    Sheet sh1 = wb.getSheet("electors");
    Sheet sh2 = wb.getSheet("Cand_Wise");

    println sh1.getRow(3).getCell(1)
}
}
