package com.seeapenny.client.server.responses;


import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.bean.ShareCategory;
import com.seeapenny.client.json.JSONUtils;
import com.seeapenny.client.server.Response;
import com.seeapenny.client.server.User;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShareResponse extends Response {

    private User user;
    private List<ShareCategory> shareCategoryList;


    @Override
    public void fromJson(JSONObject rootDto) {
        super.fromJson(rootDto);


        user = new User();
        JSONUtils.fillJSONable(rootDto, "user", user);

        JSONObject catListObj = rootDto.optJSONObject("catList");

        if (catListObj != null) {
            shareCategoryList = new ArrayList<ShareCategory>();
            JSONUtils.arrayToList(catListObj, "category", shareCategoryList, new ShareCategory());
        }

    }


    public User getUser() {
        return user;
    }

    public List<ShareCategory> getShareCategoryList() {
        return shareCategoryList;
    }
}
