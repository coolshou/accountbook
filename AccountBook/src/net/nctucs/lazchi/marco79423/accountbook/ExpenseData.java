package net.nctucs.lazchi.marco79423.accountbook;

public class ExpenseData 
{
	public static final String ID = "_id";
	public static final String PICTURE = "picture";
	public static final String SPEND = "spend";
	public static final String DATE = "date";
	public static final String CATEGORY_ID = "category_id";
	public static final String NOTE = "note";
	
	public static final String[] ALL_FIELDS = { ID, PICTURE, SPEND, DATE, CATEGORY_ID, NOTE };
	
	private long _id;
	private byte[] _picture;
	private long _spend;
	private String _date;
	private long _categoryId;
	private String _note;
	
	public ExpenseData(long id, byte[] picture, long spend, String date, long categoryId, String note) 
	{
		_id = id;
		_picture = picture;
		_spend = spend;
		_date = date;
		_categoryId = categoryId;
		_note = note;
	}
	
	public long getId()
	{
		return _id;
	}
	
	public byte[] getPicture()
	{
		return _picture;
	}
	
	public long getSpend()
	{
		return _spend;
	}
	
	public String getDate()
	{
		return _date;
	}
	
	public long getCategoryId()
	{
		return _categoryId;
	}
	
	public String getNote()
	{
		return _note;
	}
}
