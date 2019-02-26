package datavideo

import grails.transaction.Transactional

import org.apache.poi.ss.usermodel.*

import java.nio.file.Files;
import java.nio.file.Paths;

@Transactional
class CreateVideoService {

    def serviceMethod() {

    }
    private static Workbook wb;
    private static Sheet sh1;
    private static Sheet sh2;
    private static FileInputStream fis;
    private static FileOutputStream fos;
    private static Row row;
    private static Cell cell;
    private static final String subtitleTempPath = "/home/kapil/opt/d2v/resource/sub.ass";
    private static final String subtitleFilePath = "/home/kapil/opt/d2v/resource/subtitles.ass";
    private static final String configTempFilePath = "/home/kapil/opt/d2v/resource/conf.json";
    private static final String configFilePath = "/home/kapil/opt/d2v/resource/config.json";
//    private static final String makeVideoFilePath = "/home/kapil/opt/d2v/resource";
    private static String subtitles = new String();

    static void loadSubtitles() throws Exception {
        subtitles = new String(Files.readAllBytes(Paths.get(subtitleTempPath)));
    }

    static void saveSubtitle() throws Exception {
        File f = new File(subtitleFilePath);
        Writer writer = new FileWriter(f);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        bufferedWriter.write(subtitles);
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    static void setConfigFile(String time, String videoName) throws Exception {
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

    static void madeVideo(String makeVideoFilePath) {
        Runtime rt = Runtime.getRuntime();
        File dir = new File(makeVideoFilePath)

        try {
            Process process = rt.exec("videoshow -c config.json -s subtitles.ass", null, dir);
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

        String[] str=videoDataEntry.videoName.split("_")
        String constituencyName1=str[3]
        String year1=str[1]
        String electtionType=str[2]
        Constituency constituency=Constituency.findByYearAndElectionTypeAndConstituencyName(year1,electtionType,constituencyName1)

            loadSubtitles()

            String stateName=constituency.stateName
            String constituencyName=constituency.constituencyName
            String totalVoters=constituency.totalVoters
            String totalElectors=constituency.totalElectors
            String percentage=constituency.percentage
            String year=constituency.year

            //this is use to set first and second slide timming
            String firstTimming = new String();
            firstTimming+="Dialogue: Marked=0,0:00:01.00,0:00:04.00,DefaultVCD,NTP,0000,0000,0000,,{\\b1\\c&H1F883D&}2014 \\N{\\c&H3E98F3&} LOK  SABHA" +
                    " \\N{\\c&H1F883D&} ELECTION RESULTS \\N\\N\\N\\N\\N{\\b0\\c&H1011EC&} "+constituencyName+" Constituency \\N{\\c&H070709&} ("+stateName+")\n"
            firstTimming+="Dialogue: Marked=0,0:00:04.00,0:00:10.00,new,NTP,0000,0200,0200,,No of Electors \\N{\\b1\\c&H1011EC&} "+totalElectors+"\n"
            firstTimming+="Dialogue: Marked=0,0:00:06.00,0:00:10.00,new,NTP,0200,0000,0200,,No of Voters \\N{\\b1\\c&H1011EC&}"+totalVoters+"\n"
            firstTimming+="Dialogue: Marked=0,0:00:08.00,0:00:10.00,new,NTP,0000,0000,0120,,Poll (%) \\N{\\b1\\c&H1011EC&}"+percentage+"%\n\n\n"

            List<Candidate> candidateList=constituency.candidates.toList()
            int noOfCandidate=candidateList.size()
//            println(noOfCandidate)
            int noOfCandidatePerSlide=9
            int noOfSlide=(noOfCandidate/noOfCandidatePerSlide)+1 //no of candidate name slide
            int to=10 //current last time
            int from=10
            String secondTiming=new String()
            for(int i=1;i<=noOfCandidate;i++){
                int slideNo=((i-1)/noOfCandidatePerSlide)+1
                int candidateNoPerSlide=(i-1)%noOfCandidatePerSlide
                int currentSlideLastTime=(noOfCandidate/(noOfCandidatePerSlide*slideNo))>=1?noOfCandidatePerSlide*2:(noOfCandidate%noOfCandidatePerSlide)*2
                String candidateName=candidateList[i-1].candidateName
                String partySign=candidateList[i-1].partySign

                secondTiming+="Dialogue: Marked=0,0:00:"+from+".00,0:00:"+(currentSlideLastTime+to)+".00,style1,NTP,0000,0000,0"+(210-(20*candidateNoPerSlide))+",,"+i+". "+candidateName+"{\\b1} ("+partySign+") \n"
                from+=2
                if(from==currentSlideLastTime+to){
                    to=from
                }
            }
            secondTiming+="Dialogue: Marked=0,0:00:10.00,0:00:"+to+".00,new,NTP,0000,0000,0240,,{\\b1}Election Candidates \n\n\n"
//            println(from+" "+to)
//            println(secondTiming)

            Candidate firstCandidate=new Candidate()
            Candidate secondCandidate=new Candidate()
            Candidate thirdCandidate=new Candidate()
            candidateList.each {
                if (it.position == '1') {
                    firstCandidate = it
                }
                if (it.position == '2') {
                    secondCandidate = it
                }
                if (it.position == '3') {
                    thirdCandidate = it
                }
            }
            String thirdTiming=new String()
            to+=(2*4)
            thirdTiming+="Dialogue: Marked=0,0:00:"+from+".00,0:00:"+to+".00,style2,NTP,0000,0000,0240,,{\\b1}Election Results\n"
            thirdTiming+="Dialogue: Marked=0,0:00:"+(from+2)+".00,0:00:"+to+".00,style1,NTP,0000,0000,0180,,1. "+firstCandidate.candidateName+" ("+firstCandidate.partySign+") \\N{\\b1} "+firstCandidate.totalVotesPolled+" Votes\n"
            thirdTiming+="Dialogue: Marked=0,0:00:"+(from+4)+".00,0:00:"+to+".00,style1,NTP,0000,0000,0140,,2. "+secondCandidate.candidateName+" ("+secondCandidate.partySign+") \\N{\\b1} "+secondCandidate.totalVotesPolled+" Votes\n"
            thirdTiming+="Dialogue: Marked=0,0:00:"+(from+6)+".00,0:00:"+to+".00,style1,NTP,0000,0000,0100,,3. "+thirdCandidate.candidateName+" ("+thirdCandidate.partySign+") \\N{\\b1} "+thirdCandidate.totalVotesPolled+" Votes\n\n\n"

//            println thirdTiming

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
            String channelId=videoDataEntry.ownerChannel.channelId
            setConfigFile(String.valueOf(to), "../"+channelId+"/"+videoDataEntry.videoName);
            madeVideo("/home/kapil/opt/d2v/resource");

            videoDataEntry.videoPath=videoDataEntry.videoName+".mp4"
            videoDataEntry.save(flush:true)


    }
}
