package com.gun3y.nlp.mongo;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.gun3y.nlp.model.CountModel;
import com.gun3y.nlp.model.Model;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

public class MongoManager {

    private static final MongoManager instance = new MongoManager();

    public static MongoManager getInstance() {
	return instance;
    }

    Morphia morphia = new Morphia();
    Datastore ds = null;

    public MongoManager() {
	try {
	    MongoClient client = new MongoClient(new ServerAddress("localhost", 27017));
	    ds = morphia.createDatastore(client, "nlp");
	}
	catch (UnknownHostException e) {
	    e.printStackTrace();
	}
	morphia.map(Model.class);
    }

    public void removeAll() {
	ds.delete(ds.createQuery(Model.class));
    }

    public void removeAllCounts() {
	ds.delete(ds.createQuery(CountModel.class));
    }

    public Model getModelById(String id) {
	return ds.find(Model.class).field("id").equal(id).get();
    }

    public CountModel getCountModelById(String id) {
	return ds.find(CountModel.class).field("id").equal(id).get();
    }

    public List<Model> findModelById(String query) {
	if (StringUtils.isBlank(query)) {
	    return Collections.emptyList();
	}
	return ds.find(Model.class).field("id").contains(query).asList();
    }

    public List<CountModel> findCountModelById(String query) {
	if (StringUtils.isBlank(query)) {
	    return Collections.emptyList();
	}
	return ds.find(CountModel.class).field("id").contains(query).asList();
    }

    public List<CountModel> findCountModelByType(int ngram) {
	return ds.find(CountModel.class).field("ngram").equal(ngram + "").asList();
    }

    public void insertModel(Model model) {
	ds.save(model);
    }

    public void insertModel(CountModel model) {
	ds.save(model);
    }

    public void updateModel(CountModel model) {
	ds.update(model, ds.createUpdateOperations(CountModel.class).inc("count"));
    }

    public void insertModel(Set<Entry<String, Float>> modelSet) {
	List<Model> models = new ArrayList<Model>();
	if (modelSet != null) {
	    for (Entry<String, Float> modelEntry : modelSet) {
		models.add(new Model(modelEntry.getKey(), modelEntry.getValue()));
	    }
	}

	ds.save(models.toArray(new Model[models.size()]));
    }
}
