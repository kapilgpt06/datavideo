package datavideo

class DataFileEntry {

    String fileName
    Date dateCreated
    String loginUserId
    String filePath
    String status="NULL"
    static constraints = {
        loginUserId nullable: true
    }

}
