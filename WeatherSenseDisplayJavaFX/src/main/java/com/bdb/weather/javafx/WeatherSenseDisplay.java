package com.bdb.weather.javafx;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.weather.common.DailyRecords;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.TemperatureBinMgr;
import com.bdb.weather.common.WeatherAverage;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.db.DailyAveragesTable;
import com.bdb.weather.common.db.DailyRecordsTable;
import com.bdb.weather.common.db.DailySummaryTable;
import com.bdb.weather.common.db.DatabaseConstants;
import com.bdb.weather.common.db.HistoryTable;
import com.bdb.weather.common.db.WeatherStationTable;
import static com.bdb.weather.javafx.DayTemperaturePlot.createDayTemperaturePlot;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class WeatherSenseDisplay extends Application {
    private DBConnection connection;
    private WeatherStationTable stationTable;
    private WeatherStation ws;
    private HistoryTable historyTable;
    private DailyRecordsTable dailyRecordsTable;
    private DailyAveragesTable dailyAveragesTable;
    private DailySummaryTable dailySummaryTable;
    private TemperatureBinMgr temperatureBinMgr;
    private DayTemperaturePlot plot;

    @Override
    public void start(Stage stage) throws Exception {
        Locale.setDefault(Locale.US);

        openDatabase();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Scene.fxml"), ResourceBundle.getBundle("com.bdb.weathersense.Localization"));
        loader.load();
        BorderPane root = loader.getRoot();
        
        Scene scene = new Scene(root, 800, 800);
        scene.getStylesheets().add("/styles/Styles.css");
	plot = createDayTemperaturePlot(ws);
	root.setCenter(plot.getNode());
        
        stage.setTitle("WeatherSense JavaFX");
        stage.setScene(scene);
        FXMLController controller = loader.getController();
        controller.setDisplay(this);
        stage.sizeToScene();
        stage.show();

        loadData();
    }

    public void newWindow() throws Exception {
        Stage stage = new Stage();
        BorderPane root = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"), ResourceBundle.getBundle("com.bdb.weathersense.Localization"));
        
        Scene scene = new Scene(root, 800, 800);
        scene.getStylesheets().add("/styles/Styles.css");
	plot = createDayTemperaturePlot(ws);
	root.setCenter(plot.getNode());
        
        stage.setTitle("WeatherSense JavaFX");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();

        loadData();

    }

    public void loadData() {
        LocalDate date = LocalDate.now();

        DailyRecords records = dailyRecordsTable.retrieveRecordForDay(date);
        WeatherAverage averages = dailyAveragesTable.retrieveAveragesForDay(ws.getLocationCode(), date);
        List<HistoricalRecord> list = historyTable.queryRecordsForDay(date);
        temperatureBinMgr.refresh();
        SummaryRecord summaryRecord = dailySummaryTable.retrieveTodaysSummary(ws.getWindParameters(), temperatureBinMgr);

        plot.loadData(date, list, summaryRecord, records, averages);
    }

    public void openDatabase() {
        String databaseUrl = String.format(DatabaseConstants.DATABASE_URL_FORMATTER, "192.168.1.100", DatabaseConstants.DATABASE_PORT, DatabaseConstants.DATABASE_NAME);

        connection = new DBConnection(databaseUrl,
                                        DatabaseConstants.DATABASE_USER,
                                        DatabaseConstants.DATABASE_PASSWORD);


        if (!connection.connect()) {
            JOptionPane.showMessageDialog(null, "Unable to connect to the database. Please contact your administrator");
            System.exit(1);
        }
        
        stationTable = new WeatherStationTable(connection);
        ws = stationTable.getWeatherStation();
	dailySummaryTable = new DailySummaryTable(connection);
	historyTable = new HistoryTable(connection);
	dailyRecordsTable = new DailyRecordsTable(connection);
	dailyAveragesTable = new DailyAveragesTable(connection);
	temperatureBinMgr = new TemperatureBinMgr(connection);

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
