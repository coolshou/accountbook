#ifndef EXPENSEMODEL_H
#define EXPENSEMODEL_H

#include <QSqlRelationalTableModel>

class ExpenseModel: public QSqlRelationalTableModel
{
    Q_OBJECT

public:
    
    ExpenseModel(QObject * parent = 0, QSqlDatabase db = QSqlDatabase());

    virtual QVariant data ( const QModelIndex & index, int role = Qt::DisplayRole ) const;

};

#endif // EXPENSEMODEL_H
