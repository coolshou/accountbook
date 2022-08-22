#include "expensemodel.h"

#include <QPixmap>

ExpenseModel::ExpenseModel(QObject *parent, QSqlDatabase db)
    :QSqlRelationalTableModel(parent, db)
{
}

QVariant ExpenseModel::data(const QModelIndex &index, int role) const
{
    if(!index.isValid())
        return QVariant();

    if(index.row() >= rowCount() || index.column() >= columnCount())
        return QVariant();

    if(index.column() == 1)
    {
        QVariant bytes = QSqlRelationalTableModel::data(index, Qt::DisplayRole);
        QPixmap picture;
        picture.loadFromData(bytes.toByteArray(), "JPG");

        if(role == Qt::DecorationRole)
            return QVariant(picture);
        else if(role == Qt::SizeHintRole)
            return QSize(25, 15);
    }
    return QSqlRelationalTableModel::data(index, role);
}

