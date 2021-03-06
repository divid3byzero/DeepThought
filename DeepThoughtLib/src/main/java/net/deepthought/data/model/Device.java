package net.deepthought.data.model;

import net.deepthought.Application;
import net.deepthought.data.persistence.db.TableConfig;
import net.deepthought.data.persistence.db.UserDataEntity;
import net.deepthought.util.Localization;
import net.deepthought.util.OsHelper;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * Created by ganymed on 09/12/14.
 */
@Entity(name = TableConfig.DeviceTableName)
public class Device extends UserDataEntity {

  private static final long serialVersionUID = 7190723756152328858L;


  @Column(name = TableConfig.DeviceUniversallyUniqueIdColumnName)
  protected String universallyUniqueId = "";

  @Column(name = TableConfig.DeviceNameColumnName)
  protected String name = "";

  @Column(name = TableConfig.DeviceDescriptionColumnName)
  protected String description = "";

  @Column(name = TableConfig.DevicePlatformColumnName)
  protected String platform = "";

  @Column(name = TableConfig.DeviceOsVersionColumnName)
  protected String osVersion = "";

  @Column(name = TableConfig.DevicePlatformArchitectureColumnName)
  protected String platformArchitecture = "";

  @Column(name = TableConfig.DeviceLastKnownIpColumnName)
  protected String lastKnownIpAddress = "";

////  @JsonIgnore
//  @ManyToOne(fetch = FetchType.EAGER)
//  @JoinColumn(name = TableConfig.DeviceOwnerJoinColumnName)
//  protected User deviceOwner;

  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "devices") // TODO: has cascade also to be set to { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }
  protected Set<User> users = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "devices")
  protected Set<Group> groups = new HashSet<>();

  @Column(name = TableConfig.DeviceIconColumnName)
  @Lob
  protected byte[] deviceIcon = null;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = TableConfig.DeviceDeepThoughtApplicationJoinColumnName)
  protected DeepThoughtApplication application;


  protected Device() {

  }

  public Device(String universallyUniqueId, String name, String platform) {
    this.universallyUniqueId = universallyUniqueId;
    this.name = name;
    this.platform = platform;
  }

  public Device(String universallyUniqueId, String name, String platform, String osVersion) {
    this(universallyUniqueId, name, platform);
    this.osVersion = osVersion;
  }

  public Device(String universallyUniqueId, String name, String platform, String osVersion, String platformArchitecture) {
    this(universallyUniqueId, name, platform, osVersion);
    this.platformArchitecture = platformArchitecture;
  }


  public String getUniversallyUniqueId() {
    return universallyUniqueId;
  }

  protected void setUniversallyUniqueId(String universallyUniqueId) {
    Object previousValue = this.universallyUniqueId;
    this.universallyUniqueId = universallyUniqueId;
    callPropertyChangedListeners(TableConfig.DeviceUniversallyUniqueIdColumnName, previousValue, universallyUniqueId);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    Object previousValue = this.name;
    this.name = name;
    callPropertyChangedListeners(TableConfig.DeviceNameColumnName, previousValue, name);
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    Object previousValue = this.description;
    this.description = description;
    callPropertyChangedListeners(TableConfig.DeviceDescriptionColumnName, previousValue, description);
  }

  public String getPlatform() {
    return platform;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }

  public String getPlatformArchitecture() {
    return platformArchitecture;
  }

  public void setPlatformArchitecture(String platformArchitecture) {
    this.platformArchitecture = platformArchitecture;
  }

  public String getOsVersion() {
    return osVersion;
  }

  public void setOsVersion(String osVersion) {
    Object previousValue = this.osVersion;
    this.osVersion = osVersion;
    callPropertyChangedListeners(TableConfig.DeviceOsVersionColumnName, previousValue, osVersion);
  }

  public String getLastKnownIpAddress() {
    return lastKnownIpAddress;
  }

  public void setLastKnownIpAddress(String lastKnownIpAddress) {
    Object previousValue = this.lastKnownIpAddress;
    this.lastKnownIpAddress = lastKnownIpAddress;
    callPropertyChangedListeners(TableConfig.DeviceLastKnownIpColumnName, previousValue, lastKnownIpAddress);
  }

  public Set<User> getUsers() {
    return users;
  }

  public boolean addUser(User user) {
    if(users.contains(user) == false) {
      if(users.add(user)) {
        user.addDevice(this);

        callEntityAddedListeners(users, user);
        return true;
      }
    }

    return false;
  }

  public boolean removeUser(User user) {
    if(users.contains(user) == true) {
      if(users.remove(user)) {
        user.removeDevice(this);

        callEntityRemovedListeners(users, user);
        return true;
      }
    }

    return false;
  }

  public Set<Group> getGroups() {
    return groups;
  }

  public boolean addGroup(Group group) {
    if(groups.contains(group) == false) {
      if(groups.add(group)) {
        group.addDevice(this);

        callEntityAddedListeners(groups, group);
        return true;
      }
    }

    return false;
  }

  public boolean removeGroup(Group group) {
    if(groups.contains(group) == true) {
      if(groups.remove(group)) {
        group.removeDevice(this);

        callEntityRemovedListeners(groups, group);
        return true;
      }
    }

    return false;
  }

  public byte[] getDeviceIcon() {
    return deviceIcon;
  }

  public void setDeviceIcon(byte[] deviceIcon) {
    this.deviceIcon = deviceIcon;
  }

  public DeepThoughtApplication getApplication() {
    return application;
  }


  @Override
  @Transient
  public String getTextRepresentation() {
    return "Device " + getName();
  }

  @Override
  public String toString() {
    return getTextRepresentation();
  }


  public static Boolean isRunningOnAndroid() {
    try {
      Class.forName("android.app.Activity");
      return true;
    } catch(Exception ex) { }

    return false;
  }


  public static Device createUserDefaultDevice(User user) {
    String universallyUniqueId = UUID.randomUUID().toString();
    String platform = Application.getPlatformConfiguration().getPlatformName();
    if(OsHelper.isRunningOnAndroid())
      platform = "Android";

    Device userDefaultDevice = new Device(universallyUniqueId, Localization.getLocalizedString("users.default.device.name", user.getUserName(), platform),
        platform, Application.getPlatformConfiguration().getOsVersionString(), System.getProperty("os.arch"));

//    , System.getProperty("os.arch")
//    userDefaultDevice.setUserRegion(System.getProperty("user.country"));
//    if(userDefaultDevice.getUserRegion() == null)
//      userDefaultDevice.setUserRegion(System.getProperty("user.region"));
//    userDefaultDevice.setUserLanguage(System.getProperty("user.language"));
//    userDefaultDevice.setUserTimezone(System.getProperty("user.timezone"));
//    userDefaultDevice.setJavaRuntimeVersion(System.getProperty("java.runtime.version"));
//    userDefaultDevice.setJavaClassVersion(System.getProperty("java.class.version"));
//    userDefaultDevice.setJavaSpecificationVersion(System.getProperty("java.specification.version"));
//    userDefaultDevice.setJavaVirtualMachineVersion(System.getProperty("java.vm.version"));

    return userDefaultDevice;
  }

}
