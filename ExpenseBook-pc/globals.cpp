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

    const QString createExpenseTable()
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

    const QString createCategoryTable()
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

}
