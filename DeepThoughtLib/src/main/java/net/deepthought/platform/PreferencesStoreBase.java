package net.deepthought.platform;

/**
 * Created by ganymed on 24/08/15.
 */
public abstract class PreferencesStoreBase implements IPreferencesStore {

  public final static String DataFolderKey = "data.folder";

  public final static String DatabaseDataModelVersionKey = "data.model.version";

  public final static String DataModelKey = "data.model";

  public final static String DefaultDataFolder = "data/";

  public final static int DefaultDatabaseDataModelVersion = 0;

  public final static String DefaultDataModelString = null;


  protected abstract String readValueFromStore(String key, String defaultValue);

  protected abstract void saveValueToStore(String key, String value);

  protected abstract boolean doesValueExist(String key);


  @Override
  public String getDataFolder() {
    return readStringValue(DataFolderKey, DefaultDataFolder);
  }

  @Override
  public void setDataFolder(String dataFolder) {
    saveStringValue(DataFolderKey, dataFolder);
  }

  @Override
  public int getDatabaseDataModelVersion() {
    return readIntValue(DatabaseDataModelVersionKey, DefaultDatabaseDataModelVersion);
  }

  @Override
  public void setDatabaseDataModelVersion(int newDataModelVersion) {
    saveIntValue(DatabaseDataModelVersionKey, newDataModelVersion);
  }

  @Override
  public String getDataModel() {
    return readStringValue(DataModelKey, DefaultDataModelString);
  }

  @Override
  public void setDataModel(String dataModel) {
    saveStringValue(DataModelKey, dataModel);
  }


  protected String readStringValue(String key, String defaultValue) {
    if(doesValueExist(key) == true) {
      return readValueFromStore(key, defaultValue);
    }
    else
      saveValueToStore(key, defaultValue);

    return defaultValue;
  }

  protected int readIntValue(String key, int defaultValue){
    if(doesValueExist(key) == true) {
      String value = readValueFromStore(key, Integer.toString(defaultValue));
      try { return Integer.parseInt(value); } catch(Exception ex) { }
    }
    else
      saveValueToStore(key, Integer.toString(defaultValue));

    return defaultValue;
  }

  protected void saveStringValue(String key, String value) {
    saveValueToStore(key, value);
  }

  protected void saveIntValue(String key, int value) {
    saveValueToStore(key, Integer.toString(value));
  }

}
