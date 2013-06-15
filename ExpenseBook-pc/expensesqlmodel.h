#ifndef EXPENSESQLMODEL_H
#define EXPENSESQLMODEL_H

#include <QSqlQueryModel>

class ExpenseSqlModel : public QSqlQueryModel
{
    Q_OBJECT
public:
    explicit ExpenseSqlModel(QObject * parent = 0);
    virtual QVariant data ( const QModelIndex & index, int role = Qt::DisplayRole ) const;

};

#endif // EXPENSESQLMODEL_H
