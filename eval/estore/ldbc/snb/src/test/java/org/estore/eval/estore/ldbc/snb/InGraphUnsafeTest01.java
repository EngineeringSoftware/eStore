package org.estore.eval.estore.ldbc.snb;

import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.estore.eval.estore.ldbc.snb.util.*;
import org.estore.Estore;
import org.estore.EstoreOptions;
import org.estore.planner.util.Table;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InGraphUnsafeTest01 {

  private Estore estore;

  @BeforeEach
  public void setupData() throws Exception {
    estore = new Estore("myDb", new EstoreOptions().useUnsafe(true).profile(true));
    readDataSet();
  }

  @Test
  public void testInteractiveDeleteQuery2() {
    Table result =
        estore.query(
            "MATCH (m:`org.estore.eval.estore.ldbc.snb.util.Person`"
                + " {id:10995116278291})-[likes:LIKES2]->(:`[Lorg.estore.eval.estore.ldbc.snb.util.Post;`"
                + " {id:343597383821}) DELETE likes RETURN COUNT(m)");
    assertEquals(result.get("COUNT(m)").get(0), 1);
  }

  @Test
  public void testInteractiveDeleteQuery3() {
    Table result =
        estore.query(
            "MATCH (m:`org.estore.eval.estore.ldbc.snb.util.Person`"
                + " {id:19791209300608})-[likes:LIKES1]->(:`[Lorg.estore.eval.estore.ldbc.snb.util.Comment;`"
                + " {id:549755814421}) DELETE likes RETURN COUNT(m)");
    assertEquals(result.get("COUNT(m)").get(0), 1);
  }

  @Test
  public void testInteractiveDeleteQuery5() {
    Table result =
        estore.query(
            "MATCH (m:`org.estore.eval.estore.ldbc.snb.util.Forum`"
                + " {id:481036337162})-[hasMember:HAS_MEMBER]->(:`[Lorg.estore.eval.estore.ldbc.snb.util.Person;`"
                + " {id:2199023256077}) DELETE hasMember RETURN COUNT(m)");
    assertEquals(result.get("COUNT(m)").get(0), 1);
  }

  @Test
  public void testInteractiveShortQuery1() {
    Table result =
        estore.query(
            "MATCH (n:`org.estore.eval.estore.ldbc.snb.util.Person`"
                + " {id:32985348833679})-[:IS_LOCATED_IN]->(p:`[Lorg.estore.eval.estore.ldbc.snb.util.Place;`)"
                + " RETURN n.firstName AS firstName, n.lastName AS lastName, n.birthday AS"
                + " birthday, n.locationIP AS locationIP, n.browserUsed AS browserUsed, p.id AS"
                + " cityId, n.gender AS gender, n.creationDate AS creationDate");
    assertEquals(result.get("birthday").get(0), 579484800000L);
    assertEquals(result.get("firstName").get(0), "Min-Jung");
    assertEquals(result.get("lastName").get(0), "Park");
    assertEquals(result.get("gender").get(0), "female");
    assertEquals(result.get("browserUsed").get(0), "Internet Explorer");
    assertEquals(result.get("locationIP").get(0), "42.18.171.166");
    assertEquals(result.get("cityId").get(0), 1342L);
    assertEquals(result.get("creationDate").get(0), 1345825908405L);
  }

  @Test
  public void testInteractiveShortQuery5() {
    Table result =
        estore.query(
            "MATCH (m:`org.estore.eval.estore.ldbc.snb.util.Comment`"
                + " {id:206158430603})-[:HAS_CREATOR]->(p:`[Lorg.estore.eval.estore.ldbc.snb.util.Person;`)"
                + " RETURN p.id AS personId, p.firstName AS firstName, p.lastName AS lastName");
    assertEquals(result.get("firstName").get(0), "Rudolf");
    assertEquals(result.get("lastName").get(0), "Engel");
    assertEquals(result.get("personId").get(0), 2199023256437L);
  }

  @Test
  public void testInteractiveUpdateQuery2() {
    Table result =
        estore.query(
            "MATCH (person:`org.estore.eval.estore.ldbc.snb.util.Person` {id:10995116278291}),"
                + " (post:`org.estore.eval.estore.ldbc.snb.util.Post` {id:481036337280})  CREATE"
                + " (person)-[r:LIKES2]->(post) RETURN COUNT(r)");
    assertEquals(result.get("COUNT(r)").get(0), 1);
  }

  @Test
  public void testInteractiveUpdateQuery3() {
    Table result =
        estore.query(
            "MATCH (person:`org.estore.eval.estore.ldbc.snb.util.Person` {id:19791209301454}),"
                + " (comment:`org.estore.eval.estore.ldbc.snb.util.Comment` {id:481036337631})  CREATE"
                + " (person)-[r:LIKES1]->(comment) RETURN COUNT(r)");
    assertEquals(result.get("COUNT(r)").get(0), 1);
  }

  @Test
  public void testInteractiveUpdateQuery5() {
    Table result =
        estore.query(
            "MATCH (f:`org.estore.eval.estore.ldbc.snb.util.Forum` {id:549755813984}),"
                + " (p:`org.estore.eval.estore.ldbc.snb.util.Person` {id:19791209300852})  CREATE"
                + " (f)-[r:HAS_MEMBER]->(p) RETURN COUNT(r)");
    assertEquals(result.get("COUNT(r)").get(0), 1);
  }

  @Test
  public void testInteractiveUpdateQuery8() {
    Table result =
        estore.query(
            "MATCH (p1:`org.estore.eval.estore.ldbc.snb.util.Person` {id:4398046512167}),"
                + " (p2:`org.estore.eval.estore.ldbc.snb.util.Person` {id:2199023256816})  CREATE"
                + " (p1)-[r:KNOWS]->(p2) RETURN COUNT(r)");
    assertEquals(result.get("COUNT(r)").get(0), 1);
  }

  public void readDataSet() throws Exception {
    HashMap<Long, Person> persons = new HashMap<Long, Person>();
    HashMap<Long, Place> places = new HashMap<Long, Place>();
    HashMap<Long, Tag> tags = new HashMap<Long, Tag>();
    HashMap<Long, TagClass> tagclasses = new HashMap<Long, TagClass>();
    HashMap<Long, Comment> comments = new HashMap<Long, Comment>();
    HashMap<Long, Forum> forums = new HashMap<Long, Forum>();
    HashMap<Long, Post> posts = new HashMap<Long, Post>();
    HashMap<Long, Organisation> organisations = new HashMap<Long, Organisation>();

    String datasetPath = "/social_network-csv_composite-longdateformatter-sf0.1";

    // Nodes
    insertPlaces(datasetPath + "/" + "static/place_0_0.csv", places);
    insertTagClasses(datasetPath + "/" + "static/tagclass_0_0.csv", tagclasses);
    insertTags(datasetPath + "/" + "static/tag_0_0.csv", tags);
    insertForums(datasetPath + "/" + "dynamic/forum_0_0.csv", forums);
    insertPersons(datasetPath + "/" + "dynamic/person_0_0.csv", persons);
    insertComments(datasetPath + "/" + "dynamic/comment_0_0.csv", comments);
    insertPosts(datasetPath + "/" + "dynamic/post_0_0.csv", posts);
    insertOrganisations(datasetPath + "/" + "static/organisation_0_0.csv", organisations);

    // Relations
    new CreateRelation<Place, Place>()
        .insertRelations(
            datasetPath + "/" + "static/place_isPartOf_place_0_0.csv",
            Place.class,
            Place.class,
            places,
            places,
            "setIsPartOf");
    new CreateRelation<Person, Place>()
        .insertRelations(
            datasetPath + "/" + "dynamic/person_isLocatedIn_place_0_0.csv",
            Person.class,
            Place.class,
            persons,
            places,
            "setIsLocatedIn");
    new CreateRelation<Tag, TagClass>()
        .insertRelations(
            datasetPath + "/" + "static/tag_hasType_tagclass_0_0.csv",
            Tag.class,
            TagClass.class,
            tags,
            tagclasses,
            "setHasType");
    new CreateRelation<Comment, Person>()
        .insertRelations(
            datasetPath + "/" + "dynamic/comment_hasCreator_person_0_0.csv",
            Comment.class,
            Person.class,
            comments,
            persons,
            "setHasCreator");
    new CreateRelation<Comment, Place>()
        .insertRelations(
            datasetPath + "/" + "dynamic/comment_isLocatedIn_place_0_0.csv",
            Comment.class,
            Place.class,
            comments,
            places,
            "setIsLocatedIn");
    new CreateRelation<Comment, Comment>()
        .insertRelations(
            datasetPath + "/" + "dynamic/comment_replyOf_comment_0_0.csv",
            Comment.class,
            Comment.class,
            comments,
            comments,
            "setIsReplyOf1");
    new CreateRelation<Comment, Post>()
        .insertRelations(
            datasetPath + "/" + "dynamic/comment_replyOf_post_0_0.csv",
            Comment.class,
            Post.class,
            comments,
            posts,
            "setIsReplyOf2");
    new CreateRelation<Forum, Post>()
        .insertRelations(
            datasetPath + "/" + "dynamic/forum_containerOf_post_0_0.csv",
            Forum.class,
            Post.class,
            forums,
            posts,
            "setContainerOf");
    new CreateRelation<Forum, Person>()
        .insertRelations(
            datasetPath + "/" + "dynamic/forum_hasMember_person_0_0.csv",
            Forum.class,
            Person.class,
            forums,
            persons,
            "setHasMember");
    new CreateRelation<Forum, Person>()
        .insertRelations(
            datasetPath + "/" + "dynamic/forum_hasModerator_person_0_0.csv",
            Forum.class,
            Person.class,
            forums,
            persons,
            "setHasModerator");
    new CreateRelation<Forum, Tag>()
        .insertRelations(
            datasetPath + "/" + "dynamic/forum_hasTag_tag_0_0.csv",
            Forum.class,
            Tag.class,
            forums,
            tags,
            "setHasTag");
    new CreateRelation<Person, Tag>()
        .insertRelations(
            datasetPath + "/" + "dynamic/person_hasInterest_tag_0_0.csv",
            Person.class,
            Tag.class,
            persons,
            tags,
            "setHasInterest");
    new CreateRelation<Person, Person>()
        .insertRelations(
            datasetPath + "/" + "dynamic/person_knows_person_0_0.csv",
            Person.class,
            Person.class,
            persons,
            persons,
            "setKnows");
    new CreateRelation<Person, Comment>()
        .insertRelations(
            datasetPath + "/" + "dynamic/person_likes_comment_0_0.csv",
            Person.class,
            Comment.class,
            persons,
            comments,
            "setLikes1");
    new CreateRelation<Person, Post>()
        .insertRelations(
            datasetPath + "/" + "dynamic/person_likes_post_0_0.csv",
            Person.class,
            Post.class,
            persons,
            posts,
            "setLikes2");
    new CreateRelation<Post, Person>()
        .insertRelations(
            datasetPath + "/" + "dynamic/post_hasCreator_person_0_0.csv",
            Post.class,
            Person.class,
            posts,
            persons,
            "setHasCreator");
    new CreateRelation<Comment, Tag>()
        .insertRelations(
            datasetPath + "/" + "dynamic/comment_hasTag_tag_0_0.csv",
            Comment.class,
            Tag.class,
            comments,
            tags,
            "setHasTag");
    new CreateRelation<Post, Tag>()
        .insertRelations(
            datasetPath + "/" + "dynamic/post_hasTag_tag_0_0.csv",
            Post.class,
            Tag.class,
            posts,
            tags,
            "setHasTag");
    new CreateRelation<Post, Place>()
        .insertRelations(
            datasetPath + "/" + "dynamic/post_isLocatedIn_place_0_0.csv",
            Post.class,
            Place.class,
            posts,
            places,
            "setIsLocatedIn");
    new CreateRelation<Person, Organisation>()
        .insertRelations(
            datasetPath + "/" + "dynamic/person_studyAt_organisation_0_0.csv",
            Person.class,
            Organisation.class,
            persons,
            organisations,
            "setStudyAt");
    new CreateRelation<Person, Organisation>()
        .insertRelations(
            datasetPath + "/" + "dynamic/person_workAt_organisation_0_0.csv",
            Person.class,
            Organisation.class,
            persons,
            organisations,
            "setWorkAt");
    new CreateRelation<Organisation, Place>()
        .insertRelations(
            datasetPath + "/" + "static/organisation_isLocatedIn_place_0_0.csv",
            Organisation.class,
            Place.class,
            organisations,
            places,
            "setIsLocatedIn");
    new CreateRelation<TagClass, TagClass>()
        .insertRelations(
            datasetPath + "/" + "static/tagclass_isSubclassOf_tagclass_0_0.csv",
            TagClass.class,
            TagClass.class,
            tagclasses,
            tagclasses,
            "setIsSubclassOf");

    for (Person person : persons.values()) {
      estore.insert(person);
    }
    for (Place place : places.values()) {
      estore.insert(place);
    }
    for (TagClass tagclass : tagclasses.values()) {
      estore.insert(tagclass);
    }
    for (Tag tag : tags.values()) {
      estore.insert(tag);
    }
    for (Post post : posts.values()) {
      estore.insert(post);
    }
    for (Comment comment : comments.values()) {
      estore.insert(comment);
    }
    for (Forum forum : forums.values()) {
      estore.insert(forum);
    }
    for (Organisation organisation : organisations.values()) {
      estore.insert(organisation);
    }
  }

  private void insertPlaces(String filePath, HashMap<Long, Place> places) {
    try {
      Reader reader = new FileReader(filePath);
      CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
      CSVParser csvParser = new CSVParser(reader, csvFormat);

      for (CSVRecord csvRecord : csvParser) {
        long id = Long.parseLong(csvRecord.get("id"));
        String name = csvRecord.get("name");
        String url = csvRecord.get("url");
        String type = csvRecord.get("type");
        places.put(id, (new Place(id, name, url, type)));
      }
      csvParser.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void insertTagClasses(String filePath, HashMap<Long, TagClass> tagclasses) {
    try {
      Reader reader = new FileReader(filePath);
      CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
      CSVParser csvParser = new CSVParser(reader, csvFormat);

      for (CSVRecord csvRecord : csvParser) {
        long id = Long.parseLong(csvRecord.get("id"));
        String name = csvRecord.get("name");
        String url = csvRecord.get("url");

        tagclasses.put(id, new TagClass(id, name, url));
      }
      csvParser.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void insertTags(String filePath, HashMap<Long, Tag> tags) {
    try {
      Reader reader = new FileReader(filePath);
      CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
      CSVParser csvParser = new CSVParser(reader, csvFormat);

      for (CSVRecord csvRecord : csvParser) {
        long id = Long.parseLong(csvRecord.get("id"));
        String name = csvRecord.get("name");
        String url = csvRecord.get("url");

        tags.put(id, new Tag(id, name, url));
      }
      csvParser.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void insertOrganisations(String filePath, HashMap<Long, Organisation> organisations) {
    try {
      Reader reader = new FileReader(filePath);
      CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
      CSVParser csvParser = new CSVParser(reader, csvFormat);

      for (CSVRecord csvRecord : csvParser) {
        long id = Long.parseLong(csvRecord.get("id"));
        String type = csvRecord.get("type");
        String name = csvRecord.get("name");
        String url = csvRecord.get("url");

        organisations.put(id, new Organisation(id, type, name, url));
      }
      csvParser.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void insertForums(String filePath, HashMap<Long, Forum> forums) {
    try {
      Reader reader = new FileReader(filePath);
      CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
      CSVParser csvParser = new CSVParser(reader, csvFormat);

      for (CSVRecord csvRecord : csvParser) {
        long id = Long.parseLong(csvRecord.get("id"));
        String title = csvRecord.get("title");
        long creationDate = Long.parseLong(csvRecord.get("creationDate"));

        forums.put(id, new Forum(id, title, creationDate));
      }
      csvParser.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void insertPersons(String filePath, HashMap<Long, Person> persons) {
    try {
      Reader reader = new FileReader(filePath);
      CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
      CSVParser csvParser = new CSVParser(reader, csvFormat);

      for (CSVRecord csvRecord : csvParser) {
        long id = Long.parseLong(csvRecord.get("id"));
        String firstName = csvRecord.get("firstName");
        String lastName = csvRecord.get("lastName");
        String gender = csvRecord.get("gender");
        long birthday = Long.parseLong(csvRecord.get("birthday"));
        long creationDate = Long.parseLong(csvRecord.get("creationDate"));
        String locationIP = csvRecord.get("locationIP");
        String browserUsed = csvRecord.get("browserUsed");
        String language = csvRecord.get("language");
        String email = csvRecord.get("email");

        persons.put(
            id,
            new Person(
                id,
                firstName,
                lastName,
                gender,
                birthday,
                creationDate,
                locationIP,
                browserUsed,
                language,
                email));
      }
      csvParser.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void insertComments(String filePath, HashMap<Long, Comment> comments) {
    try {
      Reader reader = new FileReader(filePath);
      CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
      CSVParser csvParser = new CSVParser(reader, csvFormat);

      for (CSVRecord csvRecord : csvParser) {
        long id = Long.parseLong(csvRecord.get("id"));
        long creationDate = Long.parseLong(csvRecord.get("creationDate"));
        String locationIP = csvRecord.get("locationIP");
        String browserUsed = csvRecord.get("browserUsed");
        String content = csvRecord.get("content");
        int length = Integer.parseInt(csvRecord.get("length"));

        comments.put(id, new Comment(id, creationDate, locationIP, browserUsed, content, length));
      }
      csvParser.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void insertPosts(String filePath, HashMap<Long, Post> posts) {
    try {
      Reader reader = new FileReader(filePath);
      CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
      CSVParser csvParser = new CSVParser(reader, csvFormat);

      for (CSVRecord csvRecord : csvParser) {
        long id = Long.parseLong(csvRecord.get("id"));
        long creationDate = Long.parseLong(csvRecord.get("creationDate"));
        String locationIP = csvRecord.get("locationIP");
        String browserUsed = csvRecord.get("browserUsed");
        String language = csvRecord.get("language");
        String content = csvRecord.get("content");
        int length = Integer.parseInt(csvRecord.get("length"));

        posts.put(
            id, new Post(id, creationDate, locationIP, browserUsed, language, content, length));
      }
      csvParser.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static class CreateRelation<K, V> {
    public void insertRelations(
        String filePath,
        Class referrerClass,
        Class refereeClass,
        HashMap<Long, K> referrerMap,
        HashMap<Long, V> refereeMap,
        String setRelationMethodName) {
      try {
        HashMap<Long, HashSet<V>> relationMap = new HashMap<Long, HashSet<V>>();
        Reader reader = new FileReader(filePath);
        CSVFormat csvFormat =
            CSVFormat.Builder.create()
                .setDelimiter('|')
                .setSkipHeaderRecord(true)
                .setHeader("Referrer", "Referee")
                .build();
        CSVParser csvParser = new CSVParser(reader, csvFormat);
        Method setRelationMethod = referrerClass.getMethod(setRelationMethodName, Object[].class);

        for (CSVRecord csvRecord : csvParser) {
          long referrerId = Long.parseLong(csvRecord.get("Referrer"));
          long refereeId = Long.parseLong(csvRecord.get("Referee"));
          if (relationMap.get(referrerId) == null) {
            relationMap.put(referrerId, new HashSet<V>());
          }
          relationMap.get(referrerId).add(refereeMap.get(refereeId));
        }
        csvParser.close();
        for (Map.Entry<Long, HashSet<V>> item : relationMap.entrySet()) {
          setRelationMethod.invoke(
              referrerMap.get(item.getKey()),
              new Object[] {item.getValue().toArray(Object[]::new)});
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
