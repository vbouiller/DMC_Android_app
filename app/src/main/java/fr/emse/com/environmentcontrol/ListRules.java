package fr.emse.com.environmentcontrol;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;

import java.util.ArrayList;

import fr.emse.com.environmentcontrol.Model.RoomContextRule;

/**
 * Created by Valentin on 20/12/2017.
 */

public class ListRules extends Activity {
    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.list_activity);
        listView = (ListView) findViewById(R.id.listView);

        /*
        *
        * Activity to show list of rules
        *
        * */

        //ArrayList<RoomContextRule> rules = getIntent().getParcelableArrayListExtra("fr.emse.com.environmentcontrol.RULES");
        //System.out.println(rules);
        ResultAdapter adapter = new ResultAdapter(ListRules.this,new ArrayList<>() );
        listView.setAdapter(adapter);

        super.onCreate(savedInstanceState);
    }


}
