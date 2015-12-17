package just.lost.name;

import android.app.Activity;
import android.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Test01Activity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        
        Button recBtn = (Button)findViewById(R.id.recBtn);
        recBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(Test01Activity.this, CameraActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    
    
}