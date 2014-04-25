package activity;

import com.example.pushmahjong.GameData;
import com.example.pushmahjong.MahJong;
import com.example.pushmahjong.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class MainActivity extends Activity {

	GameData gameData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		gameData = new GameData(this);
		
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.root_layout);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		rl.addView(gameData.getGameBoard(), lp);
		gameData.getGameBoard().startGame(gameData.getMahjongList());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
