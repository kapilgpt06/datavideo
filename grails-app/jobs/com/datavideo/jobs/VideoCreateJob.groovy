package com.datavideo.jobs

import datavideo.CreateVideoService
import datavideo.EntryDataToDBService
import datavideo.VideoDataEntry

class VideoCreateJob {
    def concurrent=false
    EntryDataToDBService entryDataToDBService
    CreateVideoService createVideoService
    static triggers = {
        simple name: 'createVideoTrigger' ,startDelay: 1000*5,repeatInterval: 1000*10
    }

    def execute(){
        println "workCV1"
        List<VideoDataEntry> videoDataEntryList=VideoDataEntry.findAllByVideoPath("NULL")
        videoDataEntryList.each {
            println "workCV2"
            String[] str=it.videoName.split("_")
            String constituencyName=str[3]
            String year=str[1]
            String electtionType=str[2]
            createVideoService.convert(year,electtionType,constituencyName)

            it.videoPath=it.videoName+".mp4"
            it.save(flush:true)
        }

    }
}
