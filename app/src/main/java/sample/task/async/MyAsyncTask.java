package sample.task.async;

/**
 * Created by hirokinaganuma on 2015/03/01.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class MyAsyncTask extends AsyncTask<String, Integer, String> {
    private TextView txtResult;
    private Activity activity;
    private ProgressDialog progress;

    public MyAsyncTask(Activity activity, TextView txtResult) {
        super();
        this.activity = activity;//現在のActivityを引数として代入
        this.txtResult = txtResult;//結果を表示するtextを引数として代入
    }

    @Override
    protected void onPreExecute() {
        progress = new ProgressDialog(activity);//progressbarのインスタンス化
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//Dialogに表示するProgressBarの向きを決める
        progress.setMessage("transmitting...");
        progress.setMax(100);//progressbarの最大値を100にセットする
        progress.setProgress(0);//progressbarの初期値を0にセットする
        progress.setButton(DialogInterface.BUTTON_NEGATIVE,"キャンセル",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyAsyncTask.this.cancel(true);
                    }
                });
        progress.show();
    }


    @Override
    protected String doInBackground(String... params) {
        String result = null;
        SystemClock.sleep(1000);
        publishProgress(30);

        try {
            HttpGet req = new HttpGet(params[0]);
            DefaultHttpClient client = new DefaultHttpClient();
            HttpResponse res = client.execute(req);
            int status = res.getStatusLine().getStatusCode();
            if(status == HttpStatus.SC_OK){
                result = EntityUtils.toString(res.getEntity(), "UTF-8");
            } else {
                result = "ステータスコード：" + status;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        publishProgress(70);
        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progress.incrementProgressBy(values[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        txtResult.setText(result);
        progress.dismiss();
    }

    @Override
    protected void onCancelled() {
        txtResult.setText("キャンセルされました。");
        progress.dismiss();
    }


}
