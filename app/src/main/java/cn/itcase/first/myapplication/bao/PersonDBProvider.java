package cn.itcase.first.myapplication.bao;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2016/10/8.
 */
public class PersonDBProvider extends ContentProvider {

    //定义一个uri的匹配器，用于匹配uri，如果路径不满足条件，返回-1
    private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int INSERT = 1;        //添加数据匹配uri路径成功时返回1
    private static final int DELETE = 2;        //删除数据匹配uri路劲成功时返回2；
    private static final int UPDATE = 3;        //更改数据匹配uri路径成功时返回3
    private static final int QUERY =4;          //查询数据陪陪uri路径成功是返回4；
    private static final int QUERYONE =5;       //查询一条数据匹配uri路径成功时返回5；
    //数据库操作类的对象
    private  PersonSQLiteOpenHelper helper;
    static {
        //添加一组匹配规则
        matcher.addURI("cn.itcase.first.myapplication.bao","insert",INSERT);
        matcher.addURI("cn.itcase.first.myapplication.bao","delete",DELETE);
        matcher.addURI("cn.itcase.first.myapplication.bao","update",UPDATE);
        matcher.addURI("cn.itcase.first.myapplication.bao","query",QUERY);
        //这里的“#”号为通配符是符合“query/”皆返回queryone的返回码
        matcher.addURI("cn.itcase.first.myapplication.bao","query/#",QUERYONE);
    }
    //当内容提供者被创建的时候调用适合数据的初始化
    @Override
    public boolean onCreate() {
        helper = new PersonSQLiteOpenHelper(getContext());
        return false;
    }
    //查询数据操作
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //匹配查询的uri数据
        if(matcher.match(uri)==QUERY){
            //匹配成功，返回查询的结果集
            SQLiteDatabase db = helper.getReadableDatabase();
            //调用数据库操作的查询数据的方法
            Cursor cursor = db.query("person",projection,selection,selectionArgs,null,null,sortOrder);
            return cursor;
        }else if(matcher.match(uri)==QUERYONE){
            //匹配成功，根据id查询数据
            long id = ContentUris.parseId(uri);
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query("person",projection,"id=?",new String[]{id+""},null,null,sortOrder);
            return  cursor;
        }else{
            throw new IllegalArgumentException("路径不匹配，不能执行查询操作");
        }
    }

    //获取当前uri的数据类型
    public String getType(Uri uri) {
        if(matcher.match(uri)==QUERY){
            //返回查询的结果集
            return "vnd.android.cursor.dir/person";
        }else if(matcher.match(uri)==QUERYONE){
            return "vnd.android.cursor.item/person";
        }
        return  null;
    }

    //添加数据
    public Uri insert(Uri uri, ContentValues values) {
        if(matcher.match(uri)==INSERT){
            //匹配成功返回查询的结果集
            SQLiteDatabase db = helper.getWritableDatabase();
            db.insert("person",null,values);
        }else{
            throw new IllegalArgumentException("路径不匹配，不能执行插入操作");
        }
        return null;
    }
    //删除数据
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if(matcher.match(uri)==DELETE){
            //匹配成功返回查询的结果集
            SQLiteDatabase db = helper.getWritableDatabase();
            db.delete("person",selection,selectionArgs);
        }else{
            throw new IllegalArgumentException("路径不匹配，不能执行删除操作");
        }

        return 0;
    }
    //更新数据
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if(matcher.match(uri)==UPDATE){
            //匹配成功 返回查询的结果集
            SQLiteDatabase db = helper.getWritableDatabase();
            db.update("person",values,selection,selectionArgs);
        }else{
            throw new IllegalArgumentException("路径不匹配，不能执行修改操作");
        }
        return 0;
    }
}
