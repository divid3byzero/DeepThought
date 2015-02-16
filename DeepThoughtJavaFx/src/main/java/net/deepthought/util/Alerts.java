package net.deepthought.util;

import net.deepthought.Application;
import net.deepthought.data.model.Category;
import net.deepthought.data.model.DeepThought;
import net.deepthought.data.model.Person;
import net.deepthought.data.model.Tag;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;

/**
 * Created by ganymed on 01/02/15.
 */
public class Alerts {

  public static void deleteCategoryWithUserConfirmationIfHasSubCategoriesOrEntries(Category category) {
    if(category.hasSubCategories() || category.hasEntries()) {
      Boolean deleteCategory = Alerts.showConfirmDeleteCategoryWithSubCategoriesOrEntries(category);
      if(deleteCategory)
        category.getParentCategory().removeSubCategory(category);
    }
    else
      category.getParentCategory().removeSubCategory(category);
  }

  public static boolean showConfirmDeleteCategoryWithSubCategoriesOrEntries(Category category) {
    Action response = org.controlsfx.dialog.Dialogs.create()
        .title(Localization.getLocalizedStringForResourceKey("confirm.delete"))
        .message(Localization.getLocalizedStringForResourceKey("alert.message.category.contains.entries.or.sub.categories", category.getName(), category.getName()))
        .actions(Dialog.ACTION_YES, Dialog.ACTION_NO)
        .showConfirm();

    return Dialog.ACTION_YES.equals(response);
  }

  public static void deleteTagWithUserConfirmationIfIsSetOnEntries(Tag tag) {
    deleteTagWithUserConfirmationIfIsSetOnEntries(Application.getDeepThought(), tag);
  }

  public static void deleteTagWithUserConfirmationIfIsSetOnEntries(DeepThought deepThought, Tag tag) {
    if(tag.hasEntries()) {
      boolean confirmDeleteTag = showConfirmDeleteTagWithEntriesAlert(tag);
      if(confirmDeleteTag)
        deepThought.removeTag(tag);
    }
    else
      deepThought.removeTag(tag);
  }

  public static boolean showConfirmDeleteTagWithEntriesAlert(Tag tag) {
    Action response = org.controlsfx.dialog.Dialogs.create()
        .title(Localization.getLocalizedStringForResourceKey("confirm.delete"))
        .message(Localization.getLocalizedStringForResourceKey("alert.message.tag.is.set.on.entries", tag.getName(), tag.getEntries().size()))
        .actions(Dialog.ACTION_YES, Dialog.ACTION_NO)
        .showConfirm();

    return Dialog.ACTION_YES.equals(response);
  }

  public static void deletePersonWithUserConfirmationIfIsSetOnEntries(Person person) {
    deletePersonWithUserConfirmationIfIsSetOnEntries(Application.getDeepThought(), person);
  }

  public static void deletePersonWithUserConfirmationIfIsSetOnEntries(DeepThought deepThought, Person person) {
    if(person.isSetOnEntries()) {
      boolean confirmDeleteTag = showConfirmDeletePersonWithEntriesAlert(person);
      if(confirmDeleteTag)
        deepThought.removePerson(person);
    }
    else
      deepThought.removePerson(person);
  }

  public static boolean showConfirmDeletePersonWithEntriesAlert(Person person) {
    Action response = org.controlsfx.dialog.Dialogs.create()
        .title(Localization.getLocalizedStringForResourceKey("confirm.delete"))
        .message(Localization.getLocalizedStringForResourceKey("alert.message.person.is.set.on.entries", person.getNameRepresentation(), person.getAssociatedEntries().size()))
        .actions(Dialog.ACTION_YES, Dialog.ACTION_NO)
        .showConfirm();

    return Dialog.ACTION_YES.equals(response);
  }
}
