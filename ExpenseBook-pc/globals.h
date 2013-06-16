#ifndef GLOBALS_H
#define GLOBALS_H

class QString;
class QSize;
class QColor;

namespace ExpenseBook
{
    //應用程式
    const QString organization();
    const QString organizationDomain();
    const QString application();

    //Setting
    const QString databasePathSetting();

    //Database
    const QString createExpenseTable();
    const QString createCategoryTable();

    //const QString expenseTableFields();
    //const QString categoryTableFields();

    //Expense 時間格式
    const QString dateFormat();

    //ExpenseList
    const char* pictureFormat();
    const QSize pictureSizeInExpenseListView();

    //Role
    enum Role { IdRole=9, PictureRole, SpendRole, DateRole, CategoryRole, NoteRole };

    //Color
    const QColor mainColor();
    const QColor backgroundColor();
    const QColor fontColor();
    const QColor fontWhiteColor();
}


#endif // GLOBALS_H
