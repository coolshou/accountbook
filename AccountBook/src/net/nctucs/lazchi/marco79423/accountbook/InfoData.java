package net.nctucs.lazchi.marco79423.accountbook;

public class InfoData 
{
	public static final String ID = "_id";
	public static final String APP_VERSION = "app_version";
	
	public static final String[] ALL_FIELDS = { ID, APP_VERSION };
	
	private long _id;
	private String _appVersion;
	
	public InfoData(long id, String appVersion) 
	{
		_id = id;
		_appVersion = appVersion;
	}
	
	public long getId()
	{
		return _id;
	}

	public String getAppVersion()
	{
		return _appVersion;
	}
}
