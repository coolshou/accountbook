#ifndef EXPENSELISTITEMDELEGATE_H
#define EXPENSELISTITEMDELEGATE_H

#include <QStyledItemDelegate>

class ExpenseListItemDelegate : public QStyledItemDelegate 
{
    Q_OBJECT

public:

    explicit ExpenseListItemDelegate(QObject *parent = 0);

    QSize sizeHint(const QStyleOptionViewItem &option, const QModelIndex &index) const;
    void paint(QPainter *painter, const QStyleOptionViewItem &option, const QModelIndex &index) const;
};

#endif // EXPENSELISTITEMDELEGATE_H
