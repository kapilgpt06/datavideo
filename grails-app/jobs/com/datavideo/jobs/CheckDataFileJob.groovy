package com.datavideo.jobs

import datavideo.EntryDataToDBService
import datavideo.DataFileEntry

class CheckDataFileJob {
    def concurrent=false
    EntryDataToDBService entryDataToDBService
    static triggers = {
        simple name: 'checkDataTrigger' ,startDelay: 1000*2,repeatInterval: 1000*10
    }

    def execute(){
        println("workdb1")
        if(DataFileEntry.findByStatus("NULL")){
            println("workdb2")
            List<DataFileEntry> dataFileEntryList=DataFileEntry.findAllByStatus("NULL")

            dataFileEntryList.each {
                entryDataToDBService.entryToDB(it)
                it.status="PROCESSED"
                it.save(flush:true)
            }
        }

    }
}
