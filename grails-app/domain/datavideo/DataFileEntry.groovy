package datavideo

class DataFileEntry {

    String fileName
    Date dateCreated
    String filePath
    Channel channel
    String status="NULL"
    User fileUploadBy
    String year
    String electionType
    static constraints = {
        fileName unique: true
    }

}
