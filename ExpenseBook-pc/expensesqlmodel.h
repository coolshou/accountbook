#ifndef EXPENSESQLMODEL_H
#define EXPENSESQLMODEL_H

#include <QSqlQueryModel>
#include <QHash>

class ExpenseSqlModel : public QSqlQueryModel
{
    Q_OBJECT
public:
    explicit ExpenseSqlModel(QObject * parent = 0);

    virtual QVariant data ( const QModelIndex & index, int role = Qt::DisplayRole ) const;

    qlonglong getCategoryIdfromCategory(QString category) const;

private:

    QHash<qlonglong, QString> _categories;
};

#endif // EXPENSESQLMODEL_H
