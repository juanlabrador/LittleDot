package domains;

import java.text.DecimalFormat;

/**
 * Created by charles on 9/8/15.
 */
public class Conversion {

    public Conversion() {}

    public String celsiusToFahrenheit(Double celsius){
        return roundOff((celsius * (9/5.0)) + 32);
    }

    public String fahrenheitToCelsius(Double fahrenheit){
        return roundOff((fahrenheit - 32 ) * (5.0/9.0));
    }

    public String cmToInches(Double cm){
        return roundOff(cm * 0.3937008);
    }

    public float cmToInches(Double cm, int i){
        return (float) (cm * 0.3937008);
    }

    public String inchesToCm(Double inches){
        return roundOff(inches * 2.54);
    }

    public String kgToPounds(Double kg){
        return roundOff(kg * 2.2);
    }

    public float kgToPounds(Double kg, int i){
        return (float) (kg * 2.2);
    }

    public float kgToPounds(float kg){
        return (float) (kg * 2.2);
    }

    public String poundsToKg(Double pounds){
        return roundOff(pounds / 2.2);
    }

    public String mlToFl(Double ml){
        return roundOff(ml *  0.033814);
    }

    public String flToMl(Double fl){
        return roundOff(fl * 29.57353);
    }

    public String roundOff(double num){
        DecimalFormat df = new DecimalFormat("###.#");
        return df.format(num);
    }
}
