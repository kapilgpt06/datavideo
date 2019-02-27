package datavideo

import com.google.api.client.auth.oauth2.BearerToken
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.googleapis.media.MediaHttpUploader
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener
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

    boolean checkAccessExpire(Channel channel){
        boolean flag=false
        Date date=channel.tokenCreatedOn
        long tokenCreatedMS=date.getTime()

        long currentMS=new Date().getTime()

        long differnce=(currentMS-tokenCreatedMS)/(1000*60)
        println differnce
        if(differnce>55)
            flag=true
        flag

    }


    def uploadVideo(VideoDataEntry videoDataEntry) {
        String videoMode=grailsApplication.config.youtube.video.mode
        String videoPathPrefix=grailsApplication.config.video.path
        String refreshToken=videoDataEntry.ownerChannel.refreshToken
        if(checkAccessExpire(videoDataEntry.ownerChannel)){
            String newAccessToken=channelService.generateNewAccessTokenUsingRefreshToken(refreshToken)
            channelService.saveNewAccessToken(newAccessToken,videoDataEntry.ownerChannel)
        }

        String accessToken=videoDataEntry.ownerChannel.accesssToken
        String videoPath=videoPathPrefix+"/"+videoDataEntry.ownerChannel.channelId+"/"+videoDataEntry.videoPath
        YouTube youtube
        String videoId=new String()
        try{
            HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
            JsonFactory JSON_FACTORY = new JacksonFactory();

            Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);
            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
                    "youtube-cmdline-uploadvideo-sample").build();

            System.out.println("Uploading: " + SAMPLE_VIDEO_FILENAME);

            Video videoObjectDefiningMetadata = new Video();
      VideoStatus status = new VideoStatus();
            status.setPrivacyStatus(videoMode);
            videoObjectDefiningMetadata.setStatus(status);
            VideoSnippet snippet = new VideoSnippet();
            Calendar cal = Calendar.getInstance();
            snippet.setTitle("Test Upload via Java on " + cal.getTime());
            snippet.setDescription(
                    "Video uploaded via YouTube Data API V3 using the Java library " + "on " + cal.getTime());

            List<String> tags = new ArrayList<String>();
            tags.add("test");
            tags.add("example");
            tags.add("java");
            tags.add("YouTube Data API V3");
            tags.add("erase me");
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
            videoId=returnedVideo.getId()
            // Print data about the newly inserted video from the API response.
            System.out.println("\n================== Returned Video ==================\n");
            System.out.println("  - Id: " + returnedVideo.getId());
            System.out.println("  - Title: " + returnedVideo.getSnippet().getTitle());
            System.out.println("  - Tags: " + returnedVideo.getSnippet().getTags());
            System.out.println("  - Privacy Status: " + returnedVideo.getStatus().getPrivacyStatus());
            System.out.println("  - Video Count: " + returnedVideo.getStatistics().getViewCount());

            videoDataEntry.videoId=videoId
            videoDataEntry.save(flush:true)
        } catch (
                GoogleJsonResponseException e) {
            System.err.println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (Throwable t) {
            System.err.println("Throwable: " + t.getMessage());
            t.printStackTrace();
        }
    }
}
