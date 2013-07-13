package net.nctucs.lazchi.marco79423.ExpenseBook;

class Globals
{
	public static final String LOG_TAG = "累死雞記帳";

	public static final String DATABASE_NAME = "accountbook.db";
	public static final int DATABASE_VERSION = 3;

	public static final String DROPBOX_KEY = "7hrz989zzhcyr9t";
	public static final String DROPBOX_SECRET = "zcyop6imgp7oq5i";

	public static final String DATE_FORMAT = "yyyy/MM/dd";

	public static class Expense
	{
		public static String ID = "_id";
		public static String PICTURE_BYTES = "picture_bytes";
		public static String SPEND_STRING = "spend_string";
		public static String DATE_STRING = "date_string";
		public static String CATEGORY = "category";
		public static String NOTE = "note";
	}

	public static class ExpenseTable
	{
		public static final String TABLE = "expenses";

		public static final String ID = Expense.ID;
		public static final String PICTURE_BYTES = Expense.PICTURE_BYTES;
		public static final String SPEND_STRING = Expense.SPEND_STRING;
		public static final String DATE_STRING = Expense.DATE_STRING;
		public static final String CATEGORY_ID = "category_id";
		public static final String NOTE = Expense.NOTE;

		public static final String CREATE_TABLE =
			"CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
			ID + " INTEGER NOT NULL PRIMARY KEY," +
			PICTURE_BYTES + " BLOB," +
			SPEND_STRING + " TEXT," +
			DATE_STRING + " DATE," +
			CATEGORY_ID + " NUMERIC," +
			NOTE + " TEXT);";

		static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE;
	}

	public static class CategoryTable
	{
		public static final String TABLE = "categories";

		public static final String ID = "_id";
		public static final String CATEGORY = Expense.CATEGORY;
		public static final String ORDER_ID = "order_id";

		public static final String CREATE_TABLE =
			"CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
			ID + " INTEGER NOT NULL PRIMARY KEY," +
			CATEGORY + " TEXT," +
			ORDER_ID + " NUMERIC);";
		public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE;
	}

	public static class InfoTable
	{
		public static final String TABLE = "info";

		public static final String ID = "_id";
		public static final String APP_VERSION = "app_version";

		static final String CREATE_TABLE =
			"CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
			ID + " _id INTEGER NOT NULL PRIMARY KEY," +
			APP_VERSION + " TEXT);";
		static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE;
	}
}
