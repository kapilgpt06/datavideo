package datavideo

import grails.transaction.Transactional

import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Transactional
class ConvertToVideoService {

    def serviceMethod() {

    }
    private static Workbook wb;
    private static Sheet sh1;
    private static Sheet sh2;
    private static FileInputStream fis;
    private static FileOutputStream fos;
    private static Row row;
    private static Cell cell;
    private static final String subtitleDiePath = "/home/kapil/Project/videoshow/sub.ass";
    private static final String subtitleFilePath = "/home/kapil/Project/videoshow/subtitles.ass";
    private static final String configDieFilePath = "/home/kapil/Project/videoshow/conf.json";
    private static final String configFilePath = "/home/kapil/Project/videoshow/config.json";
    private static final String ExcelsheetFilePath = "grails-app/2014.xls";
    private static final String makeVideoFilePath = "/home/kapil/Project/videoshow";
    private static final String makeVideoFileName = "./makevideo.sh";
    private static String subtitles = new String();

    static void loadSubtitles() throws Exception {
        subtitles = new String(Files.readAllBytes(Paths.get(subtitleDiePath)));
    }

    static void saveSubtitle() throws Exception {
        File f = new File(subtitleFilePath);
        Writer writer = new FileWriter(f);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        bufferedWriter.write(subtitles);
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    static void setConfigFileTime(String time, String videoName) throws Exception {
        String data = new String(Files.readAllBytes(Paths.get(configDieFilePath)));
        data = data.replace("tcTime", time);
        data = data.replace("tcVideoName", videoName + ".mp4");
        File f = new File(configFilePath);
        Writer writer = new FileWriter(f);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        bufferedWriter.write(data);
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    static void madeVideo() {
        Runtime rt = Runtime.getRuntime();
        File dir = new File(makeVideoFilePath);
        try {
            Process process = rt.exec(makeVideoFileName, null, dir);
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

    void convert() throws Exception {

        List<Constituency> constituencyList=Constituency.list()
        for(Constituency constituency:constituencyList){

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
            println(noOfCandidate)
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
            println(from+" "+to)
            println(secondTiming)

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

            println thirdTiming

            from=to
            to+=5
            String fourthTiming=new String()

            fourthTiming+="Dialogue: Marked=0,0:00:"+from+".00,0:00:"+to+".00,new,NTP,0000,0000,0150,,"+firstCandidate.candidateName+" ("+firstCandidate.partySign+") \\N{\\b1}"+firstCandidate.totalVotesPolled+" Votes\n"
            fourthTiming+="Dialogue: Marked=0,0:00:"+from+".00,0:00:"+to+".00,style1,NTP,0000,0000,0100,,{\\b1\\c&H1011EC&} "+constituencyName+" Constituency \\N\\N{\\b0\\c&H070709&} ("+stateName+")\n"
            for(int i=0;i<5;i++){
                fourthTiming+="Dialogue: Marked=0,0:00:"+(from+i)+".00,0:00:"+(from+i)+".50,style2,NTP,0000,0000,0220,,{\\b1}Winner\n"
            }
            println fourthTiming

            println("video timing "+to)

            subtitles+=firstTimming+secondTiming+thirdTiming+fourthTiming

            saveSubtitle();
            setConfigFileTime(String.valueOf(to), "2014_lok-sabha_"+constituencyName);

            madeVideo();


        }
/*
        fis = new FileInputStream(ExcelsheetFilePath);
        wb = WorkbookFactory.create(fis);
        sh1 = wb.getSheet("electors");
        sh2 = wb.getSheet("Cand_Wise");
        //i is starting Row No of electors sheet
        int i = 4;

        //j is starting Row No of Cand_wise sheet
        int j = 4;
        for (i = 4; i < 6; i++) {

            loadSubtitles();

            String state = String.valueOf(sh1.getRow(i).getCell(1));
            String cons = String.valueOf(sh1.getRow(i).getCell(3));
            String voters = String.valueOf(sh1.getRow(i).getCell(4));
            String electors = String.valueOf(sh1.getRow(i).getCell(5));
            String percentage = String.valueOf(sh1.getRow(i).getCell(6));
            System.out.println(state + " " + cons + " " + voters + " " + electors + " " + percentage);

            //this is use to set timming of No of voters etc
            String voterTimming = new String();
            voterTimming += "Dialogue: Marked=0,0:00:01.00,0:00:04.00,DefaultVCD,NTP,0000,0000,0000,,2014 LOK  SABHA \\N ELECTION RESULTS \\N\\N\\N " + cons.toUpperCase() + " \\N CONSTITUENCY \\N " + state.toUpperCase() + "\n\n";
            voterTimming += "Dialogue: Marked=0,0:00:04.00,0:00:10.00,new,NTP,0000,0000,0000,,NUMBER OF ELECTORS \\N " + electors + "\n";
            voterTimming += "Dialogue: Marked=0,0:00:06.00,0:00:10.00,new,NTP,0000,0000,0170,,NUMBER OF VOTERS \\N " + voters + "\n";
            voterTimming += "Dialogue: Marked=0,0:00:08.00,0:00:10.00,new,NTP,0000,0000,0120,,POLL PERCENTAGE \\N " + percentage + "%\n\n";


            String currentCons = String.valueOf(sh1.getRow(i).getCell(3));
            //WINNER Details
            String winner = String.valueOf(sh2.getRow(j).getCell(7));
            String winnerVotes = String.valueOf(sh2.getRow(j).getCell(12));
            int from = 10;
            int to = 12;

            //this loop is use to set timming of different candidate
            int noOfCandidate = 0;
            String candidateTiming = new String();

            while (cons.equals(currentCons)) {
                String candidateName = String.valueOf(sh2.getRow(j).getCell(7));
                if(!candidateName.equals("None of the Above")){
                    String partySign = String.valueOf(sh2.getRow(j).getCell(11));

                    candidateTiming += "Dialogue: Marked=0,0:00:" + from + ".00,0:00:" + to + ".00,new,NTP,0000,0000,0130,," + candidateName + " \\N (" + partySign + ") \n";
                    from = from + 2;
                    to = to + 2;
                    noOfCandidate++;

                }
                j++;
                currentCons = String.valueOf(sh2.getRow(j).getCell(5));

            }
            //beacuse in upperloop value are inscred one times extra
            from = from - 2;
            to = to - 2;

            candidateTiming += "Dialogue: Marked=0,0:00:" + (to - (2 * noOfCandidate)) + ".00,0:00:" + to + ".00,new,NTP,0000,0000,0200,,NAME OF CANDIDATE \n\n";

            //this loop is use to set timming of WINNER
            String winnerTiming = "Dialogue: Marked=0,0:00:" + to + ".00,0:00:" + (to + 5) + ".00,new,NTP,0000,0000,0130,," + winner + " (TDP) \\N   " + winnerVotes + " \\N    VOTES RECIEVED \n";
            for (int k = 0; k < 5; k++) {
                winnerTiming += "Dialogue: Marked=0,0:00:" + to + ".00,0:00:" + to + ".50,style2,NTP,0000,0000,0200,,WINNER \n";
                to++;
            }

            subtitles += voterTimming + candidateTiming + winnerTiming;

            System.out.println("no of candidate " +noOfCandidate)
            System.out.println("video time "+to);

            saveSubtitle();
            setConfigFileTime(String.valueOf(to), i-3);

            madeVideo();

            fos=new FileOutputStream(ExcelsheetFilePath)
            Cell cell1=sh1.getRow(i).getCell(7)
            cell1.setCellValue("yes")
            wb.write(fos)
        }
*/


    }
}
