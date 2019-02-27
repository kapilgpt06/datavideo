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
        println("data entry job work")
        if(DataFileEntry.findByStatus("NULL")){
            println("data entry job detect")

            List<DataFileEntry> dataFileEntryList=DataFileEntry.findAllByStatus("NULL")

            dataFileEntryList.each {
                entryDataToDBService.entryToDB(it)

            }
        }

    }
}
