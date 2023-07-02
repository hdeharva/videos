package org.vaadin.example;

import com.nimbusds.jose.shaded.gson.Gson;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.model.Select;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * A sample Vaadin view class.
 * <p>
 * To implement a Vaadin view just extend any Vaadin component and
 * use @Route annotation to announce it in a URL as a Spring managed
 * bean.
 * Use the @PWA annotation make the application installable on phones,
 * tablets and some desktop browsers.
 * <p>
 * A new instance of this class is created for every new user and every
 * browser tab/window.
 */
@Route
@PWA(name = "Vaadin Application",
        shortName = "Vaadin App",
        description = "This is an example Vaadin application.",
        enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainView extends HorizontalLayout {

    private static final String api = "https://swapi.dev/api/%s/%s";
    HttpRequest request;
    HttpClient client = HttpClient.newBuilder().build();
    HttpResponse<String> response;

    private String getCharacter(String type, String id)
    {
        try {
            String resource = String.format(api, type, id);
            System.out.println(resource);
            request = HttpRequest
                    .newBuilder(new URI(resource))
                    .header("Content-Type", "Application/json")
                    .GET()
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return response.body();
    }

    public MainView() {

        final Gson gson = new Gson();
        HorizontalLayout mainLeft = new HorizontalLayout();
        HorizontalLayout mainRight = new HorizontalLayout();

        mainRight.setPadding(true);

        HorizontalLayout horizontalLayout = new HorizontalLayout();

        Select<String> valueSelect = new Select<>();
        valueSelect.setItems("people", "planets", "starships");
        valueSelect.setPlaceholder("Elige un tipo de recurso:");

        TextField placeholderField = new TextField();
        placeholderField.setPlaceholder("Introduzca el id del recurso:");

        horizontalLayout.add(valueSelect, placeholderField);

        Button button = new Button("Obtener",
                event -> {
                    String resource = valueSelect.getValue();
                    String id = placeholderField.getValue();
                    String response = getCharacter(resource, id);

                    Character character = gson.fromJson(response, Character.class);

                    mainRight.add(character.getName());
                });

        mainLeft.add(horizontalLayout, button);
        add(mainLeft, mainRight);
    }

}
