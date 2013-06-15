#include "expensesqlmodel.h"
#include "globals.h"

#include <QPixmap>

ExpenseSqlModel::ExpenseSqlModel(QObject *parent)
    :QSqlQueryModel(parent)
{
}

QVariant ExpenseSqlModel::data(const QModelIndex &index, int role) const
{
    if(!index.isValid())
        return QVariant();

    if(index.row() >= rowCount() || index.column() >= columnCount())
        return QVariant();

    QVariant bytes = QSqlQueryModel::data(ExpenseSqlModel::index(index.row(), 1), Qt::DisplayRole);
    QPixmap picture;
    picture.loadFromData(bytes.toByteArray(), ExpenseBook::pictureFormat());

    if(role == Qt::DecorationRole)
        return QVariant(picture);
    else if(role == Qt::SizeHintRole)
        return ExpenseBook::pictureSizeInExpenseListView();
    else
        return QSqlQueryModel::data(index, role);
}


