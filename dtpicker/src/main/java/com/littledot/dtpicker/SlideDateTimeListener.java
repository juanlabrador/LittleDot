package com.littledot.dtpicker;

import java.util.Calendar;
import java.util.Date;

public abstract class SlideDateTimeListener
{
    /**
     * Informs the client when the user presses "OK"
     * and selects a date and time.
     *
     * @param calendar  The {@code Calendar} object that contains the date
     *              and time that the user has selected.
     */
    public abstract void onDateTimeSet(Calendar calendar);

    /**
     * Informs the client when the user cancels the
     * dialog by pressing Cancel, touching outside
     * the dialog or pressing the Back button.
     * This override is optional.
     */
    public void onDateTimeCancel()
    {

    }
}
