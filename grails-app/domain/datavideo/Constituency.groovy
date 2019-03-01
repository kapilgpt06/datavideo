package datavideo

class Constituency {

    String stateName
    String constituencyName
    String totalVoters
    String totalElectors
    String percentage

    static hasMany = [candidates:Candidate]
    static constraints = {

    }
}
