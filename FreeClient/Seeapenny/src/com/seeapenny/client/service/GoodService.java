package com.seeapenny.client.service;

import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.SynchAction;
import com.seeapenny.client.adapter.Good;
import com.seeapenny.client.bean.GoodCategory;
import com.seeapenny.client.dao.GoodDAO;
import com.seeapenny.client.server.State;
import com.seeapenny.client.util.FieldGood;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 04.03.13
 * Time: 1:02
 * To change this template use File | Settings | File Templates.
 */
//todo
public class GoodService {

    private GoodDAO goodDAO;
    private SeeapennyApp app;

    private HashMap<Long, Long> goodLastId = new HashMap<Long, Long>();

//    private long lastGoodID;

    public GoodService() {
        goodDAO = new GoodDAO();
        app = SeeapennyApp.getInstance();
        init();

//        lastGoodID = getLastId();

    }

    public long getLastGoodId(long listId, long ownerId) {
        Long lastGoodId = goodLastId.get(listId);
        if (lastGoodId == null) {
            goodLastId.put(listId, lastGoodId = getLastId(listId, ownerId));
        }
        return lastGoodId;
    }


    public long addAndGetLastGoodId(long listId, long ownerId) {
        Long lastGoodId = goodLastId.get(listId);
        if (lastGoodId == null) {
            lastGoodId = getLastId(listId, ownerId);
        }
        goodLastId.put(listId, ++lastGoodId);
        return lastGoodId;
    }

    private long getLastId(long listId, long ownerId) {
//        checkCache();

        return goodDAO.getLastId(listId, ownerId);

//        long lastId = 0;
//        String key = buildKey(listId, ownerId);
//
//        List<Good> goodList = goodMap.get(key);
//        if (goodList != null) {
//            for (Good good : goodList) {
//                if (good.getId() > lastId) {
//                    lastId = good.getId();
//                }
//            }
//        }
//
//        return lastId;
    }


    private void init() {
//        goodMap = new HashMap<String, List<Good>>();
//        List<Good> goodList = goodDAO.getAllGood();
//
//        for (Good good : goodList) {
//            String key = buildKey(good.getListId(), good.getOwnerId());
//
//            List<Good> goodResponses = goodMap.get(key);
//            if (goodResponses == null) {
//                goodMap.put(key, goodResponses = new ArrayList<Good>());
//            }
//            goodResponses.add(good);
//        }
    }

//    private void checkCache() {
//        if (goodMap == null) {
//            init();
//        }
//    }


    private String buildKey(long listId, long ownerId) {
        return new StringBuilder().append(listId).append(":").append(ownerId).toString();
    }

//    public ArrayList<Good> getGoods(GoodFilter filter, long listId, long ownerId) {
//        return goodDAO.getGoods(filter, listId, ownerId);
//    }

    public List<Good> getAllGood() {


        return goodDAO.getAllGood();

//        checkCache();
//        List<Good> goodList = new ArrayList<Good>();
//
//        Iterator<Map.Entry<String, List<Good>>> iterator = goodMap.entrySet().iterator();
//
//        while (iterator.hasNext()) {
//            goodList.addAll(iterator.next().getValue());
//        }
//
//        return goodList;
    }

    public int getLeftGoods(long listId, long ownerId) {
        ArrayList<Good> goods = goodDAO.getAllGoodByListId(listId, ownerId);

        int index = 0;
        for (Good good : goods) {
            if (State.valueOf(good.getState()) == State.NORMAL) {
                index++;
            }
        }
        return index;
    }

    public void resetLastId(long listId, long newValue) {
        goodLastId.put(listId, newValue);
    }

    public boolean isExist(long listId, long ownerId, Long obj, String goodTitle) {
//        checkCache();
        if (obj != null) {
            return false;
        }
        if (app.checkDuplicateGood()) {

            List<Good> goodList = getAllGood(listId, ownerId);

//            String key = buildKey(listId, ownerId);
//
//            List<Good> goodList = goodMap.get(key);
            if (goodList != null) {
                for (Good good : goodList) {
                    if (good.getName().equals(goodTitle)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public Good getGoodById(long listId, long goodId, long ownerId) {
//        checkCache();

        return goodDAO.getGoodById(listId, goodId, ownerId);

//        String key = buildKey(listId, ownerId);
//
//        List<Good> goodList = goodMap.get(key);
//        if (goodList != null) {
//            for (Good good : goodList) {
//                if (good.getId() == goodId) {
//                    return good;
//                }
//            }
//        }
//
//        //todo Exception
//        return null;
    }

    public List<Good> getAllGoodByCategoryId(long ownerId, long categoryId) {

        return goodDAO.getAllGoodByCategoryId(ownerId, categoryId);

//        checkCache();
//        List<Good> goodResponseList = new ArrayList<Good>();
//
//        for (Map.Entry<String, List<Good>> entry : goodMap.entrySet()) {
//            List<Good> items = entry.getValue();
//            for (Good good : items) {
//                if (good.getCategoryId() == categoryId && good.getOwnerId() == ownerId) {
//                    goodResponseList.add(good);
//                }
//            }
//        }

//        return goodResponseList;
    }

    public ArrayList<Good> getAllGood(long listId, long ownerId) {
//        checkCache();

        ArrayList<Good> goodList = goodDAO.getAllGoodByListId(listId, ownerId);
        Collections.sort(goodList, new GoodComparator());
        return goodList;

//        String key = buildKey(listId, ownerId);
//
//        List<Good> goodList = goodMap.get(key);
//        if (goodList == null) {
//            //todo exception
//
//            return Collections.<Good>emptyList();
//        }
//
//        return goodList;
    }

//    public List<Good> getAllGoodWithoutOwner() {
//        checkCache();
//        List<Good> goodsListWithoutOwner = new ArrayList<Good>();
//
//        Collection c = goodMap.values();
//        Iterator it = c.iterator();
//        while (it.hasNext()) {
//            List<Good> goodList = (List<Good>) it.next();
//            for (Good good : goodList) {
//                if (good.getOwnerId() == 0) {
//                    goodsListWithoutOwner.add(good);
//                }
//            }
//        }
//
//        return goodsListWithoutOwner;
//    }

//    public List<Good> getAllGoodByListIdExceptDeleted(long listId, long ownerId) {
//        checkCache();
//        List<Good> goodListExceptDeleted = new ArrayList<Good>();
//
//        String key = buildKey(listId, ownerId);
//
//        List<Good> goodList = goodMap.get(key);
//        if (goodList != null) {
//            for (Good good : goodList) {
//                if (good.getListId() == listId && good.getSynchAction() != SynchAction.DELETE) {
//                    goodListExceptDeleted.add(good);
//                }
//            }
//        } else {
//            //todo exception
//        }
//        Collections.sort(goodListExceptDeleted, new GoodComparator());
//        return goodListExceptDeleted;
//    }
//
//
//    public List<Good> getAllGoodByListIdExceptDeletedAndInsert(long listId, long ownerId) {
//        checkCache();
//        List<Good> goodResponseListExceptDeletedAndInsert = new ArrayList<Good>();
//
//        String key = buildKey(listId, ownerId);
//
//        List<Good> goodList = goodMap.get(key);
//        if (goodList != null) {
//            for (Good good : goodList) {
//                if (good.getListId() == listId && good.getSynchAction() != SynchAction.DELETE && good.getSynchAction() != SynchAction.INSERT) {
//
////                    switch ((int) goodResponse.getCategoryId()) {
////                        case 1:
//////                            goodResponse.setGoodCategoryImageId(R.drawable.good_category_icon_alco);
////                            break;
////                        case 2:
//////                            goodResponse.setGoodCategoryImageId(R.drawable.good_category_icon_bakaleya);
////                            break;
////                        case 3:
//////                            goodResponse.setGoodCategoryImageId(R.drawable.good_category_icon_drink);
////                            break;
////                        case 4:
////                            break;
////                        case 5:
////                            break;
////                        case 6:
////                            break;
////                        case 7:
////                            break;
////                    }
//
//                    goodResponseListExceptDeletedAndInsert.add(good);
//                }
//            }
//        } else {
//            //todo exception
//        }
//
//        Collections.sort(goodResponseListExceptDeletedAndInsert, new GoodComparator());
//        return goodResponseListExceptDeletedAndInsert;
//    }

    public Good insert(long listId, String name, double price, double quantity, int measure, boolean priority, String note, int categoryId,
                       long imageId, int state, long ownerId, long lastEditorId, String createTime, String modifiedTime, SynchAction synchAction) {
//        checkCache();

        long lastGoodId = addAndGetLastGoodId(listId, ownerId);

        Good good = goodDAO.addGood(lastGoodId, listId, name, price, quantity, measure, priority, note, categoryId,
                imageId, state, ownerId, lastEditorId, createTime, modifiedTime, synchAction);
        if (good != null) {
//            String key = buildKey(goodResponse.getListId(), goodResponse.getOwnerId());
//
//
//            List<Good> goodList = goodMap.get(key);
//            if (goodList == null) {
//                goodMap.put(key, goodList = new ArrayList<Good>());
//            }

//            if (good.getGoodCategory() == null && good.getCategoryId() != 0) {
//                GoodCategory goodCategory = Services.getCategoryService().getCategoryId(good.getCategoryId(), good.getOwnerId());
//                good.setGoodCategory(goodCategory);
//            }

//            goodList.add(good);
        }
        return good;
    }

    public Good insertShareGood(long id, long listId, String name, double price, double quantity, int measure, boolean priority, String note, int categoryId,
                                long imageId, int state, long ownerId, long lastEditorId, String createTime, String modifiedTime, SynchAction synchAction) {
//        checkCache();
        Good good = goodDAO.addGood(id, listId, name, price, quantity, measure, priority, note, categoryId,
                imageId, state, ownerId, lastEditorId, createTime, modifiedTime, synchAction);
        if (good != null) {
//            String key = buildKey(good.getListId(), good.getOwnerId());
//
//            List<Good> goodList = goodMap.get(key);
//
//            if (goodList == null) {
//                goodMap.put(key, goodList = new ArrayList<Good>());
//            }

//            if (good.getGoodCategory() == null && good.getCategoryId() != 0) {
//                GoodCategory goodCategory = Services.getCategoryService().getCategoryId(good.getCategoryId(), good.getOwnerId());
//                good.setGoodCategory(goodCategory);
//            }

//            goodList.add(good);
        }
        return good;
    }

    public void insert(long id, long listId, String name, double price, double quantity, int measure, boolean priority, String note, int categoryId,
                       long imageId, int state, long ownerId, long lastEditorId, String createTime, String modifiedTime, SynchAction synchAction) {
//        checkCache();
        Good good = goodDAO.addGood(id, listId, name, price, quantity, measure, priority, note, categoryId,
                imageId, state, ownerId, lastEditorId, createTime, modifiedTime, synchAction);
        if (good != null) {

//            String key = buildKey(good.getListId(), good.getOwnerId());
//
//            List<Good> goodList = goodMap.get(good.getListId());
//            if (goodList == null) {
//                goodMap.put(key, goodList = new ArrayList<Good>());
//            }

//            if (good.getGoodCategory() == null && good.getCategoryId() != 0) {
//                GoodCategory goodCategory = Services.getCategoryService().getCategoryId(good.getCategoryId(), good.getOwnerId());
//                good.setGoodCategory(goodCategory);
//            }

//            goodList.add(good);
        }
    }

    public boolean save(EnumSet<FieldGood> fieldGoods, Good good) {
        //todo share actions
//        checkCache();
        boolean ok = goodDAO.save(fieldGoods, good);
        if (ok) {
//            String key = buildKey(good.getListId(), good.getOwnerId());

//            List<Good> goodList = goodMap.get(key);
//            if (goodList != null) {
//                for (Good item : goodList) {
//                    if (item.getId() == good.getId()) {
//                        goodList.remove(item);
//                        break;
//                    }
//                }

//            if (good.getGoodCategory() == null && good.getCategoryId() != 0) {
//                GoodCategory goodCategory = Services.getCategoryService().getCategoryId(good.getCategoryId(), good.getOwnerId());
//                good.setGoodCategory(goodCategory);
//            }

//                goodList.add(good);
//            } else {
//                //todo exception
//            }
        }
        return ok;
    }

    public boolean remove(long id, long listId, long ownerId) {
//        checkCache();
        boolean ok = goodDAO.remove(id, listId, ownerId);
        if (ok) {
//            String key = buildKey(listId, ownerId);
//
//            List<Good> goodList = goodMap.get(key);
//
//            if (goodList != null) {
//                for (Good item : goodList) {
//                    if (item.getId() == id) {
//                        goodList.remove(item);
//                        break;
//                    }
//                }
//            } else {
//                //todo exception
//            }
        }
        return ok;
    }

    public boolean removeAllGoods(long listId, long ownerId) {
//        checkCache();
        boolean ok = goodDAO.removeAllGoods(listId, ownerId);
        if (ok) {
            goodLastId.put(listId, 0L);
//            goodMap.remove(listId);
        }
        return ok;
    }


    public void removeAll() {
//        checkCache();
        boolean ok = goodDAO.removeAll();
        if (ok) {
            //todo нужно ли обнулять ласт ид
//            goodMap.clear();
        }
    }

//    public List<Good> getAllGoodByListIdInserted(long listId, long ownerId) {
//        String key = buildKey(listId, ownerId);
//
//        List<Good> goodList = goodMap.get(key);
//        return goodList;
//    }

    public void updateId(long oldListId, long newListId, long ownerId) {
//        checkCache();
        boolean ok = goodDAO.updateId(oldListId, newListId, ownerId);
        if (ok) {
//            String key = buildKey(oldListId, ownerId);
//
//            List<Good> goodList = goodMap.get(key);
//            if (goodList != null) {
//                for (Good item : goodList) {
//                    if (item.getId() == oldListId) {
//                        item.setListId(newListId);
//                        break;
//                    }
//                }
//            }
        }
    }

    public void updateEmptyOwnerId(long ownerId) {
        goodDAO.updateEmptyOwnerId(ownerId);
//        goodMap = null;

    }

    private class GoodComparator implements Comparator<Good> {

        @Override
        public int compare(Good good1, Good good2) {
            if (app.isIgnoreBoughtGood()) {
                if ((State.valueOf(good1.getState()) == State.BOUGHT && State.valueOf(good2.getState()) == State.BOUGHT) || (State.valueOf(good1.getState()) == State.NORMAL && State.valueOf(good2.getState()) == State.NORMAL)) {
                    String sort = app.getGoodSort();

                    if (sort.equals("name")) {
                        if (app.isSortAsc()) {
                            return good1.getName().compareTo(good2.getName());
                        } else {
                            return good2.getName().compareTo(good1.getName());
                        }

                    } else if (sort.equals("price")) {
                        if (app.isSortAsc()) {
                            return good1.getPrice() > good2.getPrice() ? 1 : -1;
                        } else {
                            return good2.getPrice() > good1.getPrice() ? 1 : -1;
                        }
                    } else if (sort.equals("category")) {
                        if (app.isSortAsc()) {
                            return good1.getCategoryId() > good2.getCategoryId() ? 1 : -1;
                        } else {
                            return good2.getCategoryId() > good1.getCategoryId() ? 1 : -1;
                        }
                    } else if (sort.equals("priority")) {
                        return good1.isPriority() ? -1 : 1;
                    } else {
                        if (app.isSortAsc()) {
                            return good2.getCreateTime().compareTo(good1.getCreateTime());
                        } else {
                            return good1.getCreateTime().compareTo(good2.getCreateTime());
                        }
                    }
                } else if (State.valueOf(good2.getState()) == State.BOUGHT && State.valueOf(good1.getState()) == State.NORMAL) {
                    return -1;
                } else {
                    return 1;
                }
            } else {
                String sort = app.getGoodSort();

                if (sort.equals("name")) {
                    if (app.isSortAsc()) {
                        return good1.getName().compareTo(good2.getName());
                    } else {
                        return good2.getName().compareTo(good1.getName());
                    }
                } else if (sort.equals("price")) {
                    if (app.isSortAsc()) {
                        return good1.getPrice() > good2.getPrice() ? 1 : -1;
                    } else {
                        return good2.getPrice() > good1.getPrice() ? 1 : -1;
                    }
                } else if (sort.equals("category")) {
                    if (app.isSortAsc()) {
                        return good1.getCategoryId() > good2.getCategoryId() ? 1 : -1;
                    } else {
                        return good2.getCategoryId() > good1.getCategoryId() ? 1 : -1;
                    }
                } else if (sort.equals("priority")) {
                    return good1.isPriority() ? -1 : 1;
                } else {
                    if (app.isSortAsc()) {
                        return good2.getCreateTime().compareTo(good1.getCreateTime());
                    } else {
                        return good1.getCreateTime().compareTo(good2.getCreateTime());
                    }
                }
            }


        }
    }
}
