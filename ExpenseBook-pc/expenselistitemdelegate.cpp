#include "expenselistitemdelegate.h"
#include "globals.h"

#include <QPainter>
#include <QDebug>

ExpenseListItemDelegate::ExpenseListItemDelegate(QObject *parent)
    :QStyledItemDelegate (parent)
{
}

QSize ExpenseListItemDelegate::sizeHint(const QStyleOptionViewItem &option, const QModelIndex &index) const
{
    return QSize(200, 58);
}

void ExpenseListItemDelegate::paint(QPainter *painter, const QStyleOptionViewItem &option, const QModelIndex &index) const
{
    QStyleOptionViewItemV4 opt = option;
    initStyleOption(&opt, index);

    QPixmap picture = index.data(ExpenseBook::PictureRole).value<QPixmap>();
    QString spend = index.data(ExpenseBook::SpendRole).toString();
    QString dateString = index.data(ExpenseBook::DateRole).toString();
    QString category = index.data(ExpenseBook::CategoryRole).toString();

    painter->save();
    QRect rect = option.rect;

    QPen pen;

    pen.setWidth(1);
    pen.setColor(ExpenseBook::backgroundColor());
    painter->setPen(pen);

    if(option.state & QStyle::State_Selected)
        painter->fillRect(rect, ExpenseBook::mainColor());

    painter->drawRect(rect);

    QRect pictureRect(rect.x() + 5, rect.y() + 5, 64, 48); //4:3
    painter->drawPixmap(pictureRect, picture);


    pen.setColor(ExpenseBook::fontColor());
    painter->setPen(pen);

    QFont font;
    font.setPixelSize(20);
    painter->setFont(font);
    painter->drawText(rect.x() + 74, rect.y() + 25, QString("金額：%1 元").arg(spend));

    font.setPixelSize(15);
    painter->setFont(font);
    painter->drawText(rect.x() + 74, rect.y() + 50, dateString);

    font.setPixelSize(15);
    painter->setFont(font);
    painter->drawText(rect.x() + 160, rect.y() + 50, category);

    painter->restore();
}
