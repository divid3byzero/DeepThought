package net.deepthought.controller;

import net.deepthought.Application;
import net.deepthought.communication.listener.AskForDeviceRegistrationListener;
import net.deepthought.communication.listener.ResponseListener;
import net.deepthought.communication.messages.AskForDeviceRegistrationRequest;
import net.deepthought.communication.messages.AskForDeviceRegistrationResponseMessage;
import net.deepthought.communication.messages.Request;
import net.deepthought.communication.messages.Response;
import net.deepthought.communication.messages.ResponseValue;
import net.deepthought.communication.model.HostInfo;
import net.deepthought.communication.registration.RegistrationRequestListener;
import net.deepthought.communication.registration.UserDeviceRegistrationRequestListener;
import net.deepthought.controller.enums.DialogResult;
import net.deepthought.controls.Constants;
import net.deepthought.controls.ContextHelpControl;
import net.deepthought.controls.utils.FXUtils;
import net.deepthought.controls.registration.FoundRegistrationServerListCell;
import net.deepthought.util.Alerts;
import net.deepthought.util.JavaFxLocalization;
import net.deepthought.util.Localization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Created by ganymed on 21/08/15.
 */
public class RegisterUserDevicesDialogController extends ChildWindowsController implements Initializable {

  private final static Logger log = LoggerFactory.getLogger(RegisterUserDevicesDialogController.class);


  protected boolean isStarted = false;


  @FXML
  protected BorderPane dialogPane;

  @FXML
  protected RadioButton rdbtnOpenRegistrationServer;
  @FXML
  protected Pane pnOpenRegistrationServer;
  @FXML
  protected ListView lstvwDevicesRequestingRegistration;

  @FXML
  protected RadioButton rdbtnSearchForRegistrationServers;
  @FXML
  protected Pane pnSearchForRegistrationServers;
  @FXML
  protected ListView<HostInfo> lstvwFoundRegistrationServers;

  @FXML
  protected Button btnStartStop;

  @FXML
  protected ToggleButton tglbtnShowHideContextHelp;
  protected ContextHelpControl contextHelpControl;


  @Override
  public void setWindowStage(Stage windowStage) {
    super.setWindowStage(windowStage);
    JavaFxLocalization.bindStageTitle(windowStage, "device.registration");
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    setupControls();
  }


  protected void setupControls() {
    rdbtnOpenRegistrationServer.selectedProperty().addListener((observable, oldValue, newValue) -> radioButtonOpenRegistrationServerSelectionChanged(newValue));

    rdbtnSearchForRegistrationServers.selectedProperty().addListener((observable, oldValue, newValue) -> radioButtonSearchForRegistrationServersSelectionChanged(newValue));

    lstvwFoundRegistrationServers.setCellFactory(listView -> new FoundRegistrationServerListCell(askForDeviceRegistrationListener));

    contextHelpControl = new ContextHelpControl("context.help.register.user.devices.");
    dialogPane.setRight(contextHelpControl);
    FXUtils.ensureNodeOnlyUsesSpaceIfVisible(contextHelpControl);
    contextHelpControl.visibleProperty().bind(tglbtnShowHideContextHelp.selectedProperty());

    tglbtnShowHideContextHelp.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    tglbtnShowHideContextHelp.setGraphic(new ImageView(Constants.ContextHelpIconPath));
  }


  protected void radioButtonOpenRegistrationServerSelectionChanged(Boolean isSelected) {
    pnOpenRegistrationServer.setDisable(isSelected == false);
    btnStartStop.setDisable(false);
  }

  protected void radioButtonSearchForRegistrationServersSelectionChanged(Boolean isSelected) {
    pnSearchForRegistrationServers.setDisable(isSelected == false);
    btnStartStop.setDisable(false);
  }


  @FXML
  public void handleButtonCloseAction(ActionEvent actionEvent) {
    if(isStarted)
      stop();

    closeDialog(DialogResult.Unset);
  }

  @FXML
  public void handleButtonStartStopAction(ActionEvent actionEvent) {
    if(isStarted == false) {
      startSelectedOption();
    }
    else {
      stop();
    }
  }

  protected void startSelectedOption() {
    rdbtnOpenRegistrationServer.setDisable(true);
    rdbtnSearchForRegistrationServers.setDisable(true);

    if(rdbtnOpenRegistrationServer.isSelected()) {
      Application.getDeepThoughtsConnector().openUserDeviceRegistrationServer(userDeviceRegistrationRequestListener);
    }
    else {
      Application.getDeepThoughtsConnector().findOtherUserDevicesToRegisterAtAsync(registrationRequestListener);
    }

    isStarted = true;
    JavaFxLocalization.bindLabeledText(btnStartStop, "to.stop");
  }

  protected void stop() {
    if(rdbtnOpenRegistrationServer.isSelected()) {
      Application.getDeepThoughtsConnector().closeUserDeviceRegistrationServer();
    }
    else {
      Application.getDeepThoughtsConnector().stopSearchingOtherUserDevicesToRegisterAt();
    }

    lstvwFoundRegistrationServers.getItems().clear();
    lstvwDevicesRequestingRegistration.getItems().clear();

    rdbtnOpenRegistrationServer.setDisable(false);
    rdbtnSearchForRegistrationServers.setDisable(false);

    isStarted = false;
    JavaFxLocalization.bindLabeledText(btnStartStop, "to.start");
  }


  protected UserDeviceRegistrationRequestListener userDeviceRegistrationRequestListener = new UserDeviceRegistrationRequestListener() {
    @Override
    public void registerDeviceRequestRetrieved(final AskForDeviceRegistrationRequest request) {
      Platform.runLater(() -> askUserIfRegisteringDeviceIsAllowed(request)); // Alert has to run on UI thread but listener method for sure is not called on UI thread
    }
  };

  protected void askUserIfRegisteringDeviceIsAllowed(final AskForDeviceRegistrationRequest request) {
    boolean userAllowsDeviceRegistration = Alerts.showDeviceAsksForRegistrationAlert(request, windowStage);
    final AskForDeviceRegistrationResponseMessage result;

    if(userAllowsDeviceRegistration == false)
      result = AskForDeviceRegistrationResponseMessage.Deny;
    else {
      result = AskForDeviceRegistrationResponseMessage.createAllowRegistrationResponse(true, Application.getLoggedOnUser(), Application.getApplication().getLocalDevice());
      // TODO: check if user information differ and if so ask which one to use
    }

    Application.getDeepThoughtsConnector().getCommunicator().sendAskForDeviceRegistrationResponse(request, result, new ResponseListener() {
      @Override
      public void responseReceived(Request request1, Response response) {
        if(result.allowsRegistration() && response.getResponseValue() == ResponseValue.Ok) {
          Alerts.showInfoMessage(windowStage, Localization.getLocalizedString("alert.message.successfully.registered.device", request.getDevice()),
              Localization.getLocalizedString("alert.title.device.registration.successful"));
        }
      }
    });
  }

  protected RegistrationRequestListener registrationRequestListener = new RegistrationRequestListener() {
    @Override
    public void openRegistrationServerFound(final HostInfo hostInfo) {
      Platform.runLater(() -> lstvwFoundRegistrationServers.getItems().add(hostInfo));
    }
  };

  protected AskForDeviceRegistrationListener askForDeviceRegistrationListener = new AskForDeviceRegistrationListener() {
    @Override
    public void serverResponded(final AskForDeviceRegistrationResponseMessage response) {
      Platform.runLater(() -> {
        if(response != null) {
          if (response.allowsRegistration())
            Alerts.showDeviceRegistrationSuccessfulAlert(response, windowStage);
          else
            Alerts.showServerDeniedDeviceRegistrationAlert(response, windowStage);
        }
      });
    }
  };

}
