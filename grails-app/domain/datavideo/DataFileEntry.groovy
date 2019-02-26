package datavideo

class DataFileEntry {

    String fileName
    Date dateCreated
    String loginUserId
    String filePath
    Channel ownerChannel
    String status="NULL"
    static constraints = {
        loginUserId nullable: true
        fileName unique: true
    }

}
