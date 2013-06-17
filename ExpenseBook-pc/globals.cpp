#include "globals.h"

#include <QString>
#include <QSize>
#include <QColor>

namespace ExpenseBook
{
    const QString organization()
    {
        return "兩大類 x 兩大類 = 四大類";
    }

    const QString organizationDomain()
    {
        return "marco79423.lazchi.nctucs.net";
    }

    const QString application()
    {
        return "累死雞記帳";
    }

    const QString databasePathSetting()
    {
        return "databasePath";
    }

    const QString createExpenseTableString()
    {
        return "CREATE TABLE IF NOT EXISTS expenses( "
        "   _id INTEGER NOT NULL PRIMARY KEY, "
        "   picture_bytes BLOB, "
        "   spend NUMERIC, "
        "   data_string DATE, "
        "   category_id NUMERIC, "
        "   note TEXT"
        ");";
    }

    const QString createCategoryTableString()
    {
        return "CREATE TABLE IF NOT EXISTS categories( "
        "   _id INTEGER NOT NULL PRIMARY KEY, "
        "   category TEXT, "
        "   order_id NUMERIC "
                ");";
    }

    const char* pictureFormat()
    {
        return "JPG";
    }

    const QSize pictureSizeInExpenseListView()
    {
        return QSize(100, 60);
    }

    const QColor mainColor()
    {
        return QColor(56, 140, 168);
    }

    const QColor backgroundColor()
    {
        return QColor(200, 200, 200);
    }

    const QColor fontColor()
    {
        return QColor(85, 85, 85);
    }

    const QColor fontWhiteColor()
    {
        return QColor(255, 255, 255);
    }

    const QString dateFormat()
    {
        return "yyyy/MM/dd";
    }

    const QString insertExpenseString()
    {
        return "INSERT INTO expenses(picture_bytes, spend, date_string, category_id, note) VALUES (:picture_bytes, :spend, :date_string, :category_id, :note);";
    }

    const QString editExpenseString()
    {
        return "UPDATE expenses SET picture_bytes=:picture_bytes, spend=:spend, date_string=:date_string, category_id=:category_id, note=:note WHERE _id=:_id;";
    }

    const QString deleteExpenseString()
    {
        //_id 可能有 bug ，只好用 ? 代替
        return "DELETE FROM expenses WHERE _id=?;";
    }

}
