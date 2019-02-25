package com.datavideo.jobs

import datavideo.EntryDataToDBService
import datavideo.DataFileEntry

class CheckDataFileJob {
    def concurrent=false
//    def sessionRequired=false
    EntryDataToDBService entryDataToDBService
    static triggers = {
        simple name: 'createVideoTrigger' ,startDelay: 1000*2,repeatInterval: 1000*10
    }

    def execute(){
        if(DataFileEntry.findByStatus("NULL")){
            List<DataFileEntry> dataFileEntryList=DataFileEntry.findAllByStatus("NULL")

            dataFileEntryList.each {
                entryDataToDBService.entryToDB(it.filePath+it.fileName)
                it.status="PROCESSED"
                it.save(flush:true)
            }
        }

    }
}
