package com.datavideo.jobs

import datavideo.CreateVideoService
import datavideo.EntryDataToDBService
import datavideo.VideoDataEntry

class VideoCreateJob {
    def concurrent=false
    CreateVideoService createVideoService
    static triggers = {
        simple name: 'createVideoTrigger' ,startDelay: 1000*5,repeatInterval: 1000*10
    }

    def execute(){
        println "create video job work"
        if(VideoDataEntry.findByVideoPath("NULL")){
            println "create video job detect"
            VideoDataEntry videoDataEntry=VideoDataEntry.findByVideoPath("NULL")
            createVideoService.convert(videoDataEntry)
        }

    }
}
