package net.deepthought.data.contentextractor;

import net.deepthought.Application;
import net.deepthought.data.html.ImageElementData;
import net.deepthought.data.model.Category;
import net.deepthought.data.model.DeepThought;
import net.deepthought.data.model.Entry;
import net.deepthought.data.model.FileLink;
import net.deepthought.data.model.Person;
import net.deepthought.data.model.Reference;
import net.deepthought.data.model.ReferenceSubDivision;
import net.deepthought.data.model.SeriesTitle;
import net.deepthought.data.model.Tag;
import net.deepthought.util.DeepThoughtError;
import net.deepthought.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganymed on 24/04/15.
 */
public class EntryCreationResult {

  protected Object source;

  protected boolean successful = false;

  protected DeepThoughtError error = null;

  protected Entry createdEntry = null;

  protected List<Tag> entryTags = new ArrayList<>();

  protected List<Category> entryCategories = new ArrayList<>();

  protected SeriesTitle extractedSeriesTitle = null;

  protected Reference extractedReference = null;

  protected ReferenceSubDivision extractedSubDivision = null;

  protected List<Person> extractedPersons = new ArrayList<>();

  protected List<FileLink> extractedAttachedFiles = new ArrayList<>();

  protected List<FileLink> extractedEmbeddedFiles = new ArrayList<>();


  public EntryCreationResult(Object source, DeepThoughtError error) {
    this.source = source;
    this.successful = false;
    this.error = error;
  }

  public EntryCreationResult(Object source, Entry createdEntry) {
    this.source = source;
    this.successful = createdEntry != null;
    this.createdEntry = createdEntry;
  }


  public Object getSource() {
    return source;
  }

  public boolean successful() {
    return successful;
  }

  public DeepThoughtError getError() {
    return error;
  }

  public Entry getCreatedEntry() {
    return createdEntry;
  }

  public boolean addTag(Tag tag) {
    return entryTags.add(tag);
  }

  public List<Tag> getTags() {
    return entryTags;
  }

  public boolean addCategory(Category category) {
    return entryCategories.add(category);
  }

  public List<Category> getCategories() {
    return entryCategories;
  }

  public boolean isAReferenceSet() {
    return extractedSeriesTitle != null || extractedReference != null || extractedSubDivision != null;
  }

  public SeriesTitle getSeriesTitle() {
    return extractedSeriesTitle;
  }

  public void setSeriesTitle(SeriesTitle extractedSeriesTitle) {
    this.extractedSeriesTitle = extractedSeriesTitle;
  }

  public Reference getReference() {
    return extractedReference;
  }

  public void setReference(Reference extractedReference) {
    this.extractedReference = extractedReference;
  }

  public ReferenceSubDivision getReferenceSubDivision() {
    return extractedSubDivision;
  }

  public void setReferenceSubDivision(ReferenceSubDivision extractedSubDivision) {
    this.extractedSubDivision = extractedSubDivision;
  }

  public boolean hasPersons() {
    return getPersons().size() > 0;
  }

  public List<Person> getPersons() {
    return extractedPersons;
  }

  public void setPersons(List<Person> extractedPersons) {
    this.extractedPersons = extractedPersons;
  }

  public List<FileLink> getAttachedFiles() {
    return extractedAttachedFiles;
  }

  public void setAttachedFiles(List<FileLink> attachedFiles) {
    this.extractedAttachedFiles = attachedFiles;
  }

  public List<FileLink> getEmbeddedFiles() {
    return extractedEmbeddedFiles;
  }

  public void setEmbeddedFiles(List<FileLink> embeddedFiles) {
    this.extractedEmbeddedFiles = embeddedFiles;
  }

  @Override
  public String toString() {
    String description = source + " Successful? " + successful + "; ";
    if(successful)
      description += createdEntry;
    else
      description += error;

    return description;
  }

  public void saveCreatedEntities() {
    DeepThought deepThought = Application.getDeepThought();
    deepThought.addEntry(createdEntry);

    for(Tag tag : entryTags) {
      if(tag.isPersisted() == false)
        deepThought.addTag(tag);
      createdEntry.addTag(tag);
    }

    for(Category category : entryCategories) {
      if(category.isPersisted() == false)
        deepThought.addCategory(category);
      createdEntry.addCategory(category);
    }

    for(Person person : extractedPersons) {
      if(person.isPersisted() == false)
        deepThought.addPerson(person);
      createdEntry.addPerson(person);
    }

    for(FileLink file : extractedAttachedFiles) {
      if(file.isPersisted() == false)
        deepThought.addFile(file);
      createdEntry.addAttachedFile(file);
    }

    for(FileLink file : extractedEmbeddedFiles) {
      if(file.isPersisted() == false)
        deepThought.addFile(file);
      createdEntry.addEmbeddedFile(file);
    }

    List<ImageElementData> abstractEmbeddedImages = Application.getHtmlHelper().extractAllImageElementsFromHtml(createdEntry.getAbstract());
    handleEmbeddedImages(abstractEmbeddedImages, createdEntry);
    List<ImageElementData> contentEmbeddedImages = Application.getHtmlHelper().extractAllImageElementsFromHtml(createdEntry.getContent());
    handleEmbeddedImages(contentEmbeddedImages, createdEntry);

    saveReferenceBases(deepThought);
  }

  protected void saveReferenceBases(DeepThought deepThought) {
    if(extractedSeriesTitle != null) {
      if(extractedSeriesTitle.isPersisted() == false)
        deepThought.addSeriesTitle(extractedSeriesTitle);
      createdEntry.setSeries(extractedSeriesTitle);
    }

    if(extractedReference != null) {
      if(extractedReference.isPersisted() == false)
        deepThought.addReference(extractedReference);
      extractedReference.setSeries(extractedSeriesTitle);
      createdEntry.setReference(extractedReference);
    }

    if(extractedSubDivision != null) {
      if(extractedSubDivision.isPersisted() == false)
        deepThought.addReferenceSubDivision(extractedSubDivision);
      extractedSubDivision.setReference(extractedReference);
      createdEntry.setReferenceSubDivision(extractedSubDivision);
    }
  }

  protected void handleEmbeddedImages(List<ImageElementData> embeddedImages, Entry entry) {
    for(ImageElementData imageData : embeddedImages) {
      if(imageData.getFileId() == null)
        aNewImageHasBeenEmbedded(imageData, entry);
      else {
        FileLink file = Application.getDeepThought().getFileById(imageData.getFileId());
        if (file != null && entry.containsEmbeddedFile(file) == false) {
          entry.addEmbeddedFile(file);
        }
      }
    }
  }

  protected void aNewImageHasBeenEmbedded(ImageElementData imageData, Entry entry) {
    FileLink newFile = imageData.createFile();
    if(StringUtils.isNullOrEmpty(newFile.getDescription()))
      newFile.setDescription(entry.getAbstractAsPlainText());

    if(Application.getDeepThought().addFile(newFile))
      entry.addEmbeddedFile(newFile);
  }

}
