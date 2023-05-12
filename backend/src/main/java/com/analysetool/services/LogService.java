package com.analysetool.services;

import com.analysetool.repositories.PostRepository;
import com.analysetool.repositories.statsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LogService {

    private PostRepository postRepository;
    private statsRepository statsRepo;
    private BufferedReader br;
    private String path = "";
    private String BlogSSPattern = "\\bGET \\/blog\\/\\w+\\/\\sHTTP\\/\\d.\\d\"\\s200\\s\\d+\\s\"\\S+\"\\s\\(\"http:\\/\\/\\S+\\?s=\\S+\"\\)\\s\\(\"\\S+\"\\s\\S+\\s\\S+\\s\\S+\\)\\s=\\ssearch\\s\n"; //search +1, view +1,(bei match) vor blog view pattern
    private String ArtikelSSPattern = "\\bGET \\/artikel\\/\\w+\\/\\sHTTP\\/\\d.\\d\"\\s200\\s\\d+\\s\"\\S+\"\\s\\(\"http:\\/\\/\\S+\\?s=\\S+\"\\)\\s\\(\"\\S+\"\\s\\S+\\s\\S+\\s\\S+\\)\\s=\\ssearch\\s\n";//search +1, view +1,(bei match) vor artikel view pattern
    //private String BlogViewPattern = "^.* GET /blog/.* HTTP/1\\.1\" 200 .*$\n";//Blog view +1 bei match
    private String BlogViewPattern = ".*GET /blog.*$"
            ;


    //Blog view +1 bei match
    private String ArtikelViewPattern = "^.* GET /artikel/.* HTTP/1\\.1\" 200 .*$\n";//Artikel view +1 bei match
    Pattern pattern1_1 = Pattern.compile("Pattern1_1");
    Pattern pattern1_2 = Pattern.compile("Pattern1_2");
    Pattern pattern2_1 = Pattern.compile("Pattern2_1");
    Pattern pattern2_2 = Pattern.compile("Pattern2_2");
    private String lastLine = "";
    private int lineCounter = 0;
    private int lastLineCounter = 0;
    private boolean liveScanning = false;

    @Autowired
    public LogService(PostRepository postRepository, statsRepository StatsRepository) {
        this.postRepository = postRepository;
        this.statsRepo = StatsRepository;
    }

    public void run(boolean liveScanning, String path)  {
        this.liveScanning = liveScanning;
        this.path = path;
        try  {
            br = new BufferedReader(new FileReader(path));
            findAMatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void findAMatch() throws IOException {
        String line;

        boolean foundPattern = false;

        while ((line = br.readLine()) != null) {

            while(lineCounter!=lastLineCounter){
                br.readLine();
                lineCounter++;
                System.out.println("Counting up");
            }

            if (!foundPattern) {
                Matcher matcher1_1 = pattern1_1.matcher(line);

                if (matcher1_1.find()) {
                    Matcher matcher1_2 = pattern1_2.matcher(line);

                    foundPattern = false;
                    if (matcher1_2.find()) {
                        // Do something with the matched 1.2 patterns
                        System.out.println(line);
                        processLine(line,2,matcher1_2);
                        foundPattern = false;
                    } else {//1.1 matched
                        System.out.println(line);
                        processLine(line,1,matcher1_1);
                        foundPattern = false;
                    }
                }
            } else {
                Matcher matcher2_1 = pattern2_1.matcher(line);
                if (matcher2_1.find()) {
                    Matcher matcher2_2 = pattern2_2.matcher(line);
                    if (matcher2_2.find()) {
                        // Do something with the matched 2.2 patterns
                        processLine(line,4,matcher2_2);
                        foundPattern = false;
                        System.out.println(line);
                    } else {
                        //2.1 match
                        processLine(line,3,matcher2_1);
                        foundPattern = false;
                        System.out.println(line);
                    }
                }
            }

            lineCounter++;
            lastLineCounter++;
            //System.out.println(lineCounter+" "+lastLine);
            br.readLine();

        }
    }


    public void processLine(String line,int patternNumber,Matcher matcher){
        lastLine=line;
        if (patternNumber==1){
            System.out.println(matcher.group(1));
        }
        if (patternNumber==2){
            System.out.println(matcher.group(1));
        }
        if (patternNumber==3){
            System.out.println(matcher.group(1));
        }
        if (patternNumber==4){
            System.out.println(matcher.group(1));
        }
    }
}








