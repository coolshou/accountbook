package net.nctucs.lazchi.marco79423.accountbook;

public class CategoryData 
{
	public static final String ID = "_id";
	public static final String CATEGORY = "category";
	public static final String ORDER_ID = "order_id";
	
	public static final String[] ALL_FIELDS = { ID, CATEGORY, ORDER_ID };
	
	private long _id;
	private String _category;
	private long _orderId;
	
	public CategoryData(long id, String category, long orderId) 
	{
		_id = id;
		_category = category;
		_orderId = orderId;
	}
	
	public long getId()
	{
		return _id;
	}
	
	public String getCategory()
	{
		return _category;
	}
	
	public long getOrderId()
	{
		return _orderId;
	}
}
