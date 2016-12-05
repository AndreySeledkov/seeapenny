package com.seeapenny.client.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.seeapenny.client.General;
import com.seeapenny.client.R;
import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.SynchAction;
import com.seeapenny.client.adapter.CategoriesAdapter;
import com.seeapenny.client.adapter.Good;
import com.seeapenny.client.adapter.MeasureAdapter;
import com.seeapenny.client.adapter.ShopList;
import com.seeapenny.client.bean.GoodCategory;
import com.seeapenny.client.http.HttpCommand;
import com.seeapenny.client.http.HttpHandler;
import com.seeapenny.client.http.HttpUploadCommand;
import com.seeapenny.client.server.*;
import com.seeapenny.client.server.responses.GoodResponseWrapper;
import com.seeapenny.client.server.responses.PhotoResponse;
import com.seeapenny.client.util.FieldGood;

import java.util.Date;
import java.util.EnumSet;
import java.util.List;

public class EditGoodActivity extends SeeapennyActivity implements View.OnClickListener, DialogInterface.OnClickListener {

    private static final int DIALOG_MODE_CHOOSE_PHOTO = 1;
    private static final String TEMP = "temp";

    private Long goodID;
    private long listID;
    private long ownerID;

    private Double price;
    private Double quantity;

    private Integer category;
    private Integer measure;
    private Boolean isPriority;
    private String name;

    private EditText titleGood;
    private EditText priceEditText;
    private EditText quantityEditText;
    private EditText noteEditText;
    private Spinner categoriesSpinner;
    private Spinner spinnerMeasure;

    private com.actionbarsherlock.view.Menu headerMenu;
    private int dialogMode;

    private ActionBar actionBar;
    private View headerOptions;

    private ImageView goodPhoto;
    private Uri photoUri;

    private TextView changePhoto;

    private View goodAdditionalPriority;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_good);


        initActionBar();

        titleGood = (EditText) findView(R.id.titleGood);
        priceEditText = (EditText) findView(R.id.price);
        quantityEditText = (EditText) findView(R.id.quantity);
        noteEditText = (EditText) findView(R.id.editTextNote);
        categoriesSpinner = (Spinner) findViewById(R.id.categories);
        spinnerMeasure = (Spinner) findViewById(R.id.spinnerWeight);
        goodAdditionalPriority = findViewById(R.id.good_important);
        TextView goodImportantText = (TextView) findViewById(R.id.good_important_text);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            goodID = (Long) extras.get("GOOD_ITEM");
            listID = (Long) extras.get("LIST_ITEM");
            ownerID = extras.getLong("OWNER_ID");

            price = (Double) extras.get("PRICE");
            quantity = (Double) extras.get("QUANTITY");
            measure = (Integer) extras.get("MEASURE");
            isPriority = (Boolean) extras.get("IS_PRIORITY");
            name = (String) extras.get("NAME");
        }

        String[] measures = resources.getStringArray(R.array.measure_array);

        MeasureAdapter measureAdapter = new MeasureAdapter(this, 0, measures);
        spinnerMeasure.setAdapter(measureAdapter);
        spinnerMeasure.setSelection(measure != null ? measure : 0);
        spinnerMeasure.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                measure = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        final List<GoodCategory> goodCategories = categoryService.getListCategory();

        categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                GoodCategory goodCategory = goodCategories.get(position);
                category = goodCategory.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(this, 0, goodCategories);
        categoriesSpinner.setAdapter(categoriesAdapter);

        titleGood.setText(name != null ? name : "");
        if (price != null) {
            priceEditText.setText(String.valueOf(price));
        }
        if (quantity != null) {
            quantityEditText.setText(String.valueOf(quantity));
        }
        goodAdditionalPriority.setSelected(isPriority != null ? isPriority : false);

        if (goodID != null) {
            Good good = goodService.getGoodById(listID, goodID, ownerID);
            noteEditText.setText(good.getNote());

            categoriesSpinner.setSelection((int) good.getCategoryId());
        }


        goodPhoto = (ImageView) findViewById(R.id.photo);

//        if (selectedGood.getSmallUrl().length() > 0) {
//            General.tryDownloadImage(goodPhoto, selectedGood.getSmallUrl());
//            changePhoto.setText(R.string.change_photo);
//        }

        goodPhoto.setOnClickListener(this);


        changePhoto = (TextView) findView(R.id.change_photo);


        goodAdditionalPriority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPriority = !isPriority;
                goodAdditionalPriority.setSelected(!goodAdditionalPriority.isSelected());
            }
        });
        goodImportantText.setText(Html.fromHtml("<font color=\"#000000\"><a href=\"\">" + resources.getString(R.string.important_good) + "</></font>"));
        goodImportantText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPriority = !isPriority;
                goodAdditionalPriority.setSelected(!goodAdditionalPriority.isSelected());
            }
        });


        View minus = findViewById(R.id.minus);
        View plus = findViewById(R.id.plus);


        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = quantityEditText.getText().toString().trim();
                if (str.length() != 0) {
                    try {
                        double quantity = Double.valueOf(quantityEditText.getText().toString().trim());
                        if (quantity > 1) {
                            quantityEditText.setText(String.format("%.1f", quantity - 1).replace(",", "."));
                        } else {
                            quantityEditText.setText(null);
                        }
                    } catch (Exception e) {

                    }
                }
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = quantityEditText.getText().toString().trim();
                if (str.length() == 0) {
                    quantityEditText.setText(String.valueOf(1.0F));
                } else {
                    try {

                        double quantity = Double.valueOf(quantityEditText.getText().toString().trim()) + 1F;

                        quantityEditText.setText(String.format("%.1f", quantity).replace(",", "."));
                    } catch (Exception e) {

                    }
                }
            }
        });


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

        actionBar.setIcon(R.drawable.header_online);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        headerMenu = menu;
        headerMenu.clear();

        com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu_edit_good, headerMenu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.save_item:
                saveGood();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
//            case R.id.cps_camera:
//                app.getPhotoChooser().photoSourceChoosed(this, true);
//                break;
//            case R.id.cps_gallery:
//                app.getPhotoChooser().photoSourceChoosed(this, false);
//                break;
            case R.id.photo:
                String key = goodID + "_" + listID + "_" + ownerID;

                app.getPhotoChooser().photoSourceChoosed(this, true, key);
//                dialogMode = DIALOG_MODE_CHOOSE_PHOTO;
//                SeeapennyApp.getPhotoChooser().showChoosePhotoSourceDialog(this, this);
                break;

        }
    }

    private void saveGood() {
        ShopList shopList = shopListService.getShopList(listID, ownerID);
        String newName = titleGood.getText().toString().trim();
        if (newName.length() == 0) {
            super.onBackPressed();
            return;
        }


        double newPrice = 0F;

        try {
            String priceStr = priceEditText.getText().toString().trim();
            if (priceStr.length() > 0) {
                newPrice = Double.valueOf(priceStr);
            }
        } catch (Exception e) {

        }

        double newQuantity = 0F;
        try {
            String quantityStr = quantityEditText.getText().toString().trim();
            if (quantityStr.length() > 0) {
                newQuantity = Double.valueOf(quantityStr);
            }
        } catch (Exception e) {

        }

        String newNote = noteEditText.getText().toString().trim();

        if (!app.isAuthorized()) {
            if (goodID != null) {
                EnumSet<FieldGood> fieldGoods = FieldGood.createEmptySaveTypes();

                Good good = goodService.getGoodById(listID, goodID, ownerID);

                good.changeName(newName, fieldGoods);
                good.changePrice(newPrice, fieldGoods);
                good.changeQuantity(newQuantity, fieldGoods);
                good.changeMeasure(measure, fieldGoods);
                good.changePriority(isPriority, fieldGoods);
                good.changeNote(newNote, fieldGoods);
                good.changeCategory(category, fieldGoods);
                good.changeImageID(0, fieldGoods);//todo
                good.changeLastEditor(app.getOwnerID(), fieldGoods);

                if (fieldGoods.size() > 0) {

                    if (shopList.isShared()) {
                        //todo
                    }

                    good.changeSynchAction(fieldGoods);

                    Date date = new Date();
                    good.setModifiedTime(date);
                    fieldGoods.add(FieldGood.MODIFIED_TIME);

                    boolean ok = goodService.save(fieldGoods, good);
                    if (ok) {


                        shopList.setLastModifiedTime(date);

                        shopList.changeSynchAction();
                        shopListService.save(shopList);


                        makeSoundAdd();
                    }
                }

            } else {
                Date date = new Date();

                Good good = goodService.insert(listID, newName, newPrice, newQuantity, measure, isPriority,
                        newNote, category, 0, State.NORMAL.getId(), 0, 0, app.formatDatetime(date), app.formatDatetime(date), SynchAction.INSERT);

                if (good != null) {

                    if (shopList.isShared()) {
                        //todo
                    }

                    shopList.setLastModifiedTime(date);
                    shopList.changeSynchAction();
                    shopListService.save(shopList);

                    makeSoundAdd();
                }
            }
            super.onBackPressed();
        } else {
            disableWaitForNextCommand();

            if (goodID != null) {
                Good good = goodService.getGoodById(listID, goodID, ownerID);

                saveGood = new HttpCommand(this, new GoodResponseWrapper());

                saveGood.addParam(LIST_OWNER_ID, good.getOwnerId());

                saveGood.addParam(LIST_ID_PARAM, good.getListId());
                saveGood.addParam(GOOD_ID_PARAM, good.getId());

                if (!good.getName().equals(newName)) {
                    saveGood.addParam(GOOD_NAME, newName);
                }

                if (good.getCategoryId() != category) {
                    saveGood.addParam(GOOD_CATEGORY, category);
                }

                if (good.getPrice() != newPrice) {
                    saveGood.addParam(GOOD_PRICE, newPrice);
                }

                if (good.getNote() != newNote) {
                    saveGood.addParam(GOOD_NOTE, newNote);
                }

                saveGood.addParam(GOOD_PRIORITY, isPriority);

                saveGood.addParam(GOOD_STATE, State.NORMAL.ordinal());

                if (good.getQuantity() != newQuantity) {
                    saveGood.addParam(GOOD_QUANTITY, newQuantity);
                }

                if (good.getMeasure() != measure) {
                    saveGood.addParam(GOOD_MEASURE, measure);
                }

                saveGood.addParam(GOOD_IMAGE_ID, 0);//todo сделать фото

                saveGood.sendAsForm(SeeapennyApp.getHttpHandler(), SeeapennyActivity.SAVE_GOODS);
            } else {

                addGood = new HttpCommand(this, new GoodResponseWrapper());

                addGood.addParam(LIST_OWNER_ID, shopList.getOwnerId());
                addGood.addParam(GOOD_ID_PARAM, goodService.getLastGoodId(listID, shopList.getOwnerId()));
                addGood.addParam(LIST_ID_PARAM, listID);
                addGood.addParam(GOOD_NAME, newName);

                addGood.addParam(GOOD_CATEGORY, category);

                if (newPrice != 0) {
                    addGood.addParam(GOOD_PRICE, newPrice);
                }

                if (newNote.length() > 0) {
                    addGood.addParam(GOOD_NOTE, newNote);
                }

                addGood.addParam(GOOD_PRIORITY, isPriority);

                addGood.addParam(GOOD_STATE, State.NORMAL.ordinal());

                if (newQuantity != 0) {
                    addGood.addParam(GOOD_QUANTITY, newQuantity);
                }

                addGood.addParam(GOOD_MEASURE, measure);
                addGood.addParam(GOOD_IMAGE_ID, 0);//todo сделать фото

                addGood.sendAsForm(SeeapennyApp.getHttpHandler(), SeeapennyActivity.ADD_GOODS);
            }

        }

    }

    @Override
    protected void onSuccessfullResponse(HttpCommand command, Response response) {
        if (photoUploadCommand == command) {
            PhotoResponse photoResponse = (PhotoResponse) response;

            Image photo = photoResponse.getPhoto();
//            user.addPhoto(photo);
//            View optionsPanel = showView(R.id.options_panel);
//            General.updateNumPhotos(optionsPanel, user, toPhotosButton);
//            sessionState.setUser(user);

//            if (user.getPhotos().size() == 1) {
            String avatarUrl = photo.getSmallUrl();

//            selectedGood.setImageId(photo.getId());
//            good.setLargeUrl(photo.getLargeUrl());
//            good.setSmallUrl(photo.getSmallUrl());

            General.tryDownloadWaitableImage(goodPhoto, null, avatarUrl); //todo
//            }
        } else if (addGood == command) {
            GoodResponseWrapper goodResponseWrapper = (GoodResponseWrapper) response;

            GoodResponse goodResponseResponse = goodResponseWrapper.getGoodResponse();

            Date serverCreateTime = goodResponseResponse.getCreateTime();
            Date localCreateTime = app.convertToLocalDate(serverCreateTime);

            Date serverLastModifiedTime = goodResponseResponse.getModifiedTime();
            Date localLastModifiedTime = app.convertToLocalDate(serverLastModifiedTime);

//            if (goodResponseResponse.getGoodCategory() == null) {
//                goodResponseResponse.setGoodCategory(categoryService.getCategoryId(goodResponseResponse.getCategoryId()));
//            }

            Good good = goodService.insert(goodResponseResponse.getListId(), goodResponseResponse.getName(), goodResponseResponse.getPrice(), goodResponseResponse.getQuantity(), goodResponseResponse.getMeasure(), goodResponseResponse.isPriority(), goodResponseResponse.getNote(),
                    goodResponseResponse.getCategoryId(), goodResponseResponse.getImageId(), goodResponseResponse.getState(), goodResponseResponse.getOwnerId(), goodResponseResponse.getLastEditorId(), app.formatDatetime(localCreateTime), app.formatDatetime(localLastModifiedTime), SynchAction.NO_CHANGES);

            if (good != null) {
                ShopList shopList = shopListService.getShopList(listID, ownerID);
                shopList.setLastModifiedTime(localLastModifiedTime);
                shopListService.save(shopList);
            }
            makeSoundAdd();

            super.onBackPressed();
        } else if (saveGood == command) {
            GoodResponseWrapper goodResponseWrapper = (GoodResponseWrapper) response;

            GoodResponse goodResponseResponse = goodResponseWrapper.getGoodResponse();

            EnumSet<FieldGood> fieldGoods = FieldGood.createEmptySaveTypes();

            Good good = goodService.getGoodById(listID, goodID, ownerID);
            good.changeName(goodResponseResponse.getName(), fieldGoods);
            good.changePrice(goodResponseResponse.getPrice(), fieldGoods);
            good.changeQuantity(goodResponseResponse.getQuantity(), fieldGoods);
            good.changeMeasure(goodResponseResponse.getMeasure(), fieldGoods);
            good.changePriority(goodResponseResponse.isPriority(), fieldGoods);
            good.changeNote(goodResponseResponse.getNote(), fieldGoods);
            good.changeCategory(goodResponseResponse.getCategoryId(), fieldGoods);
            good.changeImageID(goodResponseResponse.getImageId(), fieldGoods);
            good.changeLastEditor(goodResponseResponse.getLastEditorId(), fieldGoods);


            Date serverLastModifiedTime = good.getModifiedTime();
            Date localLastModifiedTime = app.convertToLocalDate(serverLastModifiedTime);

            if (fieldGoods.size() > 0) {
                boolean ok = goodService.save(fieldGoods, good);

                if (ok) {
                    good.setModifiedTime(localLastModifiedTime);
                    fieldGoods.add(FieldGood.MODIFIED_TIME);

                    ShopList shopList = shopListService.getShopList(listID, ownerID);
                    shopList.setLastModifiedTime(localLastModifiedTime);
                    shopListService.save(shopList);

                    makeSoundAdd();
                }
            }

            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        debug("onActivityResult reqCode: " + requestCode + " resCode: " + resultCode + " data: " + data);
        if (resultCode == RESULT_OK) {
            photoUri = SeeapennyApp.getPhotoChooser().photoUri(this, requestCode, data);
//            debug("onActivityResult photoUri: " + photoUri);
            if (photoUri != null) {

                goodPhoto.setImageURI(photoUri);


//                photoUploadCommand = new HttpUploadCommand(this, new PhotoResponse());
//                sendPhotoUploadCommand(photoUploadCommand, photoUri, UPLOAD_PHOTO_GOOD);
            }
        }
    }

    @Override
    public void onBackPressed() {
        saveGood();
    }

    public void sendPhotoUploadCommand(HttpUploadCommand photoUploadCommand, Uri photoUri, String requestUrl) {
        Cursor cur = getContentResolver().query(photoUri, new String[]{MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.MIME_TYPE},
                null, null, null);
        String photoPath = null;
        String contentType = null;
        if (cur == null) {
            photoPath = photoUri.getPath();
            contentType = "image/jpeg";

//            debug("sendPhotoUploadCommand null photoPath: " + photoPath);
        } else {
            if (cur.moveToFirst()) {
                photoPath = cur.getString(0);
                contentType = cur.getString(1);

//                debug("sendPhotoUploadCommand first photoPath: " + photoPath);
            } else {
                photoPath = photoUri.getPath();
                contentType = "image/jpeg";

//                debug("sendPhotoUploadCommand other photoPath: " + photoPath);
            }
            cur.close();
        }

//        debug("sendPhotoUploadCommand photoPath: " + photoPath);
        photoUploadCommand.sendUpload(requestUrl, photoPath, contentType, HttpHandler.MIME_JSON);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        if (dialogMode == DIALOG_MODE_CHOOSE_PHOTO) {
//            debug("photoSourceChoosed: " + which);
//            if (selectedGood.getId() != 0) {//todo e
//
//                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(selectedGood.getCreateTime());
//                SeeapennyApp.getPhotoChooser().photoSourceChoosed(this, which, timeStamp);
//            } else {
//                SeeapennyApp.getPhotoChooser().photoSourceChoosed(this, which, TEMP);
//            }
        }
    }

}
