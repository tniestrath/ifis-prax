package com.analysetool.api;
import com.analysetool.modells.providers;
import com.analysetool.services.ProvidersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProvidersControler {

    @Autowired
    private ProvidersService PS;

/*
    @RequestMapping("/create")
       public String create(@RequestParam String id){
           providers p= PS.create(id);
           return p.getId();}
*/

    @RequestMapping("/getAll")
        public List<providers> getAll(){
            return PS.getAll();
        }

}
