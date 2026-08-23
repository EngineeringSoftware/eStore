/*
 * Copyright (c) "Neo4j"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.kernel.impl.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import org.neo4j.graphdb.ConstraintViolationException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Result;

import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.extension.ImpermanentDbmsExtension;
import org.neo4j.test.extension.Inject;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@ImpermanentDbmsExtension
class Neo4jImpermanantTest01
{
    @Inject
    private GraphDatabaseService db;
    
  @Test
  public void testInteractiveDeleteQuery2() {
            try(Transaction tx = db.beginTx(); ){
   
            long t1 = System.nanoTime();
            tx.execute(
            "MATCH (m:`Person`"
                + " {id:10995116278291})-[likes:LIKES]->(:`Post`"
                + " {id:343597383821}) DELETE likes RETURN COUNT(m)");
            System.out.println("Total Query Time : " + (System.nanoTime() - t1));
            }catch(Exception e){
            }
  }

  @Test
  public void testInteractiveDeleteQuery3() {
      try(Transaction tx = db.beginTx(); ){
   
            long t1 = System.nanoTime();
            tx.execute(
            "MATCH (m:`Person`"
                + " {id:19791209300608})-[likes:LIKES]->(:`Comment`"
                + " {id:549755814421}) DELETE likes RETURN COUNT(m)");
            System.out.println("Total Query Time : " + (System.nanoTime() - t1));
            }catch(Exception e){
            }
  }

  @Test
  public void testInteractiveDeleteQuery5() {
      try(Transaction tx = db.beginTx(); ){
   
            long t1 = System.nanoTime();
            tx.execute(
            "MATCH (m:`Forum`"
                + " {id:481036337162})-[hasMember:HAS_MEMBER]->(:`Person`"
                + " {id:2199023256077}) DELETE hasMember RETURN COUNT(m)");
            System.out.println("Total Query Time : " + (System.nanoTime() - t1));
            }catch(Exception e){
            }
  }

  @Test
  public void testInteractiveShortQuery1() {
     try(Transaction tx = db.beginTx(); ){
   
            long t1 = System.nanoTime();
            tx.execute("MATCH (n:`Person`"
                + " {id:32985348833679})-[:IS_LOCATED_IN]->(p:`Place`)"
                + " RETURN n.firstName AS firstName, n.lastName AS lastName, n.birthday AS"
                + " birthday, n.locationIP AS locationIP, n.browserUsed AS browserUsed, p.id AS"
                + " cityId, n.gender AS gender, n.creationDate AS creationDate");
            System.out.println("Total Query Time : " + (System.nanoTime() - t1));
    
            }catch(Exception e){
            }
  }

  @Test
  public void testInteractiveShortQuery5() {
      try(Transaction tx = db.beginTx(); ){
   
            long t1 = System.nanoTime();
            tx.execute(
            "MATCH (m:`Comment`"
                + " {id:206158430603})-[:HAS_CREATOR]->(p:`Person`)"
                + " RETURN p.id AS personId, p.firstName AS firstName, p.lastName AS lastName");
            System.out.println("Total Query Time : " + (System.nanoTime() - t1));
            }catch(Exception e){
            }
  }

  @Test
  public void testInteractiveUpdateQuery2() {
      try(Transaction tx = db.beginTx(); ){
   
            long t1 = System.nanoTime();
            tx.execute(
            "MATCH (person:`Person` {id:10995116278291}),"
                + " (post:`Post` {id:481036337280})  CREATE"
                + " (person)-[:LIKES]->(post)");
            System.out.println("Total Query Time : " + (System.nanoTime() - t1));
            }catch(Exception e){
            }
  }

  @Test
  public void testInteractiveUpdateQuery3() {
      try(Transaction tx = db.beginTx(); ){
   
            long t1 = System.nanoTime();
            tx.execute(
            "MATCH (person:`Person` {id:19791209301454}),"
                + " (comment:`Comment` {id:481036337631})  CREATE"
                + " (person)-[:LIKES]->(comment)");
            System.out.println("Total Query Time : " + (System.nanoTime() - t1));
            }catch(Exception e){
            }
  }

  @Test
  public void testInteractiveUpdateQuery5() {
      try(Transaction tx = db.beginTx(); ){
   
            long t1 = System.nanoTime();
            tx.execute(
            "MATCH (f:`Forum` {id:549755813984}),"
                + " (p:`Person` {id:19791209300852})  CREATE"
                + " (f)-[:HAS_MEMBER]->(p)");
            System.out.println("Total Query Time : " + (System.nanoTime() - t1));
            }catch(Exception e){
            }
  }

  @Test
  public void testInteractiveUpdateQuery8() {
      try(Transaction tx = db.beginTx(); ){
   
            long t1 = System.nanoTime();
            tx.execute(
            "MATCH (p1:`Person` {id:4398046512167}),"
                + " (p2:`Person` {id:2199023256816})  CREATE"
                + " (p1)-[:KNOWS]->(p2)");
            System.out.println("Total Query Time : " + (System.nanoTime() - t1));
            }catch(Exception e){
            }
  }
    @BeforeEach
    public void setupData() {
        String datasetPath = "/social_network-csv_composite-longdateformatter-sf0.1";
        HashMap<Long, Long> places = new HashMap<Long, Long>();
        HashMap<Long, Long> tagclasses = new HashMap<Long, Long>();
        HashMap<Long, Long> tags = new HashMap<Long, Long>();
        HashMap<Long, Long> forums = new HashMap<Long, Long>();
        HashMap<Long, Long> persons = new HashMap<Long, Long>();
        HashMap<Long, Long> comments = new HashMap<Long, Long>();
        HashMap<Long, Long> posts = new HashMap<Long, Long>();
        HashMap<Long, Long> organisations = new HashMap<Long, Long>();

        insertPlaces(datasetPath + "/" + "static/place_0_0.csv", places);
        insertTagClasses(datasetPath + "/" + "static/tagclass_0_0.csv", tagclasses);
        insertTags(datasetPath + "/" + "static/tag_0_0.csv", tags);
        insertForums(datasetPath + "/" + "dynamic/forum_0_0.csv", forums);
        insertPersons(datasetPath + "/" + "dynamic/person_0_0.csv", persons);
        insertComments(datasetPath + "/" + "dynamic/comment_0_0.csv", comments);
        insertPosts(datasetPath + "/" + "dynamic/post_0_0.csv", posts);
        insertOrganisations(datasetPath + "/" + "static/organisation_0_0.csv", organisations);
        
        insertRelations(
                datasetPath + "/" + "static/place_isPartOf_place_0_0.csv",
                "Place",
                "Place",
                places,
                places,
                "IS_PART_OF");

        insertRelations(
                datasetPath + "/" + "dynamic/person_isLocatedIn_place_0_0.csv",
                "Person",
                "Place",
                persons,
                places,
                "IS_LOCATED_IN");
        insertRelations(
                datasetPath + "/" + "static/tag_hasType_tagclass_0_0.csv",
                "Tag",
                "TagClass",
                tags,
                tagclasses,
                "HAS_TYPE");
        insertRelations(
                datasetPath + "/" + "dynamic/comment_hasCreator_person_0_0.csv",
                "Comment",
                "Person",
                comments,
                persons,
                "HAS_CREATOR");
        insertRelations(
                datasetPath + "/" + "dynamic/comment_isLocatedIn_place_0_0.csv",
                "Comment",
                "Place",
                comments,
                places,
                "IS_LOCATED_IN");
        insertRelations(
                datasetPath + "/" + "dynamic/comment_replyOf_comment_0_0.csv",
                "Comment",
                "Comment",
                comments,
                comments,
                "REPLY_OF");
        insertRelations(
                datasetPath + "/" + "dynamic/comment_replyOf_post_0_0.csv",
                "Comment",
                "Post",
                comments,
                posts,
                "REPLY_OF");
        insertRelations(
                datasetPath + "/" + "dynamic/forum_containerOf_post_0_0.csv",
                "Forum",
                "Post",
                forums,
                posts,
                "CONTAINER_OF");
        insertRelations(
                datasetPath + "/" + "dynamic/forum_hasMember_person_0_0.csv",
                "Forum",
                "Person",
                forums,
                persons,
                "HAS_MEMBER");
        insertRelations(
                datasetPath + "/" + "dynamic/forum_hasModerator_person_0_0.csv",
                "Forum",
                "Person",
                forums,
                persons,
                "HAS_MODERATOR");
        insertRelations(
                datasetPath + "/" + "dynamic/forum_hasTag_tag_0_0.csv",
                "Forum",                                                                                                                                   
                "Tag",
                forums,
                tags,
                "HAS_TAG");
        insertRelations(
                datasetPath + "/" + "dynamic/person_hasInterest_tag_0_0.csv",
                "Person",
                "Tag",
                persons,
                tags,
                "HAS_INTEREST");
        insertRelations(
                datasetPath + "/" + "dynamic/person_knows_person_0_0.csv",
                "Person",
                "Person",
                persons,
                persons,
                "KNOWS");
        insertRelations(
                datasetPath + "/" + "dynamic/person_likes_comment_0_0.csv",
                "Person",
                "Comment",
                persons,
                comments,
                "LIKES");
        insertRelations(
                datasetPath + "/" + "dynamic/person_likes_post_0_0.csv",
                "Person",
                "Post",
                persons,
                posts,
                "LIKES");
        insertRelations(
                datasetPath + "/" + "dynamic/post_hasCreator_person_0_0.csv",
                "Post",
                "Person",
                posts,
                persons,
                "HAS_CREATOR");
        insertRelations(
                datasetPath + "/" + "dynamic/comment_hasTag_tag_0_0.csv",                                                                                   
                "Comment",
                "Tag",
                comments,
                tags,
                "HAS_TAG");
        insertRelations(
                datasetPath + "/" + "dynamic/post_hasTag_tag_0_0.csv",
                "Post",
                "Tag",
                posts,
                tags,
                "HAS_TAG");
        insertRelations(
                datasetPath + "/" + "dynamic/post_isLocatedIn_place_0_0.csv",
                "Post",
                "Place",
                posts,
                places,
                "IS_LOCATED_IN");
        insertRelations(
                datasetPath + "/" + "dynamic/person_studyAt_organisation_0_0.csv",
                "Person",
                "Organisation",
                persons,
                organisations,
                "STUDY_AT");
        insertRelations(
                datasetPath + "/" + "dynamic/person_workAt_organisation_0_0.csv",
                "Person",
                "Organisation",
                persons,
                organisations,
                "WORK_AT");
        insertRelations(
                datasetPath + "/" + "static/organisation_isLocatedIn_place_0_0.csv",
                "Organisation",
                "Place",
                organisations,
                places,
                "IS_LOCATED_IN");
        insertRelations(
                datasetPath + "/" + "static/tagclass_isSubclassOf_tagclass_0_0.csv",
                "TagClass",
                "TagClass",
                tagclasses,
                tagclasses,
                "IS_SUBCLASS_OF");
        
    }

    private void insertPlaces(String filePath, HashMap<Long, Long> temp) {
        try {
            Reader reader = new FileReader(filePath);
            CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
            CSVParser csvParser = new CSVParser(reader, csvFormat);

            Label label = Label.label("Place");
            try(Transaction tx = db.beginTx(); ){
                for (CSVRecord csvRecord : csvParser) {
                    long id = Long.parseLong(csvRecord.get("id:ID(Place)"));
                    String name = csvRecord.get("name");
                    String url = csvRecord.get("url");
                    String type = csvRecord.get("type");

                    Node node = tx.createNode(label);
                    node.setProperty("id", id);
                    node.setProperty("name", name);
                    node.setProperty("url", url);
                    node.setProperty("type", type);
                    temp.put(id, node.getId());
                }       
                tx.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            csvParser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  private void insertTagClasses(String filePath, HashMap<Long, Long> temp) {
    try {
      Reader reader = new FileReader(filePath);
      CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
      CSVParser csvParser = new CSVParser(reader, csvFormat);

      Label label = Label.label("Tagclass");
      try(Transaction tx = db.beginTx(); ){
          for (CSVRecord csvRecord : csvParser) {
              long id = Long.parseLong(csvRecord.get("id:ID(Tagclass)"));
              String name = csvRecord.get("name");
              String url = csvRecord.get("url");
              
              Node node = tx.createNode(label);
              node.setProperty("id", id);
              node.setProperty("name", name);
              node.setProperty("url", url);
              temp.put(id, node.getId());
          }       
          tx.commit();
      } catch (Exception e) {
          e.printStackTrace();
      }
      csvParser.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void insertTags(String filePath, HashMap<Long, Long> temp) {
    try {
      Reader reader = new FileReader(filePath);
      CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
      CSVParser csvParser = new CSVParser(reader, csvFormat);

      Label label = Label.label("Tag");
      try(Transaction tx = db.beginTx(); ){
          for (CSVRecord csvRecord : csvParser) {
              long id = Long.parseLong(csvRecord.get("id:ID(Tag)"));
              String name = csvRecord.get("name");
              String url = csvRecord.get("url");
              
              Node node = tx.createNode(label);
              node.setProperty("id", id);
              node.setProperty("name", name);
              node.setProperty("url", url);
              temp.put(id, node.getId());
          }       
          tx.commit();
      } catch (Exception e) {
          e.printStackTrace();
      }
      csvParser.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void insertOrganisations(String filePath, HashMap<Long, Long> temp) {
    try {
      Reader reader = new FileReader(filePath);
      CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
      CSVParser csvParser = new CSVParser(reader, csvFormat);

      Label label = Label.label("Organisation");
      try(Transaction tx = db.beginTx(); ){
      for (CSVRecord csvRecord : csvParser) {
        long id = Long.parseLong(csvRecord.get("id:ID(Organisation)"));
        String type = csvRecord.get("type");
        String name = csvRecord.get("name");
        String url = csvRecord.get("url");
       
        Node node = tx.createNode(label);
        node.setProperty("id", id);
        node.setProperty("name", name);
        node.setProperty("url", url);
        temp.put(id, node.getId());
      }       
      tx.commit();
      } catch (Exception e) {
          e.printStackTrace();
      }
      csvParser.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void insertForums(String filePath, HashMap<Long, Long> temp) {
    try {
      Reader reader = new FileReader(filePath);
      CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
      CSVParser csvParser = new CSVParser(reader, csvFormat);

      Label label = Label.label("Forum");
      try(Transaction tx = db.beginTx(); ){
      for (CSVRecord csvRecord : csvParser) {
        long id = Long.parseLong(csvRecord.get("id:ID(Forum)"));
        String title = csvRecord.get("title");
        long creationDate = Long.parseLong(csvRecord.get("creationDate"));

        Node node = tx.createNode(label);
        node.setProperty("id", id);
        node.setProperty("title", title);
        node.setProperty("creationDate", creationDate);
        temp.put(id, node.getId());
      }       
      tx.commit();
      } catch (Exception e) {
          e.printStackTrace();
      }
      csvParser.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void insertPersons(String filePath, HashMap<Long, Long> temp) {
    try {
      Reader reader = new FileReader(filePath);
      CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
      CSVParser csvParser = new CSVParser(reader, csvFormat);

      Label label = Label.label("Person");
      try(Transaction tx = db.beginTx(); ){
      for (CSVRecord csvRecord : csvParser) {
        long id = Long.parseLong(csvRecord.get("id:ID(Person)"));
        String firstName = csvRecord.get("firstName");
        String lastName = csvRecord.get("lastName");
        String gender = csvRecord.get("gender");
        long birthday = Long.parseLong(csvRecord.get("birthday"));
        long creationDate = Long.parseLong(csvRecord.get("creationDate"));
        String locationIP = csvRecord.get("locationIP");
        String browserUsed = csvRecord.get("browserUsed");
        String language = csvRecord.get("language");
        String email = csvRecord.get("email");

        Node node = tx.createNode(label);
        node.setProperty("id", id);
        node.setProperty("firstName", firstName);
        node.setProperty("lastName", lastName);
        node.setProperty("gender", gender);
        node.setProperty("birthday", birthday);
        node.setProperty("creationDate", creationDate);
        node.setProperty("locationIP", locationIP);
        node.setProperty("browserUsed", browserUsed);
        node.setProperty("language", language);
        node.setProperty("email", email);
        temp.put(id, node.getId());
      }       
      tx.commit();
      } catch (Exception e) {
          e.printStackTrace();
      }
      csvParser.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void insertComments(String filePath, HashMap<Long, Long> temp) {
    try {
      Reader reader = new FileReader(filePath);
      CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
      CSVParser csvParser = new CSVParser(reader, csvFormat);

      Label label = Label.label("Comment");
      try(Transaction tx = db.beginTx(); ){
      for (CSVRecord csvRecord : csvParser) {
        long id = Long.parseLong(csvRecord.get("id:ID(Comment)"));
        long creationDate = Long.parseLong(csvRecord.get("creationDate"));
        String locationIP = csvRecord.get("locationIP");
        String browserUsed = csvRecord.get("browserUsed");
        String content = csvRecord.get("content");
        int length = Integer.parseInt(csvRecord.get("length"));

        Node node = tx.createNode(label);
        node.setProperty("id", id);
        node.setProperty("creationDate", creationDate);
        node.setProperty("locationIP", locationIP);
        node.setProperty("browserUsed", browserUsed);
        node.setProperty("content", content);
        node.setProperty("length", length);
        temp.put(id, node.getId());
      }       
      tx.commit();
      } catch (Exception e) {
          e.printStackTrace();
      }
      csvParser.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void insertPosts(String filePath, HashMap<Long, Long> temp) {
    try {
      Reader reader = new FileReader(filePath);
      CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
      CSVParser csvParser = new CSVParser(reader, csvFormat);

      Label label = Label.label("Post");
      try(Transaction tx = db.beginTx(); ){
      for (CSVRecord csvRecord : csvParser) {
        long id = Long.parseLong(csvRecord.get("id:ID(Post)"));
        long creationDate = Long.parseLong(csvRecord.get("creationDate"));
        String locationIP = csvRecord.get("locationIP");
        String browserUsed = csvRecord.get("browserUsed");
        String language = csvRecord.get("language");
        String content = csvRecord.get("content");
        int length = Integer.parseInt(csvRecord.get("length"));

        Node node = tx.createNode(label);
        node.setProperty("id", id);
        node.setProperty("creationDate", creationDate);
        node.setProperty("locationIP", locationIP);
        node.setProperty("browserUsed", browserUsed);
        node.setProperty("language", language);
        node.setProperty("content", content);
        node.setProperty("length", length);
        temp.put(id, node.getId());
      }       
      tx.commit();
      } catch (Exception e) {
          e.printStackTrace();
      }
      csvParser.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

    private void insertRelations(
            String filePath,
            String referrerClass,
            String refereeClass,
            HashMap<Long, Long> referrer,
            HashMap<Long, Long> referee,
            String relationName) {
        try {
            Reader reader = new FileReader(filePath);
            CSVFormat csvFormat =
                    CSVFormat.Builder.create()
                    .setDelimiter('|')
                    .setSkipHeaderRecord(true)
                    .setHeader("Referrer", "Referee")
                    .build();
            CSVParser csvParser = new CSVParser(reader, csvFormat);
            String query = "";
            
            try(Transaction tx = db.beginTx(); )
                {
                    
                    for (CSVRecord csvRecord : csvParser) {
                        //    query = "CREATE (:"+referrerClass+" {id:"+csvRecord.get("Referrer")+"})-[:"+relationName+"]->(:"+refereeClass+" {id:"+csvRecord.get("Referee")+"})\n";
                 
                        tx.getNodeById(referrer.get((long)Long.parseLong(csvRecord.get("Referrer")))).createRelationshipTo(tx.getNodeById(referee.get((long)Long.parseLong(csvRecord.get("Referee")))), RelationshipType.withName(relationName));       
                    //tx.execute(query);
                    }
                    tx.commit();
                } catch (Exception e) {
                //// e.printStackTrace();
            }
            
            csvParser.close();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
}
