package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHandler extends SQLiteOpenHelper {
    private final static String DbName = "180711F";
    private final static int DbVersion = 1;

    public final static String AccountTable = "account_table";
    public final static String TransactionTable = "transaction_table";

    //names related to columns of account table
    public final static String AccountAccountNo = "account_no";
    public final static String AccountBankName = "bank_name";
    public final static String AccountHolderName = "holder_name";
    public final static String AccountBalance = "balance";

    //column names related to transaction table
    public final static String TransactionId = "id";
    public final static String TransactionAccountNo = "account_no";
    public final static String TransactionType = "type";
    public final static String TransactionDate = "date";
    public final static String TransactionAmount = "amount";

    //the two types of transactions
    public final static String TypeExpense = "EXPENSE";
    public final static String TypeIncome = "INCOME";

    public DbHandler(Context context) {
        super(context, DbName, null, DbVersion);
    }

    public static int getDbVersion() {//getters of attributes
        return DbVersion;
    }

    public static String get_Account_Table() {
        return AccountTable;
    }

    public static String get_Transaction_Table() {
        return TransactionTable;
    }

    public static String get_Account_AccountNo() {
        return AccountAccountNo;
    }

    public static String get_Account_BankName() {
        return AccountBankName;
    }

    public static String get_Account_HolderName() {
        return AccountHolderName;
    }

    public static String get_Account_Balance() {
        return AccountBalance;
    }

    public static String get_Transaction_Id() {
        return TransactionId;
    }

    public static String get_Transaction_AccountNo() {
        return TransactionAccountNo;
    }

    public static String get_Transaction_Type() {
        return TransactionType;
    }

    public static String get_Transaction_Date() {
        return TransactionDate;
    }

    public static String get_Transaction_Amount() {
        return TransactionAmount;
    }

    public static String get_TypeExpense() {
        return TypeExpense;
    }

    public static String get_TypeIncome() {
        return TypeIncome;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //create statement for account table
        sqLiteDatabase.execSQL(
                "create table if not exists " + AccountTable + "(" +
                        AccountAccountNo + " text primary key," +
                        AccountBankName + " text not null," +
                        AccountHolderName + " text not null," +
                        AccountBalance + " real" +
                        ");"
        );

        //create statement for transaction table
        sqLiteDatabase.execSQL(
                "create table if not exists " + TransactionTable + "(" +
                        TransactionId + " integer primary key autoincrement," +
                        TransactionDate + " text not null," +
                        TransactionAccountNo + " text not null," +
                        TransactionType + " text not null," +
                        TransactionAmount + " Real not null," +
                        " foreign key (" + TransactionAccountNo + ") references "
                        + AccountTable + "(" + AccountAccountNo + ")," +
                        "check ("+ TransactionType +"==\""+ TypeExpense +"\" or "+ TransactionType +"==\""+ TypeIncome +"\")"+
                        ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + TransactionTable);
        sqLiteDatabase.execSQL("drop table if exists " + AccountTable);
        onCreate(sqLiteDatabase);
    }


}
