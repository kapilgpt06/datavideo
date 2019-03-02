package datavideo

import grails.core.GrailsApplication
import grails.transaction.Transactional

import java.nio.file.Files;
import java.nio.file.Paths;

@Transactional
class CreateVideoService {
    GrailsApplication grailsApplication


    private static String subtitles = new String();

     void loadSubtitles() throws Exception {
        String subtitleTempPath=grailsApplication.config.video.path+"/resource/sub.ass"
        subtitles = new String(Files.readAllBytes(Paths.get(subtitleTempPath)));
    }

     void saveSubtitle() throws Exception {
         String subtitleFilePath=grailsApplication.config.video.path+"/resource/subtitles.ass"
        File f = new File(subtitleFilePath);
        Writer writer = new FileWriter(f);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        bufferedWriter.write(subtitles);
        bufferedWriter.flush();
        bufferedWriter.close();
    }

     void setConfigFile(String time, String videoName) throws Exception {
         String configTempFilePath=grailsApplication.config.video.path+"/resource/conf.json"
         String configFilePath=grailsApplication.config.video.path+"/resource/config.json"
        String data = new String(Files.readAllBytes(Paths.get(configTempFilePath)));
        data = data.replace("tcTime", time);
        data = data.replace("tcVideoName", videoName + ".mp4");
        File f = new File(configFilePath);
        Writer writer = new FileWriter(f);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        bufferedWriter.write(data);
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    void madeVideo(String makeVideoFilePath,String command) {
        Runtime rt = Runtime.getRuntime();
        File dir = new File(makeVideoFilePath)

        try {
            Process process = rt.exec(command, null, dir);
            process.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {

                System.out.println(line + "\n");
            }

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    void convert(VideoDataEntry videoDataEntry) throws Exception {

        Constituency constituency=videoDataEntry.constituency

            loadSubtitles()

            String stateName=constituency.stateName
            String constituencyName=constituency.constituencyName
            String totalVoters=constituency.totalVoters
            String totalElectors=constituency.totalElectors
            String percentage=constituency.percentage
//            String year=constituency.year

            //this is use to set first and second slide timming
            String firstTimming = new String();
            firstTimming+="Dialogue: Marked=0,0:00:01.00,0:00:04.00,DefaultVCD,NTP,0000,0000,0000,,{\\b1\\c&H1F883D&}2014 \\N{\\c&H3E98F3&} LOK  SABHA" +
                    " \\N{\\c&H1F883D&} ELECTION RESULTS \\N\\N\\N\\N\\N{\\b0\\c&H1011EC&} "+constituencyName+" Constituency \\N{\\c&H070709&} ("+stateName+")\n"
            firstTimming+="Dialogue: Marked=0,0:00:04.00,0:00:10.00,new,NTP,0000,0200,0200,,No of Electors \\N{\\b1\\c&H1011EC&} "+totalElectors+"\n"
            firstTimming+="Dialogue: Marked=0,0:00:06.00,0:00:10.00,new,NTP,0200,0000,0200,,No of Voters \\N{\\b1\\c&H1011EC&}"+totalVoters+"\n"
            firstTimming+="Dialogue: Marked=0,0:00:08.00,0:00:10.00,new,NTP,0000,0000,0120,,Poll (%) \\N{\\b1\\c&H1011EC&}"+percentage+"%\n\n\n"


            List<Candidate> candidateList=Candidate.findAllByConstituencyAndPositionLessThan(constituency,8)

            String secondTiming=new String()

            int from=10
            int to=(candidateList.size()*2)+from
            if(candidateList.candidateName.contains("None Of The Above")){
                to=((candidateList.size()-1)*2)+from
            }
            int m=1
            println "contain NOTA "+candidateList.candidateName.contains("None Of The Above")
            secondTiming+="Dialogue: Marked=0,0:00:10.00,0:00:"+to+".00,new,NTP,0000,0000,0240,,{\\b1}Election Candidates \n\n\n"
            candidateList.each {
                println it
                if(it.candidateName.split(" |\\.|\\-").join()!="NoneOfTheAbove"){
                    secondTiming+="Dialogue: Marked=0,0:00:"+from+".00,0:00:"+to+".00,style1,NTP,0000,0000,0"+(220-(20*m))+",,"+m+". "+it.candidateName+"{\\b1} ("+it.partySign+") \n"
                    from+=2
                    m++
                }
            }
            Candidate firstCandidate=Candidate.findByConstituencyAndPosition(constituency,1)
            Candidate secondCandidate=Candidate.findByConstituencyAndPosition(constituency,2)
            Candidate thirdCandidate=Candidate.findByConstituencyAndPosition(constituency,3)
            String thirdTiming=new String()
            from=to
            to+=(2*4)
            thirdTiming+="Dialogue: Marked=0,0:00:"+from+".00,0:00:"+to+".00,style2,NTP,0000,0000,0240,,{\\b1}Election Results\n"
            thirdTiming+="Dialogue: Marked=0,0:00:"+(from+2)+".00,0:00:"+to+".00,style1,NTP,0000,0000,0180,,1. "+firstCandidate.candidateName+" ("+firstCandidate.partySign+") \\N{\\b1} "+firstCandidate.totalVotesPolled+" Votes\n"
            thirdTiming+="Dialogue: Marked=0,0:00:"+(from+4)+".00,0:00:"+to+".00,style1,NTP,0000,0000,0140,,2. "+secondCandidate.candidateName+" ("+secondCandidate.partySign+") \\N{\\b1} "+secondCandidate.totalVotesPolled+" Votes\n"
            thirdTiming+="Dialogue: Marked=0,0:00:"+(from+6)+".00,0:00:"+to+".00,style1,NTP,0000,0000,0100,,3. "+thirdCandidate.candidateName+" ("+thirdCandidate.partySign+") \\N{\\b1} "+thirdCandidate.totalVotesPolled+" Votes\n\n\n"

//            println thirdTiming
            thirdTiming+="Dialogue: Marked=0,0:00:04.00,0:00:$to.00,style3,NTP,0000,0000,0003,,{\\c&H2728FD&} $constituencyName Constituency \\N{\\c&HFFFFFF&} ($stateName)\n"
            from=to
            to+=5
            String fourthTiming=new String()

            fourthTiming+="Dialogue: Marked=0,0:00:"+from+".00,0:00:"+to+".00,new,NTP,0000,0000,0150,,"+firstCandidate.candidateName+" ("+firstCandidate.partySign+") \\N{\\b1}"+firstCandidate.totalVotesPolled+" Votes\n"
            fourthTiming+="Dialogue: Marked=0,0:00:"+from+".00,0:00:"+to+".00,style1,NTP,0000,0000,0100,,{\\b1\\c&H1011EC&} "+constituencyName+" Constituency \\N\\N{\\b0\\c&H070709&} ("+stateName+")\n"
            for(int i=0;i<5;i++){
                fourthTiming+="Dialogue: Marked=0,0:00:"+(from+i)+".00,0:00:"+(from+i)+".50,style2,NTP,0000,0000,0220,,{\\b1}Winner\n"
            }
//            println fourthTiming

            println("video timing "+to)

            subtitles+=firstTimming+secondTiming+thirdTiming+fourthTiming

            saveSubtitle();
            String channelId=videoDataEntry.channel.channelId
            setConfigFile(String.valueOf(to), "../"+channelId+"/"+videoDataEntry.videoName);

            String command=grailsApplication.config.videoshow.command
            String videoPath=grailsApplication.config.video.path
            madeVideo(videoPath+"/resource",command);


            Calendar calendar=Calendar.getInstance()
            Date date=calendar.getTime()

            videoDataEntry.videoPath=videoDataEntry.videoName+".mp4"
            videoDataEntry.videoCreatedDate=date
            videoDataEntry.save(flush:true)


    }
}
