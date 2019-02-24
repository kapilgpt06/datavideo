package datavideo

import org.apache.poi.ss.usermodel.WorkbookFactory

class BootStrap {

    def init = { servletContext ->
        /*final String ExcelsheetFilePath = "grails-app/2014.xls";
        if(Candidate.list().size()==0){
            Candidate candidate1=new Candidate(stateName: "Andhra Pradesh",candidateName: "Godam Nagesh",partySign: "TRS",totalVotesPolled: "430847",position: "1")
            Candidate candidate2=new Candidate(stateName: "Andhra Pradesh",candidateName: "Naresh",partySign: "SSE",totalVotesPolled: "3242",position: "2")
            Candidate candidate3=new Candidate(stateName: "Andhra Pradesh",candidateName: "manmohan",partySign: "AQW",totalVotesPolled: "1234",position: "3")
            Candidate candidate4=new Candidate(stateName: "Andhra Pradesh",candidateName: "kapil",partySign: "DFR",totalVotesPolled: "145",position: "4")
            Candidate candidate5=new Candidate(stateName: "Andhra Pradesh",candidateName: "Ankush",partySign: "XSD",totalVotesPolled: "2344",position: "5")
            Candidate candidate6=new Candidate(stateName: "Andhra Pradesh",candidateName: "Aparsh",partySign: "ASD",totalVotesPolled: "12",position: "6")


            Constituency constituency=new Constituency(stateName: "Andhra Pradesh",constituencyName: "Adilabad ",totalVoters: "1055593",totalElectors: "1386282",percentage: "76.15")
            constituency.addToCandidates(candidate1)
                    .addToCandidates(candidate2)
                    .addToCandidates(candidate3)
                    .addToCandidates(candidate4)
                    .addToCandidates(candidate5)
                    .addToCandidates(candidate6)
                    .save(flush:true)

}
        */

    }
    def destroy = {
    }
}
