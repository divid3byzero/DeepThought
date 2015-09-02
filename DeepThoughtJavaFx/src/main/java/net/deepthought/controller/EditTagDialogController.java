package net.deepthought.controller;

import net.deepthought.Application;
import net.deepthought.controller.enums.DialogResult;
import net.deepthought.controller.enums.FieldWithUnsavedChanges;
import net.deepthought.controls.Constants;
import net.deepthought.controls.ContextHelpControl;
import net.deepthought.controls.FXUtils;
import net.deepthought.data.model.Tag;
import net.deepthought.data.model.listener.EntityListener;
import net.deepthought.data.persistence.db.BaseEntity;
import net.deepthought.util.Localization;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Created by ganymed on 31/12/14.
 */
public class EditTagDialogController extends ChildWindowsController implements Initializable {

  protected Tag tag = null;

  protected ObservableSet<FieldWithUnsavedChanges> fieldsWithUnsavedChanges = FXCollections.observableSet();


  @FXML
  protected BorderPane dialogPane;

  @FXML
  protected Button btnApply;

  @FXML
  protected ToggleButton tglbtnShowHideContextHelp;

  protected ContextHelpControl contextHelpControl;

  @FXML
  protected TextField txtfldName;
  @FXML
  protected TextField txtfldDescription;


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    btnApply.managedProperty().bind(btnApply.visibleProperty());

    setupFields();

    fieldsWithUnsavedChanges.addListener(new SetChangeListener<FieldWithUnsavedChanges>() {
      @Override
      public void onChanged(Change<? extends FieldWithUnsavedChanges> c) {
        btnApply.setDisable(fieldsWithUnsavedChanges.size() == 0);
      }
    });
  }

  protected void setupFields() {
    txtfldName.textProperty().addListener((observable, oldValue, newValue) -> fieldsWithUnsavedChanges.add(FieldWithUnsavedChanges.TagName));
    txtfldName.focusedProperty().addListener((observable, oldValue, newValue) -> fieldFocused("name"));

    txtfldDescription.textProperty().addListener((observable, oldValue, newValue) -> fieldsWithUnsavedChanges.add(FieldWithUnsavedChanges.TagDescription));
    txtfldDescription.focusedProperty().addListener((observable, oldValue, newValue) -> fieldFocused("description"));

    contextHelpControl = new ContextHelpControl("context.help.tag.");
    dialogPane.setRight(contextHelpControl);

    FXUtils.ensureNodeOnlyUsesSpaceIfVisible(contextHelpControl);
    contextHelpControl.visibleProperty().bind(tglbtnShowHideContextHelp.selectedProperty());

    tglbtnShowHideContextHelp.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    tglbtnShowHideContextHelp.setGraphic(new ImageView(Constants.ContextHelpIconPath));
  }

  protected void fieldFocused(String fieldName) {
    contextHelpControl.showContextHelpForResourceKey(fieldName);
  }


  public void setTagAndStage(Stage dialogStage, Tag tagToEdit) {
    setWindowStage(dialogStage);
    this.tag = tagToEdit;

    updateStageTitle();

    tagToEditSet(tagToEdit);
    fieldsWithUnsavedChanges.clear();
    tag.addEntityListener(tagListener);

    txtfldName.selectAll();
    txtfldName.requestFocus();
  }

  protected void tagToEditSet(Tag tag) {
    txtfldName.setText(tag.getName());
    txtfldDescription.setText(tag.getDescription());
  }

  public boolean hasUnsavedChanges() {
    return fieldsWithUnsavedChanges.size() > 0;
  }

  protected void updateStageTitle() {
    if(tag.isPersisted() == false)
      windowStage.setTitle(Localization.getLocalizedString("create.tag"));
    else
      windowStage.setTitle(Localization.getLocalizedString("edit.tag", tag.getTextRepresentation()));
  }


  @FXML
  public void handleButtonApplyAction(ActionEvent actionEvent) {
    saveEditedFields();
  }

  @FXML
  public void handleButtonCancelAction(ActionEvent actionEvent) {
    closeDialog(DialogResult.Cancel);
  }

  @FXML
  public void handleButtonOkAction(ActionEvent actionEvent) {
    saveEditedFields();
    closeDialog(DialogResult.Ok);
  }

  @Override
  protected void closeDialog() {
    if(tag != null)
      tag.removeEntityListener(tagListener);

    super.closeDialog();
  }

  protected void saveEditedFields() {
    if(fieldsWithUnsavedChanges.contains(FieldWithUnsavedChanges.TagName)) {
      tag.setName(txtfldName.getText());
      fieldsWithUnsavedChanges.remove(FieldWithUnsavedChanges.TagName);
    }

    if(fieldsWithUnsavedChanges.contains(FieldWithUnsavedChanges.TagDescription)) {
      tag.setDescription(txtfldDescription.getText());
      fieldsWithUnsavedChanges.remove(FieldWithUnsavedChanges.TagDescription);
    }

    if(tag.isPersisted() == false)
      Application.getDeepThought().addTag(tag);
  }

  @Override
  protected boolean askIfStageShouldBeClosed() {
    if(hasUnsavedChanges()) {
      Action response = Dialogs.create()
          .owner(windowStage)
          .title(Localization.getLocalizedString("alert.title.tag.contains.unsaved.changes"))
          .message(Localization.getLocalizedString("alert.message.tag.contains.unsaved.changes"))
          .actions(Dialog.ACTION_CANCEL, Dialog.ACTION_NO, Dialog.ACTION_YES)
          .showConfirm();

      if(response.equals(Dialog.ACTION_CANCEL))
        return false;
      else if(response.equals(Dialog.ACTION_YES)) {
        saveEditedFields();
      }
    }

    return true;
  }


  protected EntityListener tagListener = new EntityListener() {
    @Override
    public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

    }

    @Override
    public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {

    }

    @Override
    public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {

    }

    @Override
    public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {

    }
  };

}