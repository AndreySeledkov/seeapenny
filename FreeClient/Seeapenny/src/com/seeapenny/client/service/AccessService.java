package com.seeapenny.client.service;

import android.content.Context;

import com.seeapenny.client.bean.Access;
import com.seeapenny.client.dao.AccessDAO;
import com.seeapenny.client.server.User;

import java.util.List;

/**
 * Created by Sony on 15.07.13.
 */
public class AccessService {

    private AccessDAO accessDAO;

    public AccessService() {
        accessDAO = new AccessDAO();
    }

    public void addAccess(long contentID, AccessType accessType, long userFrom, long userTo) {
        accessDAO.add(contentID, accessType.getId(), userFrom, userTo);
    }

    public void removeAccess(long contentID, AccessType accessType, long userTo) {
        accessDAO.remove(contentID, accessType.getId(), userTo);
    }

    public List<User> getShareUsers(long contentID, AccessType accessType) {
        return null;
//        return accessDAO.getShareUsers(contentID, accessType.getId());
    }


    public boolean isShared(AccessType accessType, long contentID) {
        List<Access> accessList = accessDAO.getAccessEntries(accessType, contentID);
        return accessList.size() > 0;
    }

    public boolean removeAll(){
        return accessDAO.removeAll();
    }

}
