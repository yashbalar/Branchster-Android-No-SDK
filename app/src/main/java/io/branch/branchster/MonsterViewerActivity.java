package io.branch.branchster;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import io.branch.branchster.fragment.InfoFragment;
import io.branch.branchster.util.MonsterImageView;
import io.branch.branchster.util.MonsterObject;
import io.branch.branchster.util.MonsterPreferences;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.BRANCH_STANDARD_EVENT;
import io.branch.referral.util.BranchEvent;
import io.branch.referral.util.LinkProperties;

public class MonsterViewerActivity extends FragmentActivity implements InfoFragment.OnFragmentInteractionListener {
    static final int SEND_SMS = 12345;

    private static String TAG = MonsterViewerActivity.class.getSimpleName();
    public static final String MY_MONSTER_OBJ_KEY = "my_monster_obj_key";

    TextView monsterUrl;
    View progressBar;

    MonsterImageView monsterImageView_;
    MonsterObject myMonsterObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monster_viewer);

        monsterImageView_ = (MonsterImageView) findViewById(R.id.monster_img_view);
        monsterUrl = (TextView) findViewById(R.id.shareUrl);
        progressBar = findViewById(R.id.progress_bar);

        // Change monster
        findViewById(R.id.cmdChange).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MonsterCreatorActivity.class);
                startActivity(i);
                finish();
            }
        });

        // More info
        findViewById(R.id.infoButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                InfoFragment infoFragment = InfoFragment.newInstance();
                ft.replace(R.id.container, infoFragment).addToBackStack("info_container").commit();
            }
        });

        //Share monster
        findViewById(R.id.share_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                shareMyMonster();
            }
        });

        initUI();
    }

    private void initUI() {
        myMonsterObject = getIntent().getParcelableExtra(MY_MONSTER_OBJ_KEY);

        if (myMonsterObject != null) {
            String monsterName = getString(R.string.monster_name);

            if (!TextUtils.isEmpty(myMonsterObject.getMonsterName())) {
                monsterName = myMonsterObject.getMonsterName();
            }

            ((TextView) findViewById(R.id.txtName)).setText(monsterName);
            String description = MonsterPreferences.getInstance(this).getMonsterDescription();

            if (!TextUtils.isEmpty(myMonsterObject.getMonsterDescription())) {
                description = myMonsterObject.getMonsterDescription();
            }

            ((TextView) findViewById(R.id.txtDescription)).setText(description);

            // set my monster image
            monsterImageView_.setMonster(myMonsterObject);

            /* TASK #2 #c Using an asynchronous method inside `initUI()`, get a shortURL with params,
            using the static keys provided in `MonsterPreferences` and the values from
            `myMonsterObject.prepareBranchDict()`. Put that URL into the
            `monsterUrl` TextView, and be sure to move the
            `progressBar.setVisibility(View.GONE);` inside the completionhandler
            method(generateShortUrl) is asynchronous*/
            BranchUniversalObject branchUniversalObject = myMonsterObject.toBranchUniversalObject();

            LinkProperties lp = new LinkProperties()
                    .setChannel("sms")
                    .setFeature("sharing")
                    .setCampaign("SMS 1");

            branchUniversalObject.generateShortUrl(this, lp, new Branch.BranchLinkCreateListener() {
                @Override
                public void onLinkCreate(String url, BranchError error) {
                    if (error == null) {
                        monsterUrl.setText(url);
                        Log.i("BRANCH SDK", "got my Branch link to share: " + url);
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });

        } else {
            Log.e(TAG, "Monster is null. Unable to view monster");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        logViewBranchEvent();
    }

    private void logViewBranchEvent() {

        /* TASK #2 #b Track that the user visited the monster view page (call event "monster_view")
        on the MonsterViewerActivity. This time, include state information (specified
        in `myMonsterObject.monsterMetaData()`)*/
        new BranchEvent("monster_view")
                .addContentItems(myMonsterObject.toBranchUniversalObject())
                .logEvent(MonsterViewerActivity.this);
    }

    private void logShareBranchEvent() {

        /* TASK #2 #d Track successful Share via Branch */
        new BranchEvent(BRANCH_STANDARD_EVENT.SHARE)
                .addContentItems(myMonsterObject.toBranchUniversalObject())
                .logEvent(MonsterViewerActivity.this);
    }

    /**
     * Method to share my custom monster with sharing with Branch Share sheet
     */
    private void shareMyMonster() {
        progressBar.setVisibility(View.VISIBLE);

        /* TASK #2 #d #e Inside `shareMyMonster`, get a short URL with params. Get the params
        from `myMonsterObject.prepareBranchDict()`, set the channel to be "sms",
        and make sure that you use an asynchronous method.
        method(generateShortUrl) is asynchronous */

        BranchUniversalObject branchUniversalObject = myMonsterObject.toBranchUniversalObject();

        LinkProperties lp = new LinkProperties()
                .setChannel("sms")
                .setFeature("sharing")
                .setCampaign("SMS 1");

        branchUniversalObject.generateShortUrl(this, lp, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                if (error == null) {
                    Log.i("BRANCH SDK", "got my Branch link to share: " + url);
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_TEXT, String.format("Check out my Branchster named %s at %s", myMonsterObject.getMonsterName(), url));
                    startActivityForResult(i, SEND_SMS);
                } else {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (SEND_SMS == requestCode) {
            if (RESULT_OK == resultCode) {
                logShareBranchEvent();
            }
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Exit")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).create().show();
        }
    }


    @Override
    public void onFragmentInteraction() {
        //no-op
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initUI();
    }
}
