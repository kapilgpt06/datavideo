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
import grails.transaction.Transactional

@Transactional
class UploadVideoService {

    def serviceMethod() {

    }

    Object uploadVideo(String accessToken) {
        try{
            HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
            JsonFactory JSON_FACTORY = new JacksonFactory();

            Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);

            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
                    "youtube-cmdline-uploadvideo-sample").build();

            System.out.println("Uploading: " + SAMPLE_VIDEO_FILENAME);

            // Add extra information to the video before uploading.
            Video videoObjectDefiningMetadata = new Video();

            // Set the video to be publicly visible. This is the default
            // setting. Other supporting settings are "unlisted" and "private."
            VideoStatus status = new VideoStatus();
            status.setPrivacyStatus("private");
            videoObjectDefiningMetadata.setStatus(status);

            // Most of the video's metadata is set on the VideoSnippet object.
            VideoSnippet snippet = new VideoSnippet();

            // This code uses a Calendar instance to create a unique name and
            // description for test purposes so that you can easily upload
            // multiple files. You should remove this code from your project
            // and use your own standard names instead.
            Calendar cal = Calendar.getInstance();
            snippet.setTitle("Test Upload via Java on " + cal.getTime());
            snippet.setDescription(
                    "Video uploaded via YouTube Data API V3 using the Java library " + "on " + cal.getTime());

            // Set the keyword tags that you want to associate with the video.
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
                    new FileInputStream("/home/kapil/Project/videoshow/sample-video.mp4"));

            // Insert the video. The command sends three arguments. The first
            // specifies which information the API request is setting and which
            // information the API response should return. The second argument
            // is the video resource that contains metadata about the new video.
            // The third argument is the actual video content.
            YouTube.Videos.Insert videoInsert = youtube.videos()
                    .insert("snippet,statistics,status", videoObjectDefiningMetadata, mediaContent);

            // Set the upload type and add an event listener.
            MediaHttpUploader uploader = videoInsert.getMediaHttpUploader();

            // Indicate whether direct media upload is enabled. A value of
            // "True" indicates that direct media upload is enabled and that
            // the entire media content will be uploaded in a single request.
            // A value of "False," which is the default, indicates that the
            // request will use the resumable media upload protocol, which
            // supports the ability to resume an upload operation after a
            // network interruption or other transmission failure, saving
            // time and bandwidth in the event of network failures.
            uploader.setDirectUploadEnabled(true);

            MediaHttpUploaderProgressListener progressListener = new MediaHttpUploaderProgressListener() {
                public void progressChanged(MediaHttpUploader uploader1) throws IOException {
                    switch (uploader1.getUploadState()) {
//                    case INITIATION_STARTED:
//                        System.out.println("Initiation Started");
//                        break;
//                    case INITIATION_COMPLETE:
//                        System.out.println("Initiation Completed");
//                        break;
//                    case MEDIA_IN_PROGRESS:
//                        System.out.println("Upload in progress");
//                        System.out.println("Upload percentage: " + uploader1.getNumBytesUploaded());
//                        break;
//                    case MEDIA_COMPLETE:
//                        System.out.println("Upload Completed!");
//                        break;
//                    case NOT_STARTED:
//                        System.out.println("Upload Not Started!");
//                        break;
                    }
                }
            };
            uploader.setProgressListener(progressListener);

            // Call the API and upload the video.
            Video returnedVideo = videoInsert.execute();

            println returnedVideo
            // Print data about the newly inserted video from the API response.
            System.out.println("\n================== Returned Video ==================\n");
            System.out.println("  - Id: " + returnedVideo.getId());
            System.out.println("  - Title: " + returnedVideo.getSnippet().getTitle());
            System.out.println("  - Tags: " + returnedVideo.getSnippet().getTags());
            System.out.println("  - Privacy Status: " + returnedVideo.getStatus().getPrivacyStatus());
            System.out.println("  - Video Count: " + returnedVideo.getStatistics().getViewCount());

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
