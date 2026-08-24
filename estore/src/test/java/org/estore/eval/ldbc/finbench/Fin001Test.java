package org.estore.eval.ldbc.finbench;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.estore.Estore;
import org.estore.EstoreException;
import org.estore.EstoreOptions;
import org.estore.compiler.CompileQuery;
import org.estore.eval.EvalUtil;
import org.estore.eval.ldbc.finbench.util.*;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Fin001Test {

    private Estore estore;

    @BeforeEach
    public void setupData() throws EstoreException {
        estore = new Estore(Fin001Test.class.getName(), new EstoreOptions().profile(false));

        readDataSet();
    }

    @CompileQuery
    @Test
    public void testTw1() {
        long t1 = System.nanoTime();
        Table result =
                estore.query(
                        "CREATE (:`org.estore.eval.ldbc.finbench.util.Person` {personId: 1, personName:"
                                + " 'George'})-[:Own]->(:`org.estore.eval.ldbc.finbench.util.Account`"
                                + " {accountId: 1020342322, createTime: '26th March', isBlocked: False,"
                                + " accountType: 'brokerage account'})");
        EvalUtil.printQueryTime("Tw1", t1);
    }

    @CompileQuery
    @Test
    public void testTw2() {
        long t1 = System.nanoTime();
        Table result =
                estore.query(
                        "CREATE (:`org.estore.eval.ldbc.finbench.util.Company` {companyId: 12345,"
                                + " companyName: 'Rand'})-[:Own]->(:`org.estore.eval.ldbc.finbench.util.Account`"
                                + " {accountId: 1213243435, createTime: 'February 5th', isBlocked: False,"
                                + " accountType: 'brokerage account'})");
        EvalUtil.printQueryTime("Tw2", t1);
    }

    @CompileQuery
    @Test
    public void testTw3() {
        long t1 = System.nanoTime();
        Table result =
                estore.query(
                        " MATCH (dst:`org.estore.eval.ldbc.finbench.util.Account` {accountId:"
                                + " 4619004367821865972}), (src:`org.estore.eval.ldbc.finbench.util.Account`"
                                + " {accountId: 99079191802151398}) CREATE (dst)-[:Transfer]->(src)");
        EvalUtil.printQueryTime("Tw3", t1);
    }

    @CompileQuery
    @Test
    public void testTw4() {
        long t1 = System.nanoTime();
        Table result =
                estore.query(
                        " MATCH (dst:`org.estore.eval.ldbc.finbench.util.Account` {accountId:"
                                + " 4619004367821865972, accountType:'card'}),"
                                + " (src:`org.estore.eval.ldbc.finbench.util.Account` {accountId:"
                                + " 99079191802151398}) CREATE (dst)-[:Withdraw]->(src)");
        EvalUtil.printQueryTime("Tw4", t1);
    }

    @CompileQuery
    @Test
    public void testTw8() {
        long t1 = System.nanoTime();
        Table result =
                estore.query(
                        "MATCH (acc:`org.estore.eval.ldbc.finbench.util.Account` {accountId:"
                                + " 4700350636091245930}), (loan:`org.estore.eval.ldbc.finbench.util.Loan`"
                                + " {loanId: 4684025087442027461}) CREATE (loan)-[:Deposit]->(acc)");
        EvalUtil.printQueryTime("Tw8", t1);
    }

    @CompileQuery
    @Test
    public void testTw9() {
        long t1 = System.nanoTime();
        Table result =
                estore.query(
                        "MATCH (acc:`org.estore.eval.ldbc.finbench.util.Account` {accountId:"
                                + " 4700350636091245930}), (loan:`org.estore.eval.ldbc.finbench.util.Loan`"
                                + " {loanId: 4684025087442027461}) CREATE (acc)-[:Repay]->(loan)");
        EvalUtil.printQueryTime("Tw9", t1);
    }

    @CompileQuery
    @Test
    public void testTw13() {
        long t1 = System.nanoTime();
        Table result =
                estore.query(
                        "MATCH (p1:`org.estore.eval.ldbc.finbench.util.Person` {personId: 2199023255767}),"
                                + " (p2:`org.estore.eval.ldbc.finbench.util.Person` {personId: 10995116278183})"
                                + " CREATE (p1)<-[:Guarantee]-(p2)");
        EvalUtil.printQueryTime("Tw13", t1);
    }

    @CompileQuery
    @Test
    public void testTsr1() {
        long t1 = System.nanoTime();
        Table result =
                estore.query(
                        "MATCH (account:`org.estore.eval.ldbc.finbench.util.Account` {accountId:"
                                + " 4700350636091245930}) RETURN account.createTime, account.isBlocked,"
                                + " account.accountType");
        EvalUtil.printQueryTime("Tsr1", t1);
        assertEquals(result.get("account.createTime").get(0), "2020-11-11 18:44:24.021");
        assertEquals(result.get("account.isBlocked").get(0), false);
        assertEquals(result.get("account.accountType").get(0), "certificate of deposit");
    }

    public void readDataSet() throws EstoreException {
        HashMap<Long, Person> persons = new HashMap<Long, Person>();
        HashMap<Long, Account> accounts = new HashMap<Long, Account>();
        HashMap<Long, Company> companys = new HashMap<Long, Company>();
        HashMap<Long, Loan> loans = new HashMap<Long, Loan>();
        HashMap<Long, Medium> mediums = new HashMap<Long, Medium>();

        String datasetPath = "src/test/java/org/estore/eval/ldbc/data//sf0.01";

        // Nodes
        insertAccounts(datasetPath + "/" + "snapshot/Account.csv", accounts);
        insertCompanys(datasetPath + "/" + "snapshot/Company.csv", companys);
        insertLoans(datasetPath + "/" + "snapshot/Loan.csv", loans);
        insertMediums(datasetPath + "/" + "snapshot/Medium.csv", mediums);
        insertPersons(datasetPath + "/" + "snapshot/Person.csv", persons);

        // Relations
        new CreateRelation<Account, Loan>()
                .insertRelations(
                        datasetPath + "/" + "snapshot/AccountRepayLoan.csv",
                        Account.class,
                        Loan.class,
                        accounts,
                        loans,
                        "setRepay");
        new CreateRelation<Account, Account>()
                .insertRelations(
                        datasetPath + "/" + "snapshot/AccountTransferAccount.csv",
                        Account.class,
                        Account.class,
                        accounts,
                        accounts,
                        "setTransfer");
        new CreateRelation<Account, Account>()
                .insertRelations(
                        datasetPath + "/" + "snapshot/AccountWithdrawAccount.csv",
                        Account.class,
                        Account.class,
                        accounts,
                        accounts,
                        "setWithdraw");
        new CreateRelation<Company, Loan>()
                .insertRelations(
                        datasetPath + "/" + "snapshot/CompanyApplyLoan.csv",
                        Company.class,
                        Loan.class,
                        companys,
                        loans,
                        "setApply");
        new CreateRelation<Company, Company>()
                .insertRelations(
                        datasetPath + "/" + "snapshot/CompanyGuaranteeCompany.csv",
                        Company.class,
                        Company.class,
                        companys,
                        companys,
                        "setGuarantee");
        new CreateRelation<Company, Company>()
                .insertRelations(
                        datasetPath + "/" + "snapshot/CompanyInvestCompany.csv",
                        Company.class,
                        Company.class,
                        companys,
                        companys,
                        "setInvest");
        new CreateRelation<Company, Account>()
                .insertRelations(
                        datasetPath + "/" + "snapshot/CompanyOwnAccount.csv",
                        Company.class,
                        Account.class,
                        companys,
                        accounts,
                        "setOwn");
        new CreateRelation<Loan, Account>()
                .insertRelations(
                        datasetPath + "/" + "snapshot/LoanDepositAccount.csv",
                        Loan.class,
                        Account.class,
                        loans,
                        accounts,
                        "setDeposit");
        new CreateRelation<Medium, Account>()
                .insertRelations(
                        datasetPath + "/" + "snapshot/MediumSignInAccount.csv",
                        Medium.class,
                        Account.class,
                        mediums,
                        accounts,
                        "setSignIn");
        new CreateRelation<Person, Loan>()
                .insertRelations(
                        datasetPath + "/" + "snapshot/PersonApplyLoan.csv",
                        Person.class,
                        Loan.class,
                        persons,
                        loans,
                        "setApply");
        new CreateRelation<Person, Person>()
                .insertRelations(
                        datasetPath + "/" + "snapshot/PersonGuaranteePerson.csv",
                        Person.class,
                        Person.class,
                        persons,
                        persons,
                        "setGuarantee");
        new CreateRelation<Person, Company>()
                .insertRelations(
                        datasetPath + "/" + "snapshot/PersonInvestCompany.csv",
                        Person.class,
                        Company.class,
                        persons,
                        companys,
                        "setInvest");
        new CreateRelation<Person, Account>()
                .insertRelations(
                        datasetPath + "/" + "snapshot/PersonOwnAccount.csv",
                        Person.class,
                        Account.class,
                        persons,
                        accounts,
                        "setOwn");

        for (Person person : persons.values()) {
            estore.insert(person);
        }
        for (Account account : accounts.values()) {
            estore.insert(account);
        }
        for (Company company : companys.values()) {
            estore.insert(company);
        }
        for (Loan loan : loans.values()) {
            estore.insert(loan);
        }
        for (Medium medium : mediums.values()) {
            estore.insert(medium);
        }
    }

    private void insertPersons(String filePath, HashMap<Long, Person> persons) {
        try {
            Reader reader = new FileReader(filePath);
            CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
            CSVParser csvParser = new CSVParser(reader, csvFormat);

            for (CSVRecord csvRecord : csvParser) {
                long personId = Long.parseLong(csvRecord.get("personId"));
                String personName = csvRecord.get("personName");
                boolean isBlocked = Boolean.parseBoolean(csvRecord.get("isBlocked"));
                String createTime = csvRecord.get("createTime");
                String gender = csvRecord.get("gender");
                String birthday = csvRecord.get("birthday");
                String country = csvRecord.get("country");
                String city = csvRecord.get("city");

                persons.put(
                        personId,
                        (new Person(
                                personId,
                                personName,
                                isBlocked,
                                createTime,
                                gender,
                                birthday,
                                country,
                                city)));
            }
            csvParser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertAccounts(String filePath, HashMap<Long, Account> accounts) {
        try {
            Reader reader = new FileReader(filePath);
            CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
            CSVParser csvParser = new CSVParser(reader, csvFormat);

            for (CSVRecord csvRecord : csvParser) {
                long accountId = Long.parseLong(csvRecord.get("accountId"));
                String createTime = csvRecord.get("createTime");
                boolean isBlocked = Boolean.parseBoolean(csvRecord.get("isBlocked"));
                String accountType = csvRecord.get("accoutType");
                String nickname = csvRecord.get("nickname");
                String phonenum = csvRecord.get("phonenum");
                String email = csvRecord.get("email");
                String freqLoginType = csvRecord.get("freqLoginType");
                long lastLoginTime = Long.parseLong(csvRecord.get("lastLoginTime"));
                String accountLevel = csvRecord.get("accountLevel");

                accounts.put(
                        accountId,
                        new Account(
                                accountId,
                                createTime,
                                isBlocked,
                                accountType,
                                nickname,
                                phonenum,
                                email,
                                freqLoginType,
                                lastLoginTime,
                                accountLevel));
            }
            csvParser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertCompanys(String filePath, HashMap<Long, Company> companys) {
        try {
            Reader reader = new FileReader(filePath);
            CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
            CSVParser csvParser = new CSVParser(reader, csvFormat);

            for (CSVRecord csvRecord : csvParser) {
                long companyId = Long.parseLong(csvRecord.get("companyId"));
                String companyName = csvRecord.get("companyName");
                boolean isBlocked = Boolean.parseBoolean(csvRecord.get("isBlocked"));
                String createTime = csvRecord.get("createTime");
                String country = csvRecord.get("country");
                String city = csvRecord.get("city");
                String business = csvRecord.get("business");
                String description = csvRecord.get("description");
                String url = csvRecord.get("url");

                companys.put(
                        companyId,
                        new Company(
                                companyId,
                                companyName,
                                isBlocked,
                                createTime,
                                country,
                                city,
                                business,
                                description,
                                url));
            }
            csvParser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertLoans(String filePath, HashMap<Long, Loan> loans) {
        try {
            Reader reader = new FileReader(filePath);
            CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
            CSVParser csvParser = new CSVParser(reader, csvFormat);

            for (CSVRecord csvRecord : csvParser) {
                long loanId = Long.parseLong(csvRecord.get("loanId"));
                double loanAmount = Double.parseDouble(csvRecord.get("loanAmount"));
                double balance = Double.parseDouble(csvRecord.get("balance"));
                String createTime = csvRecord.get("createTime");
                String loanUsage = csvRecord.get("loanUsage");
                double interestRate = Double.parseDouble(csvRecord.get("interestRate"));

                loans.put(
                        loanId,
                        new Loan(loanId, loanAmount, balance, createTime, loanUsage, interestRate));
            }
            csvParser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertMediums(String filePath, HashMap<Long, Medium> mediums) {
        try {
            Reader reader = new FileReader(filePath);
            CSVFormat csvFormat = CSVFormat.newFormat('|').withFirstRecordAsHeader();
            CSVParser csvParser = new CSVParser(reader, csvFormat);

            for (CSVRecord csvRecord : csvParser) {
                long mediumId = Long.parseLong(csvRecord.get("mediumId"));
                String mediumType = csvRecord.get("mediumType");
                boolean isBlocked = Boolean.parseBoolean(csvRecord.get("isBlocked"));
                String createTime = csvRecord.get("createTime");
                long lastLoginTime = Long.parseLong(csvRecord.get("lastLoginTime"));
                String riskLevel = csvRecord.get("riskLevel");

                mediums.put(
                        mediumId,
                        new Medium(
                                mediumId,
                                mediumType,
                                isBlocked,
                                createTime,
                                lastLoginTime,
                                riskLevel));
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
                Method setRelationMethod =
                        referrerClass.getMethod(setRelationMethodName, Object[].class);

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
                            new Object[] {item.getValue().toArray(new Object[0])});
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
