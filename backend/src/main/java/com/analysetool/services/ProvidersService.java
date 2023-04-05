package com.analysetool.services;
import com.analysetool.repositories.providersrepository;
import com.analysetool.modells.providers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ProvidersService {

    @Autowired
    private providersrepository PR;

    //create operation
    public providers create(String Id){
    return PR.save(new providers(Id));
    }

    //retrieve operation
    public List<providers> getAll(){
        return PR.findAll();
    }

    //update operation
        //public providers update(String Id, String data){
        //providers P = PR.findById(Id);
        //P.setdata(data);
        //    return PR.save(P);
        // }

    //delete operation
    public void deleteAll(){
        PR.deleteAll();
    }
    //public void delete(String Id){
    //    providers P = (providers) PR.findById(Id);
   //     PR.delete(P);
   // }
}
