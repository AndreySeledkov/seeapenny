package com.seeapenny.client.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.seeapenny.client.General;
import com.seeapenny.client.R;
import com.seeapenny.client.adapter.CurrenciesAdapter;
import com.seeapenny.client.adapter.CurrencyItem;
import com.seeapenny.client.adapter.LanguageAdapter;
import com.seeapenny.client.adapter.LanguageItem;
import com.seeapenny.client.list.ListHandler;
import com.seeapenny.client.list.ListItemsFactory;
import com.seeapenny.client.quickaction.ActionItem;
import com.seeapenny.client.quickaction.QuickAction;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 24.05.13
 * Time: 1:08
 * To change this template use File | Settings | File Templates.
 */
public class SettingActivity extends SeeapennyActivity {

    private ActionBar actionBar;
    private View headerOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        final CheckBox checkBoxSound = (CheckBox) findView(R.id.checkbox_sound);
        checkBoxSound.setSelected(app.getSound());
        checkBoxSound.setChecked(app.getSound());
        checkBoxSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                app.setSound(b);
                checkBoxSound.setSelected(app.getSound());
            }
        });

        View layoutSound = findView(R.id.layout_sound);
        layoutSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBoxSound.performClick();
            }
        });


        final CheckBox checkBoxVibrate = (CheckBox) findView(R.id.checkbox_vibration);
        checkBoxVibrate.setSelected(app.getVibrate());
        checkBoxVibrate.setChecked(app.getVibrate());
        checkBoxVibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                app.setVibrate(b);
                checkBoxVibrate.setSelected(app.getVibrate());
            }
        });

        View layoutVibrate = findView(R.id.layout_vibrate);
        layoutVibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBoxVibrate.performClick();
            }
        });


        final CheckBox checkBoxRotation = (CheckBox) findView(R.id.checkbox_rotation);
        checkBoxRotation.setSelected(app.getRotation());
        checkBoxRotation.setChecked(app.getRotation());
        checkBoxRotation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                app.setRotation(b);

                if (!app.getRotation()) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                }
                checkBoxRotation.setSelected(app.getRotation());
            }
        });

        View layoutRotation = findView(R.id.layout_rotation);
        layoutRotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBoxRotation.performClick();
            }
        });


        final CheckBox checkBoxRecognition = (CheckBox) findView(R.id.checkbox_recognition);
        checkBoxRecognition.setSelected(app.getVoiceRecognitionScreen());
        checkBoxRecognition.setChecked(app.getVoiceRecognitionScreen());
        checkBoxRecognition.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                app.setVoiceRecognitionScreen(b);

                checkBoxRecognition.setSelected(app.getVoiceRecognitionScreen());
            }
        });

        View layoutSpeechRecognition = findView(R.id.layout_speech_recognition);
        layoutSpeechRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBoxRecognition.performClick();
            }
        });


        final CheckBox checkBoxLock = (CheckBox) findView(R.id.checkbox_lock);
        checkBoxLock.setSelected(app.getScreen());
        checkBoxLock.setChecked(app.getScreen());
        checkBoxLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                app.setScreen(b);

                if (app.getScreen()) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                } else {
                    getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
                checkBoxLock.setSelected(app.getScreen());
            }
        });

        View layoutScreenLock = findView(R.id.layout_screen_lock);
        layoutScreenLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBoxLock.performClick();
            }
        });


        View layoutLanguage = findView(R.id.layout_language);
        layoutLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(SettingActivity.this, R.style.MyDialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setContentView(R.layout.language_dialog);

                ListView listView = (ListView) dialog.findViewById(R.id.list);

                String[] languages = resources.getStringArray(R.array.languages);

                final ArrayList<LanguageItem> list = new ArrayList<LanguageItem>();
                for (int i = 0; i < languages.length; ++i) {
                    LanguageItem languageItem = new LanguageItem();
                    languageItem.setName(languages[i]);
                    languageItem.setPosition(i);
                    languageItem.setSelected(app.loadLocale() == i);

                    list.add(languageItem);
                }

                final LanguageAdapter listNameAdapter = new LanguageAdapter(SettingActivity.this, 0, list);
                listNameAdapter.setFactory(new ListItemsFactory<LanguageItem>() {
                    @Override
                    public void createMoreItems(ListHandler<LanguageItem> languageItemListHandler, int position) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override
                    public void setOnClickListener(LanguageItem item, View view) {
                        String lang;
                        switch (item.getPosition()) {
                            case 0:
                                lang = "en";
                                break;
                            case 1:
                            default:
                                lang = "ru";
                                break;
                        }

                        app.changeLang(lang, item.getPosition());
//
                        showAlertReenterDialog();
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


//                    public void onClick(DialogInterface dialog, int item) {
//                        String lang;
//                        switch (item) {
//                            case 0:
//                                lang = "en";
//                                break;
//                            case 1:
//                                lang = "fr";
//                                break;
//                            case 2:
//                                lang = "ar";
//                                break;
//                            case 3:
//                                lang = "es";
//                            case 4:
//                                lang = "pt";
//                                break;
//                            case 5:
//                                lang = "de";
//                                break;
//                            case 6:
//                                lang = "it";
//                                break;
//                            case 7:
//                                lang = "ru";
//                                break;
//                            case 8:
//                                lang = "ro";
//                                break;
//                            case 9:
//                                lang = "nl";
//                                break;
//                            case 10:
//                                lang = "el";
//                                break;
//                            case 11:
//                                lang = "zh";
//                                break;
//                            case 12:
//                                lang = "ko";
//                                break;
//                            case 13:
//                                lang = "tr";
//                                break;
//                            case 14:
//                                lang = "hr";
//                                break;
//                            case 15:
//                                lang = "sv";
//                                break;
//                            case 16:
//                                lang = "be";
//                                break;
//                            case 17:
//                                lang = "bg";
//                                break;
//                            case 18:
//                                lang = "da";
//                                break;
//                            case 19:
//                                lang = "ga";
//                                break;
//                            case 20:
//                                lang = "pl";
//                                break;
//                            case 21:
//                                lang = "fi";
//                                break;
//                            case 22:
//                                lang = "he";
//                                break;
//                            case 23:
//                                lang = "hi";
//                                break;
//                            case 24:
//                                lang = "cs";
//                                break;
//                            case 25:
//                                lang = "ja";
//                                break;
//                            default:
//                                lang = "en";
//                                break;
//                        }
//                        app.changeLang(lang, item);
//
//                        showAlertReenterDialog();
//                        dialog.dismiss();
//                    }
//                });

            }
        });

        View layoutCurrency = findView(R.id.layout_currency);
        layoutCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(SettingActivity.this, R.style.MyDialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.currency_dialog);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        dialog.dismiss();
                    }
                });

                ListView listView = (ListView) dialog.findViewById(R.id.list);

                String[] currencies = resources.getStringArray(R.array.currencies);


                final ArrayList<CurrencyItem> list = new ArrayList<CurrencyItem>();
                for (int i = 0; i < currencies.length; ++i) {
                    CurrencyItem currencyItem = new CurrencyItem();
                    currencyItem.setName(currencies[i]);
                    currencyItem.setPosition(i);
                    currencyItem.setSelected(app.getCurrencyPosition() == i);
                    list.add(currencyItem);
                }

                final CurrenciesAdapter listNameAdapter = new CurrenciesAdapter(SettingActivity.this, 0, list);
                listNameAdapter.setFactory(new ListItemsFactory<CurrencyItem>() {
                    @Override
                    public void createMoreItems(ListHandler<CurrencyItem> languageItemListHandler, int position) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override
                    public void setOnClickListener(CurrencyItem item, View view) {
                        switch (item.getPosition()) {
                            case 0:
                                app.setCurrency("руб.");
                                app.setCurrencyPosition(0);
                                break;
                            case 1:
                                app.setCurrency("грн.");
                                app.setCurrencyPosition(1);
                                break;
                            case 2:
                                app.setCurrency("$");
                                app.setCurrencyPosition(2);
                                break;
                            case 3:
                                app.setCurrency("€");
                                app.setCurrencyPosition(3);
                                break;
                            case 4:
                                app.setCurrency("ман.");
                                app.setCurrencyPosition(4);
                                break;
                            case 5:
                                app.setCurrency("драм");
                                app.setCurrencyPosition(5);
                                break;
                            case 6:
                                app.setCurrency("руб.");
                                app.setCurrencyPosition(6);
                                break;
                            case 7:
                                app.setCurrency("тнг");
                                app.setCurrencyPosition(7);
                                break;
                            case 8:
                                app.setCurrency("сом");
                                app.setCurrencyPosition(8);
                                break;
                            case 9:
                                app.setCurrency("леи");
                                app.setCurrencyPosition(9);
                                break;
                            case 10:
                                app.setCurrency("сом");
                                app.setCurrencyPosition(10);
                                break;
                            case 11:
                                app.setCurrency("m");
                                app.setCurrencyPosition(11);
                                break;
                            case 12:
                                app.setCurrency("сум");
                                app.setCurrencyPosition(12);
                                break;
                            case 13:
                                app.setCurrency("Т");
                                app.setCurrencyPosition(13);
                                break;
                            case 14:
                                app.setCurrency("Lari");
                                app.setCurrencyPosition(14);
                                break;
                            case 15:
                                app.setCurrency("Lt");
                                app.setCurrencyPosition(15);
                                break;
                            case 16:
                                app.setCurrency("Ls");
                                app.setCurrencyPosition(16);
                                break;
                            case 17:
                                app.setCurrency("лей");
                                app.setCurrencyPosition(17);
                                break;
                            case 18:
                                app.setCurrency("лев");
                                app.setCurrencyPosition(18);
                                break;
                            default:
                                app.setCurrency("руб.");
                                app.setCurrencyPosition(0);
                                break;
                        }
                        dialog.dismiss();

                        showToast(resources.getString(R.string.changed_currency, app.getCurrency()));

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
        });


        final CheckBox checkBoxDuplicateGood = (CheckBox) findView(R.id.checkbox_duplicate_lock);
        checkBoxDuplicateGood.setSelected(app.checkDuplicateGood());
        checkBoxDuplicateGood.setChecked(app.checkDuplicateGood());
        checkBoxDuplicateGood.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                app.saveDuplicateGood(b);

                checkBoxDuplicateGood.setSelected(app.checkDuplicateGood());
            }
        });

        View layoutDuplicateGood = findView(R.id.layout_duplicate_good);
        layoutDuplicateGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBoxDuplicateGood.performClick();
            }
        });


//        View layoutCategories = findView(R.id.layout_categories);
//        layoutCategories.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SettingActivity.this, CategoriesActivity.class);
//                startActivity(intent);
//            }
//        });


        final CheckBox checkBoxNote = (CheckBox) findView(R.id.checkbox_note);
        checkBoxNote.setSelected(app.getProductNote());
        checkBoxNote.setChecked(app.getProductNote());
        checkBoxNote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                app.setProductNote(b);

                checkBoxNote.setSelected(app.getProductNote());
            }
        });


        View layoutNote = findView(R.id.layout_note);
        layoutNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBoxDuplicateGood.performClick();
            }
        });


        final CheckBox checkBoxImage = (CheckBox) findView(R.id.checkbox_image);
        checkBoxImage.setSelected(app.getProductImage());
        checkBoxImage.setChecked(app.getProductImage());
        checkBoxImage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                app.setProductImage(b);
                checkBoxImage.setSelected(app.getProductImage());
            }
        });

        View layoutImage = findView(R.id.layout_image);
        layoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBoxImage.performClick();
            }
        });


        initActionBar();


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initActionBar() {
        actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);

        if (!app.isAuthorized()) {
            actionBar.setIcon(R.drawable.header_offline);
        } else {
            actionBar.setIcon(R.drawable.header_online);
        }

        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        headerOptions = mInflater.inflate(R.layout.header_options, null);
        actionBar.setCustomView(headerOptions);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }

        return true;
    }

    private void showAlertReenterDialog() {
        final Dialog dialog = new Dialog(this, R.style.MyDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.alert_dialog);

        ((TextView) dialog.findViewById(R.id.message)).setText(getResources().getString(R.string.reEnterToChangeLanguage));

        dialog.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
