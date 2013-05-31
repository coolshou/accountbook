package net.nctucs.lazchi.marco79423.ExpenseBook;

public class Globals 
{
	public static final String DROPBOX_KEY = "7hrz989zzhcyr9t";
	public static final String DROPBOX_SECRET = "zcyop6imgp7oq5i";

	public static class ExpenseTable
	{
		public static final String TABLE = "expenses";

		public static final String ID = "_id";
		public static final String PICTURE = "picture";
		public static final String SPEND = "spend";
		public static final String DATE = "date";
		public static final String CATEGORY_ID = "category_id";
		public static final String NOTE = "note";

		public static final String CREATE_TABLE =
			"CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
			ID + " INTEGER NOT NULL PRIMARY KEY," +
			PICTURE + " BLOB," +
			SPEND + " NUMERIC," +
			DATE + " DATE," +
			CATEGORY_ID + " NUMERIC," +
			NOTE + " TEXT);";

		static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE;
	}

	public static class CategoryTable
	{
		public static final String TABLE = "categories";

		public static final String ID = "_id";
		public static final String CATEGORY = "category";
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
