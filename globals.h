#ifndef GLOBALS_H
#define GLOBALS_H

namespace AccountBook
{
    const int EXPENSE_TAB = 0;
    //const int STATISTICS_TAB = 1;
    const int SETTING_TAB = 1;
    //const int SETTING_TAB = 2;

    const char SETTINGS_DATABASE_PATH[] = "databasePath";
    
    const char CREATE_EXPENSES_TABLE[] =
        "CREATE TABLE IF NOT EXISTS expenses( "
        "   _id INTEGER NOT NULL PRIMARY KEY, "
        "   picture BLOB, "
        "   spend NUMERIC, "
        "   data DATE, "
        "   category_id NUMERIC, "
        "   note TEXT"
        ");";

    const char CREATE_CATEGORYIES_TABLE[] =
        "CREATE TABLE IF NOT EXISTS categories( "
        "   _id INTEGER NOT NULL PRIMARY KEY, "
        "   category TEXT, "
        "   order_id NUMERIC "
        ");";

    const char CREATE_INFO_TABLE[] =
        "CREATE TABLE IF NOT EXISTS info( "
        "   _id INTEGER NOT NULL PRIMARY KEY, "
        "    app_version TEXT "
        ");";
}


#endif // GLOBALS_H
