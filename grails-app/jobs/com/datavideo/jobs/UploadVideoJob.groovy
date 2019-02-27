package com.datavideo.jobs

import datavideo.Channel
import datavideo.UploadVideoService
import datavideo.VideoDataEntry

class UploadVideoJob {
    def concurrent=false
    UploadVideoService uploadVideoService
    static triggers = {
        simple name: 'uploadVideoTrigger' ,startDelay: 1000*8,repeatInterval: 1000*10
    }

    def execute(){
        println("upload video job work")
        if(VideoDataEntry.findByVideoIdAndVideoPathNotEqual("NULL","NULL")){
            println("upload video job detect")
            VideoDataEntry videoDataEntry=VideoDataEntry.findByVideoIdAndVideoPathNotEqual("NULL","NULL")
            uploadVideoService.uploadVideo(videoDataEntry)


        }
    }
}
