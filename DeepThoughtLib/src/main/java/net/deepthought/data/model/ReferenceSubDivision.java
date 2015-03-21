package net.deepthought.data.model;

import net.deepthought.data.model.enums.ReferenceIndicationUnit;
import net.deepthought.data.model.enums.ReferenceSubDivisionCategory;
import net.deepthought.data.persistence.db.TableConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;

/**
 * Created by ganymed on 21/01/15.
 */
@Entity(name = TableConfig.ReferenceSubDivisionTableName)
@DiscriminatorValue(TableConfig.ReferenceSubDivisionDiscriminatorValue)
public class ReferenceSubDivision extends ReferenceBase implements Comparable<ReferenceSubDivision> {

  private static final long serialVersionUID = -967783983570745569L;


  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = TableConfig.ReferenceSubDivisionCategoryJoinColumnName)
  protected ReferenceSubDivisionCategory category;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = TableConfig.ReferenceSubDivisionReferenceJoinColumnName)
  protected Reference reference;

  @Column(name = TableConfig.ReferenceSubDivisionOrderColumnName)
  protected int subDivisionOrder; // Chapter number, number of Article, ...

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = TableConfig.ReferenceSubDivisionParentSubDivisionJoinColumnName)
  protected ReferenceSubDivision parentSubDivision;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentSubDivision"/*, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }*/)
  @OrderBy("subDivisionOrder ASC") // TODO: subDivisions afterwards don't get sorted
  protected Collection<ReferenceSubDivision> subDivisions = new ArrayList<>();

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "referenceSubDivision")
  protected Collection<Entry> entries = new HashSet<>();


  @Column(name = TableConfig.ReferenceSubDivisionLengthColumnName)
  protected String length;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = TableConfig.ReferenceSubDivisionLengthUnitJoinColumnName)
  protected ReferenceIndicationUnit lengthUnit;



  public ReferenceSubDivision() {

  }

  public ReferenceSubDivision(String title) {
    super(title);
  }

  public ReferenceSubDivision(String title, String subTitle) {
    super(title, subTitle);
  }



  public ReferenceSubDivisionCategory getCategory() {
    return category;
  }

  public void setCategory(ReferenceSubDivisionCategory category) {if(this.category != null)
    this.category.removeReferenceSubDivision(this);

    Object previousValue = this.category;
    this.category = category;

    if(this.category != null)
      this.category.addReferenceSubDivision(this);

    callPropertyChangedListeners(TableConfig.ReferenceCategoryJoinColumnName, previousValue, category);
  }

  public Collection<Entry> getEntries() {
    return entries;
  }

  protected boolean addEntry(Entry entry) {
    if(entries.add(entry)) {
      callEntityAddedListeners(entries, entry);
      return true;
    }

    return false;
  }

  protected boolean removeEntry(Entry entry) {
    if(entries.remove(entry)) {
      callEntityRemovedListeners(entries, entry);
      return true;
    }

    return false;
  }

  public Reference getReference() {
    return reference;
  }

  protected void setReference(Reference reference) {
    Object previousValue = this.reference;
    this.reference = reference;
    callPropertyChangedListeners(TableConfig.ReferenceSubDivisionReferenceJoinColumnName, previousValue, reference);
  }

  public ReferenceSubDivision getParentSubDivision() {
    return parentSubDivision;
  }

  public boolean hasSubDivisions() {
    return getSubDivisions().size() > 0;
  }

  public Collection<ReferenceSubDivision> getSubDivisions() {
    return subDivisions;
  }

  public boolean addSubDivision(ReferenceSubDivision subDivision) {
    subDivision.parentSubDivision = this;
    subDivision.subDivisionOrder = subDivisions.size();
    if(subDivision.reference == null && this.reference != null)
      subDivision.reference = this.reference;

    boolean result = subDivisions.add(subDivision);
    if(result) {
      callEntityAddedListeners(subDivisions, subDivision);
    }

    return result;
  }

  public boolean removeSubDivision(ReferenceSubDivision subDivision) {
    int removeSubDivisionIndex = subDivision.getSubDivisionOrder();

    boolean result = subDivisions.remove(subDivision);
    if(result) {
      subDivision.parentSubDivision = null;

      for(Entry entry : new ArrayList<>(subDivision.getEntries()))
        entry.setReferenceSubDivision(null);

      for(ReferenceSubDivision subDivisionEnum : subDivisions) {
        if(subDivisionEnum.getSubDivisionOrder() >= removeSubDivisionIndex)
          subDivisionEnum.setSubDivisionOrder(subDivisionEnum.getSubDivisionOrder() - 1);
      }

      callEntityRemovedListeners(subDivisions, subDivision);
    }

    return result;
  }

  public int getSubDivisionOrder() {
    return subDivisionOrder;
  }

  public void setSubDivisionOrder(int subDivisionOrder) {
    Object previousValue = this.subDivisionOrder;
    this.subDivisionOrder = subDivisionOrder;
    callPropertyChangedListeners(TableConfig.ReferenceSubDivisionOrderColumnName, previousValue, subDivisionOrder);
  }

  public String getLength() {
    return length;
  }

  public void setLength(String length) {
    Object previousValue = this.length;
    this.length = length;
    callPropertyChangedListeners(TableConfig.ReferenceSubDivisionLengthColumnName, previousValue, length);
  }

  public ReferenceIndicationUnit getLengthUnit() {
    return lengthUnit;
  }

  public void setLengthUnit(ReferenceIndicationUnit lengthUnit) {
    Object previousValue = this.lengthUnit;
    this.lengthUnit = lengthUnit;
    callPropertyChangedListeners(TableConfig.ReferenceSubDivisionLengthUnitJoinColumnName, previousValue, lengthUnit);
  }


  @Override
  public String toString() {
    return "ReferenceSubDivision " + getTextRepresentation();
  }

  @Override
  public int compareTo(ReferenceSubDivision other) {
    if(other == null)
      return 1;

    return ((Integer) getSubDivisionOrder()).compareTo(other.getSubDivisionOrder());
  }
}
