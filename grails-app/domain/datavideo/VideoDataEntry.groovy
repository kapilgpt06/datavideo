package datavideo

class VideoDataEntry {

    Channel ownerChannel
    String videoName
    String videoId="NULL"
    String videoPath="NULL"
    static constraints = {
        videoPath nullable: true
        videoId nullable: true
        videoName unique: true
    }
}
