package com.wzy.lamanpro.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.wzy.lamanpro.bean.DaoMaster;
import com.wzy.lamanpro.bean.DaoSession;
import com.wzy.lamanpro.bean.GFs;
import com.wzy.lamanpro.bean.GFsDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class MainDaoUtils {
    private GFsDao gFsDao;
    private Context context;


    public MainDaoUtils(Context context) {
        this.context = context;
        SQLiteDatabase writableDatabase = DBManager.getInstance(context).getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(writableDatabase);
        DaoSession daoSession = daoMaster.newSession();
        gFsDao = daoSession.getGFsDao();
    }


    /**
     * 插入用户
     *
     * @param gFs 账户
     */
    public void insertProductList(GFs gFs) {
        if (gFs == null) {
            Toast.makeText(context, "信息有错，不能添加", Toast.LENGTH_SHORT).show();
            return;
        }
        gFsDao.insert(gFs);
    }

    /**
     * 插入用户集合
     *
     * @param gfs 账户集合
     */
    public void insertGFList(List<GFs> gfs) {
        if (gfs == null || gfs.isEmpty()) {
            return;
        }

        gFsDao.insertInTx(gfs);
    }

    /**
     * 更新账户
     *
     * @param gFs 用户账户
     */
    public boolean updateData(GFs gFs) {
        if (gFs == null) {
            return false;
        }
        gFsDao.update(gFs);
        return true;
    }


//    /**
//     * 查询用户密码
//     */
//    public String queryUserPass(String account) {
//
//        QueryBuilder<Users> qb = usersDao.queryBuilder();
//        qb.where(UsersDao.Properties.Account.eq(account));
//        if (qb.list() == null || qb.list().size() == 0)
//            return "";
//        return qb.list().get(0).getPassword();
//    }

//    /**
//     * 查询用户密码
//     */
//    public String queryUserName(String account) {
//
//        QueryBuilder<Users> qb = usersDao.queryBuilder();
//        qb.where(UsersDao.Properties.Account.eq(account));
//        if (qb.list() == null || qb.list().size() == 0)
//            return "";
//        return qb.list().get(0).getName();
//    }

//    /**
//     * 查询用户列表个数
//     */
//    public int queryAccountSize() {
//        QueryBuilder<Users> qb = usersDao.queryBuilder();
//        return qb.list().size();
//    }

    /**
     * 查询所有用户列表
     */
    public List<GFs> queryAllGFs() {
        QueryBuilder<GFs> qb = gFsDao.queryBuilder();
        return qb.list();
    }

    /**
     * 查询用户个数
     */
    public int queryGFsSize(String name) {
        QueryBuilder<GFs> qb = gFsDao.queryBuilder();
        qb.where(GFsDao.Properties.Name.eq(name));
        return qb.list().size();
    }

    /**
     * 查询数据
     */
    public GFs queryGF(Long id) {
        QueryBuilder<GFs> qb = gFsDao.queryBuilder();
        qb.where(GFsDao.Properties.Id.eq(id));
        return qb.list().get(0);
    }

    /**
     * 删除数据
     */
    public void deleteGF(GFs gFs) {
        gFsDao.delete(gFs);
    }

    /**
     * 删除全部数据
     */
    public void deleteAllGF() {
        gFsDao.deleteAll();
    }

    /**
     * 删除数据
     */
    public void deleteGF(Long id) {
        QueryBuilder<GFs> qb = gFsDao.queryBuilder();
        qb.where(GFsDao.Properties.Id.eq(id)).buildDelete().executeDeleteWithoutDetachingEntities();
    }
}