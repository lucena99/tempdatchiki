package ru.psv4.tempdatchiki.mvc;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.psv4.tempdatchiki.beans.ControllerService;
import ru.psv4.tempdatchiki.beans.RecipientService;
import ru.psv4.tempdatchiki.model.Controller;
import ru.psv4.tempdatchiki.model.Recipient;

import javax.annotation.Resource;

@Route
public class MainViewOld extends VerticalLayout {

    private RecipientService recipientService;
    private ControllerService controllerService;

    private Grid<Recipient> recipientsGrid;
    private TextField recipientsFilter;
    private Button addRecipientBtn;

    private Grid<Controller> controllersGrid;
    private TextField controllersFilter;
    private Button addControllerBtn;

    public MainViewOld(RecipientService recipientService, ControllerService controllerService) {

        this.recipientService = recipientService;
        this.controllerService = controllerService;

        recipientsGrid = new Grid<>(Recipient.class);
        recipientsFilter = new TextField();
        addRecipientBtn = new Button("Новый слушатель", VaadinIcon.PLUS.create());

        HorizontalLayout recipientsHeader = new HorizontalLayout(recipientsFilter, addRecipientBtn);
        add(recipientsHeader, recipientsGrid);

        controllersGrid = new Grid<>(Controller.class);
        controllersFilter = new TextField();
        addControllerBtn = new Button("Новый контроллер", VaadinIcon.PLUS.create());

        HorizontalLayout controllersHeader = new HorizontalLayout(controllersFilter, addControllerBtn);
        add(controllersHeader, controllersGrid);

        recipientsGrid.setHeight("300px");
        recipientsGrid.setColumns("name");
        recipientsFilter.setPlaceholder("Фильтровать по наименованию слушателя");

        controllersGrid.setHeight("300px");
        controllersGrid.setColumns("name");
        controllersFilter.setPlaceholder("Фильтровать по наименованию котроллер");

        listRecipients("");
        listControllers("");
    }

    void listRecipients(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            recipientsGrid.setItems(recipientService.getList());
        }
        else {
//            recipientsGrid.setItems(repo.findByLastNameStartsWithIgnoreCase(filterText));
            recipientsGrid.setItems();
        }
    }

    void listControllers(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            controllersGrid.setItems(controllerService.getList());
        }
        else {
//            recipientsGrid.setItems(repo.findByLastNameStartsWithIgnoreCase(filterText));
            controllersGrid.setItems();
        }
    }

}