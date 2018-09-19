package com.ajoylab.blockchain.wallet.ui;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ajoylab.blockchain.wallet.R;

public abstract class BCBaseActivity extends AppCompatActivity {

	protected Toolbar setupToolbar() {
		Toolbar toolbar = findViewById(R.id.toolbar);
		if (null != toolbar) {
			setSupportActionBar(toolbar);
			toolbar.setTitle(getTitle());
		}
		enableDisplayHomeAsUp();
		return toolbar;
	}

	protected void setTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setTitle(title);
        }
    }

    protected void setSubtitle(String subtitle) {
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setSubtitle(subtitle);
        }
    }

	protected void enableDisplayHomeAsUp() {
		ActionBar actionBar = getSupportActionBar();
		if (null != actionBar) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	protected void disableDisplayHomeAsUp() {
		ActionBar actionBar = getSupportActionBar();
		if (null != actionBar) {
			actionBar.setDisplayHomeAsUpEnabled(false);
		}
	}

	protected void showToolbar() {
		ActionBar actionBar = getSupportActionBar();
		if (null != actionBar) {
			actionBar.show();
		}
	}

	protected void hideToolbar() {
        ActionBar actionBar = getSupportActionBar();
	    if (null != actionBar) {
	        actionBar.hide();
        }
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int i = item.getItemId();

		if (android.R.id.home == i) {
			finish();
		}

		return true;
	}
}
