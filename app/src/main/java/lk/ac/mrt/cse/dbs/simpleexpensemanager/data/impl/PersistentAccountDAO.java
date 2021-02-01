package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.Database.DbHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private DbHandler dbHandler;

    public PersistentAccountDAO(DbHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase db = dbHandler.getReadableDatabase();

        //query to get all account numbers from account table
        Cursor cursor = db.rawQuery(
                "SELECT " + dbHandler.get_Account_AccountNo() + " FROM " + dbHandler.get_Account_Table(),
                null
        );

        ArrayList<String> accountNumbersList = new ArrayList<>();

        //loop through the results and add to the list
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            accountNumbersList.add(cursor.getString(0));
        }

        cursor.close();
        return accountNumbersList;
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase db = dbHandler.getReadableDatabase();

        //query to select all rows of account table
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + dbHandler.get_Account_Table(),
                null
        );

        ArrayList<Account> accountsList = new ArrayList<>();

        //loop through the results and create account objects and add to list
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Account account = new Account(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3)
            );
            accountsList.add(account);
        }

        cursor.close();
        return accountsList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = dbHandler.getReadableDatabase();

        //query to get the row from the account table with relevant account number
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + dbHandler.get_Account_Table() + " WHERE " + dbHandler.get_Account_AccountNo() + "=?;"
                , new String[]{accountNo});

        //if a result exist create an account object else throw error
        Account account;
        if (cursor != null && cursor.moveToFirst()) {
            account = new Account(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3)
            );
        } else {
            throw new InvalidAccountException("The Account "+accountNo+" is Invalid");
        }
        cursor.close();
        return account;
    }

    @Override
    public void addAccount(Account account){

        //check if an already an account with this no exist
        Account alreadyExistingAccount = null;
        try {
            alreadyExistingAccount = getAccount(account.getAccountNo());
        } catch (InvalidAccountException e) {
            e.printStackTrace();
        }
        if (alreadyExistingAccount!=null){
            System.out.println("Account already exists.");
            return;
        }

        SQLiteDatabase db = dbHandler.getWritableDatabase();

        ContentValues contentvalues = new ContentValues();
        contentvalues.put(dbHandler.get_Account_AccountNo(), account.getAccountNo());
        contentvalues.put(dbHandler.get_Account_BankName(), account.getBankName());
        contentvalues.put(dbHandler.get_Account_HolderName(), account.getAccountHolderName());
        contentvalues.put(dbHandler.get_Account_Balance(), account.getBalance());

        //insert new row to account table
        db.insert(dbHandler.get_Account_Table(), null, contentvalues);
        db.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = dbHandler.getWritableDatabase();

        //query for the account to check whether such an account exist
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + dbHandler.get_Account_Table() + " WHERE " + dbHandler.get_Account_AccountNo() + "=?;"
                , new String[]{accountNo}
        );

        //if such one exist remove it else throw exception
        if (cursor.moveToFirst()) {
            db.delete(
                    dbHandler.AccountTable,
                    dbHandler.get_Account_AccountNo() + " = ?",
                    new String[]{accountNo}
            );
        } else {
            throw new InvalidAccountException("The Account "+accountNo+" is Invalid!");
        }
        cursor.close();

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = dbHandler.getWritableDatabase();

        //if there's a account is not specified to update
        if (accountNo==null) {
            throw new InvalidAccountException("Account is not specified!.");
        }

        //get the account related using getAccount method
        Account account = this.getAccount(accountNo);

        //if such account exists
        if (account != null) {
            double updatedBalance;

            //update the balance of account according to the type of transaction
            if (expenseType == ExpenseType.INCOME) {
                updatedBalance = account.getBalance() + amount;
            } else if (expenseType == ExpenseType.EXPENSE) {
                updatedBalance = account.getBalance() - amount;
            } else {
                throw new InvalidAccountException("Invalid Expense Type!");
            }

            //if the account does not have enough balance throw error
            if (updatedBalance < 0){
                throw  new InvalidAccountException("Balance of " + account.getBalance() + " is insufficient for the transaction!");
            }

            // if ok query to update the balance in the account table
            db.execSQL(
                    "UPDATE " + dbHandler.get_Account_Table() +
                            " SET " + dbHandler.get_Account_Balance() + " = ?" +
                            " WHERE " + dbHandler.get_Account_AccountNo()  + " = ?",
                    new String[]{Double.toString(updatedBalance), accountNo});

        } else { //in order to throw an error if such account does not exists
            throw new InvalidAccountException("The Account "+accountNo+" is Invalid!");
        }



    }
}