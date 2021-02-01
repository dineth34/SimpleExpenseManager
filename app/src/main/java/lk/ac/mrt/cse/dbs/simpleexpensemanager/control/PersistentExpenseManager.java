package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.Database.DbHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

public class PersistentExpenseManager extends ExpenseManager {
    private DbHandler dbHandler;

    public PersistentExpenseManager(Context context){
        this.dbHandler = new DbHandler(context);
        setup();
    }
    @Override
    public void setup(){
        TransactionDAO persistentTransactionDAO = new PersistentTransactionDAO(dbHandler);
        setTransactionsDAO(persistentTransactionDAO);

        AccountDAO persistentAccountDAO = new PersistentAccountDAO(dbHandler);
        setAccountsDAO(persistentAccountDAO);

        // sample dummy data
        Account dummy_Account1 = new Account("34567A", "Yoda Bank", "Dineth Wijesooriya", 100000.0);
        Account dummy_Account2 = new Account("45612K", "York Bank", "Mahesh Madushan", 6000.0);
        getAccountsDAO().addAccount(dummy_Account1);
        getAccountsDAO().addAccount(dummy_Account2);
    }
}