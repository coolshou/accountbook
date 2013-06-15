#include "strings.h"

#include <QString>

namespace AccountBook
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

    const QString welcomeMessageTitle()
    {
        return "歡迎使用累死雞記帳";
    }

    const QString welcomeMessageContent()
    {
        return "廢話不多說，使用記帳前請先指定資料庫存放的位置\n\n"
                "P.S. 建議時常要備份喔~";
    }
}
