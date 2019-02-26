package datavideo

class Exp {
static void main(String[] args)throws Exception{
    Runtime rt = Runtime.getRuntime();
    File dir = new File("/home/kapil/Project/videoshow/new")
    if(!dir.exists()){
        dir.mkdirs();
    }
    try {
        Process process = rt.exec("videoshow -c ../videoshow/config.json -s ../videoshow/subtitles.ass ", null, dir);
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
}
