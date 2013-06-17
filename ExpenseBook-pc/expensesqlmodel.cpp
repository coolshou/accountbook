#include "expensesqlmodel.h"
#include "globals.h"

#include <QSqlQuery>
#include <QPixmap>
#include <QDebug>

ExpenseSqlModel::ExpenseSqlModel(QObject *parent)
    :QSqlQueryModel(parent)
{
    setQuery("SELECT _id, picture_bytes, spend, date_string, category_id, note FROM expenses ORDER BY date_string DESC, _id DESC");


    QSqlQuery query;
    query.exec("SELECT _id, category FROM categories");
    while(query.next())
    {
        qlonglong id = query.value("_id").toLongLong();
        _categories[id] = query.value("category").toString();
    }
}

QVariant ExpenseSqlModel::data(const QModelIndex &index, int role) const
{
    if(!index.isValid())
        return QVariant();

    if(index.row() >= rowCount() || index.column() >= columnCount())
        return QVariant();

    //Picture
    QVariant id = QSqlQueryModel::data(ExpenseSqlModel::index(index.row(), 0), Qt::DisplayRole);
    QVariant bytes = QSqlQueryModel::data(ExpenseSqlModel::index(index.row(), 1), Qt::DisplayRole);
    QPixmap picture;
    picture.loadFromData(bytes.toByteArray(), ExpenseBook::pictureFormat());

    QVariant spend = QSqlQueryModel::data(ExpenseSqlModel::index(index.row(), 2), Qt::DisplayRole);
    QVariant dateString = QSqlQueryModel::data(ExpenseSqlModel::index(index.row(), 3), Qt::DisplayRole);
    QVariant category = _categories[QSqlQueryModel::data(ExpenseSqlModel::index(index.row(), 4), Qt::DisplayRole).toLongLong()];
    QVariant note = QSqlQueryModel::data(ExpenseSqlModel::index(index.row(), 5), Qt::DisplayRole);

    switch(role)
    {
    case ExpenseBook::IdRole: return id;
    case ExpenseBook::PictureRole: return QVariant(picture);
    case ExpenseBook::SpendRole: return spend;
    case ExpenseBook::DateRole: return dateString;
    case ExpenseBook::CategoryRole: return category;
    case ExpenseBook::NoteRole: return note;
    default: return QSqlQueryModel::data(index, role);
    }
}

qlonglong ExpenseSqlModel::getCategoryIdfromCategory(QString category) const
{
    foreach(qlonglong categoryId, _categories.keys())
    {
        if(_categories[categoryId] == category)
            return categoryId;
    }
    return -1;
}


