package datavideo

import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory

class Exp {
static void main(String[] args)throws Exception {

    String str="None Of The &.Above"
    println str.split(" |\\.|\\&|\\-").join()
}

}
