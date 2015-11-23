package com.bdb.weather.javafx;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class FXMLController implements Initializable {
    private WeatherSenseDisplay display;
    @FXML
    private DayTemperaturePlot plot;
    
    public void setDisplay(WeatherSenseDisplay display) {
        this.display = display;
    }
    
    public DayTemperaturePlot getPlot() {
        return plot;
    }

    @FXML
    private void newWindow(ActionEvent event) {
        try {
            display.newWindow();
        }
        catch (Exception ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void handleExit(ActionEvent event) {
        System.exit(0);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
}
