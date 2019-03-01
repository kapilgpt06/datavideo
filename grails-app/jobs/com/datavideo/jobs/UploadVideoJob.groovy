package com.datavideo.jobs

import datavideo.Channel
import datavideo.UploadVideoService
import datavideo.VideoDataEntry

class UploadVideoJob {
    def concurrent=false
    UploadVideoService uploadVideoService
    static boolean allIsWell=true
    static Date date
    static triggers = {
        simple name: 'uploadVideoTrigger' ,startDelay: 1000*8,repeatInterval: 1000*10
    }

    def execute(){
        println("upload video job work")
        if(allIsWell){

            if(VideoDataEntry.findByVideoIdAndVideoPathNotEqual("NULL","NULL")){
                println("upload video job detect")
//                VideoDataEntry videoDataEntry=VideoDataEntry.findByVideoIdAndVideoPathNotEqual("NULL","NULL")
//                uploadVideoService.uploadVideo(videoDataEntry)

            }

        }else{
            Date newDate=new Date()
            Long newDateInMS=newDate.getTime()
            Long dateInMS=date.getTime()
            Long diff=(newDateInMS-dateInMS)/(1000*60)
            if(diff>60){
                allIsWell=true
            }
        }
    }
}
