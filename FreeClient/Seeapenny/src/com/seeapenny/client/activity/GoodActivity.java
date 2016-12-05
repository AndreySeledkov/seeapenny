package com.seeapenny.client.activity;


import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.seeapenny.client.R;
import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.SynchAction;
import com.seeapenny.client.adapter.CopyShopListAdapter;
import com.seeapenny.client.adapter.ExpandableGoodByCategoryAdapter;
import com.seeapenny.client.adapter.Good;
import com.seeapenny.client.adapter.GoodAdapter;
import com.seeapenny.client.adapter.GoodNameAdapter;
import com.seeapenny.client.adapter.MeasureAdapter;
import com.seeapenny.client.adapter.ShopList;
import com.seeapenny.client.http.HttpCommand;
import com.seeapenny.client.list.ListHandler;
import com.seeapenny.client.list.ListItemsFactory;
import com.seeapenny.client.quickaction.ActionItem;
import com.seeapenny.client.quickaction.QuickAction;
import com.seeapenny.client.server.GoodResponse;
import com.seeapenny.client.server.Response;
import com.seeapenny.client.server.State;
import com.seeapenny.client.server.responses.GoodResponseWrapper;
import com.seeapenny.client.server.responses.ExecuteGoodResult;
import com.seeapenny.client.server.responses.ExecuteListResult;
import com.seeapenny.client.service.Services;
import com.seeapenny.client.service.ShopListService;
import com.seeapenny.client.util.ExtrasConst;
import com.seeapenny.client.util.FieldGood;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

public class GoodActivity extends SeeapennyActivity implements ListItemsFactory<Good> {

    private static final int ACTION_ITEM_STATE = 0;
    private static final int ACTION_ITEM_EDIT = 1;
    private static final int ACTION_ITEM_MOVE = 2;
    private static final int ACTION_ITEM_REMOVE = 3;
    private static final int ACTION_ITEM_COPY = 4;

    private GoodAdapter goodAdapter;
    private ExpandableGoodByCategoryAdapter goodByCategoryAdapter;
    private ExpandableListView goodByCategoryListView;


    private ListView goodListView;

    private AutoCompleteTextView addNewGoodEditText;
    private View btnSpeak;
    private View btnDone;

    private ListView shopGoodNamesListView;
    private View shopListNamesView;

    private Good selectedGoodResponse;

    private QuickAction mQuickAction;
    private com.actionbarsherlock.view.Menu headerMenu;

    private ActionBar actionBar;
    private View headerOptions;

    private TextView listTime;

    private Button emptyGoodButton;

    private MenuItem menuItemActionView;

    private View goodAdditional;
    private LinearLayout goodAdditionalView;

    private EditText goodAdditionalPrice;
    private EditText goodAdditionalQuantity;
    private Spinner goodAdditionalMeasure;
    private View goodAdditionalImportant;
    private boolean isPriority;

    private View minus;
    private View plus;

    private View cover;
    private Long listID;
    private Long ownerID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goods);

        initActionBar();
        initQuickAction();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            listID = (Long) extras.get(ExtrasConst.LIST_ID);
            ownerID = (Long) extras.get(ExtrasConst.OWNER_ID);
        }

        ShopList shopListResponse = Services.getListService().getShopList(listID, ownerID);


        listTime = (TextView) findView(R.id.listTime);
        setListTime(shopListResponse.getLastModifiedTime());

        TextView listTitle = (TextView) findView(R.id.listTitle);
        listTitle.setText(Html.fromHtml(resources.getString(R.string.list, "\"<b>" + shopListResponse.getName() + "</b>\"")));

        goodListView = (ListView) findViewById(R.id.list);

        goodAdapter = new GoodAdapter(null, this, this);


        goodByCategoryListView = (ExpandableListView) findViewById(R.id.ex_list);

        goodByCategoryAdapter = new ExpandableGoodByCategoryAdapter(this, new ArrayList<String>(), new ArrayList<ArrayList<Good>>(), null, this);
        goodByCategoryListView.setAdapter(goodByCategoryAdapter);


        emptyGoodButton = (Button) findView(R.id.empty);

        goodAdditionalView = (LinearLayout) findView(R.id.good_additional_view);


        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view;

        if (getWindowManager().getDefaultDisplay().getWidth() > 480 || getWindowManager().getDefaultDisplay().getWidth() > getWindowManager().getDefaultDisplay().getHeight()) {
            view = inflater.inflate(R.layout.good_additional_large, null);
        } else {
            view = inflater.inflate(R.layout.good_additional_small, null);
        }

        goodAdditionalView.addView(view);

        goodAdditional = findView(R.id.good_additional);

        goodAdditionalPrice = (EditText) view.findViewById(R.id.good_additional_price);
        goodAdditionalQuantity = (EditText) view.findViewById(R.id.good_additional_quantity);

        goodAdditionalMeasure = (Spinner) view.findViewById(R.id.goods_measure);

        String[] measures = resources.getStringArray(R.array.measure_array);
        MeasureAdapter measureAdapter = new MeasureAdapter(this, 0, measures);
        goodAdditionalMeasure.setAdapter(measureAdapter);

        minus = view.findViewById(R.id.minus);
        plus = view.findViewById(R.id.plus);

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = goodAdditionalQuantity.getText().toString().trim();
                if (str.length() != 0) {

                    try {
                        double quantity = Double.valueOf(goodAdditionalQuantity.getText().toString().trim());
                        if (quantity > 1) {
                            goodAdditionalQuantity.setText(String.format("%.1f", quantity - 1).replace(",", "."));
                        } else {
                            goodAdditionalQuantity.setText(null);
                        }
                    } catch (Exception e) {

                    }
                }
            }
        });


        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = goodAdditionalQuantity.getText().toString().trim();
                if (str.length() == 0) {
                    goodAdditionalQuantity.setText(String.valueOf(1.0F));
                } else {
                    try {
                        double quantity = Double.valueOf(goodAdditionalQuantity.getText().toString().trim()) + 1F;
                        goodAdditionalQuantity.setText(String.format("%.1f", quantity).replace(",", "."));
                    } catch (Exception e) {

                    }
                }
            }
        });

        goodAdditionalImportant = view.findViewById(R.id.good_important);
        goodAdditionalImportant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPriority = !isPriority;
                goodAdditionalImportant.setSelected(!goodAdditionalImportant.isSelected());
            }
        });

        TextView goodImportantText = (TextView) view.findViewById(R.id.good_important_text);
        goodImportantText.setText(Html.fromHtml("<font color=\"#FFFFFF\"><a href=\"\">" + resources.getString(R.string.important_good) + "</></font>"));

        goodImportantText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPriority = !isPriority;
                goodAdditionalImportant.setSelected(!goodAdditionalImportant.isSelected());
            }
        });

        cover = findView(R.id.cover);
        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuItemActionView.collapseActionView();
            }
        });

        shopGoodNamesListView = (ListView) findViewById(R.id.shop_list_names);
        shopListNamesView = findViewById(R.id.shop_list_names_view);
    }

    private void initActionBar() {

        actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);

        if (!app.getSessionState().isSessionValid()) {
            actionBar.setIcon(R.drawable.header_offline);
        } else {
            actionBar.setIcon(R.drawable.header_online);
        }

        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        headerOptions = mInflater.inflate(R.layout.header_options, null);
        actionBar.setCustomView(headerOptions);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    private void initQuickAction() {
        mQuickAction = new QuickAction(this, QuickAction.HORIZONTAL);
        mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction quickAction, int pos, int actionId) {
                switch (actionId) {
                    case ACTION_ITEM_STATE:

                        if (!app.getSessionState().isSessionValid()) {

                            EnumSet<FieldGood> fieldGood = FieldGood.createEmptySaveTypes();

                            Good good = goodService.getGoodById(selectedGoodResponse.getListId(), selectedGoodResponse.getId(), selectedGoodResponse.getOwnerId());
                            good.changeState(selectedGoodResponse.getState() == State.NORMAL.getId() ? State.BOUGHT.getId() : State.NORMAL.getId(), fieldGood);
                            good.changeLastEditor(app.getOwnerID(), fieldGood);


                            if (fieldGood.size() > 0) {
                                Date date = new Date();

                                good.changeSynchAction(fieldGood);

                                good.setModifiedTime(date);
                                fieldGood.add(FieldGood.MODIFIED_TIME);

                                boolean ok = goodService.save(fieldGood, good);
                                if (ok) {

                                    ShopList shopList = Services.getListService().getShopList(listID, ownerID);
                                    shopList.setLastModifiedTime(new Date());

                                    shopList.changeSynchAction();
                                    shopListService.save(shopList);

                                    setListTime(shopList.getLastModifiedTime());

                                    refreshGoodItems();

                                    if (selectedGoodResponse.getState() == State.BOUGHT.getId()) {
                                        makeVibrate();
                                    }
                                }
                            }

                            selectedGoodResponse = null;

                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(addNewGoodEditText.getWindowToken(), 0);

                            menuItemActionView.collapseActionView();

                        } else {
                            if (!waitResponse) {
                                disableWaitForNextCommand();
                                changeStateGood = new HttpCommand(GoodActivity.this, new GoodResponseWrapper());
                                changeStateGood.addParam(LIST_OWNER_ID, selectedGoodResponse.getOwnerId());
                                changeStateGood.addParam(LIST_ID_PARAM, selectedGoodResponse.getListId());
                                changeStateGood.addParam(GOOD_ID_PARAM, selectedGoodResponse.getId());

                                changeStateGood.addParam(GOOD_STATE, selectedGoodResponse.getState() == State.NORMAL.getId() ? State.BOUGHT.getId() : State.NORMAL.getId());

                                changeStateGood.sendAsForm(SeeapennyApp.getHttpHandler(), SeeapennyActivity.SAVE_GOODS);
                            }
                        }

                        selectedGoodResponse = null;
                        break;
                    case ACTION_ITEM_EDIT:
                        actionItemEdit();

                        break;
                    case ACTION_ITEM_MOVE:
                        showMoveDialog(selectedGoodResponse.getId());
                        break;
                    case ACTION_ITEM_COPY:
                        showCopyDialog(selectedGoodResponse.getId());
                        break;
                    case ACTION_ITEM_REMOVE:
                        confirmRemoveGood(selectedGoodResponse.getId());
                        break;
                }
                goodAdapter.notifyDataSetChanged();
            }
        });


        mQuickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
            @Override
            public void onDismiss() {
                selectedGoodResponse = null;
                goodAdapter.notifyDataSetChanged();
            }
        });
    }

    private void actionItemEdit() {
        headerMenu.performIdentifierAction(R.id.add_item, 0);

        addNewGoodEditText.setText(selectedGoodResponse.getName());
        addNewGoodEditText.setSelection(selectedGoodResponse.getName().length());
        addNewGoodEditText.setTag(selectedGoodResponse.getId());
        addNewGoodEditText.requestFocus();


        if (selectedGoodResponse.getPrice() > 0) {
            goodAdditionalPrice.setText(String.valueOf(selectedGoodResponse.getPrice()));
        } else {
            goodAdditionalPrice.setText(null);
        }

        if (selectedGoodResponse.getQuantity() > 0) {
            goodAdditionalQuantity.setText(String.valueOf(selectedGoodResponse.getQuantity()));
        } else {
            goodAdditionalQuantity.setText(null);
        }

        goodAdditionalMeasure.setSelection(selectedGoodResponse.getMeasure());

        goodAdditionalImportant.setSelected(selectedGoodResponse.isPriority());
        isPriority = selectedGoodResponse.isPriority();
    }

    @Override
    public void beforeSend(HttpCommand command) {
        super.beforeSend(command);

        final AnimationDrawable homeDrawable = (AnimationDrawable) getResources().getDrawable(R.drawable.header_progress_animation);

        actionBar.setIcon(homeDrawable);

        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                homeDrawable.start();
            }
        });
    }

    @Override
    public void afterReceive(HttpCommand httpCommand) {
        super.afterReceive(httpCommand);

        if (app.isAuthorized()) {
            actionBar.setIcon(R.drawable.header_online);
        } else {
            actionBar.setIcon(R.drawable.header_offline);
        }
    }

    @Override
    public void onResume() {
        super.onResume();


        if (app.getSessionState().isSessionValid()) {
            actionBar.setIcon(R.drawable.header_online);
        } else {
            actionBar.setIcon(R.drawable.header_offline);
        }
        isPriority = false;

        ShopList shopList = shopListService.getShopList(listID, ownerID);
        setListTime(shopList.getLastModifiedTime());

        refreshGoodItems();
    }

    public void refreshGoodItems() {
        ShopList shopList = Services.getListService().getShopList(listID, ownerID);

        ArrayList<Good> goods = goodService.getAllGood(shopList.getId(), shopList.getOwnerId());
        if ("category".equals(app.getGoodSort())) {
            String[] innerCategories = SeeapennyApp.getInstance().getResources().getStringArray(R.array.categories);
            ArrayList<String> categories = new ArrayList<String>();

            ArrayList<ArrayList<Good>> childList = new ArrayList<ArrayList<Good>>();

            int currentCategoryId = -1;

            ArrayList<Good> goodCategory = new ArrayList<Good>();
            for (Good good : goods) {
                if (good.getCategoryId() > currentCategoryId) {
                    goodCategory = new ArrayList<Good>();

                    if (good.getCategoryId() == 0) {
                        categories.add(resources.getString(R.string.without_category));
                    } else {
                        categories.add(innerCategories[good.getCategoryId() - 1]);
                    }

                    childList.add(goodCategory);
                    currentCategoryId = good.getCategoryId();
                }


                goodCategory.add(good);
            }


            goodByCategoryAdapter.refreshAllItems(categories, childList);
            for (int i = 0; i < categories.size(); i++) {
                goodByCategoryListView.expandGroup(i);
            }
            goodListView.setVisibility(View.GONE);
            goodByCategoryListView.setVisibility(View.VISIBLE);

        } else {
            goodAdapter.refreshAllItems(goods);

            goodByCategoryListView.setVisibility(View.GONE);
            goodListView.setVisibility(View.VISIBLE);
        }

        if (goods.isEmpty()) {
            emptyGoodButton.setVisibility(View.VISIBLE);
            emptyGoodButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    headerMenu.performIdentifierAction(R.id.add_item, 0);

                    addNewGoodEditText.requestFocus();

                }
            });
        } else {
            emptyGoodButton.setVisibility(View.GONE);
            emptyGoodButton.setOnClickListener(null);
        }

    }

    private int countLeftGoods(List<Good> goodList) {
        int index = 0;
        for (Good good : goodList) {
            if (State.valueOf(good.getState()) == State.NORMAL) {
                index++;
            }
        }
        return index;
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {

        headerMenu = menu;

        headerMenu.clear();

        com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu_good, headerMenu);

        initActionView();

        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu:
                actionMenuSelected(item);
                break;
            case R.id.sort:
                showSortDialog();
                break;
            case R.id.deleteAllList:
                confirmRemoveAllGood();
                break;
            case R.id.changeStateAll:
                changeStatesAllGood();
                break;
            case R.id.shareList:

                showShareDialog();
                break;
            case R.id.settings:
                intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
        }


        return super.onMenuItemSelected(featureId, item);
    }

    private void showShareDialog() {
        final ShopList shopList = shopListService.getShopList(listID, ownerID);

        final Dialog dialog = new Dialog(this, R.style.MyDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.share_dialog);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                goodAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        View shareAccount = dialog.findViewById(R.id.shareAccount);
        if (app.getSessionState().isSessionValid()) {
            shareAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(GoodActivity.this, ShareActivity.class);
                    intent.putExtra(ExtrasConst.LIST_ID, shopList.getId());
                    startActivity(intent);

                    dialog.dismiss();

                }
            });
            shareAccount.setVisibility(View.VISIBLE);
            dialog.findViewById(R.id.shareAccountView).setVisibility(View.VISIBLE);
        }


        View shareService = dialog.findViewById(R.id.shareService);
        shareService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showListShare(shopList);
                goodAdapter.notifyDataSetChanged();
            }
        });
        View cancel = dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                goodAdapter.notifyDataSetChanged();
            }
        });


        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        int pxWidth = (int) ((300 * displayMetrics.density) + 0.5);
        int pxHeight = (int) ((500 * displayMetrics.density) + 0.5);
        dialog.getWindow().setLayout(pxWidth, pxHeight);

        if (!isFinishing()) {
            dialog.show();
        }
    }

    private void actionMenuSelected(MenuItem item) {
        final ShopList shopList = shopListService.getShopList(listID, ownerID);

        MenuItem menuItemDeleteAllList = item.getSubMenu().findItem(R.id.deleteAllList);
        MenuItem menuItemExcludedAllList = item.getSubMenu().findItem(R.id.changeStateAll);
        MenuItem menuItemShareList = item.getSubMenu().findItem(R.id.shareList);
        MenuItem menuItemSortList = item.getSubMenu().findItem(R.id.sort);

        if (goodAdapter.isEmpty() && goodByCategoryAdapter.isEmpty()) {
            menuItemDeleteAllList.setVisible(false);
            menuItemExcludedAllList.setVisible(false);
            menuItemShareList.setVisible(false);
            menuItemSortList.setVisible(false);
        } else {
            menuItemDeleteAllList.setVisible(true);
            menuItemExcludedAllList.setVisible(true);
            menuItemShareList.setVisible(true);
            menuItemSortList.setVisible(true);

            ArrayList<Good> goodList = goodService.getAllGood(shopList.getId(), shopList.getOwnerId());

            boolean allExcluded = true;
            for (Good good : goodList) {
                if (State.valueOf(good.getState()) == State.NORMAL) {
                    menuItemExcludedAllList.setTitle(R.string.excluded_all);
                    menuItemExcludedAllList.setIcon(R.drawable.action_item_exclude_all);
                    allExcluded = false;
                    break;
                }
            }
            if (allExcluded) {
                menuItemExcludedAllList.setTitle(R.string.goodBackAll);
                menuItemExcludedAllList.setIcon(R.drawable.action_item_deselect_all);

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.add_item:
                if (app.getVoiceRecognitionScreen()) {
                    btnSpeak.setVisibility(View.VISIBLE);
                    btnDone.setVisibility(View.GONE);
                } else {
                    btnSpeak.setVisibility(View.GONE);
                    btnDone.setVisibility(View.VISIBLE);
                }

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                addNewGoodEditText.requestFocus();
                addNewGoodEditText.setTag(null);


                goodAdditionalPrice.setText(null);
                goodAdditionalQuantity.setText(null);
                goodAdditionalMeasure.setSelection(0);
                goodAdditionalImportant.setSelected(false);
                isPriority = false;

                break;
        }

        return true;
    }

    private void initActionView() {
        final ShopList shopList = shopListService.getShopList(listID, ownerID);

        menuItemActionView = headerMenu.getItem(0);
        View view = menuItemActionView.getActionView();

        menuItemActionView.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                cover.setVisibility(View.VISIBLE);
                headerMenu.getItem(1).setVisible(false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                headerMenu.getItem(1).setVisible(true);
                addNewGoodEditText.setText(null);
                goodAdditional.setVisibility(View.GONE);
                cover.setVisibility(View.GONE);


                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(addNewGoodEditText.getWindowToken(), 0);
                return true;
            }
        });

        btnDone = view.findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = addNewGoodEditText.getText().toString().trim();
                Long obj = (Long) addNewGoodEditText.getTag();

                if (title.length() > 0) {
                    if (!goodService.isExist(shopList.getId(), shopList.getOwnerId(), obj, title)) {

                        String priceStr = goodAdditionalPrice.getText().toString().trim();
                        String quantityStr = goodAdditionalQuantity.getText().toString().trim();

                        double price = 0F;
                        if (priceStr.length() != 0) {
                            try {
                                price = Double.valueOf(priceStr);
                            } catch (Exception e) {

                            }
                        }

                        double quantity = 0F;
                        if (quantityStr.length() != 0) {

                            try {
                                quantity = Double.valueOf(quantityStr);
                            } catch (Exception e) {

                            }
                        }

                        int measure = goodAdditionalMeasure.getSelectedItemPosition();

                        if (obj != null) {
                            long localGoodId = obj;
                            sendChangeGood(localGoodId, title, price, quantity, measure, isPriority);

                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(addNewGoodEditText.getApplicationWindowToken(), 0);

                            menuItemActionView.collapseActionView();

                            addNewGoodEditText.setTag(null);

                        } else {


                            sendGood(title, price, quantity, measure, isPriority);

                            goodAdditionalMeasure.setSelection(0);
                            goodAdditionalPrice.setText(null);
                            goodAdditionalQuantity.setText(null);
                            goodAdditionalImportant.setSelected(false);
                            isPriority = false;
                        }
                    } else {
                        addNewGoodEditText.setError(resources.getString(R.string.goodExist));
                        addNewGoodEditText.requestFocus();
                    }
                }
            }
        });

        final ImageView fullGood = (ImageView) view.findViewById(R.id.fullGood);
        fullGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GoodActivity.this, EditGoodActivity.class);

                Object obj = addNewGoodEditText.getTag();

                String name = addNewGoodEditText.getText().toString().trim();
                String price = goodAdditionalPrice.getText().toString().trim();
                String quantity = goodAdditionalQuantity.getText().toString().trim();
                int measure = goodAdditionalMeasure.getSelectedItemPosition();

                intent.putExtra("NAME", name);

                if (price.length() > 0) {
                    try {
                        intent.putExtra("PRICE", Double.valueOf(price));
                    } catch (Exception e) {
                    }
                }

                if (quantity.length() > 0) {
                    try {
                        intent.putExtra("QUANTITY", Double.valueOf(quantity));
                    } catch (Exception e) {
                    }
                }
                intent.putExtra("MEASURE", measure);
                intent.putExtra("IS_PRIORITY", isPriority);

                if (obj != null) {
                    intent.putExtra("GOOD_ITEM", (Long) obj);
                }

                menuItemActionView.collapseActionView();

                intent.putExtra("LIST_ITEM", shopList.getId());
                intent.putExtra("OWNER_ID", shopList.getOwnerId());
                startActivity(intent);
            }
        });


        addNewGoodEditText = (AutoCompleteTextView ) view.findViewById(R.id.addNewGood);
        addNewGoodEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String title = addNewGoodEditText.getText().toString().trim();
                Long obj = (Long) addNewGoodEditText.getTag();

                if (title.length() > 0) {
                    if (!goodService.isExist(shopList.getId(), shopList.getOwnerId(), obj, title)) {

                        String priceStr = goodAdditionalPrice.getText().toString().trim();
                        String quantityStr = goodAdditionalQuantity.getText().toString().trim();

                        double price = 0F;
                        try {
                            if (priceStr.length() != 0) {
                                price = Double.valueOf(priceStr);
                            }
                        } catch (Exception e) {

                        }

                        double quantity = 0F;
                        try {

                            if (quantityStr.length() != 0) {
                                quantity = Double.valueOf(quantityStr);
                            }
                        } catch (Exception e) {

                        }

                        int measure = goodAdditionalMeasure.getSelectedItemPosition();


                        if (obj != null) {
                            long localGoodId = obj;
                            sendChangeGood(localGoodId, title, price, quantity, measure, isPriority);

                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(addNewGoodEditText.getApplicationWindowToken(), 0);

                            menuItemActionView.collapseActionView();

                            addNewGoodEditText.setTag(null);

                            return false;
                        } else {
                            sendGood(title, price, quantity, measure, isPriority);
                            return true;
                        }
                    } else {
                        addNewGoodEditText.setError(resources.getString(R.string.goodExist));
                        addNewGoodEditText.requestFocus();
                    }
                }
                return true;
            }
        });

        addNewGoodEditText.setOnTouchListener(new RightDrawableOnTouchListener(addNewGoodEditText) {
            @Override
            public boolean onDrawableTouch(final MotionEvent event) {
                addNewGoodEditText.setText(null);

                return true;
            }
        });


        final GoodNameAdapter adapter = new GoodNameAdapter(this, 0, app.getListName());
        adapter.setFactory(new ListItemsFactory<String>() {
            @Override
            public void createMoreItems(ListHandler<String> handler, int position) {

            }

            @Override
            public void setOnClickListener(String item, View view) {
                addNewGoodEditText.setText(item);
                shopListNamesView.setVisibility(View.GONE);
            }
        });
        shopGoodNamesListView.setAdapter(adapter);
        addNewGoodEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence cs, int arg1, int arg2, int length) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int length = editable.toString().length();

                adapter.getFilter().filter(editable);
                if (length >= 2) {
                    shopListNamesView.setVisibility(View.VISIBLE);
                } else {
                    shopListNamesView.setVisibility(View.GONE);
                }

                if (app.getVoiceRecognitionScreen()) {
                    if (length >= 1) {
                        btnSpeak.setVisibility(View.GONE);
                        btnDone.setVisibility(View.VISIBLE);
                        fullGood.setVisibility(View.VISIBLE);
                        goodAdditional.setVisibility(View.VISIBLE);
                    } else if (length < 1) {
                        btnSpeak.setVisibility(View.VISIBLE);
                        btnDone.setVisibility(View.GONE);
                        goodAdditional.setVisibility(View.GONE);
                    }
                } else {
                    if (length >= 1) {
                        goodAdditional.setVisibility(View.VISIBLE);
                    } else if (length < 1) {
                        goodAdditional.setVisibility(View.GONE);
                    }

                    btnSpeak.setVisibility(View.GONE);
                    btnDone.setVisibility(View.VISIBLE);
                    fullGood.setVisibility(View.VISIBLE);
                }

            }
        });

        btnSpeak = view.findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault().toString());

                try {
                    startActivityForResult(intent, GoodActivity.RESULT_SPEECH);
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(GoodActivity.this,
                            resources.getString(R.string.not_support_speech_to_text),
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });


        if (app.getVoiceRecognitionScreen()) {
            btnSpeak.setVisibility(View.VISIBLE);
            btnDone.setVisibility(View.GONE);
        } else {
            btnSpeak.setVisibility(View.GONE);
            btnDone.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void createMoreItems(ListHandler<Good> myListListHandler, int position) {

    }

    @Override
    protected void onSuccessfullResponse(HttpCommand command, Response response) {
        ShopList shopListResponse = shopListService.getShopList(listID, ownerID);
        if (changeStateGood == command) {
            GoodResponseWrapper goodResponseWrapper = (GoodResponseWrapper) response;
            GoodResponse goodResponseResponse = goodResponseWrapper.getGoodResponse();

            EnumSet<FieldGood> fields = FieldGood.createEmptySaveTypes();

            Good good = goodService.getGoodById(goodResponseResponse.getListId(), goodResponseResponse.getId(), goodResponseResponse.getOwnerId());
            good.changeState(goodResponseResponse.getState(), fields);

            if (fields.size() > 0) {

                Date serverLastModifiedTime = goodResponseResponse.getModifiedTime();
                Date localLastModifiedTime = app.convertToLocalDate(serverLastModifiedTime);

                good.setModifiedTime(localLastModifiedTime);
                fields.add(FieldGood.MODIFIED_TIME);

                boolean ok = goodService.save(fields, good);
                if (ok) {
                    shopListResponse.setLastModifiedTime(localLastModifiedTime);
                    shopListService.save(shopListResponse);
                }
                setListTime(serverLastModifiedTime);

                refreshGoodItems();
                if (good.getState() == State.BOUGHT.getId()) {
                    makeVibrate();
                }
            }
        } else if (saveGood == command) {

            GoodResponseWrapper goodResponseWrapper = (GoodResponseWrapper) response;
            GoodResponse goodResponseResponse = goodResponseWrapper.getGoodResponse();

            EnumSet<FieldGood> fields = FieldGood.createEmptySaveTypes();

            Good good = goodService.getGoodById(goodResponseResponse.getListId(), goodResponseResponse.getId(), goodResponseResponse.getOwnerId());

            good.changeName(goodResponseResponse.getName(), fields);
            good.changeCategory(goodResponseResponse.getCategoryId(), fields);
            good.changePrice(goodResponseResponse.getPrice(), fields);
            good.changeState(goodResponseResponse.getState(), fields);
            good.changeQuantity(goodResponseResponse.getQuantity(), fields);
            good.changeMeasure(goodResponseResponse.getMeasure(), fields);
            good.changePriority(goodResponseResponse.isPriority(), fields);
            good.changeNote(goodResponseResponse.getNote(), fields);
            good.changeLastEditor(goodResponseResponse.getLastEditorId(), fields);
            good.changeImageID(goodResponseResponse.getImageId(), fields);

            if (fields.size() > 0) {

                Date serverLastModifiedTime = goodResponseResponse.getModifiedTime();
                Date localLastModifiedTime = app.convertToLocalDate(serverLastModifiedTime);

                good.setModifiedTime(localLastModifiedTime);
                fields.add(FieldGood.MODIFIED_TIME);

                boolean ok = goodService.save(fields, good);
                if (ok) {
                    shopListResponse.setLastModifiedTime(localLastModifiedTime);
                    shopListService.save(shopListResponse);
                }
                setListTime(serverLastModifiedTime);

                refreshGoodItems();
                makeSoundAdd();
            }
        } else if (addGood == command || copyGood == command) {
            GoodResponseWrapper goodResponseWrapper = (GoodResponseWrapper) response;
            GoodResponse goodResponseResponse = goodResponseWrapper.getGoodResponse();

            Date serverCreateTime = goodResponseResponse.getCreateTime();
            Date serverLastModifiedTime = goodResponseResponse.getModifiedTime();
            Date localLastModifiedTime = app.convertToLocalDate(serverLastModifiedTime);
            Date localCreateTime = app.convertToLocalDate(serverCreateTime);

            Good good = goodService.insert(goodResponseResponse.getListId(), goodResponseResponse.getName(), goodResponseResponse.getPrice(), goodResponseResponse.getQuantity(), goodResponseResponse.getMeasure(), goodResponseResponse.isPriority(), goodResponseResponse.getNote(),
                    goodResponseResponse.getCategoryId(), goodResponseResponse.getImageId(), goodResponseResponse.getState(), goodResponseResponse.getOwnerId(), goodResponseResponse.getLastEditorId(),
                    app.formatDatetime(localCreateTime), app.formatDatetime(localLastModifiedTime), SynchAction.NO_CHANGES);
            if (good != null) {
                if (shopListResponse.isShared()) {
                    //todo
                }

                ShopList copyShopList = shopListService.getShopList(good.getListId(), good.getOwnerId());
                copyShopList.setLastModifiedTime(localLastModifiedTime);
                shopListService.save(copyShopList);

                if (copyShopList.getId() == shopListResponse.getId()) {
                    setListTime(serverLastModifiedTime);
                    refreshGoodItems();
                }

                makeSoundAdd();
            }
        } else if (removeGood == command) {
            ExecuteGoodResult removeGoodResponse = (ExecuteGoodResult) response;

            Date serverLastModifiedTime = removeGoodResponse.getModifiedTime();
            Date localLastModifiedTime = app.convertToLocalDate(serverLastModifiedTime);


            Good good = goodService.getGoodById(removeGoodResponse.getListId(), removeGoodResponse.getGoodId(), removeGoodResponse.getOwnerId());
            boolean ok = goodService.remove(good.getId(), good.getListId(), good.getOwnerId());
            if (ok) {
                shopListResponse.setLastModifiedTime(localLastModifiedTime);
                shopListService.save(shopListResponse);

                setListTime(localLastModifiedTime);
                refreshGoodItems();
                makeSoundRemove();
            }
        } else if (removeAllGood == command) {
            ExecuteListResult removeGoodResponse = (ExecuteListResult) response;

            if (removeGoodResponse.isExecuted()) {
                goodService.removeAllGoods(shopListResponse.getId(), shopListResponse.getOwnerId());

                shopListResponse.setLastModifiedTime(removeGoodResponse.getModifiedTime());

                shopListService.save(shopListResponse);
                setListTime(removeGoodResponse.getModifiedTime());

                refreshGoodItems();

                makeSoundRemove();
            }

        } else if (changeStateAllGood == command) {
            ExecuteListResult executeListResultResponse = (ExecuteListResult) response;

            boolean ok = false;

            List<Good> goodResponseList = goodService.getAllGood(executeListResultResponse.getId(), executeListResultResponse.getOwnerId());
            for (Good good : goodResponseList) {
                EnumSet<FieldGood> fieldGoods = FieldGood.createEmptySaveTypes();
                if (shopListResponse.getCountLeftGoods() > 0) {
                    good.setState(State.BOUGHT.getId());
                } else {
                    good.setState(State.NORMAL.getId());
                }
                fieldGoods.add(FieldGood.STATE);
                ok |= goodService.save(fieldGoods, good);
            }

            if (ok) {
                shopListResponse.setLastModifiedTime(executeListResultResponse.getModifiedTime());
                shopListService.save(shopListResponse);

                setListTime(executeListResultResponse.getModifiedTime());

                refreshGoodItems();
                if (shopListResponse.getCountLeftGoods() == 0) {
                    makeVibrate();
                }
            }
        }

        waitResponse = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && null != data) {

            ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            addNewGoodEditText.append(text.get(0));
        }
    }

    private void sendGood(String title, double price, double quantity, int measure, boolean priority) {
        ShopList shopListResponse = Services.getListService().getShopList(listID, ownerID);

        if (!app.isAuthorized()) {
            Date date = new Date();

            Good goodResponse = goodService.insert(shopListResponse.getId(), title, price, quantity, measure, priority, "", 0, 0,
                    State.NORMAL.getId(), shopListResponse.getOwnerId(), 0, app.formatDatetime(date), app.formatDatetime(date), SynchAction.INSERT);

            if (goodResponse != null) {
                if (shopListResponse.isShared()) {
                    //todo
                }


                shopListResponse.setLastModifiedTime(date);
                shopListResponse.changeSynchAction();

                shopListService.save(shopListResponse);

                setListTime(date);

                refreshGoodItems();

                emptyGoodButton.setVisibility(View.GONE);

                makeSoundAdd();
            }
        } else {
            if (!waitResponse) {

                disableWaitForNextCommand();

                addGood = new HttpCommand(this, new GoodResponseWrapper());
                addGood.addParam(LIST_OWNER_ID, shopListResponse.getOwnerId());
                addGood.addParam(GOOD_ID_PARAM, goodService.getLastGoodId(shopListResponse.getId(), shopListResponse.getOwnerId()));
                addGood.addParam(LIST_ID_PARAM, shopListResponse.getId());
                addGood.addParam(GOOD_NAME, title);

                if (price != 0) {
                    addGood.addParam(GOOD_PRICE, price);
                }
                if (quantity != 0) {
                    addGood.addParam(GOOD_QUANTITY, quantity);
                }
                if (measure != 0) {
                    addGood.addParam(GOOD_MEASURE, measure);
                }

                addGood.addParam(GOOD_PRIORITY, isPriority);

                addGood.sendAsForm(SeeapennyApp.getHttpHandler(), SeeapennyActivity.ADD_GOODS);

            }
        }

        addNewGoodEditText.setText(null);
    }

    private void sendChangeGood(long goodId, String title, double price, double quantity, int measure, boolean priority) {
        ShopList shopListResponse = Services.getListService().getShopList(listID, ownerID);
        if (!app.isAuthorized()) {

            EnumSet<FieldGood> fieldGoods = FieldGood.createEmptySaveTypes();

            Good good = goodService.getGoodById(shopListResponse.getId(), goodId, shopListResponse.getOwnerId());
            good.changeName(title, fieldGoods);
            good.changePrice(price, fieldGoods);
            good.changeQuantity(quantity, fieldGoods);
            good.changeMeasure(measure, fieldGoods);
            good.changePriority(priority, fieldGoods);
            good.changeLastEditor(app.getOwnerID(), fieldGoods);

            if (fieldGoods.size() > 0) {

                if (shopListResponse.isShared()) {
                    //todo
                }

                good.changeSynchAction(fieldGoods);

                Date modifiedTime = new Date();
                good.setModifiedTime(modifiedTime);
                fieldGoods.add(FieldGood.MODIFIED_TIME);

                boolean ok = goodService.save(fieldGoods, good);
                if (ok) {

                    shopListResponse.setLastModifiedTime(modifiedTime);

                    shopListResponse.changeSynchAction();
                    shopListService.save(shopListResponse);

                    setListTime(modifiedTime);
                    refreshGoodItems();
                    makeSoundAdd();
                }
            }

            selectedGoodResponse = null;

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(addNewGoodEditText.getWindowToken(), 0);

            menuItemActionView.collapseActionView();


        } else {
            if (!waitResponse) {
                disableWaitForNextCommand();

                Good good = goodService.getGoodById(shopListResponse.getId(), goodId, shopListResponse.getOwnerId());

                ///todo передавть только те параметры кот изменились

                saveGood = new HttpCommand(this, new GoodResponseWrapper());
                saveGood.addParam(LIST_OWNER_ID, shopListResponse.getOwnerId());
                saveGood.addParam(LIST_ID_PARAM, shopListResponse.getId());
                saveGood.addParam(GOOD_ID_PARAM, good.getId());

                if (!good.getName().equals(title)) {
                    saveGood.addParam(GOOD_NAME, title);
                }

                if (good.getPrice() != price) {
                    saveGood.addParam(GOOD_PRICE, price);
                }

                if (good.getQuantity() != quantity) {
                    saveGood.addParam(GOOD_QUANTITY, quantity);
                }

                if (good.getMeasure() != measure) {
                    saveGood.addParam(GOOD_MEASURE, measure);
                }

                if (good.isPriority() != isPriority) {
                    saveGood.addParam(GOOD_PRIORITY, isPriority);
                }

                saveGood.sendAsForm(SeeapennyApp.getHttpHandler(), SeeapennyActivity.SAVE_GOODS);

            }
        }

        addNewGoodEditText.setText(null);
    }

    private void removeGoodInList(long goodId) {

        ShopList shopList = Services.getListService().getShopList(listID, ownerID);

        Good good = goodService.getGoodById(shopList.getId(), goodId, shopList.getOwnerId());

        if (!app.isAuthorized()) {
            switch (good.getSynchAction()) {
                case INSERT:
                    goodService.remove(good.getId(), good.getListId(), good.getOwnerId());
                    break;
                case UPDATE:
                case DELETE:
                case NO_CHANGES:

                    EnumSet<FieldGood> fields = FieldGood.createEmptySaveTypes();

                    good.setSynchAction(SynchAction.DELETE);
                    fields.add(FieldGood.SYNCH_ACTION);

                    Date modifiedTime = new Date();

                    boolean ok = goodService.save(fields, good);

                    if (ok) {
                        shopList.setLastModifiedTime(modifiedTime);
                        shopList.setSynchAction(SynchAction.UPDATE);

                        shopListService.save(shopList);
                    }
                default:

            }

            refreshGoodItems();
            makeSoundRemove();

        } else {
            if (!waitResponse) {
                disableWaitForNextCommand();

                removeGood = new HttpCommand(this, new ExecuteGoodResult());
                removeGood.addParam(LIST_OWNER_ID, good.getOwnerId());
                removeGood.addParam(LIST_ID_PARAM, good.getListId());
                removeGood.addParam(GOOD_ID_PARAM, good.getId());

                removeGood.sendAsForm(SeeapennyApp.getHttpHandler(), SeeapennyActivity.REMOVE_GOODS);
            }
        }

        selectedGoodResponse = null;


    }

    private void removeAllGoodInList() {
        ShopList shopList = Services.getListService().getShopList(listID, ownerID);
        List<Good> goods = new ArrayList<Good>(goodService.getAllGood(shopList.getId(), shopList.getOwnerId()));

        if (!app.isAuthorized()) {

            for (Good good : goods) {

                if (good.getSynchAction() != SynchAction.INSERT) {

                    EnumSet<FieldGood> fieldGoods = FieldGood.createEmptySaveTypes();

                    good.setSynchAction(SynchAction.DELETE);
                    fieldGoods.add(FieldGood.SYNCH_ACTION);

                    boolean ok = goodService.save(fieldGoods, good);
                    if (ok) {
                        shopList.setLastModifiedTime(new Date());
                        shopList.changeSynchAction();

                        shopListService.save(shopList);
                    }
                } else {
                    goodService.remove(good.getId(), good.getListId(), good.getOwnerId());
                }

            }
            refreshGoodItems();

            makeSoundRemove();
        } else {
            if (!waitResponse) {

                disableWaitForNextCommand();
                removeAllGood = new HttpCommand(this, new ExecuteListResult());
                removeAllGood.addParam(LIST_OWNER_ID, shopList.getOwnerId());
                removeAllGood.addParam(LIST_ID_PARAM, shopList.getId());

                removeAllGood.sendAsForm(SeeapennyApp.getHttpHandler(), SeeapennyActivity.REMOVE_ALL_GOOD);
            }
        }

        selectedGoodResponse = null;
    }

    @Override
    public void setOnClickListener(final Good item, View view) {
        view.setEnabled(false);
        selectedGoodResponse = item;
        switch (view.getId()) {
            case -1:
                if (addNewGoodEditText != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(addNewGoodEditText.getWindowToken(), 0);
                }

                addNewGoodEditText.setText(null);
                addNewGoodEditText.setTag(selectedGoodResponse.getId());

                menuItemActionView.collapseActionView();

                mQuickAction.clearActionItems();

                ActionItem actionItemBought = null;

                switch (State.valueOf(selectedGoodResponse.getState())) {
                    case NORMAL:
                        actionItemBought = new ActionItem(ACTION_ITEM_STATE, resources.getString(R.string.goodBought), resources.getDrawable(R.drawable.action_item_ok));
                        break;
                    case BOUGHT:
                        actionItemBought = new ActionItem(ACTION_ITEM_STATE, resources.getString(R.string.goodBack), resources.getDrawable(R.drawable.action_item_no));
                        break;
                }

                ActionItem actionItemEdit = new ActionItem(ACTION_ITEM_EDIT, resources.getString(R.string.goodEdit), resources.getDrawable(R.drawable.action_item_edit));
                ActionItem actionItemMove = new ActionItem(ACTION_ITEM_MOVE, resources.getString(R.string.goodMove), resources.getDrawable(R.drawable.action_item_move));
                ActionItem actionItemCopy = new ActionItem(ACTION_ITEM_COPY, resources.getString(R.string.goodCopy), resources.getDrawable(R.drawable.action_item_copy));
                ActionItem actionItemRemove = new ActionItem(ACTION_ITEM_REMOVE, resources.getString(R.string.goodRemove), resources.getDrawable(R.drawable.action_item_delete));


                mQuickAction.addActionItem(actionItemBought);

                mQuickAction.addActionItem(actionItemEdit);
                mQuickAction.addActionItem(actionItemMove);
                mQuickAction.addActionItem(actionItemCopy);
                mQuickAction.addActionItem(actionItemRemove);

                mQuickAction.show(view);
                break;
            case R.id.mark:
                if (!app.isAuthorized()) {
                    ShopList shopList = Services.getListService().getShopList(item.getListId(), item.getOwnerId());
                    EnumSet<FieldGood> fieldGoods = FieldGood.createEmptySaveTypes();
                    item.changeState(item.getState() == State.NORMAL.getId() ? State.BOUGHT.getId() : State.NORMAL.getId(), fieldGoods);

                    if (fieldGoods.size() > 0) {

                        if (shopList.isShared()) {
                            //todo
                        }

                        Date date = new Date();

                        item.changeSynchAction(fieldGoods);

                        item.setModifiedTime(date);
                        fieldGoods.add(FieldGood.MODIFIED_TIME);

                        shopList.setLastModifiedTime(date);

                        shopList.changeSynchAction();

                        shopListService.save(shopList);

                        refreshGoodItems();

                        if (item.getState() == State.BOUGHT.getId()) {
                            makeVibrate();
                        }
                    }

                } else {
                    if (!waitResponse) {


                        disableWaitForNextCommand();
                        changeStateGood = new HttpCommand(GoodActivity.this, new GoodResponseWrapper());
                        changeStateGood.addParam(LIST_OWNER_ID, item.getOwnerId());
                        changeStateGood.addParam(LIST_ID_PARAM, item.getListId());
                        changeStateGood.addParam(GOOD_ID_PARAM, item.getId());
                        changeStateGood.addParam(GOOD_STATE, item.getState() == State.NORMAL.getId() ? State.BOUGHT.getId() : State.NORMAL.getId());

                        changeStateGood.sendAsForm(SeeapennyApp.getHttpHandler(), SeeapennyActivity.SAVE_GOODS);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void confirmRemoveGood(final long localGoodID) {
        final Dialog dialog = new Dialog(this, R.style.MyDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirm_dialog);

        ((TextView) dialog.findViewById(R.id.simple_dialog_header)).setText(resources.getString(R.string.isRemoveGood, selectedGoodResponse.getName()));

        dialog.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                removeGoodInList(localGoodID);
            }
        });

        dialog.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        if (!isFinishing()) {
            dialog.show();
        }
    }

    private void confirmRemoveAllGood() {
        final Dialog dialog = new Dialog(this, R.style.MyDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirm_dialog);

        ((TextView) dialog.findViewById(R.id.simple_dialog_header)).setText(resources.getString(R.string.isRemoveAllGood));

        dialog.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                removeAllGoodInList();
            }
        });

        dialog.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        if (!isFinishing()) {
            dialog.show();
        }
    }

    public void onBackPressed() {
        finish();
    }

    private void changeStatesAllGood() {
        ShopList shopList = Services.getListService().getShopList(listID, ownerID);

        if (!app.isAuthorized()) {

            boolean ok = false;
            ArrayList<Good> goodList = goodService.getAllGood(shopList.getId(), shopList.getOwnerId());
            for (Good good : goodList) {
                EnumSet<FieldGood> fieldGoods = FieldGood.createEmptySaveTypes();

                if (shopList.getCountLeftGoods() != 0) {
                    good.setState(State.BOUGHT.getId());
                } else {
                    good.setState(State.NORMAL.getId());
                }
                fieldGoods.add(FieldGood.STATE);

                good.changeSynchAction(fieldGoods);

                ok |= goodService.save(fieldGoods, good);
            }

            if (ok) {
                shopList.setLastModifiedTime(new Date());
                shopListService.save(shopList);

                if (shopList.getCountLeftGoods() > 0) {
                    makeVibrate();
                }
                refreshGoodItems();
            }
        } else {
            if (!waitResponse) {
                disableWaitForNextCommand();
                changeStateAllGood = new HttpCommand(this, new ExecuteListResult());
                changeStateAllGood.addParam(LIST_ID_PARAM, shopList.getId());
                changeStateAllGood.addParam(LIST_OWNER_ID, shopList.getOwnerId());

                if (shopList.getCountLeftGoods() != 0) {
                    changeStateAllGood.addParam(GOOD_STATE, State.BOUGHT.ordinal());
                } else {
                    changeStateAllGood.addParam(GOOD_STATE, State.NORMAL.ordinal());
                }

                changeStateAllGood.sendAsForm(SeeapennyApp.getHttpHandler(), SeeapennyActivity.EXCLUDED_ALL_GOOD);

            }
        }
    }

    private void showCopyDialog(final long goodId) {
        final List<ShopList> shopLists = shopListService.getAllList(ShopListService.includeFilter);

        final Dialog dialog = new Dialog(GoodActivity.this, R.style.MyDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.copy_shop_list_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dialog.dismiss();
            }
        });

        ListView listView = (ListView) dialog.findViewById(R.id.list);

        TextView header = (TextView) dialog.findViewById(R.id.header);
        header.setText(resources.getString(R.string.copy_good_header, "\"" + selectedGoodResponse.getName() + "\""));

        final CopyShopListAdapter listNameAdapter = new CopyShopListAdapter(GoodActivity.this, 0, shopLists);
        listNameAdapter.setFactory(new ListItemsFactory<ShopList>() {
            @Override
            public void createMoreItems(ListHandler<ShopList> languageItemListHandler, int position) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void setOnClickListener(ShopList item, View view) {
                copyGoodInList(item, goodId);
                dialog.dismiss();

            }
        });
        listView.setAdapter(listNameAdapter);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dialog.dismiss();
            }
        });

        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        int pxWidth = (int) ((300 * displayMetrics.density) + 0.5);
        int pxHeight = (int) ((400 * displayMetrics.density) + 0.5);
        dialog.getWindow().setLayout(pxWidth, pxHeight);

        if (!isFinishing()) {
            dialog.show();
        }
    }

    private void showMoveDialog(final long localGoodId) {
        ShopList shopList = Services.getListService().getShopList(listID, ownerID);

        final List<ShopList> shopLists = shopListService.getAllList(ShopListService.includeFilter);

        final ArrayList<ShopList> listsName = new ArrayList<ShopList>();
        for (ShopList list : shopLists) {
            if (list.getId() != shopList.getId()) {
                listsName.add(list);
            }
        }

        final Dialog dialog = new Dialog(GoodActivity.this, R.style.MyDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.copy_shop_list_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dialog.dismiss();
            }
        });

        View empty = dialog.findViewById(R.id.empty);

        ListView listView = (ListView) dialog.findViewById(R.id.list);

        TextView header = (TextView) dialog.findViewById(R.id.header);
        header.setText(resources.getString(R.string.move_good_header, "\"" + selectedGoodResponse.getName() + "\""));

        final CopyShopListAdapter listNameAdapter = new CopyShopListAdapter(GoodActivity.this, 0, listsName);
        listNameAdapter.setFactory(new ListItemsFactory<ShopList>() {
            @Override
            public void createMoreItems(ListHandler<ShopList> languageItemListHandler, int position) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void setOnClickListener(ShopList item, View view) {

                moveGoodInList(item, localGoodId);
                dialog.dismiss();

            }
        });
        listView.setAdapter(listNameAdapter);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dialog.dismiss();
            }
        });

        if (listsName.size() == 0) {
            empty.setVisibility(View.VISIBLE);
        }

        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        int pxWidth = (int) ((300 * displayMetrics.density) + 0.5);
        int pxHeight = (int) ((400 * displayMetrics.density) + 0.5);
        dialog.getWindow().setLayout(pxWidth, pxHeight);

        if (!isFinishing()) {
            dialog.show();
        }

    }

    private void copyGoodInList(ShopList toShopList, long goodId) {
        ShopList shopList = Services.getListService().getShopList(listID, ownerID);

        if (!app.isAuthorized()) {
            Good copyGood = goodService.getGoodById(shopList.getId(), goodId, shopList.getOwnerId());

            Date createDate = new Date();

            Good good = goodService.insert(toShopList.getId(), copyGood.getName(), copyGood.getPrice(), copyGood.getQuantity(), copyGood.getMeasure(), copyGood.isPriority(), copyGood.getNote(),
                    copyGood.getCategoryId(), copyGood.getImageId(), copyGood.getState(), copyGood.getOwnerId(), copyGood.getLastEditorId(), app.formatDatetime(createDate), app.formatDatetime(createDate), SynchAction.INSERT);

            if (good != null) {

                if (shopList.isShared()) {
                    //todo
                }

                toShopList.setLastModifiedTime(createDate);

                toShopList.changeSynchAction();
                shopListService.save(toShopList);

                if (toShopList.getId() == shopList.getId()) {
                    refreshGoodItems();
                    setListTime(copyGood.getModifiedTime());
                }

                makeSoundAdd();
            }
        } else {
            if (!waitResponse) {

                disableWaitForNextCommand();

                Good goodResponse = goodService.getGoodById(shopList.getId(), goodId, shopList.getOwnerId());

                copyGood = new HttpCommand(this, new GoodResponseWrapper());
                copyGood.addParam(GOOD_ID_PARAM, goodService.getLastGoodId(toShopList.getId(), toShopList.getOwnerId()));
                copyGood.addParam(LIST_OWNER_ID, toShopList.getOwnerId());
                copyGood.addParam(LIST_ID_PARAM, toShopList.getId());
                copyGood.addParam(GOOD_NAME, goodResponse.getName());
                copyGood.addParam(GOOD_STATE, goodResponse.getState());

                copyGood.addParam(GOOD_PRICE, goodResponse.getPrice());
                copyGood.addParam(GOOD_QUANTITY, goodResponse.getQuantity());
                copyGood.addParam(GOOD_MEASURE, goodResponse.getMeasure());
                copyGood.addParam(GOOD_PRIORITY, goodResponse.isPriority());
                copyGood.addParam(GOOD_NOTE, goodResponse.getNote());
                copyGood.addParam(GOOD_IMAGE_ID, goodResponse.getImageId());

                copyGood.sendAsForm(SeeapennyApp.getHttpHandler(), SeeapennyActivity.ADD_GOODS);
            }
        }

    }

    private void moveGoodInList(ShopList toShopList, long goodId) {
        ShopList shopListResponse = Services.getListService().getShopList(listID, ownerID);

        Good goodResponse = goodService.getGoodById(shopListResponse.getId(), goodId, shopListResponse.getOwnerId());
        if (!app.isAuthorized()) {

            Good movedGood = goodService.insert(toShopList.getId(), goodResponse.getName(), goodResponse.getPrice(), goodResponse.getQuantity(), goodResponse.getMeasure(), goodResponse.isPriority(), goodResponse.getNote(),
                    goodResponse.getCategoryId(), goodResponse.getImageId(), goodResponse.getState(), goodResponse.getOwnerId(), goodResponse.getLastEditorId(), app.formatDatetime(goodResponse.getCreateTime()), app.formatDatetime(goodResponse.getModifiedTime()), SynchAction.INSERT);

            if (movedGood != null) {
                if (shopListResponse.isShared()) {
                    //todo
                }

                Date date = new Date();
                toShopList.setLastModifiedTime(date);
                toShopList.changeSynchAction();
                shopListService.save(toShopList);

                switch (goodResponse.getSynchAction()) {
                    case INSERT:
                        goodService.remove(goodResponse.getId(), goodResponse.getListId(), goodResponse.getOwnerId());
                        break;
                    case UPDATE:
                    case NO_CHANGES:
                    case DELETE:
                        EnumSet<FieldGood> fieldGoods = FieldGood.createEmptySaveTypes();
                        goodResponse.setSynchAction(SynchAction.DELETE);
                        fieldGoods.add(FieldGood.SYNCH_ACTION);

                        goodService.save(fieldGoods, goodResponse);

                        break;
                    default:
                }

                shopListResponse.setLastModifiedTime(date);
                shopListResponse.changeSynchAction();
                shopListService.save(shopListResponse);

                makeSoundAdd();

                goodAdapter.removeItem(goodResponse);

            }

        } else if (goodResponse.getOwnerId() == app.getOwnerID()) { //проверяем наш ли это товар
            if (!waitResponse) {

                disableWaitForNextCommand();

                copyGood = new HttpCommand(this, new GoodResponseWrapper());
                copyGood.addParam(GOOD_ID_PARAM, goodService.getLastGoodId(toShopList.getId(), toShopList.getOwnerId()));
                copyGood.addParam(LIST_OWNER_ID, toShopList.getOwnerId());
                copyGood.addParam(LIST_ID_PARAM, toShopList.getId());
                copyGood.addParam(GOOD_NAME, goodResponse.getName());
                copyGood.addParam(GOOD_STATE, goodResponse.getState());

                copyGood.addParam(GOOD_PRICE, goodResponse.getPrice());
                copyGood.addParam(GOOD_QUANTITY, goodResponse.getQuantity());
                copyGood.addParam(GOOD_MEASURE, goodResponse.getMeasure());
                copyGood.addParam(GOOD_PRIORITY, goodResponse.isPriority());
                copyGood.addParam(GOOD_NOTE, goodResponse.getNote());
                copyGood.addParam(GOOD_IMAGE_ID, goodResponse.getImageId());

                copyGood.sendAsForm(SeeapennyApp.getHttpHandler(), SeeapennyActivity.ADD_GOODS);


                disableWaitForNextCommand();
                removeGood = new HttpCommand(this, new ExecuteGoodResult());
                removeGood.addParam(LIST_ID_PARAM, goodResponse.getListId());
                removeGood.addParam(LIST_OWNER_ID, goodResponse.getOwnerId());
                removeGood.addParam(GOOD_ID_PARAM, goodResponse.getId());

                removeGood.sendAsForm(SeeapennyApp.getHttpHandler(), SeeapennyActivity.REMOVE_GOODS);

            }
        }
    }

    private void setListTime(Date lastModifiedTime) {
        listTime.setText(app.formatElapsed(lastModifiedTime));
    }

    private void showSortDialog() {
        final Dialog dialog = new Dialog(this, R.style.MyDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.sort_dialog);
        dialog.setCanceledOnTouchOutside(false);

        initSort(dialog);

        View sortByName = dialog.findViewById(R.id.sort_by_name);
        sortByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                app.saveGoodSort("name");
                initSort(dialog);
            }
        });

        View sortByPrice = dialog.findViewById(R.id.sort_by_price);
        sortByPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                app.saveGoodSort("price");
                initSort(dialog);
            }
        });

        View sortByCategory = dialog.findViewById(R.id.sort_by_category);
        sortByCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                app.saveGoodSort("category");
                initSort(dialog);
            }
        });

        View sortByPriority = dialog.findViewById(R.id.sort_by_priority);
        sortByPriority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                app.saveGoodSort("priority");
                initSort(dialog);
            }
        });

        View sortByDate = dialog.findViewById(R.id.sort_by_date);
        sortByDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                app.saveGoodSort("create_time");
                initSort(dialog);
            }
        });

        final ImageView sortAscImg = (ImageView) dialog.findViewById(R.id.sort_asc_img);
        final ImageView sortDescImg = (ImageView) dialog.findViewById(R.id.sort_desc_img);

        if (app.isSortAsc()) {
            sortAscImg.setSelected(true);
            sortDescImg.setSelected(false);
        } else {
            sortAscImg.setSelected(false);
            sortDescImg.setSelected(true);
        }

        View sortDescView = dialog.findViewById(R.id.sort_desc_view);
        sortDescView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                app.setSortAsc(false);
                if (app.isSortAsc()) {
                    sortAscImg.setSelected(true);
                    sortDescImg.setSelected(false);
                } else {
                    sortAscImg.setSelected(false);
                    sortDescImg.setSelected(true);
                }
            }
        });

        View sortAscView = dialog.findViewById(R.id.sort_asc_view);
        sortAscView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                app.setSortAsc(true);
                if (app.isSortAsc()) {
                    sortAscImg.setSelected(true);
                    sortDescImg.setSelected(false);
                } else {
                    sortAscImg.setSelected(false);
                    sortDescImg.setSelected(true);
                }
            }
        });

        final View checkboxIgnore = dialog.findViewById(R.id.checkbox_ignore);
        checkboxIgnore.setSelected(app.isIgnoreBoughtGood());
        checkboxIgnore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                app.setIgnoreBoughtGood(!checkboxIgnore.isSelected());
                checkboxIgnore.setSelected(app.isIgnoreBoughtGood());
            }
        });

        Button button = (Button) dialog.findViewById(R.id.yes);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshGoodItems();
                dialog.dismiss();
            }
        });


        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        int pxWidth = (int) ((300 * displayMetrics.density) + 0.5);
        int pxHeight = (int) ((500 * displayMetrics.density) + 0.5);
        dialog.getWindow().setLayout(pxWidth, pxHeight);

        if (!isFinishing()) {
            dialog.show();
        }

    }

    private void initSort(Dialog dialog) {
        ImageView sortByNameImg = (ImageView) dialog.findViewById(R.id.sort_by_name_img);
        ImageView sortByPriceImg = (ImageView) dialog.findViewById(R.id.sort_by_price_img);
        ImageView sortByCategoryImg = (ImageView) dialog.findViewById(R.id.sort_by_category_img);
        ImageView sortByPriorityImg = (ImageView) dialog.findViewById(R.id.sort_by_priority_img);
        ImageView sortByDateImg = (ImageView) dialog.findViewById(R.id.sort_by_date_img);

        String s = app.getGoodSort();
        if (s.equals("name")) {
            sortByNameImg.setSelected(true);
            sortByPriceImg.setSelected(false);
            sortByCategoryImg.setSelected(false);
            sortByPriorityImg.setSelected(false);
            sortByDateImg.setSelected(false);
        } else if (s.equals("price")) {
            sortByPriceImg.setSelected(true);

            sortByNameImg.setSelected(false);
            sortByCategoryImg.setSelected(false);
            sortByPriorityImg.setSelected(false);
            sortByDateImg.setSelected(false);


        } else if (s.equals("category")) {
            sortByCategoryImg.setSelected(true);

            sortByPriceImg.setSelected(false);
            sortByNameImg.setSelected(false);
            sortByPriorityImg.setSelected(false);
            sortByDateImg.setSelected(false);

        } else if (s.equals("priority")) {
            sortByPriorityImg.setSelected(true);

            sortByCategoryImg.setSelected(false);
            sortByPriceImg.setSelected(false);
            sortByNameImg.setSelected(false);
            sortByDateImg.setSelected(false);


        } else if (s.equals("create_time")) {
            sortByDateImg.setSelected(true);

            sortByPriorityImg.setSelected(false);
            sortByCategoryImg.setSelected(false);
            sortByNameImg.setSelected(false);
            sortByPriceImg.setSelected(false);
        }
    }
}
