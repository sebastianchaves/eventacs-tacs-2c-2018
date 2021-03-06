package com.eventacs.mongo;

import com.eventacs.event.dto.EventListDTO;
import com.eventacs.httpclient.LocalDateTimeConverter;
import com.eventacs.user.dto.AlarmDAO;
import com.eventacs.user.dto.AlarmDTO;
import com.eventacs.user.exception.AlarmNotFound;
import com.eventacs.user.exception.EventListNotFound;
import com.mongodb.*;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class EventacsMongoClient {

    private MongoClient mongoClient;
    private DB database;
    private Morphia morphia;

    public EventacsMongoClient() {
        this.mongoClient = new MongoClient("mongo", 27017);
        this.database = mongoClient.getDB("eventacs");
        this.morphia = new Morphia();
        morphia.getMapper().getConverters().addConverter(new LocalDateTimeConverter());
    }


    private Datastore getDatastore(String dbName) {
        return morphia.createDatastore(mongoClient, dbName);
    }

    private DBCollection getCollection(String collection) {
        return database.getCollection(collection);
    }

    public <T> List<T> getElementsAs(Class<T> clazz, Map<String, Object> conditions, String collectionName, String dbName) {
        List<DBObject> queryResult = new ArrayList<>();
        BasicDBObject searchQuery = new BasicDBObject();
        Datastore datastore = this.getDatastore(dbName);
        List<T> result = new ArrayList<>();

        DBCollection collection = this.getCollection(collectionName);

        searchQuery.putAll(conditions);

        DBCursor cursor = collection.find(searchQuery);

        cursor.getQuery();

        while (cursor.hasNext()) {
            queryResult.add(cursor.next());
        }

        morphia.map(clazz);
        queryResult.forEach(qr -> result.add(morphia.fromDBObject(datastore, clazz, qr)));

        return result;
    }

    public <T> List<T> getAllElements(Class<T> clazz, String collectionName, String dbName) {
        List<DBObject> queryResult = new ArrayList<>();
        Datastore datastore = this.getDatastore(dbName);
        List<T> result = new ArrayList<>();

        DBCollection collection = this.getCollection(collectionName);

        DBCursor cursor = collection.find();

        cursor.getQuery();

        while (cursor.hasNext()) {
            queryResult.add(cursor.next());
        }

        morphia.map(clazz);
        queryResult.forEach(qr -> result.add(morphia.fromDBObject(datastore, clazz, qr)));

        return result;
    }

    public void createDocument(String collectionName, Map<String, Object> documentElements) {
        BasicDBObject document = new BasicDBObject();
        DBCollection collection = this.getCollection(collectionName);

        document.putAll(documentElements);
        collection.insert(document);
    }

    public Long deleteEventList(Long listId) {
        Map<String, Object> conditions = new HashMap<>();
        BasicDBObject deleteQuery = new BasicDBObject();
        DBCollection collection = this.getCollection("eventLists");

        conditions.put("listId", listId);
        List<EventListDTO> eventlists = getElementsAs(EventListDTO.class, conditions, "eventLists", "eventacs");

        deleteQuery.put("listId", listId);

        if(eventlists.size() != 0) {
            collection.remove(deleteQuery);
            return eventlists.get(0).getListId();
        } else {
            throw new EventListNotFound("User not found for this event list Id: " + listId);
        }
    }

    public Long update(String idName, Long id, Map<String, Object> documentElements, String collectionName) {
        BasicDBObject query = new BasicDBObject();
        DBCollection collection = this.getCollection(collectionName);
        BasicDBObject newDocument = new BasicDBObject();
        BasicDBObject updateObject = new BasicDBObject();

        query.put(idName, id);
        newDocument.putAll(documentElements);
        updateObject.put("$set", newDocument);

        collection.update(query, updateObject);

        return id;
    }

    public Long addEvents(String idName, Long id, BasicDBList documentElements, String collectionName) {
        BasicDBObject query = new BasicDBObject();
        DBCollection collection = this.getCollection(collectionName);
        BasicDBObject updateObject = new BasicDBObject();

        query.put(idName, id);

        updateObject.append("$set", new BasicDBObject("events", documentElements));

        collection.update(query, updateObject);

        return id;
    }

    public Long listIdGenerator() {

        if(getCollection("eventLists").count() == 0){
            return 1L;
        } else {
            List<DBObject> queryResult = new ArrayList<>();
            BasicDBObject sorting = new BasicDBObject();
            Datastore datastore = this.getDatastore("eventacs");
            DBCollection collection = this.getCollection("eventLists");

            sorting.put("listId", -1);

            DBCursor cursor = collection.find().sort(sorting);

            cursor.getQuery();

            while (cursor.hasNext()) {
                queryResult.add(cursor.next());
            }

            morphia.map(EventListDTO.class);

            EventListDTO eventListDTO = morphia.fromDBObject(datastore, EventListDTO.class, queryResult.get(0));

            return eventListDTO.getListId() + 1L;
        }
    }

    public Long alarmIdGenerator() {
        if(getCollection("alarms").count() == 0){
            return 1L;
        } else {
            List<DBObject> queryResult = new ArrayList<>();
            BasicDBObject sorting = new BasicDBObject();
            Datastore datastore = this.getDatastore("eventacs");
            DBCollection collection = this.getCollection("alarms");

            sorting.put("alarmId", -1);

            DBCursor cursor = collection.find().sort(sorting);

            DBObject alarm = cursor.next();

            queryResult.add(alarm);

            morphia.map(AlarmDAO.class);

            return morphia.fromDBObject(datastore, AlarmDAO.class, queryResult.get(0)).getAlarmId() + 1L;
        }
    }

    public void dropDatabase(){
        this.database.dropDatabase();
    }

    public void deleteAlarm(Long alarmId) {
        Map<String, Object> conditions = new HashMap<>();
        BasicDBObject deleteQuery = new BasicDBObject();
        DBCollection collection = this.getCollection("alarms");

        conditions.put("alarmId", alarmId);
        List<AlarmDAO> alarms = getElementsAs(AlarmDAO.class, conditions, "alarms", "eventacs");

        deleteQuery.put("alarmId", alarmId);

        if(alarms.size() != 0) {
            collection.remove(deleteQuery);
            // return alarms.get(0).getAlarmId();
        } else {
            throw new AlarmNotFound("Alarm not found for this alarm Id : " + alarmId);
        }
    }
}