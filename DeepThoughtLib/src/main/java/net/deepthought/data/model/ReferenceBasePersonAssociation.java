package net.deepthought.data.model;

import net.deepthought.data.model.enums.PersonRole;
import net.deepthought.data.persistence.db.AssociationEntity;
import net.deepthought.data.persistence.db.TableConfig;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * Created by ganymed on 30/01/15.
 */
@Entity(name = TableConfig.ReferenceBasePersonAssociationTableName)
public class ReferenceBasePersonAssociation extends AssociationEntity {

  private static final long serialVersionUID = -1098887985449009796L;

  //  @Id
  @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
  @JoinColumn(name = TableConfig.ReferenceBasePersonAssociationReferenceBaseJoinColumnName)
  protected ReferenceBase referenceBase;

//  @Id
  @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
  @JoinColumn(name = TableConfig.ReferenceBasePersonAssociationPersonJoinColumnName)
  protected Person person;

//  @Id
  @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
  @JoinColumn(name = TableConfig.ReferenceBasePersonAssociationPersonRoleJoinColumnName)
  protected PersonRole role;

  @Column(name = TableConfig.ReferenceBasePersonAssociationPersonOrderColumnName)
  protected int personOrder = Integer.MAX_VALUE;


  protected ReferenceBasePersonAssociation() {

  }

  public ReferenceBasePersonAssociation(ReferenceBase referenceBase, Person person, PersonRole role) {
    this.referenceBase = referenceBase;
    this.person = person;
    this.role = role;
  }

  public ReferenceBasePersonAssociation(ReferenceBase referenceBase, Person person, PersonRole role, int personOrder) {
    this(referenceBase, person, role);
    this.personOrder = personOrder;
  }


  public ReferenceBase getReferenceBase() {
    return referenceBase;
  }

  public Person getPerson() {
    return person;
  }

  public PersonRole getRole() {
    return role;
  }

  public int getPersonOrder() {
    return personOrder;
  }

  public void setPersonOrder(int personOrder) {
    this.personOrder = personOrder;
  }


  //  @Override
//  public boolean equals(Object o) {
//    if (this == o) return true;
//    if (!(o instanceof EntryPersonRelation)) return false;
//
//    EntryPersonRelation that = (EntryPersonRelation) o;
//
//    if (!entry.equals(that.entry)) return false;
//    if (!person.equals(that.person)) return false;
//    if (!role.equals(that.role)) return false;
//
//    return true;
//  }
//
//  @Override
//  public int hashCode() {
//    int result = entry.hashCode();
//    result = 31 * result + person.hashCode();
//    result = 31 * result + role.hashCode();
//    return result;
//  }


  @Override
  @Transient
  public String getTextRepresentation() {
    return "ReferenceBasePersonAssociation: Person = " + person + ", Entry = " + referenceBase;
  }
}
