package com.seeapenny.client.activity;

import android.app.*;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.android.vending.billing.BillingService;
import com.android.vending.billing.PurchaseObserver;
import com.seeapenny.client.*;
import com.seeapenny.client.adapter.DrawerMenu;
import com.seeapenny.client.adapter.ExpandableShopListAdapter;
import com.seeapenny.client.adapter.Good;
import com.seeapenny.client.adapter.MenuAdapter;
import com.seeapenny.client.adapter.ShareAdapter;
import com.seeapenny.client.adapter.ShareUser;
import com.seeapenny.client.adapter.ShopList;
import com.seeapenny.client.http.HttpCommand;
import com.seeapenny.client.http.HttpCommandListener;
import com.seeapenny.client.list.ListHandler;
import com.seeapenny.client.list.ListItemsFactory;
import com.seeapenny.client.quickaction.ActionItem;
import com.seeapenny.client.quickaction.QuickAction;
import com.seeapenny.client.server.GoodResponse;
import com.seeapenny.client.server.Response;
import com.seeapenny.client.server.ShopListResponse;
import com.seeapenny.client.server.State;
import com.seeapenny.client.server.User;
import com.seeapenny.client.server.responses.*;
import com.seeapenny.client.service.AccessType;
import com.seeapenny.client.service.Services;
import com.seeapenny.client.service.ShopListService;
import com.seeapenny.client.util.ExtrasConst;
import com.seeapenny.client.util.FieldGood;

import java.util.*;

public class ShopListActivity extends SeeapennyActivity implements ListItemsFactory<ShopList>, View.OnClickListener {

    private static final int RESULT_SPEECH = 1;

    private static final int ACTION_ITEM_OPEN = 0;
    private static final int ACTION_ITEM_EDIT = 1;
    private static final int ACTION_ITEM_SORT = 2;

    private static final int ACTION_ITEM_SEND = 3;
    private static final int ACTION_ITEM_REMIND = 5;
    private static final int ACTION_ITEM_REMIND_REMOVE = 6;
    private static final int ACTION_ITEM_REMOVE = 7;

    private static final int MENU_LEFT_AUTH = 2;
    private static final int MENU_LEFT_PROFILE = 3;
    private static final int MENU_LEFT_HELP = 4;
    private static final int MENU_LEFT_GUIDE = 5;
    private static final int MENU_LEFT_ABOUT_APP = 6;
    private static final int MENU_LEFT_LOGOUT = 7;

    private EditText editTextAddNewList;
    private ImageView btnSpeak;
    private ImageView btnDone;

    private ExpandableShopListAdapter adapter;
    private ExpandableListView listView;

    private ShopList selectedShopListResponse;
    private Menu headerMenu;

    private QuickAction quickActionOptions;

    private Button emptyListButton;

    private ActionBar actionBar;
    private View headerOptions;
    private TextView headerText;

    private MenuItem menuItemActionView;

    private boolean showDatePicker;
    private boolean showTimePicker;

    private View cover;

    private Handler handler;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private View mDrawer;

    private ActionBarDrawerToggle mDrawerToggle;
    private List<DrawerMenu> menus;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_list);

        initActionBar();
        initQuickAction();

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        listView = (ExpandableListView) findViewById(R.id.list);

        adapter = new ExpandableShopListAdapter(this, new ArrayList<ShopList>(), new ArrayList<ArrayList<Good>>(), this, new ListItemsFactory<Good>() {
            @Override
            public void createMoreItems(ListHandler<Good> goodListHandler, int position) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void setOnClickListener(Good item, View view) {
                switch (view.getId()) {
                    case R.id.content:

                        EnumSet<FieldGood> fieldGood = FieldGood.createEmptySaveTypes();

                        Good good = goodService.getGoodById(item.getListId(), item.getId(), item.getOwnerId());
                        good.changeState(item.getState() == State.NORMAL.getId() ? State.BOUGHT.getId() : State.NORMAL.getId(), fieldGood);

                        if (fieldGood.size() > 0) {
                            Date date = new Date();

                            good.changeSynchAction(fieldGood);

                            good.setModifiedTime(date);
                            fieldGood.add(FieldGood.MODIFIED_TIME);

                            boolean ok = goodService.save(fieldGood, good);
                            if (ok) {
                                ShopList shopList = Services.getListService().getShopList(good.getListId(), good.getOwnerId());
                                shopList.setLastModifiedTime(date);

                                shopList.changeSynchAction();
                                shopListService.save(shopList);
                                refreshLists();

                                if (good.getState() == State.BOUGHT.getId()) {
                                    makeVibrate();
                                }

                                showHistoryDialog(shopList);
                            }
                        }
                        break;
                }
            }
        });
        listView.setAdapter(adapter);

        cover = findView(R.id.cover);
        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuItemActionView.collapseActionView();
            }
        });

        emptyListButton = (Button) findViewById(R.id.emptyListButton);
        emptyListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                headerMenu.performIdentifierAction(R.id.add_item, 0);

                editTextAddNewList.requestFocus();

            }
        });

        if (!app.isShownGuideDialog()) {
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 0) {
                        showGuide();
                    }
                }
            };
            handler.sendEmptyMessageDelayed(0, 1500);
            app.setShownGuideDialog(true);
        }

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            DrawerMenu menu = menus.get(position);
            switch (menu.getId()) {
                case MENU_LEFT_AUTH:
                    General.toLogReg(ShopListActivity.this);
                    break;
                case MENU_LEFT_PROFILE:
                    Intent profileIntent = new Intent(ShopListActivity.this, ProfileActivity.class);
                    startActivity(profileIntent);
                    break;
                case MENU_LEFT_HELP:
                    Intent helpIntent = new Intent(ShopListActivity.this, SupportActivity.class);
                    startActivity(helpIntent);
                    break;
                case MENU_LEFT_GUIDE:
                    Intent guideIntent = new Intent(ShopListActivity.this, GuideActivity.class);
                    startActivity(guideIntent);
                    break;
                case MENU_LEFT_ABOUT_APP:
                    Intent appIntent = new Intent(ShopListActivity.this, AboutAppActivity.class);
                    startActivity(appIntent);
                    break;
                case MENU_LEFT_LOGOUT:

                    app.setAuthorized(false);
                    logout();

                    initActionBar();
                    break;
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void initActionBar() {
        menus = new ArrayList<DrawerMenu>();
        menus.add(new DrawerMenu(MENU_LEFT_AUTH, resources.getString(R.string.menuLeftAuth), R.drawable.left_menu_enter));
        menus.add(new DrawerMenu(MENU_LEFT_PROFILE, resources.getString(R.string.menuLeftProfile), R.drawable.left_menu_profile));
        menus.add(new DrawerMenu(MENU_LEFT_HELP, resources.getString(R.string.menuLeftHelp), R.drawable.left_menu_help));
        menus.add(new DrawerMenu(MENU_LEFT_GUIDE, resources.getString(R.string.menuLeftGuide), R.drawable.left_menu_support));
        menus.add(new DrawerMenu(MENU_LEFT_ABOUT_APP, resources.getString(R.string.menuLeftAboutApp), R.drawable.left_menu_help));


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawer = findViewById(R.id.left_drawer);

        mDrawerList = (ListView) findViewById(R.id.left_drawer_list);
        mDrawerList.setAdapter(new MenuAdapter(this,
                R.layout.drawer_list_item, menus));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());


        TextView lastSynch = (TextView) findViewById(R.id.lastSynch);
        if (app.getLastSynchTime() == null) {
            lastSynch.setText(resources.getString(R.string.no_auth));
        } else {
            lastSynch.setText(app.getLastSynchTime());
        }

        actionBar = getSupportActionBar();


        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);

        if (app.isAuthorized()) {
            actionBar.setIcon(R.drawable.header_online);
        } else {
            actionBar.setIcon(R.drawable.header_offline);
        }

        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        headerOptions = mInflater.inflate(R.layout.header_options, null);

        headerText = (TextView) headerOptions.findViewById(R.id.header_text);
        if (app.isSynchronization()) {
            headerText.setText(resources.getString(R.string.synchronizd));
        }


        actionBar.setCustomView(headerOptions);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    private void initQuickAction() {
        quickActionOptions = new QuickAction(this, QuickAction.HORIZONTAL);
        quickActionOptions.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction quickAction, int pos, int actionId) {
                switch (actionId) {
                    case ACTION_ITEM_OPEN:

                        Intent intent = new Intent(ShopListActivity.this, GoodActivity.class);
                        intent.putExtra(ExtrasConst.LIST_ID, selectedShopListResponse.getId());
                        intent.putExtra(ExtrasConst.OWNER_ID, selectedShopListResponse.getOwnerId());
                        startActivity(intent);

                        break;
                    case ACTION_ITEM_EDIT:

                        headerMenu.performIdentifierAction(R.id.add_item, 0);

                        editTextAddNewList.setText(selectedShopListResponse.getName());
                        editTextAddNewList.setSelection(selectedShopListResponse.getName().length());
                        editTextAddNewList.setTag(selectedShopListResponse.getId());
                        editTextAddNewList.requestFocus();

                        adapter.notifyDataSetChanged();

                        break;
                    case ACTION_ITEM_SORT:
                        showSortDialog();
                        break;
                    case ACTION_ITEM_SEND:
                        //todo services
                        break;
                    case ACTION_ITEM_REMIND_REMOVE:

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(ShopListActivity.this, (int) selectedShopListResponse.getId(),
                                new Intent(ShopListActivity.this, AlarmManagerBroadcastReceiver.class),
                                PendingIntent.FLAG_NO_CREATE);

                        if (pendingIntent != null) {
                            AlarmManager am = (AlarmManager) ShopListActivity.this.getSystemService(Context.ALARM_SERVICE);
                            am.cancel(pendingIntent);
                            pendingIntent.cancel();
                        }

                        showToast(resources.getString(R.string.remindRemoved));

                        break;
                    case ACTION_ITEM_REMIND:


                        final long listID = selectedShopListResponse.getId();
                        final long ownerID = selectedShopListResponse.getOwnerId();

                        quickActionOptions.dismiss();

                        final Calendar c = Calendar.getInstance();
                        int year = c.get(Calendar.YEAR);
                        int month = c.get(Calendar.MONTH);
                        int day = c.get(Calendar.DAY_OF_MONTH);


                        showDatePicker = false;
                        showTimePicker = false;

                        final DatePickerDialog datePickerDialog = new DatePickerDialog(ShopListActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, final int year, final int month, final int day) {
                                if (!showDatePicker) {
                                    showDatePicker = true;
                                    int hour = c.get(Calendar.HOUR_OF_DAY);
                                    int minute = c.get(Calendar.MINUTE);

                                    final TimePickerDialog timePickerDialog = new TimePickerDialog(ShopListActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {

                                            if (!showTimePicker) {
                                                showTimePicker = true;

                                                c.set(Calendar.YEAR, year);
                                                c.set(Calendar.MONTH, month);
                                                c.set(Calendar.DAY_OF_MONTH, day);
                                                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                                c.set(Calendar.MINUTE, minute);
                                                c.set(Calendar.SECOND, 0);

                                                app.getGoodListNotificationBroadcastReceiver().setOnetimeTimer(ShopListActivity.this, c.getTimeInMillis(), listID, ownerID);

                                                showToast(resources.getString(R.string.remindAdded));

                                            }
                                        }
                                    }, hour, minute, DateFormat.is24HourFormat(ShopListActivity.this));


                                    timePickerDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                        @Override
                                        public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                                            showTimePicker = true;
                                            timePickerDialog.dismiss();
                                            return false;
                                        }
                                    });

                                    timePickerDialog.show();
                                }
                            }
                        }, year, month, day);

                        datePickerDialog.setCancelable(true);
                        datePickerDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                                showDatePicker = true;
                                datePickerDialog.dismiss();
                                return false;
                            }
                        });
                        datePickerDialog.show();

                        break;
                    case ACTION_ITEM_REMOVE:
                        confirmRemoveList(selectedShopListResponse.getId(), selectedShopListResponse.getOwnerId());
                        break;
                }
            }
        });


        quickActionOptions.setOnDismissListener(new QuickAction.OnDismissListener() {
            @Override
            public void onDismiss() {
                selectedShopListResponse = null;
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        initActionBar();

        refreshLists();

    }

    @Override
    protected void onPause() {
        if (mDrawerLayout.isDrawerOpen(mDrawer)) {
            mDrawerLayout.closeDrawer(mDrawer);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        app.removeSyncIcon();
//        Services.clear();
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
    }

    @Override
    public void onBackPressed() {

        final Dialog dialog = new Dialog(this, R.style.MyDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirm_dialog);


        ((TextView) dialog.findViewById(R.id.simple_dialog_header)).setText(resources.getString(R.string.closeApplication));

        dialog.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dialog.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dialog.dismiss();
                finish();
            }
        });

        if (!isFinishing()) {
            dialog.show();
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu:
                break;

            case R.id.history_lists:
                intent = new Intent(this, HistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.delete_all_list:
                confirmRemoveAllList();
                break;
            case R.id.settings:

                intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if (!app.isSynchronization()) {
                    if (mDrawerLayout.isDrawerOpen(mDrawer)) {
                        mDrawerLayout.closeDrawer(mDrawer);
                    } else {
                        mDrawerLayout.openDrawer(mDrawer);
                    }
                }
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

                editTextAddNewList.requestFocus();

                break;
            case R.id.menu:
                MenuItem menuItemDeleteAllList = item.getSubMenu().findItem(R.id.delete_all_list);

                if (adapter.isEmpty()) {
                    menuItemDeleteAllList.setVisible(false);
                } else {
                    menuItemDeleteAllList.setVisible(true);
                }
                break;
        }

        return true;
    }

    private void initActionView() {
        menuItemActionView = headerMenu.getItem(0);
        View view = menuItemActionView.getActionView();

        menuItemActionView.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                headerMenu.getItem(1).setVisible(false);
                cover.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                headerMenu.getItem(1).setVisible(true);
                editTextAddNewList.setText(null);
                editTextAddNewList.setTag(null);
                cover.setVisibility(View.GONE);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTextAddNewList.getWindowToken(), 0);

                return true;
            }
        });

        editTextAddNewList = (EditText) view.findViewById(R.id.addNewList);
        editTextAddNewList.setOnTouchListener(new RightDrawableOnTouchListener(editTextAddNewList) {
            @Override
            public boolean onDrawableTouch(final MotionEvent event) {
                editTextAddNewList.setText(null);

                return true;
            }
        });
        editTextAddNewList.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String title = editTextAddNewList.getText().toString().trim();

                if (title.length() > 0) {
                    actionSaveList(title, (Long) editTextAddNewList.getTag());
                    return false;
                }
                return true;
            }
        });

        btnDone = (ImageView) view.findViewById(R.id.send);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTextAddNewList.getText().toString().trim();

                if (title.length() > 0) {
                    actionSaveList(title, (Long) editTextAddNewList.getTag());
                }
            }
        });

        btnSpeak = (ImageView) view.findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault().toString());

                try {
                    startActivityForResult(intent, ShopListActivity.RESULT_SPEECH);
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(ShopListActivity.this,
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


        editTextAddNewList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence cs, int arg1, int arg2, int length) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (app.getVoiceRecognitionScreen()) {
                    int length = editable.toString().length();

                    if (length >= 1 || selectedShopListResponse != null) {
                        btnSpeak.setVisibility(View.INVISIBLE);
                        btnDone.setVisibility(View.VISIBLE);
                    } else if (length < 1) {
                        btnSpeak.setVisibility(View.VISIBLE);
                        btnDone.setVisibility(View.INVISIBLE);
                    }
                } else {
                    btnSpeak.setVisibility(View.INVISIBLE);
                    btnDone.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void actionSaveList(String title, Long listId) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextAddNewList.getWindowToken(), 0);

        menuItemActionView.collapseActionView();

        if (listId != null) {

            ShopList shopListResponse = shopListService.getShopList(listId, app.getOwnerID());
            boolean ok = shopListResponse.changeName(title);
            if (ok) {
                shopListResponse.setLastModifiedTime(new Date());
                shopListResponse.changeSynchAction();

                saveShopList(shopListResponse);
            }
        } else {

            ShopList shopList = new ShopList();
            shopList.setOwnerId(app.getOwnerID());
            shopList.setName(title);

            Date createDate = new Date();

            shopList.setLastModifiedTime(createDate);
            shopList.setCreateTime(createDate);
            shopList.setSynchAction(SynchAction.INSERT);

            shopListService.addShopList(shopList.getOwnerId(), shopList.getName(), shopList.getFormatCreateTime(), shopList.getFormatLastModifiedTime(), shopList.getSynchAction());

            refreshLists();

            editTextAddNewList.setText(null);

            makeSoundAdd();

            emptyListButton.setVisibility(View.GONE);
        }

        app.putInListName(title);
        menuItemActionView.collapseActionView();
        editTextAddNewList.setTag(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        headerMenu = menu;

        headerMenu.clear();

        if (app.isSynchronization()) {
            com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
            inflater.inflate(R.menu.menu_list_synchronization, headerMenu);

            final AnimationDrawable homeDrawable = (AnimationDrawable) getResources().getDrawable(R.drawable.header_progress_animation);

            actionBar.setIcon(homeDrawable);

            getWindow().getDecorView().post(new Runnable() {
                @Override
                public void run() {
                    homeDrawable.start();
                }
            });

        } else {
            com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
            inflater.inflate(R.menu.menu_list, headerMenu);
            headerText.setText(resources.getString(R.string.app_name));
            initActionView();


            cover.setVisibility(View.GONE);
        }
        return true;
    }

    private void showHistoryDialog(final ShopList shopList) {

        if (shopList.getCountLeftGoods() == 0) {
            final Dialog dialog = new Dialog(this, R.style.MyDialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.history_dialog);

            final View checkBoxRemove = dialog.findViewById(R.id.remove_view);
            checkBoxRemove.setSelected(app.isRemoveShoplist());
            checkBoxRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    app.setRemoveShoplist(!app.isRemoveShoplist());
                    checkBoxRemove.setSelected(app.isRemoveShoplist());
                }
            });

            dialog.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();

                    Services.getHistoryService().addShopList(shopList);

                    if (app.isRemoveShoplist()) {
                        remove(shopList.getId(), shopList.getOwnerId());
                    }


                }
            });

            dialog.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();

                    if (app.isRemoveShoplist()) {
                        remove(shopList.getId(), shopList.getOwnerId());
                    }
                }
            });

            if (!isFinishing()) {
                dialog.show();
            }
        }
    }

    private void saveShopList(ShopList shopListResponse) {
        shopListService.save(shopListResponse);

        refreshLists();

        editTextAddNewList.setTag(null);
        editTextAddNewList.setText(null);

        makeSoundAdd();
    }


    private void removeAllList() {

        List<ShopList> shopLists = shopListService.getAllList(ShopListService.includeFilter);
        for (ShopList shopList : shopLists) {
            if (shopList.getSynchAction() == SynchAction.INSERT) {
                shopListService.remove(shopList.getId(), shopList.getOwnerId());
                goodService.removeAllGoods(shopList.getId(), shopList.getOwnerId());
            } else {
                shopList.setSynchAction(SynchAction.DELETE);
                shopListService.save(shopList);

                ArrayList<Good> goods = goodService.getAllGood(shopList.getId(), app.getOwnerID());
                for (Good good : goods) {
                    EnumSet<FieldGood> fieldGoods = FieldGood.createEmptySaveTypes();

                    good.setSynchAction(SynchAction.DELETE);
                    fieldGoods.add(FieldGood.SYNCH_ACTION);

                    goodService.save(fieldGoods, good);
                }
            }
        }


        refreshLists();

        makeSoundRemove();

    }

    private void remove(long listID, long ownerId) {


        ShopList shopList = shopListService.getShopList(listID, ownerId);
        if (shopList.getSynchAction() == SynchAction.INSERT) {
            shopListService.remove(shopList.getId(), shopList.getOwnerId());
            goodService.removeAllGoods(shopList.getId(), shopList.getOwnerId());
        } else {
            Date date = new Date();
            shopList.setLastModifiedTime(date);
            shopList.setSynchAction(SynchAction.DELETE);

            shopListService.save(shopList);

            ArrayList<Good> goods = goodService.getAllGood(listID, app.getOwnerID());
            for (Good good : goods) {
                EnumSet<FieldGood> fieldGoods = FieldGood.createEmptySaveTypes();

                good.setSynchAction(SynchAction.DELETE);
                fieldGoods.add(FieldGood.SYNCH_ACTION);

                goodService.save(fieldGoods, good);
            }
        }

        selectedShopListResponse = null;

        refreshLists();

        makeSoundRemove();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && null != data) {

            ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            editTextAddNewList.setText(text.get(0));
        }
    }

    @Override
    public void createMoreItems(ListHandler<ShopList> myGoodListListHandler, int position) {
    }

    public void refreshLists() {
        ArrayList<ShopList> shopLists;

        shopLists = shopListService.getAllList(ShopListService.excludeFilter);
        ArrayList<ArrayList<Good>> childList = new ArrayList<ArrayList<Good>>();
        for (ShopList shopList : shopLists) {
            ArrayList<Good> goods = goodService.getAllGood(shopList.getId(), shopList.getOwnerId());
            for (Good good : goods) {
                shopList.setTotalPrice(shopList.getTotalPrice() + good.getPrice() * good.getQuantity());
            }

            childList.add(goods);
        }

        if (shopLists.isEmpty()) {
            adapter.removeAllItems();
            emptyListButton.setVisibility(View.VISIBLE);
        } else {
            adapter.refreshAllItems(shopLists, childList);
            emptyListButton.setVisibility(View.GONE);


            shopLists.get(0).setSelected(true);
            listView.expandGroup(0);
        }
    }

    public void refreshMenu() {
        invalidateOptionsMenu();
        refreshLists();
        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuItemActionView.collapseActionView();
            }
        });
    }

    public void notifySyncError(int code, String reason) {
        sendLogError("notifySyncError", code + "/" + reason);
        showSyncErrorDialog();
    }

    public void showSyncErrorDialog() {
        final Dialog dialog = new Dialog(this, R.style.MyDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_dialog);
        dialog.setCancelable(false);

        View yes = dialog.findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SyncService syncService = new SyncService();

                if (Build.VERSION.SDK_INT >= 14) {
                    syncService.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    syncService.execute();
                }

                app.showSyncIcon();
            }
        });


        if (!isFinishing()) {
            dialog.show();
        }

    }

    @Override
    public void setOnClickListener(final ShopList item, View view) {
        view.setEnabled(false);
        selectedShopListResponse = item;
        Intent intent;
        switch (view.getId()) {
            case R.id.listContent:

                if (editTextAddNewList != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editTextAddNewList.getWindowToken(), 0);
                }

                editTextAddNewList.setText(null);
                editTextAddNewList.setTag(null);

                menuItemActionView.collapseActionView();

                quickActionOptions.clearActionItems();

                ActionItem actionItemOpen = new ActionItem(ACTION_ITEM_OPEN, resources.getString(R.string.listOpen), getResources().getDrawable(R.drawable.action_item_open));
                ActionItem actionItemSort = new ActionItem(ACTION_ITEM_SORT, resources.getString(R.string.sort), getResources().getDrawable(R.drawable.action_item_sort));
                ActionItem actionItemSend = new ActionItem(ACTION_ITEM_SEND, resources.getString(R.string.share), getResources().getDrawable(R.drawable.action_item_open_access));
                ActionItem actionItemRemove = new ActionItem(ACTION_ITEM_REMOVE, resources.getString(R.string.listRemove), getResources().getDrawable(R.drawable.action_item_delete));

                quickActionOptions.addActionItem(actionItemOpen);

                if (item.getOwnerId() == app.getOwnerID()) {
                    ActionItem actionItemEdit = new ActionItem(ACTION_ITEM_EDIT, resources.getString(R.string.listEdit), getResources().getDrawable(R.drawable.action_item_edit));
                    quickActionOptions.addActionItem(actionItemEdit);
                }

                quickActionOptions.addActionItem(actionItemSort);
                quickActionOptions.addActionItem(actionItemSend);


                boolean alarmUp = (PendingIntent.getBroadcast(this, (int) selectedShopListResponse.getId(),
                        new Intent(this, AlarmManagerBroadcastReceiver.class),
                        PendingIntent.FLAG_NO_CREATE) != null);

                ActionItem actionItemRemind;

                if (alarmUp) {
                    actionItemRemind = new ActionItem(ACTION_ITEM_REMIND_REMOVE, resources.getString(R.string.listRemindRemove), getResources().getDrawable(R.drawable.action_item_remind_off));
                } else {
                    actionItemRemind = new ActionItem(ACTION_ITEM_REMIND, resources.getString(R.string.listRemind), getResources().getDrawable(R.drawable.action_item_remind));
                }

                quickActionOptions.addActionItem(actionItemRemind);

                quickActionOptions.addActionItem(actionItemRemove);
//                if (!app.isAuthorized()) {
//                    quickActionOptions.addActionItem(actionItemRemove);
//                } else if (app.getSessionState().getUser() != null && item.getOwnerId() == app.getSessionState().getUser().getId()) {
//                    quickActionOptions.addActionItem(actionItemRemove);
//                }

                quickActionOptions.show(view);

                break;
            case R.id.go_to_goods:


                menuItemActionView.collapseActionView();

                intent = new Intent(this, GoodActivity.class);
                intent.putExtra(ExtrasConst.LIST_ID, item.getId());
                intent.putExtra(ExtrasConst.OWNER_ID, item.getOwnerId());
                startActivity(intent);

                selectedShopListResponse = null;
                break;
            case R.id.actual:

                int groupPosition = (Integer) view.getTag();

                if (listView.isGroupExpanded(groupPosition)) {
                    listView.collapseGroup(groupPosition);
                } else {
                    listView.expandGroup(groupPosition);
                }
                adapter.notifyDataSetChanged();

                break;
            case R.id.last_edit_layout:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }

    private void confirmRemoveList(final long listId, final long ownerId) {
        final Dialog dialog = new Dialog(this, R.style.MyDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirm_dialog);

        ((TextView) dialog.findViewById(R.id.simple_dialog_header)).setText(resources.getString(R.string.isRemoveList, selectedShopListResponse.getName()));

        dialog.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                remove(listId, ownerId);
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

    private void confirmRemoveAllList() {
        final Dialog dialog = new Dialog(this, R.style.MyDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirm_dialog);

        ((TextView) dialog.findViewById(R.id.simple_dialog_header)).setText(resources.getString(R.string.isRemoveAllList));

        dialog.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                removeAllList();
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

    private void showSortDialog() {
        final Dialog dialog = new Dialog(this, R.style.MyDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.sort_dialog);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });


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
                adapter.notifyDataSetChanged();
                refreshLists();
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

    private void showGuide() {
        final Dialog dialog = new Dialog(this, R.style.MyDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.guide_dialog);
        dialog.setCancelable(false);

        dialog.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent guideIntent = new Intent(ShopListActivity.this, GuideActivity.class);
                startActivity(guideIntent);
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

}
