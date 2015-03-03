package net.deepthought.controls.reference;

import net.deepthought.Application;
import net.deepthought.controller.ChildWindowsController;
import net.deepthought.controller.ChildWindowsControllerListener;
import net.deepthought.controller.Dialogs;
import net.deepthought.controller.enums.DialogResult;
import net.deepthought.controller.enums.FieldWithUnsavedChanges;
import net.deepthought.controls.BaseEntityListCell;
import net.deepthought.controls.FXUtils;
import net.deepthought.controls.NewOrEditButton;
import net.deepthought.controls.event.FieldChangedEvent;
import net.deepthought.controls.event.NewOrEditButtonMenuActionEvent;
import net.deepthought.data.listener.ApplicationListener;
import net.deepthought.data.model.DeepThought;
import net.deepthought.data.model.Entry;
import net.deepthought.data.model.Reference;
import net.deepthought.data.model.ReferenceSubDivision;
import net.deepthought.data.model.SeriesTitle;
import net.deepthought.data.model.enums.ReferenceIndicationUnit;
import net.deepthought.data.model.listener.EntityListener;
import net.deepthought.data.persistence.db.BaseEntity;
import net.deepthought.data.persistence.db.TableConfig;
import net.deepthought.util.DeepThoughtError;
import net.deepthought.util.Empty;
import net.deepthought.util.Localization;
import net.deepthought.util.StringUtils;

import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * Created by ganymed on 01/02/15.
 */
public class EntryReferenceControl extends HBox {

  private final static Logger log = LoggerFactory.getLogger(EntryReferenceControl.class);


  protected Entry entry = null;

  protected DeepThought deepThought = null;

  protected ObservableSet<FieldWithUnsavedChanges> fieldsWithUnsavedChanges = FXCollections.observableSet();

  protected ObservableList<SeriesTitle> comboBoxSeriesTitleItems;
  protected ObservableList<Reference> comboBoxReferenceItems;

  protected EventHandler<FieldChangedEvent> fieldChangedEvent;


  @FXML
  protected Pane paneSeriesTitleReferenceAndSubDivisionSettings;
  @FXML
  protected Pane paneSeriesTitle;
  @FXML
  protected ComboBox<SeriesTitle> cmbxSeriesTitle;
  @FXML
  protected NewOrEditButton btnNewOrEditSeriesTitle;
  @FXML
  protected Pane paneReference;
  @FXML
  protected ComboBox<Reference> cmbxReference;
  @FXML
  protected NewOrEditButton btnNewOrEditReference;
  @FXML
  protected Pane paneReferenceSubDivision;

  @FXML
  protected Pane paneReferenceIndicationSettings;
  @FXML
  protected Pane paneReferenceIndicationStartSettings;
  @FXML
  protected TextField txtfldReferenceIndicationStart;
  @FXML
  protected ComboBox<ReferenceIndicationUnit> cmbxReferenceIndicationStartUnit;
  @FXML
  protected NewOrEditButton btnNewOrEditReferenceIndicationStartUnit;
  @FXML
  protected Pane paneReferenceIndicationEndSettings;
  @FXML
  protected TextField txtfldReferenceIndicationEnd;
  @FXML
  protected ComboBox<ReferenceIndicationUnit> cmbxReferenceIndicationEndUnit;
  @FXML
  protected NewOrEditButton btnNewOrEditReferenceIndicationEndUnit;


  public EntryReferenceControl() {
    this(null);
  }

  public EntryReferenceControl(Entry entry, EventHandler<FieldChangedEvent> fieldChangedEvent) {
    this(entry);
    this.fieldChangedEvent = fieldChangedEvent;
  }

  public EntryReferenceControl(Entry entry) {
    this.entry = entry;
    if(entry != null)
      entry.addEntityListener(entryListener);

    setDisable(entry == null);
    deepThought = Application.getDeepThought();

    Application.addApplicationListener(new ApplicationListener() {
      @Override
      public void deepThoughtChanged(DeepThought deepThought) {
        EntryReferenceControl.this.deepThoughtChanged(deepThought);
      }

      @Override
      public void errorOccurred(DeepThoughtError error) {

      }
    });

    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("controls/EntryReferenceControl.fxml"));
    fxmlLoader.setRoot(this);
    fxmlLoader.setController(this);
    fxmlLoader.setResources(Localization.getStringsResourceBundle());

    try {
      fxmlLoader.load();
      setupControl();

      if(deepThought != null)
        deepThought.addEntityListener(deepThoughtListener);
    } catch (IOException ex) {
      log.error("Could not load EntryReferenceControl", ex);
    }
  }

  protected void deepThoughtChanged(DeepThought newDeepThought) {
    if(this.deepThought != null)
      this.deepThought.removeEntityListener(deepThoughtListener);

    this.deepThought = newDeepThought;

    comboBoxReferenceItems.clear();

    if(newDeepThought != null) {
      newDeepThought.addEntityListener(deepThoughtListener);
      resetComboBoxSeriesTitleItems(deepThought);
      resetComboBoxReferencesItems(deepThought);
    }
  }

  protected void setupControl() {
    ensureNodeOnlyUsesSpaceIfVisible(this);

    setupPaneSeriesTitle();

    setupPaneReference();

    setupPaneReferenceSubDivision();

    setupPaneReferenceIndicationSettings();


//    final TextField txtfldReference = new TextField();
//    TextFields.bindAutoCompletion(txtfldReference, suggestionProvider -> {
//      return findReferencesToSuggestion(suggestionProvider);
//    }, new StringConverter<Reference>() {
//      @Override
//      public String toString(Reference reference) {
//        if(reference != null)
//          return reference.getStringRepresentation();
//        return txtfldReference.getText();
//      }
//
//      @Override
//      public Reference fromString(String string) {
//        Reference reference = Reference.findReferenceFromStringRepresentation(string);
//        if (reference != null)
//          return reference;
//
//        // TODO: maybe it helps returning a dummy object
////        return Reference.createReferenceFromStringRepresentation(string);
//        return null;
//      }
//    });
//
//    paneSeriesTitleReferenceAndSubDivisionSettings.getChildren().add(txtfldReference);

    setFieldsVisibility(entry);
  }

  protected void setupPaneSeriesTitle() {
    ensureNodeOnlyUsesSpaceIfVisible(paneSeriesTitle);

    btnNewOrEditSeriesTitle = new NewOrEditButton(); // create btnNewOrEditSeriesTitle before cmbxSeriesTitle's value gets set (otherwise cmbxSeriesTitleValueChangedListener causes a NullPointerException)
    btnNewOrEditSeriesTitle.setOnAction(event -> handleButtonEditOrNewSeriesTitleAction(event));
    btnNewOrEditSeriesTitle.setOnNewMenuItemEventActionHandler(event -> handleMenuItemNewSeriesTitleAction(event));
    paneSeriesTitle.getChildren().add(btnNewOrEditSeriesTitle);

    cmbxSeriesTitle.setEditable(false); // TODO: undo as soon searching / creating directly in ComboBox is possible again
    comboBoxSeriesTitleItems = cmbxSeriesTitle.getItems(); // make a copy as in autoCompleteComboBox a FilteredList gets layed over ComboBox's items and only operate on that list (as operating on
    // cmbxSeriesTitle.getItems() would then operate on FilteredList and that is prohibited)
    cmbxSeriesTitle.setItems(comboBoxSeriesTitleItems);
    resetComboBoxSeriesTitleItems(Application.getDeepThought());

    cmbxSeriesTitle.setCellFactory(param -> new BaseEntityListCell<SeriesTitle>());
    cmbxSeriesTitle.valueProperty().addListener(cmbxSeriesTitleValueChangedListener);
    cmbxSeriesTitle.setConverter(new StringConverter<SeriesTitle>() {
      @Override
      public String toString(SeriesTitle series) {
        return series.getTextRepresentation();
      }

      @Override
      public SeriesTitle fromString(String string) {
        return null;
      }
    });

    if(entry.getSeries() == null)
      cmbxSeriesTitle.setValue(Empty.Series);
    else
      cmbxSeriesTitle.setValue(entry.getSeries());
  }

  protected void setupPaneReference() {
    ensureNodeOnlyUsesSpaceIfVisible(paneReference);

    btnNewOrEditReference = new NewOrEditButton();// create btnNewOrEditReference before cmbxReference's value gets set (otherwise cmbxReferenceValueChangedListener causes a NullPointerException)
    btnNewOrEditReference.setOnAction(event -> handleButtonEditOrNewReferenceAction(event));
    btnNewOrEditReference.setOnNewMenuItemEventActionHandler(event -> handleMenuItemNewReferenceAction(event));
    paneReference.getChildren().add(btnNewOrEditReference);

    setupComboBoxReference();
  }

  protected void setupComboBoxReference() {
    cmbxReference.setEditable(false); // TODO: undo as soon searching / creating directly in ComboBox is possible again
    comboBoxReferenceItems = cmbxReference.getItems(); // make a copy as in autoCompleteComboBox a FilteredList gets layed over ComboBox's items and only operate on that list (as operating on
    // cmbxReference.getItems() would then operate on FilteredList and that is prohibited)
    cmbxReference.setItems(comboBoxReferenceItems);
    resetComboBoxReferencesItems(Application.getDeepThought());

    cmbxReference.setCellFactory(new Callback<ListView<Reference>, ListCell<Reference>>() {
      @Override
      public ListCell<Reference> call(ListView<Reference> param) {
        return new BaseEntityListCell<Reference>();
      }
    });


    cmbxReference.setConverter(new StringConverter<Reference>() {
      @Override
      public String toString(Reference reference) {
        log.debug("toString called for {}", reference);
        if(reference != null)
          return reference.getTextRepresentation();
        return cmbxReference.getEditor().getText();
      }

      @Override
      public Reference fromString(String string) {
        log.debug("fromString called for {}", string);
        Reference reference = Reference.findReferenceFromStringRepresentation(string);
        if(reference != null)
          return reference;

//        // TODO: maybe it helps returning a dummy object
//        return Reference.createReferenceFromStringRepresentation(string);
        return null;
      }
    });
//
//    cmbxReference.getEditor().addEventFilter(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
//      if(event.getCode().equals(KeyCode.ENTER)) {
//        log.debug("Enter has been pressed, selected item is {}", cmbxReference.getSelectionModel().getSelectedItem());
//        handleComboBoxReferenceEnterHasBeenPressed();
//      }
//
//      setButtonEditOrNewReferenceText();
//    });
//
    cmbxReference.valueProperty().addListener(cmbxReferenceValueChangedListener);

//    cmbxReference.valueProperty().addListener(new ChangeListener<Reference>() {
//      @Override
//      public void changed(ObservableValue<? extends Reference> observable, Reference oldValue, Reference newValue) {
//        log.debug("cmbxReference value changed to {}", newValue);
//        entry.setReference(newValue);
//
//        if(oldValue == null && newValue != null)
//          setFieldsVisibility(entry);
//      }
//    });

//    FXUtils.<Reference>autoCompleteComboBox(cmbxReference, new Callback<FXUtils.DoesItemMatchSearchTermParam<Reference>, Boolean>() {
//      @Override
//      public Boolean call(FXUtils.DoesItemMatchSearchTermParam<Reference> param) {
//        return doesSearchTermMatchReference(param);
//      }
//    });

    if(entry.getReference() == null)
      cmbxReference.setValue(Empty.Reference);
    else
      cmbxReference.setValue(entry.getReference());
  }

//  protected void handleComboBoxReferenceEnterHasBeenPressed() {
//    Reference selectedReference = cmbxReference.getSelectionModel().getSelectedItem();
//    if(selectedReference != null) {
//      if(selectedReference.getId() == null) {
//        Application.getDeepThought().addReference(selectedReference);
//        resetComboBoxReferencesItems(Application.getDeepThought());
//      }
//      entry.setReference(cmbxReference.getSelectionModel().getSelectedItem());
//    }
//    else {
//      String enteredText = cmbxReference.getEditor().getText();
//      Reference foundReference = Reference.findReferenceFromStringRepresentation(enteredText);
//      log.debug("Enter has been pressed in ComboBox Reference, foundReference is {}", foundReference);
//      if (foundReference != null) {
//        entry.setReference(foundReference);
//      } else {
//        createNewReference();
//      }
//    }
//
//    cmbxReference.getEditor().positionCaret(cmbxReference.getEditor().getText().length());
//  }

//  protected void createNewReference() {
//    log.debug("Creating a new Reference from string {}", cmbxReference.getEditor().getText());
//    Reference newReference = Reference.createReferenceFromStringRepresentation(cmbxReference.getEditor().getText());
//    Application.getDeepThought().addReference(newReference);
//    resetComboBoxReferencesItems(Application.getDeepThought());
//    entry.setReference(newReference);
////    updateComboBoxReferenceSelectedItem(newReference);
//  }

  protected void setupPaneReferenceSubDivision() {
    ensureNodeOnlyUsesSpaceIfVisible(paneReferenceSubDivision);
  }

  protected void setupPaneReferenceIndicationSettings() {
    //    ensureNodeOnlyUsesSpaceIfVisible(paneReferenceIndicationSettings);

    txtfldReferenceIndicationStart.setText(entry.getIndicationStart());
    txtfldReferenceIndicationStart.textProperty().addListener((observable, oldValue, newValue) -> {
      fireFieldChangedEvent(FieldWithUnsavedChanges.EntryReferenceStart, newValue);

      if(StringUtils.isNotNullOrEmpty(txtfldReferenceIndicationStart.getText()))
        paneReferenceIndicationEndSettings.setVisible(true);
    });

    ensureNodeOnlyUsesSpaceIfVisible(paneReferenceIndicationStartSettings);
    cmbxReferenceIndicationStartUnit.getItems().addAll(Application.getDeepThought().getReferenceIndicationUnits());
    cmbxReferenceIndicationStartUnit.setValue(entry.getIndicationStartUnit());
    cmbxReferenceIndicationStartUnit.valueProperty().addListener(cmbxReferenceIndicationStartUnitValueChangedListener);
    cmbxReferenceIndicationStartUnit.setCellFactory(param -> new BaseEntityListCell<ReferenceIndicationUnit>());
    cmbxReferenceIndicationStartUnit.setConverter(new StringConverter<ReferenceIndicationUnit>() {
      @Override
      public String toString(ReferenceIndicationUnit unit) {
        return unit.getTextRepresentation();
      }

      @Override
      public ReferenceIndicationUnit fromString(String string) {
        return null;
      }
    });

    btnNewOrEditReferenceIndicationStartUnit = new NewOrEditButton();
    btnNewOrEditReferenceIndicationStartUnit.setPrefWidth(100);
    btnNewOrEditReferenceIndicationStartUnit.setButtonFunction(NewOrEditButton.ButtonFunction.New);
    btnNewOrEditReferenceIndicationStartUnit.setShowEditMenuItem(true);
    btnNewOrEditReferenceIndicationStartUnit.setOnAction(event -> handleButtonEditOrNewReferenceIndicationStartUnitAction(event));
    btnNewOrEditReferenceIndicationStartUnit.setOnNewMenuItemEventActionHandler(event -> handleMenuItemNewReferenceIndicationStartUnitAction(event));
    paneReferenceIndicationStartSettings.getChildren().add(btnNewOrEditReferenceIndicationStartUnit);
    btnNewOrEditReferenceIndicationStartUnit.setDisable(true);

    txtfldReferenceIndicationEnd.setText(entry.getIndicationEnd());
    txtfldReferenceIndicationEnd.textProperty().addListener((observable, oldValue, newValue) -> fieldsWithUnsavedChanges.add(FieldWithUnsavedChanges.EntryReferenceEnd));

    ensureNodeOnlyUsesSpaceIfVisible(paneReferenceIndicationEndSettings);
    cmbxReferenceIndicationEndUnit.getItems().addAll(Application.getDeepThought().getReferenceIndicationUnits());
    cmbxReferenceIndicationEndUnit.setValue(entry.getIndicationEndUnit());
    cmbxReferenceIndicationEndUnit.valueProperty().addListener(cmbxReferenceIndicationEndUnitValueChangedListener);
    cmbxReferenceIndicationEndUnit.setCellFactory(param -> new BaseEntityListCell<ReferenceIndicationUnit>());
    cmbxReferenceIndicationEndUnit.setConverter(new StringConverter<ReferenceIndicationUnit>() {
      @Override
      public String toString(ReferenceIndicationUnit unit) {
        return unit.getTextRepresentation();
      }

      @Override
      public ReferenceIndicationUnit fromString(String string) {
        return null;
      }
    });

    btnNewOrEditReferenceIndicationEndUnit = new NewOrEditButton();
    btnNewOrEditReferenceIndicationEndUnit.setPrefWidth(100);
    btnNewOrEditReferenceIndicationEndUnit.setButtonFunction(NewOrEditButton.ButtonFunction.New);
    btnNewOrEditReferenceIndicationEndUnit.setShowEditMenuItem(true);
    btnNewOrEditReferenceIndicationEndUnit.setOnAction(event -> handleButtonEditOrNewReferenceIndicationEndUnitAction(event));
    btnNewOrEditReferenceIndicationEndUnit.setOnNewMenuItemEventActionHandler(event -> handleMenuItemNewReferenceIndicationEndUnitAction(event));
    paneReferenceIndicationEndSettings.getChildren().add(btnNewOrEditReferenceIndicationEndUnit);
  }

  protected Collection<Reference> findReferencesToSuggestion(AutoCompletionBinding.ISuggestionRequest suggestionProvider) {
    List<Reference> suggestions = new ArrayList<>();

    for(Reference reference : Application.getDeepThought().getReferencesSorted()) {
      if(suggestionProvider.isCancelled())
        break;

      if(reference.getTitle().toLowerCase().contains(suggestionProvider.getUserText().toLowerCase()))
        suggestions.add(reference);
    }

    return suggestions;
  }

  protected void ensureNodeOnlyUsesSpaceIfVisible(Node node) {
    node.managedProperty().bind(node.visibleProperty());
  }

//  protected void setFieldsVisibility(Entry entry) {
//    EntryTemplate template = entry.getTemplate();
//
//    paneSeriesTitle.setVisible(template.showSeriesTitle() || entry.getReference() != null || entry.getSeries() != null);
//    paneReference.setVisible(template.showReference() || entry.getReference() != null);
//    paneReferenceSubDivision.setVisible(template.showReferenceSubDivision() || entry.getReference() != null || entry.getReferenceSubDivision() != null);
//
//    paneReferenceIndicationSettings.setVisible(paneReference.isVisible());
//
//    paneReferenceIndicationStartSettings.setVisible(template.showReferenceStart() || entry.getReference() != null);
//    paneReferenceIndicationEndSettings.setVisible(template.showReferenceStart() || StringUtils.isNotNullOrEmpty(entry.getReferenceStart()));
//
//    this.setVisible(paneReference.isVisible());
//  }
  protected void setFieldsVisibility(Entry entry) {
    SeriesTitle seriesTitle = cmbxSeriesTitle.getValue();
    Reference reference = cmbxReference.getValue();
    ReferenceSubDivision subDivision = null; // TODO

    paneSeriesTitle.setVisible(reference != Empty.Reference || seriesTitle != Empty.Series);

//    paneReference.setVisible(entry.getReference() != null);

    paneReferenceSubDivision.setVisible(reference != Empty.Reference || subDivision != null);

    paneReferenceIndicationSettings.setVisible(paneReference.isVisible());

    paneReferenceIndicationStartSettings.setVisible(reference != Empty.Reference);
    paneReferenceIndicationEndSettings.setVisible(StringUtils.isNotNullOrEmpty(txtfldReferenceIndicationStart.getText()));

    this.setVisible(paneReference.isVisible());
  }

  protected Boolean doesSearchTermMatchReference(FXUtils.DoesItemMatchSearchTermParam<Reference> param) {
    return param.getItem().getTitle().toLowerCase().contains(param.getSearchTerm().toLowerCase());
  }


  public void handleButtonEditOrNewSeriesTitleAction(ActionEvent event) {
    if(btnNewOrEditSeriesTitle.getButtonFunction() == NewOrEditButton.ButtonFunction.New)
      createNewSeriesTitle();
    else
      Dialogs.showEditSeriesTitleDialog(cmbxSeriesTitle.getSelectionModel().getSelectedItem());
  }

  public void handleMenuItemNewSeriesTitleAction(NewOrEditButtonMenuActionEvent event) {
    createNewSeriesTitle();
  }

  protected void createNewSeriesTitle() {
    //      SeriesTitle newSeriesTitle = SeriesTitle.createReferenceFromStringRepresentation(cmbxSeriesTitle.getEditor().getText()); // TODO: use as soon as typing directly in  ComboBox is possible again
    final SeriesTitle newSeriesTitle = new SeriesTitle();
    Dialogs.showEditSeriesTitleDialog(newSeriesTitle, new ChildWindowsControllerListener() {
      @Override
      public void windowClosing(Stage stage, ChildWindowsController controller) {
        if (controller.getDialogResult() == DialogResult.Ok) {
//          entry.setSeries(newSeriesTitle);
          cmbxSeriesTitle.setValue(newSeriesTitle);

          Reference selectedReference = cmbxReference.getValue();
          if(selectedReference != Empty.Reference && newSeriesTitle.containsSerialParts(selectedReference) == false) {
            if(selectedReference.getSeries() == null)
              selectedReference.setSeries(cmbxSeriesTitle.getValue());
            else
              cmbxReference.setValue(Empty.Reference);
          }
        }
      }

      @Override
      public void windowClosed(Stage stage, ChildWindowsController controller) {

      }
    });
  }


  public void handleButtonEditOrNewReferenceAction(ActionEvent event) {
    if(btnNewOrEditReference.getButtonFunction() == NewOrEditButton.ButtonFunction.New)
      createNewReference();
    else {
      Dialogs.showEditReferenceDialog(cmbxReference.getValue());
    }
  }

  public void handleMenuItemNewReferenceAction(NewOrEditButtonMenuActionEvent event) {
    createNewReference();
  }

  protected void createNewReference() {
    //      Reference newReference = Reference.createReferenceFromStringRepresentation(cmbxReference.getEditor().getText()); // TODO: use as soon as typing directly in ComboBox is
    // possible again
    final Reference newReference = new Reference();
    Dialogs.showEditReferenceDialog(newReference, new ChildWindowsControllerListener() {
      @Override
      public void windowClosing(Stage stage, ChildWindowsController controller) {
        if (controller.getDialogResult() == DialogResult.Ok)
//          entry.setReference(newReference);
        cmbxReference.setValue(newReference);

        if(cmbxSeriesTitle.getValue() != Empty.Series)
          cmbxSeriesTitle.getValue().addSerialPart(newReference);
      }

      @Override
      public void windowClosed(Stage stage, ChildWindowsController controller) {

      }
    });
  }


  public void handleButtonEditOrNewReferenceIndicationStartUnitAction(ActionEvent event) {
    if(btnNewOrEditReferenceIndicationStartUnit.getButtonFunction() == NewOrEditButton.ButtonFunction.New)
      createNewReferenceIndicationUnitForReferenceStart();
//    else
//      Dialogs.showEditReferenceIndicationUnitDialog(cmbxReferenceIndicationStartUnit.getSelectionModel().getSelectedItem());
  }

  public void handleMenuItemNewReferenceIndicationStartUnitAction(NewOrEditButtonMenuActionEvent event) {
    createNewReferenceIndicationUnitForReferenceStart();
  }

  protected void createNewReferenceIndicationUnitForReferenceStart() {
    final ReferenceIndicationUnit newReferenceIndicationUnit = new ReferenceIndicationUnit();
//    Dialogs.showEditReferenceIndicationUnitDialog(newReferenceIndicationUnit, new ChildWindowsControllerListener() {
//      @Override
//      public void windowClosing(Stage stage, ChildWindowsController controller) {
//        if (controller.getDialogResult() == DialogResult.Ok)
//          entry.setReferenceStartUnit(newReferenceIndicationUnit);
//      }
//
//      @Override
//      public void windowClosed(Stage stage, ChildWindowsController controller) {
//
//      }
//    });
  }


  public void handleButtonEditOrNewReferenceIndicationEndUnitAction(ActionEvent event) {
    if(btnNewOrEditReferenceIndicationEndUnit.getButtonFunction() == NewOrEditButton.ButtonFunction.New)
      createNewReferenceIndicationUnitForReferenceEnd();
//    else
//      Dialogs.showEditReferenceIndicationUnitDialog(cmbxReferenceIndicationEndUnit.getSelectionModel().getSelectedItem());
  }

  public void handleMenuItemNewReferenceIndicationEndUnitAction(NewOrEditButtonMenuActionEvent event) {
    createNewReferenceIndicationUnitForReferenceEnd();
  }

  protected void createNewReferenceIndicationUnitForReferenceEnd() {
    final ReferenceIndicationUnit newReferenceIndicationUnit = new ReferenceIndicationUnit();
//    Dialogs.showEditReferenceIndicationUnitDialog(newReferenceIndicationUnit, new ChildWindowsControllerListener() {
//      @Override
//      public void windowClosing(Stage stage, ChildWindowsController controller) {
//        if (controller.getDialogResult() == DialogResult.Ok)
//          entry.setReferenceEndUnit(newReferenceIndicationUnit);
//      }
//
//      @Override
//      public void windowClosed(Stage stage, ChildWindowsController controller) {
//
//      }
//    });
  }


  protected ChangeListener<SeriesTitle> cmbxSeriesTitleValueChangedListener = new ChangeListener<SeriesTitle>() {
    @Override
    public void changed(ObservableValue<? extends SeriesTitle> observable, SeriesTitle oldValue, SeriesTitle newValue) {
      if(newValue != entry.getSeries())
        //entry.setSeries(newValue);
        fireFieldChangedEvent(FieldWithUnsavedChanges.EntrySeriesTitle, newValue);

      if(newValue == null || Empty.Series.equals(newValue)) {
        btnNewOrEditSeriesTitle.setButtonFunction(NewOrEditButton.ButtonFunction.New);
        btnNewOrEditSeriesTitle.setShowNewMenuItem(false);
      }
      else {
        btnNewOrEditSeriesTitle.setButtonFunction(NewOrEditButton.ButtonFunction.Edit);
        btnNewOrEditSeriesTitle.setShowNewMenuItem(true);
      }
    }
  };

  protected ChangeListener<Reference> cmbxReferenceValueChangedListener = new ChangeListener<Reference>() {
    @Override
    public void changed(ObservableValue<? extends Reference> observable, Reference oldValue, Reference newValue) {
      log.debug("Selected reference changed to {}", newValue);
      if(newValue != entry.getReference())
//        entry.setReference(newValue);
        fireFieldChangedEvent(FieldWithUnsavedChanges.EntryReference, newValue);

      if((oldValue == null || oldValue == Empty.Reference) && newValue != Empty.Reference)
        setFieldsVisibility(entry);

      if(newValue == null || Empty.Reference.equals(newValue)) {
        btnNewOrEditReference.setButtonFunction(NewOrEditButton.ButtonFunction.New);
        btnNewOrEditReference.setShowNewMenuItem(false);
      }
      else {
        btnNewOrEditReference.setButtonFunction(NewOrEditButton.ButtonFunction.Edit);
        btnNewOrEditReference.setShowNewMenuItem(true);
      }

      cmbxSeriesTitle.setValue(newValue.getSeries());
    }
  };

  protected ChangeListener<ReferenceIndicationUnit> cmbxReferenceIndicationStartUnitValueChangedListener = new ChangeListener<ReferenceIndicationUnit>() {
    @Override
    public void changed(ObservableValue<? extends ReferenceIndicationUnit> observable, ReferenceIndicationUnit oldValue, ReferenceIndicationUnit newValue) {
//      entry.setReferenceStartUnit(newValue);
      fireFieldChangedEvent(FieldWithUnsavedChanges.EntryReferenceStartUnit, newValue);
    }
  };

  protected ChangeListener<ReferenceIndicationUnit> cmbxReferenceIndicationEndUnitValueChangedListener = new ChangeListener<ReferenceIndicationUnit>() {
    @Override
    public void changed(ObservableValue<? extends ReferenceIndicationUnit> observable, ReferenceIndicationUnit oldValue, ReferenceIndicationUnit newValue) {
//      entry.setReferenceEndUnit(newValue);
      fireFieldChangedEvent(FieldWithUnsavedChanges.EntryReferenceEndUnit, newValue);
    }
  };


  protected EntityListener entryListener = new EntityListener() {
    @Override
    public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {
      if(propertyName.equals(TableConfig.EntrySeriesTitleJoinColumnName)) {
        updateComboBoxSeriesTitleSelectedItem((SeriesTitle) newValue);
      }
      else if(propertyName.equals(TableConfig.EntryReferenceJoinColumnName)) {
        updateComboBoxReferenceSelectedItem((Reference) newValue);
      }
      else if(propertyName.equals(TableConfig.EntryIndicationStartUnitJoinColumnName)) {
        updateComboBoxReferenceStartUnitSelectedItem((ReferenceIndicationUnit) newValue);
      }
      else if(propertyName.equals(TableConfig.EntryIndicationEndUnitJoinColumnName)) {
        updateComboBoxReferenceEndUnitSelectedItem((ReferenceIndicationUnit) newValue);
      }
      else if(propertyName.equals(TableConfig.EntryEntryTemplateJoinColumnName)) {
        setFieldsVisibility((Entry) entity);
      }
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

  protected void updateComboBoxSeriesTitleSelectedItem(SeriesTitle newValue) {
//    cmbxSeriesTitle.valueProperty().removeListener(cmbxSeriesTitleValueChangedListener);

    if(newValue== null)
      cmbxSeriesTitle.setValue(Empty.Series);
    else
      cmbxSeriesTitle.setValue(newValue);
    resetComboBoxReferencesItems(Application.getDeepThought());

//    cmbxSeriesTitle.valueProperty().addListener(cmbxSeriesTitleValueChangedListener);
  }

  protected void updateComboBoxReferenceSelectedItem(Reference newValue) {
//    cmbxReference.valueProperty().removeListener(cmbxReferenceValueChangedListener);

    if(newValue== null)
      cmbxReference.setValue(Empty.Reference);
    else
      cmbxReference.setValue(newValue);

//    cmbxReference.valueProperty().addListener(cmbxReferenceValueChangedListener);
  }

  protected void updateComboBoxReferenceStartUnitSelectedItem(ReferenceIndicationUnit newValue) {
    cmbxReferenceIndicationStartUnit.valueProperty().removeListener(cmbxReferenceIndicationStartUnitValueChangedListener);

    cmbxReferenceIndicationStartUnit.setValue(newValue);

    cmbxReferenceIndicationStartUnit.valueProperty().addListener(cmbxReferenceIndicationStartUnitValueChangedListener);
  }

  protected void updateComboBoxReferenceEndUnitSelectedItem(ReferenceIndicationUnit newValue) {
    cmbxReferenceIndicationEndUnit.valueProperty().removeListener(cmbxReferenceIndicationEndUnitValueChangedListener);

    cmbxReferenceIndicationEndUnit.setValue(newValue);

    cmbxReferenceIndicationEndUnit.valueProperty().addListener(cmbxReferenceIndicationEndUnitValueChangedListener);
  }

  protected EntityListener deepThoughtListener = new EntityListener() {
    @Override
    public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

    }

    @Override
    public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {
      checkIfReferencesHaveBeenUpdated(collectionHolder, addedEntity);
    }

    @Override
    public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {
      if(updatedEntity.equals(entry.getReference())) {
        cmbxReference.valueProperty().removeListener(cmbxReferenceValueChangedListener);
        cmbxReference.setValue(Empty.Reference);
        cmbxReference.setValue((Reference) updatedEntity);
        cmbxSeriesTitle.setValue(((Reference) updatedEntity).getSeries());
        cmbxReference.valueProperty().addListener(cmbxReferenceValueChangedListener);
      }
      else if(updatedEntity.equals(entry.getSeries())) {
        cmbxSeriesTitle.valueProperty().removeListener(cmbxSeriesTitleValueChangedListener);
        cmbxSeriesTitle.setValue(Empty.Series);
        cmbxSeriesTitle.setValue((SeriesTitle) updatedEntity);
        cmbxSeriesTitle.valueProperty().addListener(cmbxSeriesTitleValueChangedListener);
      }
    }

    @Override
    public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {
      checkIfReferencesHaveBeenUpdated(collectionHolder, removedEntity);
    }
  };

  protected void checkIfReferencesHaveBeenUpdated(BaseEntity collectionHolder, BaseEntity entity) {
    if(collectionHolder instanceof DeepThought) {
      if(entity instanceof Reference) {
        DeepThought deepThought = (DeepThought)collectionHolder;
        resetComboBoxReferencesItems(deepThought);
      }
      else if(entity instanceof SeriesTitle) {
        DeepThought deepThought = (DeepThought)collectionHolder;
        resetComboBoxSeriesTitleItems(deepThought);
      }
      else if(entity instanceof ReferenceIndicationUnit) {
        DeepThought deepThought = (DeepThought)collectionHolder;
        resetComboBoxesReferenceIndicationUnitItems(deepThought);
      }
    };
  }

  protected void resetComboBoxSeriesTitleItems(DeepThought deepThought) {
    comboBoxSeriesTitleItems.clear();
    comboBoxSeriesTitleItems.add(Empty.Series);
    comboBoxSeriesTitleItems.addAll(deepThought.getSeriesTitlesSorted());
    cmbxSeriesTitle.setVisibleRowCount(10);
  }

  protected void resetComboBoxReferencesItems(DeepThought deepThought) {
    log.debug("Selected Reference before {}", cmbxReference.getValue());

    comboBoxReferenceItems.clear();

    if(cmbxSeriesTitle.getValue() != null && cmbxSeriesTitle.getValue() != Empty.Series) { // is a Series is set, show Series' SerialParts first
      comboBoxReferenceItems.addAll(cmbxSeriesTitle.getValue().getSerialPartsSorted());
    }

    comboBoxReferenceItems.add(Empty.Reference);

    if(cmbxSeriesTitle.getValue() != null && cmbxSeriesTitle.getValue() != Empty.Series) { // remove Series's serialParts from List with all (remaining) References
      Collection remainingSerialParts = deepThought.getReferencesSorted();
      remainingSerialParts.removeAll(cmbxSeriesTitle.getValue().getSerialParts());
      comboBoxReferenceItems.addAll(remainingSerialParts);
    }
    else
      comboBoxReferenceItems.addAll(deepThought.getReferencesSorted());

    cmbxReference.setVisibleRowCount(10);

    log.debug("Selected Reference after {}", cmbxReference.getValue());
  }

  protected void resetComboBoxesReferenceIndicationUnitItems(DeepThought deepThought) {
    cmbxReferenceIndicationStartUnit.getItems().clear();
    cmbxReferenceIndicationStartUnit.getItems().addAll(deepThought.getReferenceIndicationUnits());

    cmbxReferenceIndicationEndUnit.getItems().clear();
    cmbxReferenceIndicationEndUnit.getItems().addAll(deepThought.getReferenceIndicationUnits());
  }



  protected void fireFieldChangedEvent(FieldWithUnsavedChanges changedField, Object newValue) {
    if(fieldChangedEvent != null)
      fieldChangedEvent.handle(new FieldChangedEvent(this, changedField, newValue));
  }

  public EventHandler<FieldChangedEvent> getFieldChangedEvent() {
    return fieldChangedEvent;
  }

  public void setFieldChangedEvent(EventHandler<FieldChangedEvent> fieldChangedEvent) {
    this.fieldChangedEvent = fieldChangedEvent;
  }


  public SeriesTitle getSeriesTitle() {
    if(cmbxSeriesTitle.getValue() == Empty.Series)
      return null;
    return cmbxSeriesTitle.getValue();
  }

  public Reference getReference() {
    if(cmbxReference.getValue() == Empty.Reference)
      return null;
    return cmbxReference.getValue();
  }

  public ReferenceSubDivision getReferenceSubDivision() {
    // TODO:
    return null;
  }

  public String getReferenceStart() {
    return txtfldReferenceIndicationStart.getText();
  }

  public ReferenceIndicationUnit getReferenceStartUnit() {
    if(cmbxReferenceIndicationStartUnit.getValue() == Empty.ReferenceIndicationUnit)
      return null;
    return cmbxReferenceIndicationStartUnit.getValue();
  }

  public String getReferenceEnd() {
    return txtfldReferenceIndicationEnd.getText();
  }

  public ReferenceIndicationUnit getReferenceEndUnit() {
    if(cmbxReferenceIndicationEndUnit.getValue() == Empty.ReferenceIndicationUnit)
      return null;
    return cmbxReferenceIndicationEndUnit.getValue();
  }

}
