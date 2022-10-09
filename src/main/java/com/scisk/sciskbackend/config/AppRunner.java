package com.scisk.sciskbackend.config;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.scisk.sciskbackend.datasourceentity.UserDS;
import com.scisk.sciskbackend.entity.User;
import com.scisk.sciskbackend.repository.UserRepository;
import com.scisk.sciskbackend.service.CounterService;
import com.scisk.sciskbackend.util.GlobalParams;
import com.scisk.sciskbackend.util.PasswordEncodingManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoOperations;
import org.bson.Document;
import org.springframework.stereotype.Component;
import java.util.*;

@Log4j2
@Component
public class AppRunner implements ApplicationRunner {
	
	@Autowired
    private MongoOperations mongo;

	@Autowired
	private UserRepository userRepository;

    @Autowired
    private PasswordEncodingManager passwordEncodingManager;

    @Autowired
    private CounterService counterService;

    public AppRunner() {
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
    	
        // on créé la collection counters dans la base de données pour la génération des ids
        createAndPopulateCountersCollection();

        // on créé un utilisateur superuser
        createSuperUser();
    }

    public void createSuperUser() {
        Set<String> collectionNames = mongo.getCollectionNames();
        boolean userCollectionExists = false;

        for (final String name : collectionNames) {
            if (name.equalsIgnoreCase(GlobalParams.USER_COLLECTION_NAME)) {
                userCollectionExists = true;
                break;
            }
        }

        MongoCollection mongoCollection;
        if (!userCollectionExists) {
            mongoCollection = mongo.createCollection(GlobalParams.USER_COLLECTION_NAME);
        } else {
            mongoCollection = mongo.getCollection(GlobalParams.USER_COLLECTION_NAME);
        }

        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("email", GlobalParams.SUPERUSER_EMAIL);
        Document obj = (Document) mongoCollection.find(whereQuery).first();
        if (Objects.isNull(obj)) {
            User user = User.builder()
                    .firstname(GlobalParams.SUPERUSER_FIRSTNAME)
                    .lastname(GlobalParams.SUPERUSER_LASTNAME)
                    .email(GlobalParams.SUPERUSER_EMAIL)
                    .roles(Collections.singletonList(GlobalParams.Role.ADMINISTRATOR.name()))
                    .status(GlobalParams.UserStatus.ACTIVE.name())
                    .password(passwordEncodingManager.encode(GlobalParams.SUPERUSER_PASSWORD))
                    .build();
            user.setId(counterService.getNextSequence(GlobalParams.USER_COLLECTION_NAME));
            userRepository.save(UserDS.map(user));
        }

    }

    /**
     * Créer la collection counters dans la bd. Cette collection sert à stocker les séquences des ids des autres tables
     */
    public void createAndPopulateCountersCollection() {
        List<String> documentNames = new ArrayList<>();
        documentNames.add(GlobalParams.REFRESHTOKEN_COLLECTION_NAME);
        documentNames.add(GlobalParams.PAYMENT_COLLECTION_NAME);
        documentNames.add(GlobalParams.USER_COLLECTION_NAME);
        documentNames.add(GlobalParams.RECORD_COLLECTION_NAME);
        documentNames.add(GlobalParams.RECORD_STEP_COLLECTION_NAME);
        documentNames.add(GlobalParams.RECORD_JOB_COLLECTION_NAME);
        documentNames.add(GlobalParams.SERVICE_COLLECTION_NAME);
        documentNames.add(GlobalParams.STEP_COLLECTION_NAME);
        documentNames.add(GlobalParams.JOB_COLLECTION_NAME);
        documentNames.add(GlobalParams.NEEDED_DOCUMENT_COLLECTION_NAME);
        documentNames.add(GlobalParams.NEEDED_DOCUMENT_COLLECTION_NAME);

        Set<String> collectionNames = mongo.getCollectionNames();
        boolean countersCollectionExists = false;
        for (final String name : collectionNames) {
            if (name.equalsIgnoreCase(GlobalParams.COUNTERS_COLLECTION_NAME)) {
                countersCollectionExists = true;
                break;
            }
        }

        if (!countersCollectionExists) {
            log.info("com.osutech.osulistbackend.interfaceadapters.runners.run : create counters collection");
            MongoCollection mongoCollection = mongo.createCollection(GlobalParams.COUNTERS_COLLECTION_NAME);
            var docs = new ArrayList<Document>();
            Document d;
            for (String doc : documentNames) {
                d = new Document("_id", doc);
                d.append("seq", 0);
                docs.add(d);
            }
            mongoCollection.insertMany(docs);
        } else {
            MongoCollection mongoCollection = mongo.getCollection(GlobalParams.COUNTERS_COLLECTION_NAME);
            BasicDBObject whereQuery = new BasicDBObject();
            Document obj;
            for (String doc : documentNames) {
                whereQuery.put("_id", doc);
                obj = (Document) mongoCollection.find(whereQuery).first();
                if (Objects.isNull(obj)) {
                    Document d = new Document("_id", doc);
                    d.append("seq", 0);
                    mongoCollection.insertOne(d);
                }
            }
        }
    }

}
