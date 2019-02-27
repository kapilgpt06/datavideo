package datavideo

class VideoDataEntry {

    String videoName
    String videoId="NULL"
    String videoPath="NULL"
    DataFileEntry dataFile
    Channel channel
    String year
    String electionType
    Constituency constituency
    Date videoCreatedDate
    Date videoUploadDate
    static constraints = {
        videoPath nullable: true
        videoId nullable: true
        videoName unique: true
        videoCreatedDate nullable: true
        videoUploadDate nullable: true

    }
}
