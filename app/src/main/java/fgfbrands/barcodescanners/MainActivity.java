package fgfbrands.barcodescanners;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.List;
import android.app.ListActivity;
import android.widget.ArrayAdapter;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.VersionInfo;
import org.odata4j.consumer.ODataClient;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.behaviors.OClientBehaviors;
import org.odata4j.core.OEntity;
import org.odata4j.jersey.consumer.ODataJerseyConsumer;

import org.odata4j.jersey.consumer.behaviors.AllowSelfSignedCertsBehavior;


import jcifs.Config;
import zxing.IntentIntegrator;
import zxing.IntentResult;


//public class MainActivity extends ActionBarActivity implements OnClickListener{
//
//    private Button scanBtn;
//    private TextView formatTxt,contentTxt;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        scanBtn =(Button)findViewById(R.id.scan_button);
//        formatTxt = (TextView)findViewById(R.id.scan_format);
//        contentTxt = (TextView)findViewById(R.id.scan_content);
//        scanBtn.setOnClickListener(this);
//    }
//
//    public void onClick(View v){
//        if (v.getId()==R.id.scan_button){
//            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
//            scanIntegrator.initiateScan();
//        }
//    }
//
//    public void onActivityResult(int requestCode, int resultCode, Intent intent){
//        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,intent);
//        if(scanningResult!=null){
//            String scanContent = scanningResult.getContents();
//            String scanFormat = scanningResult.getFormatName();
//            formatTxt.setText("FORMAT:" + scanFormat);
//            contentTxt.setText("CONTENT" + scanContent);
//        }else{
//            Toast toast = Toast.makeText(getApplicationContext(),
//                    "NO SCAN DATA RECEIVED!", Toast.LENGTH_SHORT);
//            toast.show();
//        }
//
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//}
public class MainActivity extends Activity{
    Button callbtn;
    ListView wcflist;
    ArrayList cat;
    ArrayAdapter cat_adpt;



    @Override
    public void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        callbtn = (Button) findViewById(R.id.callbtn);
        wcflist = (ListView) findViewById(R.id.wcflistview);
        cat = new ArrayList();

        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                new wcfService().execute();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    private static class SharePointAuthenticator extends Authenticator {

        private String user;
        private String pass;

        public SharePointAuthenticator(String user, String pass) {
            this.user = user;
            this.pass = pass;
        }

        public PasswordAuthentication getPasswordAuthentication() {

            return (new PasswordAuthentication(user, pass.toCharArray()));

        }

    }

    public class wcfService extends AsyncTask<Void,Void,ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(Void...arg0){
            Registry<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder.<AuthSchemeProvider>create()
                    .register(AuthSchemes.NTLM, new NTLMSchemeFactory())
                    .build();
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setDefaultAuthSchemeRegistry(authSchemeRegistry)
                    .build();

            NTCredentials creds = new NTCredentials("username","password","","FGF");

            httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY, creds);

            // Setup the HTTP GET and execute it
            HttpHost target = new HttpHost(HOSTNAME, 443, "https");

            HttpContext localContext = new BasicHttpContext();
            HttpGet httpget = new HttpGet("/site/_vti_bin/ListData.svc/TestList");
            HttpResponse response1 = httpclient.execute(target, httpget, localContext);



            Config.setProperty("jcifs.smb.client.domain", "FGF");


            Authenticator.setDefault(new SharePointAuthenticator("FGF\\psung","Raptors2016"));




            ODataConsumer con = ODataJerseyConsumer.create("https://odata.fgfbrands.com/c2g/odataservice.svc/");
            List<OEntity> myEntity = con.getEntities("Buying_Group").execute().toList();
            if (myEntity.size()>0){
                for(OEntity entity: myEntity){
                    cat.add(entity.getProperty("Buying_Group_Code").getValue().toString()+"-"+entity.getProperty("Address").getValue().toString());
                }
            }
            return cat;
        }
        protected void onPostExecute(ArrayList<String> result){
            super.onPostExecute(result);
            cat_adpt = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,result);
            wcflist.setAdapter(cat_adpt);
        }
    }
}
