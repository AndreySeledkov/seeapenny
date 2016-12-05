package com.seeapenny.client.service;

import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.SynchAction;
import com.seeapenny.client.adapter.ShopList;
import com.seeapenny.client.dao.ShopListDAO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ShopListService {
    private ShopListDAO listDAO;
    private long lastShopListID;

    public static final ShopListFilter includeFilter = new ShopListFilter(true);
    public static final ShopListFilter excludeFilter = new ShopListFilter(false);

    public ShopListService() {
        listDAO = new ShopListDAO();
//        getAllList();

        lastShopListID = getLastId();
    }

    private Comparator<ShopList> shopListComparator = new Comparator<ShopList>() {
        @Override
        public int compare(ShopList o, ShopList o1) {
            return o1.getLastModifiedTime().compareTo(o.getLastModifiedTime());
        }
    };

    public ArrayList<ShopList> getAllListForSynch(ShopListFilter filter) {
        return listDAO.getAllList(filter);
    }

    public ArrayList<ShopList> getAllList(ShopListFilter filter) {
        ArrayList<ShopList> shopLists = listDAO.getAllList(filter);
        Collections.sort(shopLists, shopListComparator);
        return shopLists;


//        checkCache();
//        return shopListResponses;
    }


//    public ArrayList<ShopList> getLists(ShopListFilter filter) {
//        ArrayList<ShopList> shopLists = listDAO.getShopLists(filter);
//        Collections.sort(shopLists, new ShopListComparator());
//        return shopLists;
//    }

//    public List<ShopList> getAllListExceptDeletedAndInsert() {
////        checkCache();
//
//        List<ShopList> allListsExceptedDeletedAndInsert = listDAO.getAllListExceptDeletedAndInsert();
//
////        List<ShopList> allListsExceptedDeletedAndInsert = new ArrayList<ShopList>();
////        for (ShopList shopListResponse : shopListResponses) {
////            if (shopListResponse.getSynchAction() != SynchAction.DELETE && shopListResponse.getSynchAction() != SynchAction.INSERT) {
////                allListsExceptedDeletedAndInsert.add(shopListResponse);
////            }
////        }
//
//        Collections.sort(allListsExceptedDeletedAndInsert, new ShopListComparator());
//        return allListsExceptedDeletedAndInsert;
//    }

//    public List<ShopList> getAllListExceptDeleted() {
////        checkCache();
//
//        List<ShopList> allListsExceptedDeleted = listDAO.getAllListExceptDeleted();
//
////        List<ShopList> allListsExceptedDeleted = new ArrayList<ShopList>();
////        for (ShopList shopListResponse : shopListResponses) {
////            if (shopListResponse.getSynchAction() != SynchAction.DELETE) {
////                allListsExceptedDeleted.add(shopListResponse);
////            }
////        }
//        Collections.sort(allListsExceptedDeleted, new ShopListComparator());
//        return allListsExceptedDeleted;
//    }

//    public List<ShopList> getAllListWithoutOwner() {
////        checkCache();
//
//        List<ShopList> allListsWithoutOwner = listDAO.allListsWithoutOwner();
//
////        List<ShopList> allListsWithoutOwner = new ArrayList<ShopList>();
////        for (ShopList shopListResponse : shopListResponses) {
////            if (shopListResponse.getOwnerId() == 0) {
////                allListsWithoutOwner.add(shopListResponse);
////            }
////        }
//
//        return allListsWithoutOwner;
//    }

    public void updateEmptyOwnerId(long ownerId) {
        listDAO.updateEmptyOwnerId(ownerId);
//        shopListResponses = null;
    }

    public long getLastShopListID() {
        return lastShopListID;
    }

    private long getLastId() {
//        checkCache();

        return listDAO.getLastId();

//        long lastId = 0;
//        for (ShopList shopListResponse : shopListResponses) {
//            if (shopListResponse.getId() > lastId) {
//                lastId = shopListResponse.getId();
//            }
//        }
//        return lastId;
    }

    public void resetLastId() {
        lastShopListID = getLastId();
    }

    public ShopList addShopList(long id, long ownerID, String name, String createTime, String lastModifiedTime, SynchAction synchAction) {
        ShopList shopList = listDAO.addShopList(id, ownerID, name, createTime, lastModifiedTime, synchAction);
//        if (shopList != null) {
//            shopListResponses.add(0, shopList);
//        }
        return shopList;
    }

    public ShopList addShopList(long ownerID, String name, String createTime, String lastModifiedTime, SynchAction synchAction) {
        ShopList shopList = listDAO.addShopList(++lastShopListID, ownerID, name, createTime, lastModifiedTime, synchAction);
//        if (shopList != null) {
//            shopListResponses.add(0, shopList);
//        }
        return shopList;
    }

    //исподьзовать только для добавление share list
    public ShopList addShareShopList(long id, long ownerID, String name, String createTime, String lastModifiedTime, SynchAction synchAction) {
        ShopList shopList = listDAO.addShopList(id, ownerID, name, createTime, lastModifiedTime, synchAction);
//        if (shopList != null) {
//            shopListResponses.add(0, shopList);
//        }
        return shopList;
    }

    public ShopList addShopListRecovery(long id, long ownerID, String name, String createTime, String lastModifiedTime, SynchAction synchAction) {
        if (ownerID == SeeapennyApp.getInstance().getOwnerID()) {
            lastShopListID = Math.max(lastShopListID, id);
        }

        ShopList shopList = listDAO.addShopList(id, ownerID, name, createTime, lastModifiedTime, synchAction);
//        if (shopList != null) {
//            shopListResponses.add(0, shopList);
//        }
        return shopList;
    }

    public boolean save(long id, long ownerId, String name, String lastModifiedTime, SynchAction synchAction) {
        return listDAO.save(id, ownerId, name, lastModifiedTime, synchAction);


//        Iterator<ShopList> shopListIterator = shopListResponses.iterator();
//        while (shopListIterator.hasNext()) {
//            ShopList item = shopListIterator.next();
//            if (item.getId() == shopListResponse.getId()) {
//                shopListIterator.remove();
//                break;
//            }
//        }
//        shopListResponses.add(shopListResponse);
    }

    public boolean save(ShopList shopList) {
        return listDAO.save(shopList);


//        Iterator<ShopList> shopListIterator = shopListResponses.iterator();
//        while (shopListIterator.hasNext()) {
//            ShopList item = shopListIterator.next();
//            if (item.getId() == shopListResponse.getId()) {
//                shopListIterator.remove();
//                break;
//            }
//        }
//        shopListResponses.add(shopListResponse);
    }

    public ShopList getShopList(long listID, long ownerId) {
//        checkCache();

        return listDAO.getShopList(listID, ownerId);

//        for (ShopList item : shopListResponses) {
//            if (item.getId() == listID) {
//                return item;
//            }
//        }
//        //todo EXCEPTION
//
//        return null;
    }

//    public List<ShopList> getAllShopListInserted() {
////        checkCache();
//
//        List<ShopList> shopLists = new ArrayList<ShopList>();
//
//        for (ShopList item : shopListResponses) {
//            if (item.getSynchAction() == SynchAction.INSERT) {
//                shopLists.add(item);
//            }
//        }
//
//        return shopLists;
//    }

//    public long getMaxIdInserted() {
////        checkCache();
//
//
//        return listDAO.getMaxIdInserted();
//
////        long max = 0L;
////
////        for (ShopList item : shopListResponses) {
////            if (item.getSynchAction() == SynchAction.INSERT && item.getId() > max) {
////                max = item.getId();
////            }
////        }
////
////        return max;
//    }

//    public boolean updateId(long newID, long oldID, long ownerId) {
//        boolean b = listDAO.updateId(newID, oldID, ownerId);
//
////        if (b) {
////            Iterator<ShopList> shopListIterator = shopListResponses.iterator();
////            while (shopListIterator.hasNext()) {
////                ShopList item = shopListIterator.next();
////                if (item.getId() == oldID) {
////                    item.setId(newID);
////                    break;
////                }
////            }
////        }
//        return b;
//    }

    public boolean remove(long listID, long ownerId) {
        return listDAO.remove(listID, ownerId);


//        if (ok) {
//
//            Iterator<ShopList> shopListIterator = shopListResponses.iterator();
//            while (shopListIterator.hasNext()) {
//                ShopList shopListResponse = shopListIterator.next();
//                if (shopListResponse.getId() == listID) {
//                    shopListIterator.remove();
//                    break;
//                }
//            }
//
//            //todo EXCEPTION
//        } else {
//            //todo EXCEPTION
//        }

    }

    public void removeAll() {
        boolean ok = listDAO.removeAll();

        resetLastId();

//        if (ok) {
//            shopListResponses.clear();
//        } else {
//            //todo EXCEPTION
//        }
    }

    public void truncate() {

    }
}
