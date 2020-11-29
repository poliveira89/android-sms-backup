package sms_backup.poliveira.org.smsbackup;

import android.Manifest;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private final String INBOX = "content://sms/inbox";
  private final String SENT = "content://sms/sent";

  private StringBuilder dataset;
  private String firstRow = "";
  private List<String> headers;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    dataset = new StringBuilder();

    String[] permissions = new String[]{
        Manifest.permission.READ_SMS,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    };
    ActivityCompat.requestPermissions(this, permissions, 1);

    final TextView portView = (TextView) this.findViewById(R.id.log);
    portView.setMovementMethod(new ScrollingMovementMethod());
    Button btnBackup = (Button) this.findViewById(R.id.btnBackup);
    btnBackup.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        try {
          Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
              .mkdirs();
          File dir = Environment
              .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
          File file = new File(dir, "sms_backup.csv");
          file.createNewFile();
          FileOutputStream fos = new FileOutputStream(file);

          getInbox();
          getSent();

          for (String header : headers) {
            firstRow += header + ";";
          }
          firstRow += "\n";
          fos.write(firstRow.getBytes());
          fos.write(dataset.toString().getBytes());
          fos.flush();
          fos.close();
          portView.setText("Success!");
        } catch (Exception e) {
          e.printStackTrace();
          portView.setText(e.getMessage());
        }
      }
    });
  }

  private void getInbox() throws Exception {
    Cursor cursor = this.getContentResolver().query(Uri.parse(INBOX), null, null, null, null);

    if (cursor.moveToFirst()) {
      do {
        String row = "";
        headers = new ArrayList<>();
        for (int index = 0; index < cursor.getColumnCount(); index++) {
          if (!headers.contains(cursor.getColumnName(index))) {
            headers.add(cursor.getColumnName(index));
          }
          row += getCsvSuitableFormat(cursor.getString(index)) + ";";
        }
        row += "\n";
        dataset.append(row);
      } while (cursor.moveToNext());
    } else {
      throw new Exception("Fails to retrieve SMS inbox messages.");
    }
  }

  private void getSent() throws Exception {
    Cursor cursor = this.getContentResolver().query(Uri.parse(SENT), null, null, null, null);

    if (cursor.moveToFirst()) {
      do {
        String row = "";
        for (int index = 0; index < cursor.getColumnCount(); index++) {
          row += getCsvSuitableFormat(cursor.getString(index)) + ";";
        }
        row += "\n";
        dataset.append(row);
      } while (cursor.moveToNext());
    } else {
      throw new Exception("Fails to retrieve SMS sent messages.");
    }
  }

  private String getCsvSuitableFormat(String input) {
    /*
    * Per RFC 4180
    * [definition of the CSV format](https://tools.ietf.org/html/rfc4180#page-2)
    *
    * "Fields containing line breaks (CRLF), double quotes, and commas
    *  should be enclosed in double-quotes."
    *
    * "If double-quotes are used to enclose fields, then a double-quote
    * appearing inside a field must be escaped by preceding it with
    * another double quote."
    **/

    // do nothing if null object reference is passed in
    if (input == null){
      return input;
    }

    // wrap with double quotes if the string contains newlines, commas, double-
    // quotes, or semicolons (because we delimit with semicolon).
    // And ensure double-quotes inside the field get
    // preceded by an additional double quote.
    if (
      input.contains("\n") ||
      input.contains(";") ||
      input.contains(",") ||
      input.contains("\"")
    ){
      return  "\"" + input.replace("\"", "\"\"") + "\"";
    }

    return input;
  }
}

