package eu.pryds.ve;

import java.util.ArrayList;

import org.json.JSONObject;

import com.android.vending.billing.IInAppBillingService;

import eu.pryds.ve.util.IabHelper;
import eu.pryds.ve.util.IabHelper.OnConsumeFinishedListener;
import eu.pryds.ve.util.IabHelper.OnIabPurchaseFinishedListener;
import eu.pryds.ve.util.IabHelper.QueryInventoryFinishedListener;
import eu.pryds.ve.util.IabResult;
import eu.pryds.ve.util.Inventory;
import eu.pryds.ve.util.Purchase;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.content.ServiceConnection;

public class DonateActivity extends Activity implements OnClickListener {
    
    public static final String SKU_DONATE_SMALL = "donate10";
    public static final String SKU_DONATE_LARGER = "donate50";
    public static final String SKU_DONATE_LARGE = "donate100";
    
    /** TODO: This functionality needs to be throughly tested with real
     *  transactions (refundable) from an account that is not my
     *  developer account.
     *  
     *  Note: Must be tested with a developer signed .apk, i.e. produce signed
     *  package in Eclipse, upload to test phone, install and run.
     */
    
    ArrayList<String> skuList = new ArrayList<String>();
    IabHelper mHelper;
    IInAppBillingService mService;
    DonateActivity thisActivity;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        thisActivity = this;
        
        bindService(new 
                Intent("com.android.vending.billing.InAppBillingService.BIND"),
                        mServiceConn, Context.BIND_AUTO_CREATE);
        
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMII" +
                "BCgKCAQEA03wTaEj7aE+TJ3qnXMjkOCylWMTJKPHfEuexX6RaxY01y9vs1j" +
                "c/sJD0PHyPAL4WfMqVmFGc4yUwwSd688+2obBxTCq9rJAoXw0DTsUTKlLvG" +
                "Q/8r4ofi3fT3nCiXIrejwG6ihrUOzp9khtbY0yDVIgt3HAs2Rqkoy+DtBZS" +
                "sxWNJ7rPGujlhQhDo5X9winD9gUn2ZpeWzcEEs+AbfVGOpYFIJdwtpajUr2" +
                "9bJQ0/jrcZN8A2SEKmJHIJJvKRbkfLEX889/PYoCtmmR9ao3FesCNUX9XHT" +
                "JK/codZ/QB1rE+zeTTDy0PiB7liQ9x/98aj8phnt8HQKjmirYpLL+m2wIDAQAB";
        
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true); //TODO: Remove this line
        
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
               if (!result.isSuccess()) {
                  // Oh no, there was a problem.
                   new AlertDialog.Builder(thisActivity)
                   .setTitle(R.string.error)
                   .setMessage(getResources().getText(R.string.donate_init_error) + " " + result.toString())
                   .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           // do nothing
                       }
                   })
                   .show();
               }
               
               // Have we been disposed of in the meantime? If so, quit.
               if (mHelper == null)
                   return;
               
               // Query to get an inventory of stuff we own:
               mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
    }
    
    QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null)
                return;
            
            // Failure?
            if (result.isFailure()) {
                new AlertDialog.Builder(thisActivity)
                .setTitle(R.string.error)
                .setMessage(getResources().getText(R.string.donate_query_error) + " (mQueryFinishedListener) " + result.toString())
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
                return;
            }
            
            // Check for owned (i.e. not consumed) items. If found, consume immediately
            Purchase smallDonationPurchase = inventory.getPurchase(SKU_DONATE_SMALL);
            if (smallDonationPurchase != null) {
                mHelper.consumeAsync(inventory.getPurchase(SKU_DONATE_SMALL), mConsumeFinishedListener);
                return;
            }
            Purchase largerDonationPurchase = inventory.getPurchase(SKU_DONATE_LARGER);
            if (largerDonationPurchase != null) {
                mHelper.consumeAsync(inventory.getPurchase(SKU_DONATE_LARGER), mConsumeFinishedListener);
                return;
            }
            Purchase largeDonationPurchase = inventory.getPurchase(SKU_DONATE_LARGE);
            if (largeDonationPurchase != null) {
                mHelper.consumeAsync(inventory.getPurchase(SKU_DONATE_LARGE), mConsumeFinishedListener);
                return;
            }
            
            // Query for items available for purchase:
            ArrayList<String> skuList = new ArrayList<String>();
            skuList.add(SKU_DONATE_SMALL);
            skuList.add(SKU_DONATE_LARGER);
            skuList.add(SKU_DONATE_LARGE);
            final Bundle querySkus = new Bundle();
            querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
            
            Bundle skuDetails;
            try {
                skuDetails = mService.getSkuDetails(3,
                        getPackageName(), "inapp", querySkus);
            
                int response = skuDetails.getInt("RESPONSE_CODE");
                if (response == 0) {
                    ArrayList<String> responseList
                        = skuDetails.getStringArrayList("DETAILS_LIST");
                    
                    for (String thisResponse : responseList) {
                        JSONObject object = new JSONObject(thisResponse);
                        String sku = object.getString("productId");
                        String title = object.getString("title");
                        String price = object.getString("price");
                        String desc = object.getString("description");
                        if (sku.equals(SKU_DONATE_SMALL)) {
                            updateItem(title, price, desc,
                                    R.id.donate_small_title,
                                    R.id.donate_small_price,
                                    R.id.donate_small_desc,
                                    R.id.donate_small_button);
                        } else if (sku.equals(SKU_DONATE_LARGER)) {
                            updateItem(title, price, desc,
                                    R.id.donate_larger_title,
                                    R.id.donate_larger_price,
                                    R.id.donate_larger_desc,
                                    R.id.donate_larger_button);
                        } else if (sku.equals(SKU_DONATE_LARGE)) {
                            updateItem(title, price, desc,
                                    R.id.donate_large_title,
                                    R.id.donate_large_price,
                                    R.id.donate_large_desc,
                                    R.id.donate_large_button);
                        }
                    }
                }
            } catch (Exception e) {
                new AlertDialog.Builder(thisActivity)
                .setTitle(R.string.error)
                .setMessage(getResources().getText(R.string.donate_query_error) + " (check items for purchase) " + e.toString())
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
            }
            
        }
    };
    
    public void onClick(View v) {
        // React to Donate button clicks; initiate purchases:
        switch (v.getId()) {
        case R.id.donate_small_button:
            mHelper.launchPurchaseFlow(this, SKU_DONATE_SMALL, 10001,   
                    mPurchaseFinishedListener, "");
            break;
        case R.id.donate_larger_button:
            mHelper.launchPurchaseFlow(this, SKU_DONATE_LARGER, 10001,   
                    mPurchaseFinishedListener, "");
        case R.id.donate_large_button:
            mHelper.launchPurchaseFlow(this, SKU_DONATE_LARGE, 10001,   
                    mPurchaseFinishedListener, "");
        default:
            break;
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mHelper == null)
            return;
        
        //Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    
    OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            // if we were disposed of in the meantime, quit.
            if (mHelper == null)
                return;
            
            if (result.isFailure()) {
                new AlertDialog.Builder(thisActivity)
                    .setTitle(R.string.error)
                    .setMessage(getResources().getText(R.string.donate_purchase_consume_error) + " " + result.toString())
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
                return;
            }
            
            // Consume the purchased item (so it's ready for purchase again):
            mHelper.consumeAsync(purchase, mConsumeFinishedListener);
        }
    };
    
    OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        @Override
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            // if we were disposed of in the meantime, quit.
            if (mHelper == null)
                return;
            
            if (result.isSuccess()) {
                new AlertDialog.Builder(thisActivity)
                    .setMessage(R.string.donate_thanks)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
                    
            } else {
                new AlertDialog.Builder(thisActivity)
                .setTitle(R.string.error)
                .setMessage(getResources().getText(R.string.donate_purchase_consume_error) + " " + result.toString())
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
            }
            //(update UI not necessary)
        }
    };
    
    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, 
           IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
     };
    
    @Override
    public void onDestroy() {
       super.onDestroy();
       if (mHelper != null) {
           mHelper.dispose();
           mHelper = null;
       }
       if (mService != null) {
           unbindService(mServiceConn);
       }
    }
    
    private void updateItem(String title, String price, String desc, int titleId, int priceId, int descId, int buttonId) {
        TextView box;
        box = (TextView) findViewById(titleId);
        box.setText(title);
        
        box = (TextView) findViewById(priceId);
        box.setText(price);
        
        box = (TextView) findViewById(descId);
        box.setText(desc);
        
        Button button = (Button) findViewById(buttonId);
        button.setOnClickListener(this);
        button.setEnabled(true);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.donate, menu);
        return true;
    }
}
