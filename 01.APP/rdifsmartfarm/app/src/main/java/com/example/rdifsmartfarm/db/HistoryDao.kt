package com.example.rdifsmartfarm.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.rdifsmartfarm.entity.History
import com.example.rdifsmartfarm.entity.RFID
import com.example.rdifsmartfarm.utils.MToast
import com.example.rdifsmartfarm.utils.TimeCycle
import com.example.smartagriculturalgreenhouses.db.BaseDao

class HistoryDao(private val context: Context) : BaseDao {
    private var helper = DBOpenHelper(context)
    private lateinit var db: SQLiteDatabase
    private val TAG = "HistoryDao"
    override fun insert(bean: Any): Int {
        return try {
            val rf: History = bean as History
            db = helper.writableDatabase
            val values = ContentValues()
            values.put("fid", rf.fid)
            values.put("temp", rf.temp)
            values.put("heart", rf.heart)
            values.put("humi", rf.humi)
            values.put("temp_v", rf.temp_v)
            values.put("createDateTime", TimeCycle.getDateTime())
            db.insert("history", null, values)
            db.close()
            1
        } catch (e: Exception) {
            e.printStackTrace()
            MToast.mToast(context, "添加失败")
            -1
        }
    }

    override fun update(bean: Any, vararg data: String): Int {
        return try {
            db = helper.writableDatabase
            val values = ContentValues()
            val rf = bean as RFID
            values.put("fid", rf.fid)
            values.put("temp", rf.temp)
            values.put("heart", rf.heart)
            values.put("humi", rf.humi)
            values.put("temp_v", rf.temp_v)
            db.update("history", values, "hid=?", arrayOf(data[0]))
            db.close()
            1
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            MToast.mToast(context, "修改失败")
            -1
        }
    }

    override fun query(vararg data: String): MutableList<Any>? {
        return try {
            val result: MutableList<Any> = ArrayList()
            val cursor: Cursor
            val sql: String
            db = helper.readableDatabase
            when (data.size) {
                1 -> {
                    sql = "SELECT * from history where fid LIKE ? ORDER BY createDateTime DESC"
                    cursor = db.rawQuery(sql, arrayOf("%${data[0]}%"))
                }

                else -> {
                    sql = "select * from history ORDER BY createDateTime DESC"
                    cursor = db.rawQuery(sql, null)
                }
            }
            while (cursor.moveToNext()) {
                val user = RFID(
                    cursor.getInt(cursor.getColumnIndexOrThrow("hid")),
                    cursor.getString(cursor.getColumnIndexOrThrow("fid")),
                    cursor.getFloat(cursor.getColumnIndexOrThrow("temp_v")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("heart")),
                    cursor.getFloat(cursor.getColumnIndexOrThrow("temp")),
                    cursor.getFloat(cursor.getColumnIndexOrThrow("humi")),
                    cursor.getString(cursor.getColumnIndexOrThrow("createDateTime"))
                )
                result.add(user)
            }
            cursor.close()
            db.close()
            result
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.e("查询", e.toString())
            MToast.mToast(context, "查询失败")
            null
        }
    }

    override fun delete(vararg data: String): Int {
        return try {
            if (data.isEmpty()) {
                return 0
            }
            db = helper.writableDatabase
            if (data.size == 1) {
                db.delete("history", "rid=?", arrayOf(data[0]))
            } else {
                db.delete("history", "fid=?", arrayOf(data[0]))
            }
            db.close()
            1
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            MToast.mToast(context, "删除失败")
            -1
        }
    }
}