package com.bdb.weather.javafx;

import com.bdb.weather.common.WeatherStation;
import static com.bdb.weather.javafx.DayTemperaturePlot.createDayTemperaturePlot;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class WeatherSenseDisplay extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        //Parent root = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"));
	Group root = new Group();
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
	WeatherStation ws = new WeatherStation();
	DayTemperaturePlot plot = createDayTemperaturePlot(ws);
	root.getChildren().add(plot.getNode());
        
        stage.setTitle("WeatherSense JavaFX");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
