package com.datavideo.jobs

import datavideo.CopyDataToDBService
import datavideo.DataFileEntry

class CreateVideoJob {
    def concurrent=false
//    def sessionRequired=false
    CopyDataToDBService copyDataToDBService
    static triggers = {
        simple name: 'createVideoTrigger' ,startDelay: 1000*2,repeatInterval: 1000*10
    }

    def execute(){
        if(DataFileEntry.findByStatus("NULL")){
            List<DataFileEntry> dataFileEntryList=DataFileEntry.findAllByStatus("NULL")

            dataFileEntryList.each {
                copyDataToDBService.copyToDB(it.filePath+it.fileName)
                it.status="PROCESSED"
                it.save(flush:true)
            }
        }

    }
}
