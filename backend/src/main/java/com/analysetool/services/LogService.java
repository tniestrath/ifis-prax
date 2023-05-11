package com.analysetool.services;

import com.analysetool.repositories.PostRepository;
import com.analysetool.repositories.statsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    private  PostRepository postRepository;
    private  statsRepository statsRepo;
    private String path= "";
    private String BlogSSPattern = "\\bGET \\/blog\\/\\w+\\/\\sHTTP\\/\\d.\\d\"\\s200\\s\\d+\\s\"\\S+\"\\s\\(\"http:\\/\\/\\S+\\?s=\\S+\"\\)\\s\\(\"\\S+\"\\s\\S+\\s\\S+\\s\\S+\\)\\s=\\ssearch\\s\n"; //search +1, view +1,(bei match) vor blog view pattern
    private String ArtikelSSPattern="\\bGET \\/artikel\\/\\w+\\/\\sHTTP\\/\\d.\\d\"\\s200\\s\\d+\\s\"\\S+\"\\s\\(\"http:\\/\\/\\S+\\?s=\\S+\"\\)\\s\\(\"\\S+\"\\s\\S+\\s\\S+\\s\\S+\\)\\s=\\ssearch\\s\n";//search +1, view +1,(bei match) vor artikel view pattern
    private String BlogViewPattern="^.* GET /blog/.* HTTP/1\\.1\" 200 .*$\n";//Blog view +1 bei match
    private String ArtikelViewPattern="^.* GET /artikel/.* HTTP/1\\.1\" 200 .*$\n";//Artikel view +1 bei match
    private String lastLine = "";
    private int lineCounter=0;
    private int lastLineCounter=0;
    private boolean liveScanning = false;

    @Autowired
    public LogService(PostRepository postRepository, statsRepository StatsRepository) {
        this.postRepository = postRepository;
        this.statsRepo = StatsRepository;
    }

    public void run(boolean liveScanning,String path){
        this.liveScanning=liveScanning;
        this.path=path;

    }

}
