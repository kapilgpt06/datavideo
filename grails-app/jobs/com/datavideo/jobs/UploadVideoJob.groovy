package com.datavideo.jobs

import datavideo.Channel
import datavideo.DataFileEntry
import datavideo.EntryDataToDBService
import datavideo.UploadVideoService
import datavideo.VideoDataEntry

class UploadVideoJob {
    def concurrent=false
    UploadVideoService uploadVideoService
    static triggers = {
        simple name: 'uploadVideoTrigger' ,startDelay: 1000*8,repeatInterval: 1000*10
    }

    def execute(){
        println("workUV1")
        if(VideoDataEntry.findByVideoId("NULL")){
            List<VideoDataEntry> videoDataEntryList=VideoDataEntry.findAllByVideoIdAndVideoPathNotEqual("NULL","NULL")
            videoDataEntryList.each {
                Channel channel=it.ownerChannel
                String videoId=uploadVideoService.uploadVideo(channel.accesssToken,"/home/kapil/opt/d2v/resource/"+it.videoPath)
                it.videoId=videoId
                it.save(flush:true)
            }
        }
    }
}
