package org.estore.eval.estore.ldbc.finbench;

import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.arcadedb.graph.MutableVertex;
import com.arcadedb.database.Database;
import com.arcadedb.database.DatabaseFactory;
import com.arcadedb.query.sql.executor.ResultSet;

class ArcadeDBEmbeddedTest03 {
  private DatabaseFactory dbFactory;
  private Database db;

  @Test
  public void testData() {
    try {
      db.begin();
      db.transaction(
          () -> {
            long t1 = System.nanoTime();
            ResultSet res = db.query("cypher", "MATCH (n) RETURN COUNT(n)");
            System.out.println("Total Query Time : " + (System.nanoTime() - t1));
            while (res.hasNext()) {
              System.out.println(res.next());
            }
          });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testTw1() {
    try {
      db.begin();
      db.transaction(
          () -> {
            long t1 = System.nanoTime();
            ResultSet res =
                db.query(
                    "cypher",
                    "CREATE (:`Person` {personId: 1, personName:"
                        + " 'George'})-[:Own]->(:`Account`"
                        + " {accountId: 1020342322, createTime: '26th March', isBlocked: False,"
                        + " accountType: 'brokerage account'})");
            System.out.println("Total Query Time : " + (System.nanoTime() - t1));
          });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testTw2() {
    try {
      db.begin();
      db.transaction(
          () -> {
            long t1 = System.nanoTime();
            ResultSet res =
                db.query(
                    "cypher",
                    "CREATE (:`Company` {companyId: 12345,"
                        + " companyName: 'Rand'})-[:Own]->(:`Account`"
                        + " {accountId: 1213243435, createTime: 'February 5th', isBlocked: False,"
                        + " accountType: 'brokerage account'})");
            System.out.println("Total Query Time : " + (System.nanoTime() - t1));
          });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testTw3() {
    try {
      db.begin();
      db.transaction(
          () -> {
            long t1 = System.nanoTime();
            ResultSet res =
                db.query(
                    "cypher",
                    " MATCH (dst:`Account` {accountId:"
                        + " 4619004367821865972}), (src:`Account`"
                        + " {accountId: 99079191802151398}) CREATE (dst)-[:Transfer]->(src)");
            System.out.println("Total Query Time : " + (System.nanoTime() - t1));
          });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testTw4() {
    try {
      db.begin();
      db.transaction(
          () -> {
            long t1 = System.nanoTime();

            ResultSet res =
                db.query(
                    "cypher",
                    " MATCH (dst:`Account` {accountId:"
                        + " 4619004367821865972, accountType:'card'}),"
                        + " (src:`Account` {accountId:"
                        + " 99079191802151398}) CREATE (dst)-[:Withdraw]->(src)");
            System.out.println("Total Query Time : " + (System.nanoTime() - t1));
          });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testTw8() {
    try {
      db.begin();
      db.transaction(
          () -> {
            long t1 = System.nanoTime();

            ResultSet res =
                db.query(
                    "cypher",
                    "MATCH (acc:`Account` {accountId:"
                        + " 4700350636091245930}), (loan:`Loan`"
                        + " {loanId: 4684025087442027461}) CREATE (loan)-[:Deposit]->(acc)");
            System.out.println("Total Query Time : " + (System.nanoTime() - t1));
          });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testTw9() {
    try {
      db.begin();
      db.transaction(
          () -> {
            long t1 = System.nanoTime();

            ResultSet res =
                db.query(
                    "cypher",
                    "MATCH (acc:`Account` {accountId:"
                        + " 4700350636091245930}), (loan:`Loan`"
                        + " {loanId: 4684025087442027461}) CREATE (acc)-[:Repay]->(loan)");
            System.out.println("Total Query Time : " + (System.nanoTime() - t1));
          });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testTw13() {
    try {
      db.begin();
      db.transaction(
          () -> {
            long t1 = System.nanoTime();

            ResultSet res =
                db.query(
                    "cypher",
                    "MATCH (p1:`Person` {personId: 2199023255767}),"
                        + " (p2:`Person` {personId: 10995116278183})"
                        + " CREATE (p1)<-[:Guarantee]-(p2)");
            System.out.println("Total Query Time : " + (System.nanoTime() - t1));
          });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testTsr1() {
    try {
      db.begin();
      db.transaction(
          () -> {
            long t1 = System.nanoTime();

            ResultSet res =
                db.query(
                    "cypher",
                    "MATCH (account:`Account` {accountId:"
                        + " 4700350636091245930}) RETURN account.createTime, account.isBlocked,"
                        + " account.accountType");
            System.out.println("Total Query Time : " + (System.nanoTime() - t1));
          });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @BeforeEach
  public void setupData() {
    try {
      dbFactory = new DatabaseFactory("ArcadeDB/database");
      db = dbFactory.create();
    } catch (Exception e) {
      e.printStackTrace();
    }

    String datasetPath = "/sf0.3";
    HashMap<Long, MutableVertex> accounts = new HashMap<Long, MutableVertex>();
    HashMap<Long, MutableVertex> companys = new HashMap<Long, MutableVertex>();
    HashMap<Long, MutableVertex> loans = new HashMap<Long, MutableVertex>();
    HashMap<Long, MutableVertex> mediums = new HashMap<Long, MutableVertex>();
    HashMap<Long, MutableVertex> persons = new HashMap<Long, MutableVertex>();

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

  private void insertPersons(String filePath, HashMap<Long, MutableVertex> temp) {
    try {
      Reader reader = new FileReader(filePath);
      CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
      CSVParser csvParser = new CSVParser(reader, csvFormat);

      try {
        db.begin();
        db.transaction(
            () -> {
              db.getSchema().createVertexType("Person");

              for (CSVRecord csvRecord : csvParser) {
                long personId = Long.parseLong(csvRecord.get("personId:ID(Person)"));
                String personName = csvRecord.get("personName");
                boolean isBlocked = Boolean.parseBoolean(csvRecord.get("isBlocked"));
                String createTime = csvRecord.get("createTime");
                String gender = csvRecord.get("gender");
                String birthday = csvRecord.get("birthday");
                String country = csvRecord.get("country");
                String city = csvRecord.get("city");

                MutableVertex vert =
                    db.newVertex("Person")
                        .set("personId", personId)
                        .set("personName", personName)
                        .set("isBlocked", isBlocked)
                        .set("createTime", createTime)
                        .set("gender", gender)
                        .set("birthday", birthday)
                        .set("country", country)
                        .set("city", city)
                        .save();
                temp.put(personId, vert);
              }
            });
        db.commit();
      } catch (Exception e) {
        e.printStackTrace();
      }
      csvParser.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void insertAccounts(String filePath, HashMap<Long, MutableVertex> temp) {
    try {
      Reader reader = new FileReader(filePath);
      CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
      CSVParser csvParser = new CSVParser(reader, csvFormat);

      try {
        db.begin();
        db.transaction(
            () -> {
              db.getSchema().createVertexType("Account");

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

                MutableVertex vert =
                    db.newVertex("Account")
                        .set("accountId", accountId)
                        .set("createTime", createTime)
                        .set("isBlocked", isBlocked)
                        .set("accountType", isBlocked)
                        .set("nickname", nickname)
                        .set("phonenum", phonenum)
                        .set("email", email)
                        .set("freqLoginType", freqLoginType)
                        .set("lastLoginTime", lastLoginTime)
                        .set("accountLevel", accountLevel)
                        .save();
                temp.put(accountId, vert);
              }
            });
        db.commit();
      } catch (Exception e) {
        e.printStackTrace();
      }
      csvParser.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void insertCompanys(String filePath, HashMap<Long, MutableVertex> temp) {
    try {
      Reader reader = new FileReader(filePath);
      CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
      CSVParser csvParser = new CSVParser(reader, csvFormat);

      try {
        db.begin();
        db.transaction(
            () -> {
              db.getSchema().createVertexType("Company");

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

                MutableVertex vert =
                    db.newVertex("Company")
                        .set("companyId", companyId)
                        .set("companyName", companyName)
                        .set("isBlocked", isBlocked)
                        .set("createTime", createTime)
                        .set("country", country)
                        .set("city", city)
                        .set("business", business)
                        .set("description", description)
                        .set("url", url)
                        .save();
                temp.put(companyId, vert);
              }
            });
        db.commit();
      } catch (Exception e) {
        e.printStackTrace();
      }
      csvParser.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void insertLoans(String filePath, HashMap<Long, MutableVertex> temp) {
    try {
      Reader reader = new FileReader(filePath);
      CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
      CSVParser csvParser = new CSVParser(reader, csvFormat);

      try {
        db.begin();
        db.transaction(
            () -> {
              db.getSchema().createVertexType("Loan");

              for (CSVRecord csvRecord : csvParser) {
                long loanId = Long.parseLong(csvRecord.get("loanId:ID(Loan)"));
                double loanAmount = Double.parseDouble(csvRecord.get("loanAmount"));
                double balance = Double.parseDouble(csvRecord.get("balance"));
                String createTime = csvRecord.get("createTime");
                String loanUsage = csvRecord.get("loanUsage");
                double interestRate = Double.parseDouble(csvRecord.get("interestRate"));

                MutableVertex vert =
                    db.newVertex("Loan")
                        .set("loanId", loanId)
                        .set("loanAmount", loanAmount)
                        .set("balance", balance)
                        .set("createTime", createTime)
                        .set("loanUsage", loanUsage)
                        .set("interestRate", interestRate)
                        .save();
                temp.put(loanId, vert);
              }
            });
        db.commit();
      } catch (Exception e) {
        e.printStackTrace();
      }
      csvParser.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void insertMediums(String filePath, HashMap<Long, MutableVertex> temp) {
    try {
      Reader reader = new FileReader(filePath);
      CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
      CSVParser csvParser = new CSVParser(reader, csvFormat);

      try {
        db.begin();
        db.transaction(
            () -> {
              db.getSchema().createVertexType("Medium");

              for (CSVRecord csvRecord : csvParser) {
                long mediumId = Long.parseLong(csvRecord.get("mediumId:ID(Medium)"));
                String mediumType = csvRecord.get("mediumType");
                boolean isBlocked = Boolean.parseBoolean(csvRecord.get("isBlocked"));
                String createTime = csvRecord.get("createTime");
                long lastLoginTime = Long.parseLong(csvRecord.get("lastLoginTime"));
                String riskLevel = csvRecord.get("riskLevel");

                MutableVertex vert =
                    db.newVertex("Medium")
                        .set("mediumId", mediumId)
                        .set("mediumType", mediumType)
                        .set("isBlocked", isBlocked)
                        .set("createTime", createTime)
                        .set("lastLoginTime", lastLoginTime)
                        .set("riskLevel", riskLevel)
                        .save();
                temp.put(mediumId, vert);
              }
            });
        db.commit();
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
      HashMap<Long, MutableVertex> referrer,
      HashMap<Long, MutableVertex> referee,
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

      try {
        db.begin();
        db.transaction(
            () -> {
              db.getSchema().createEdgeType(relationName);

              for (CSVRecord csvRecord : csvParser) {
                long referrerID = (long) Long.parseLong(csvRecord.get("Referrer"));
                long refereeID = (long) Long.parseLong(csvRecord.get("Referee"));
                referrer
                    .get(referrerID)
                    .newEdge(relationName, referee.get(refereeID), false)
                    .save();
              }
            });
        db.commit();
      } catch (Exception e) {
        e.printStackTrace();
      }
      csvParser.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
