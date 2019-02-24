package datavideo

class Constituency {

    String stateName
    String constituencyName
    String totalVoters
    String totalElectors
    String percentage
    String year="2014"
    String electionType="loksabha"

    static hasMany = [candidates:Candidate]
    static constraints = {
    }
}
