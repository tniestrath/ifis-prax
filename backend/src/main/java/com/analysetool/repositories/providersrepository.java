package com.analysetool.repositories;
import com.analysetool.modells.providers;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface providersrepository extends MongoRepository<providers,String> {


}
