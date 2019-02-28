package datavideo

import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory

class Exp {
static void main(String[] args)throws Exception {
    Map map=[
        "code" : 403,
        "errors" : [ [
                         "domain" : "youtube.quota",
                         "message" : "The request cannot be completed because you have exceeded your <a href=\"/youtube/v3/getting-started#quota\">quota</a>.",
                         "reason" : "quotaExceeded"
                     ] ],
        "message" : "The request cannot be completed because you have exceeded your <a href=\"/youtube/v3/getting-started#quota\">quota</a>."
    ]

    println map.errors[0].domain
}
}
