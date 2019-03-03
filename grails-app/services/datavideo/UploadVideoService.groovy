package datavideo

import com.datavideo.jobs.UploadVideoJob
import com.google.api.client.auth.oauth2.BearerToken
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.googleapis.media.MediaHttpUploader
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.InputStreamContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.Video
import com.google.api.services.youtube.model.VideoSnippet
import com.google.api.services.youtube.model.VideoStatus
import grails.core.GrailsApplication
import grails.transaction.Transactional

@Transactional
class UploadVideoService {
    GrailsApplication grailsApplication
    ChannelService channelService
    private static final String SAMPLE_VIDEO_FILENAME = "sample-video.mp4";
    private static final String VIDEO_FILE_FORMAT = "video/*";

    boolean checkAccessExpire(Channel channel) {
        boolean flag = false
        Date date = channel.tokenCreatedOn
        long tokenCreatedMS = date.getTime()

        long currentMS = new Date().getTime()

        long differnce = (currentMS - tokenCreatedMS) / (1000 * 60)
        if (differnce > 55)
            flag = true
        flag

    }


    def uploadVideo(VideoDataEntry videoDataEntry) {
        String videoMode = grailsApplication.config.youtube.video.mode
        String videoPathPrefix = grailsApplication.config.video.path

        String videoTittle = videoDataEntry.videoName.replace("_", " ")
        String electionType = videoDataEntry.electionType
        String year = videoDataEntry.year
        String refreshToken = videoDataEntry.channel.refreshToken

        if (checkAccessExpire(videoDataEntry.channel)) {
            String newAccessToken = channelService.generateNewAccessTokenUsingRefreshToken(refreshToken)
            channelService.saveNewAccessToken(newAccessToken, videoDataEntry.channel)
        }

        String accessToken = videoDataEntry.channel.accesssToken
        String videoPath = videoPathPrefix + "/" + videoDataEntry.channel.channelId + "/" + videoDataEntry.videoPath
        YouTube youtube
        String videoId = new String()
        try {
            HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
            JsonFactory JSON_FACTORY = new JacksonFactory();

            Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);
            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
                    "Election Result video upload").build();

            System.out.println("Uploading: " + videoTittle);

            Video videoObjectDefiningMetadata = new Video();
            VideoStatus status = new VideoStatus();
            status.setPrivacyStatus(videoMode);
            videoObjectDefiningMetadata.setStatus(status);
            VideoSnippet snippet = new VideoSnippet();
            Calendar cal = Calendar.getInstance();
            snippet.setTitle(videoTittle + " Constituency Result");
            snippet.setDescription("This is India " + electionType + " Election Result");

            List<String> tags = new ArrayList<String>();
            tags.add("Election");
            tags.add(year);
            tags.add(electionType);
            tags.add("Goverment");
            tags.add("People Time");
            snippet.setTags(tags);

            // Add the completed snippet object to the video resource.
            videoObjectDefiningMetadata.setSnippet(snippet);

            InputStreamContent mediaContent = new InputStreamContent(VIDEO_FILE_FORMAT,
                    new FileInputStream(videoPath));

            YouTube.Videos.Insert videoInsert = youtube.videos()
                    .insert("snippet,statistics,status", videoObjectDefiningMetadata, mediaContent);

            // Set the upload type and add an event listener.
            MediaHttpUploader uploader = videoInsert.getMediaHttpUploader();

            uploader.setDirectUploadEnabled(true);
            Video returnedVideo = videoInsert.execute();

            println returnedVideo
            videoId = returnedVideo.getId()
            // Print data about the newly inserted video from the API response.
            System.out.println("\n================== Returned Video ==================\n");
            System.out.println("  - Id: " + returnedVideo.getId());
            System.out.println("  - Title: " + returnedVideo.getSnippet().getTitle());
            System.out.println("  - Tags: " + returnedVideo.getSnippet().getTags());
            System.out.println("  - Privacy Status: " + returnedVideo.getStatus().getPrivacyStatus());
            System.out.println("  - Video Count: " + returnedVideo.getStatistics().getViewCount());


            Calendar calendar = Calendar.getInstance()
            Date date = calendar.getTime()

            videoDataEntry.videoId = videoId
            videoDataEntry.videoUploadDate = date
            videoDataEntry.save(flush: true)
        }
        catch (Throwable t) {

            String exceptionClass= t.getClass().getName()
            String exceptionMessage=t.getMessage()
            UploadVideoJob.allIsWell=false
            UploadVideoJob.date=new Date()
            sendMail(exceptionClass,exceptionMessage)
            System.err.println(exceptionMessage)
        }
    }

    def sendMail(String subjectMsg,String message) {
        String email=grailsApplication.config.exception.email
        sendMail {
            to email
            subject subjectMsg
            body message

        }
    }


}
