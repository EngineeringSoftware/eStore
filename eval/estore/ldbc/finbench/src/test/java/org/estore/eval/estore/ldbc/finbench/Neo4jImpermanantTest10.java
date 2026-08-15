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

import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.extension.ImpermanentDbmsExtension;
import org.neo4j.test.extension.Inject;

@ImpermanentDbmsExtension
class Neo4jImpermanantTest10 {
  @Inject private GraphDatabaseService db;

  @Test
  public void testTw1() {
    try (Transaction tx = db.beginTx(); ) {
      long t1 = System.nanoTime();
      tx.execute(
          "CREATE (:`Person` {personId: 1, personName:"
              + " 'George'})-[:Own]->(:`Account`"
              + " {accountId: 1020342322, createTime: '26th March', isBlocked: False,"
              + " accountType: 'brokerage account'})");
      System.out.println("Total Query Time : " + (System.nanoTime() - t1));
    } catch (Exception e) {
    }
  }

  @Test
  public void testTw2() {
    try (Transaction tx = db.beginTx(); ) {

      long t1 =System.nanoTime();

      tx.execute(
          "CREATE (:`Company` {companyId: 12345,"
              + " companyName: 'Rand'})-[:Own]->(:`Account`"
              + " {accountId: 1213243435, createTime: 'February 5th', isBlocked: False,"
              + " accountType: 'brokerage account'})");
      System.out.println("Total Query Time : " + (System.nanoTime() - t1));
    } catch (Exception e) {
    }
  }

  @Test
  public void testTw3() {
    try (Transaction tx = db.beginTx(); ) {

      long t1 =System.nanoTime();

      tx.execute(
          " MATCH (dst:`Account` {accountId:"
              + " 4619004367821865972}), (src:`Account`"
              + " {accountId: 99079191802151398}) CREATE (dst)-[:Transfer]->(src)");
      System.out.println("Total Query Time : " + (System.nanoTime() - t1));
    } catch (Exception e) {
    }
  }

  @Test
  public void testTw4() {
    try (Transaction tx = db.beginTx(); ) {

      long t1 =System.nanoTime();

      tx.execute(
          " MATCH (dst:`Account` {accountId:"
              + " 4619004367821865972, accountType:'card'}),"
              + " (src:`Account` {accountId:"
              + " 99079191802151398}) CREATE (dst)-[:Withdraw]->(src)");
      System.out.println("Total Query Time : " + (System.nanoTime() - t1));
    } catch (Exception e) {
    }
  }

  @Test
  public void testTw8() {
    try (Transaction tx = db.beginTx(); ) {

      long t1 =System.nanoTime();

      tx.execute(
          "MATCH (acc:`Account` {accountId:"
              + " 4700350636091245930}), (loan:`Loan`"
              + " {loanId: 4684025087442027461}) CREATE (loan)-[:Deposit]->(acc)");
      System.out.println("Total Query Time : " + (System.nanoTime() - t1));
    } catch (Exception e) {
    }
  }

  @Test
  public void testTw9() {
    try (Transaction tx = db.beginTx(); ) {

      long t1 =System.nanoTime();

      tx.execute(
          "MATCH (acc:`Account` {accountId:"
              + " 4700350636091245930}), (loan:`Loan`"
              + " {loanId: 4684025087442027461}) CREATE (acc)-[:Repay]->(loan)");
      System.out.println("Total Query Time : " + (System.nanoTime() - t1));
    } catch (Exception e) {
    }
  }

  @Test
  public void testTw13() {
    try (Transaction tx = db.beginTx(); ) {

      long t1 =System.nanoTime();

      tx.execute(
          "MATCH (p1:`Person` {personId: 2199023255767}),"
              + " (p2:`Person` {personId: 10995116278183})"
              + " CREATE (p1)<-[:Guarantee]-(p2)");
      System.out.println("Total Query Time : " + (System.nanoTime() - t1));
    } catch (Exception e) {
    }
  }

  @Test
  public void testTsr1() {
    try (Transaction tx = db.beginTx(); ) {

      long t1 =System.nanoTime();

      tx.execute(
          "MATCH (account:`Account` {accountId:"
              + " 4700350636091245930}) RETURN account.createTime, account.isBlocked,"
              + " account.accountType");
      System.out.println("Total Query Time : " + (System.nanoTime() - t1));
    } catch (Exception e) {
    }
  }

  @BeforeEach
  public void setupData() throws Exception {
    String datasetPath = "/sf10";
    HashMap<Long, Long> accounts = new HashMap<Long, Long>();
    HashMap<Long, Long> companys = new HashMap<Long, Long>();
    HashMap<Long, Long> loans = new HashMap<Long, Long>();
    HashMap<Long, Long> mediums = new HashMap<Long, Long>();
    HashMap<Long, Long> persons = new HashMap<Long, Long>();

    insertAccounts(datasetPath + "/" + "snapshot/Account.csv", accounts);
    insertCompanys(datasetPath + "/" + "snapshot/Company.csv", companys);
    insertLoans(datasetPath + "/" + "snapshot/Loan.csv", loans);
    insertMediums(datasetPath + "/" + "snapshot/Medium.csv", mediums);
    insertPersons(datasetPath + "/" + "snapshot/Person.csv", persons);
    System.out.println(
        accounts.size() + companys.size() + loans.size() + mediums.size() + persons.size());
    // Relations

    insertRelations(
        datasetPath + "/" + "snapshot/AccountRepayLoan.csv",
        "Account",
        "Loan",
        accounts,
        loans,
        "setRepay");
    insertRelations(
        datasetPath + "/" + "snapshot/AccountTransferAccount.csv",
        "Account",
        "Account",
        accounts,
        accounts,
        "setTransfer");
    insertRelations(
        datasetPath + "/" + "snapshot/AccountWithdrawAccount.csv",
        "Account",
        "Account",
        accounts,
        accounts,
        "setWithdraw");
    insertRelations(
        datasetPath + "/" + "snapshot/CompanyApplyLoan.csv",
        "Company",
        "Loan",
        companys,
        loans,
        "setApply");

    insertRelations(
        datasetPath + "/" + "snapshot/CompanyGuaranteeCompany.csv",
        "Company",
        "Company",
        companys,
        companys,
        "setGuarantee");
    insertRelations(
        datasetPath + "/" + "snapshot/CompanyInvestCompany.csv",
        "Company",
        "Company",
        companys,
        companys,
        "setInvest");
    insertRelations(
        datasetPath + "/" + "snapshot/CompanyOwnAccount.csv",
        "Company",
        "Account",
        companys,
        accounts,
        "setOwn");
    insertRelations(
        datasetPath + "/" + "snapshot/LoanDepositAccount.csv",
        "Loan",
        "Account",
        loans,
        accounts,
        "setDeposit");
    insertRelations(
        datasetPath + "/" + "snapshot/MediumSignInAccount.csv",
        "Medium",
        "Account",
        mediums,
        accounts,
        "setSignIn");
    insertRelations(
        datasetPath + "/" + "snapshot/PersonApplyLoan.csv",
        "Person",
        "Loan",
        persons,
        loans,
        "setApply");
    insertRelations(
        datasetPath + "/" + "snapshot/PersonGuaranteePerson.csv",
        "Person",
        "Person",
        persons,
        persons,
        "setGuarantee");
    insertRelations(
        datasetPath + "/" + "snapshot/PersonInvestCompany.csv",
        "Person",
        "Company",
        persons,
        companys,
        "setInvest");
    insertRelations(
        datasetPath + "/" + "snapshot/PersonOwnAccount.csv",
        "Person",
        "Account",
        persons,
        accounts,
        "setOwn");
  }

  private void insertPersons(String filePath, HashMap<Long, Long> temp) {
    try {
      Reader reader = new FileReader(filePath);
      CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
      CSVParser csvParser = new CSVParser(reader, csvFormat);

      Label label = Label.label("Person");
      try (Transaction tx = db.beginTx(); ) {
        for (CSVRecord csvRecord : csvParser) {
          long personId = Long.parseLong(csvRecord.get("personId:ID(Person)"));
          String personName = csvRecord.get("personName");
          boolean isBlocked = Boolean.parseBoolean(csvRecord.get("isBlocked"));
          String createTime = csvRecord.get("createTime");
          String gender = csvRecord.get("gender");
          String birthday = csvRecord.get("birthday");
          String country = csvRecord.get("country");
          String city = csvRecord.get("city");

          Node node = tx.createNode(label);
          node.setProperty("id", personId);
          node.setProperty("personName", personName);
          node.setProperty("isBlocked", isBlocked);
          node.setProperty("createTime", createTime);
          node.setProperty("gender", gender);
          node.setProperty("birthday", birthday);
          node.setProperty("country", country);
          node.setProperty("city", city);
          temp.put(personId, node.getId());
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

  private void insertAccounts(String filePath, HashMap<Long, Long> temp) {
    try {
      Reader reader = new FileReader(filePath);
      CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
      CSVParser csvParser = new CSVParser(reader, csvFormat);

      Label label = Label.label("Account");
      try (Transaction tx = db.beginTx(); ) {
        for (CSVRecord csvRecord : csvParser) {
          long accountId = Long.parseLong(csvRecord.get("accountId:ID(Account)"));
          String createTime = csvRecord.get("createTime");
          boolean isBlocked = Boolean.parseBoolean(csvRecord.get("isBlocked"));
          String accountType = csvRecord.get("accoutType");
          String nickname = csvRecord.get("nickname");
          String phonenum = csvRecord.get("phonenum");
          String email = csvRecord.get("email");
          String freqLoginType = csvRecord.get("freqLoginType");
          long lastLoginTime = Long.parseLong(csvRecord.get("lastLoginTime"));
          String accountLevel = csvRecord.get("accountLevel");

          Node node = tx.createNode(label);
          node.setProperty("id", accountId);
          node.setProperty("createTime", createTime);
          node.setProperty("isBlocked", isBlocked);
          node.setProperty("accountType", isBlocked);
          node.setProperty("nickname", nickname);
          node.setProperty("phonenum", phonenum);
          node.setProperty("email", email);
          node.setProperty("freqLoginType", freqLoginType);
          node.setProperty("lastLoginTime", lastLoginTime);
          node.setProperty("accountLevel", accountLevel);
          temp.put(accountId, node.getId());
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

  private void insertCompanys(String filePath, HashMap<Long, Long> temp) {
    try {
      Reader reader = new FileReader(filePath);
      CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
      CSVParser csvParser = new CSVParser(reader, csvFormat);

      Label label = Label.label("Company");
      try (Transaction tx = db.beginTx(); ) {
        for (CSVRecord csvRecord : csvParser) {
          long companyId = Long.parseLong(csvRecord.get("companyId:ID(Company)"));
          String companyName = csvRecord.get("companyName");
          boolean isBlocked = Boolean.parseBoolean(csvRecord.get("isBlocked"));
          String createTime = csvRecord.get("createTime");
          String country = csvRecord.get("country");
          String city = csvRecord.get("city");
          String business = csvRecord.get("business");
          String description = csvRecord.get("description");
          String url = csvRecord.get("url");

          Node node = tx.createNode(label);
          node.setProperty("id", companyId);
          node.setProperty("companyName", companyName);
          node.setProperty("isBlocked", isBlocked);
          node.setProperty("createTime", createTime);
          node.setProperty("country", country);
          node.setProperty("city", city);
          node.setProperty("business", business);
          node.setProperty("description", description);
          node.setProperty("url", url);
          temp.put(companyId, node.getId());
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

  private void insertLoans(String filePath, HashMap<Long, Long> temp) {
    try {
      Reader reader = new FileReader(filePath);
      CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
      CSVParser csvParser = new CSVParser(reader, csvFormat);

      Label label = Label.label("Loan");
      try (Transaction tx = db.beginTx(); ) {
        for (CSVRecord csvRecord : csvParser) {
          long loanId = Long.parseLong(csvRecord.get("loanId:ID(Loan)"));
          double loanAmount = Double.parseDouble(csvRecord.get("loanAmount"));
          double balance = Double.parseDouble(csvRecord.get("balance"));
          String createTime = csvRecord.get("createTime");
          String loanUsage = csvRecord.get("loanUsage");
          double interestRate = Double.parseDouble(csvRecord.get("interestRate"));

          Node node = tx.createNode(label);
          node.setProperty("id", loanId);
          node.setProperty("loanAmount", loanAmount);
          node.setProperty("balance", balance);
          node.setProperty("createTime", createTime);
          node.setProperty("loanUsage", loanUsage);
          node.setProperty("interestRate", interestRate);
          temp.put(loanId, node.getId());
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

  private void insertMediums(String filePath, HashMap<Long, Long> temp) {
    try {
      Reader reader = new FileReader(filePath);
      CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
      CSVParser csvParser = new CSVParser(reader, csvFormat);

      Label label = Label.label("Medium");
      try (Transaction tx = db.beginTx(); ) {
        for (CSVRecord csvRecord : csvParser) {

          long mediumId = Long.parseLong(csvRecord.get("mediumId:ID(Medium)"));
          String mediumType = csvRecord.get("mediumType");
          boolean isBlocked = Boolean.parseBoolean(csvRecord.get("isBlocked"));
          String createTime = csvRecord.get("createTime");
          long lastLoginTime = Long.parseLong(csvRecord.get("lastLoginTime"));
          String riskLevel = csvRecord.get("riskLevel");

          Node node = tx.createNode(label);
          node.setProperty("id", mediumId);
          node.setProperty("mediumType", mediumType);
          node.setProperty("isBlocked", isBlocked);
          node.setProperty("createTime", createTime);
          node.setProperty("lastLoginTime", lastLoginTime);
          node.setProperty("riskLevel", riskLevel);
          temp.put(mediumId, node.getId());
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

      try (Transaction tx = db.beginTx(); ) {

        for (CSVRecord csvRecord : csvParser) {
          //    query = "CREATE (:"+referrerClass+"
          // {id:"+csvRecord.get("Referrer")+"})-[:"+relationName+"]->(:"+refereeClass+"
          // {id:"+csvRecord.get("Referee")+"})\n";

          tx.getNodeById(referrer.get((long) Long.parseLong(csvRecord.get("Referrer"))))
              .createRelationshipTo(
                  tx.getNodeById(referee.get((long) Long.parseLong(csvRecord.get("Referee")))),
                  RelationshipType.withName(relationName));
          // tx.execute(query);
        }
        tx.commit();
      } catch (Exception e) {
        //// e.printStackTrace();
      }

      csvParser.close();
    } catch (Exception e) {
      // e.printStackTrace();
    }
  }
}
