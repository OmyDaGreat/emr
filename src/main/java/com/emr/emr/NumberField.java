package com.emr.emr;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.DoubleStringConverter;

import java.util.function.UnaryOperator;

public class NumberField extends TextField {

    public NumberField() {
        super();
        setupNumberFilter();
    }

    public NumberField(String text) {
        super(text);
        setupNumberFilter();
    }
    
    public Double getDouble() {
		return Double.parseDouble(getText());
	}

    private void setupNumberFilter() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) {
                return change;
            }

            if (newText.matches("-?\\d*(\\.\\d*)?")) {
                return change;
            }

            return null;
        };

        TextFormatter<Double> textFormatter = new TextFormatter<>(new DoubleStringConverter(), 0.0, filter);
        this.setTextFormatter(textFormatter);
    }
}
