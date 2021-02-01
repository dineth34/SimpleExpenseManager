package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;



import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.Database.DbHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private DbHandler dbHandler;
    private DateFormat DateFormat;

    public PersistentTransactionDAO(DbHandler dbHandler) {
        this.dbHandler = dbHandler;
        this.DateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db= dbHandler.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHandler.get_Transaction_Date(), this.DateFormat.format(date));
        contentValues.put(dbHandler.get_Transaction_AccountNo(), accountNo);
        contentValues.put(dbHandler.get_Transaction_Type(), expenseType.toString());
        contentValues.put(dbHandler.get_Transaction_Amount(), amount);

        //add a new row to the transaction table
        db.insert(dbHandler.get_Transaction_Table(), null, contentValues);

    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase db = dbHandler.getReadableDatabase();

        //query to get all the transactions ordered by date where newest transaction is at top
        Cursor cursor = db.rawQuery(
                "select * from" + dbHandler.get_Transaction_Table() + " order by " + dbHandler.get_Transaction_Date() + " desc ",
                null
        );

        ArrayList<Transaction> Transactions_List = new ArrayList<>();

        //loop and add transactions to the list creating transaction objects
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            try {

                Transaction transaction = new Transaction(
                        this.DateFormat.parse(cursor.getString(1)),
                        cursor.getString(2),
                        ExpenseType.valueOf(cursor.getString(3).toUpperCase()),
                        cursor.getDouble(4)
                );

                Transactions_List.add(transaction);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return Transactions_List;

    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {

        SQLiteDatabase db = dbHandler.getReadableDatabase();

        //select limited number of rows from transaction table
        Cursor cursor = db.rawQuery(
                " select * from " + dbHandler.get_Transaction_Table() + " order by " + dbHandler.get_Transaction_Date() + " desc " +
                        " LIMIT ?;"
                , new String[]{Integer.toString(limit)}
        );


        ArrayList<Transaction> Transactions_List = new ArrayList<>();

        //loop and add transactions to the list while creating transaction objects
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            try {

                Transaction transaction = new Transaction(
                        this.DateFormat.parse(cursor.getString(1)),
                        cursor.getString(2),
                        ExpenseType.valueOf(cursor.getString(3).toUpperCase()),
                        cursor.getDouble(4)
                );

                Transactions_List.add(transaction);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return Transactions_List;

    }
}