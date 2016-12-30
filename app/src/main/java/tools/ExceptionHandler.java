package tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Looper;

import com.littledot.mystxx.littledot.MainFeedActivity;
import com.littledot.mystxx.littledot.R;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by juanlabrador on 29/09/15.
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final static String EMAIL = "juanjavierlabrador@gmail.com";
    private final Activity mContext;
    private final String LINE_SEPARATOR = "\n";

    public ExceptionHandler(Activity context) {
        mContext = context;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        StringWriter stackTrace = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stackTrace));
        StringBuilder errorReport = new StringBuilder();
        errorReport.append("*** CAUSE OF ERROR ***\n\n");
        errorReport.append(stackTrace.toString());

        errorReport.append("\n*** DEVICE INFORMATION ***\n");
        errorReport.append("Brand: ");
        errorReport.append(Build.BRAND);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Device: ");
        errorReport.append(Build.DEVICE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Model: ");
        errorReport.append(Build.MODEL);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Id: ");
        errorReport.append(Build.ID);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Product: ");
        errorReport.append(Build.PRODUCT);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("\n*** FIRMWARE ***\n");
        errorReport.append("SDK: ");
        errorReport.append(Build.VERSION.SDK);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Release: ");
        errorReport.append(Build.VERSION.RELEASE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Incremental: ");
        errorReport.append(Build.VERSION.INCREMENTAL);
        errorReport.append(LINE_SEPARATOR);

        sendErrorMail(errorReport);
    }

    /**
     * This method for call alert dialog when application crashed!
     * @param errorContent content
     */
    public void sendErrorMail(final StringBuilder errorContent) {
        final AlertDialog.Builder builder= new AlertDialog.Builder(mContext);
        new Thread(){
            @Override
            public void run() {
                Looper.prepare();
                builder.setTitle(mContext.getString(R.string.title_dialog_report));
                builder.create();
                builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mContext.startActivity(new Intent(mContext, MainFeedActivity.class));
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(10);
                    }
                });
                builder.setPositiveButton(R.string.button_report, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent sendIntent = new Intent(Intent.ACTION_SEND);
                        String subject = mContext.getString(R.string.email_subject_report);
                        StringBuilder body = new StringBuilder(errorContent);
                        //body.append('\n').append('\n');
                        //body.append(errorContent).append('\n').append('\n');
                        //  sendIntent.setType("text/plain");
                        sendIntent.setType("message/rfc822");
                        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { EMAIL });
                        sendIntent.putExtra(Intent.EXTRA_TEXT, body.toString());
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                        sendIntent.setType("message/rfc822");
                        // context.startActivity(Intent.createChooser(sendIntent, "Error Report"));
                        mContext.startActivity(sendIntent);
                        System.exit(10);
                    }
                });
                builder.setMessage(mContext.getString(R.string.content_dialog_report));
                builder.show();
                Looper.loop();
            }
        }.start();
    }
}
