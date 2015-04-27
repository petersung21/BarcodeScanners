package fgfbrands.barcodescanners;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;
import android.app.ListActivity;
import android.widget.ArrayAdapter;
import org.odata4j.consumer.ODataClient;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntity;
import org.odata4j.jersey.consumer.ODataJerseyConsumer;

import zxing.IntentIntegrator;
import zxing.IntentResult;

/**
 * Created by psung on 3/13/2015.
 */
public class dataConsumption extends Activity{
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

        callbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View arg0){
                new wcfService().execute();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
       getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    public class wcfService extends AsyncTask<Void,Void,ArrayList<String>>{
        @Override
        protected ArrayList<String> doInBackground(Void...arg0){
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
            cat_adpt = new ArrayAdapter<String>(dataConsumption.this,android.R.layout.simple_list_item_1,result);
            wcflist.setAdapter(cat_adpt);
        }
    }
}