#ifndef EXPENSETAB_H
#define EXPENSETAB_H

#include <QWidget>

class QSqlQueryModel;
class QSqlRelationalTableModel;

namespace Ui
{
    class ExpenseTab;
}

class ExpenseTab : public QWidget
{
    Q_OBJECT
    
public:
    explicit ExpenseTab(QWidget *parent = 0);

    void loadData();

    ~ExpenseTab();
    
private:
    Ui::ExpenseTab *_ui;

    QPixmap _currentPicture;

    void _onCapturePushButtonClicked();
    void _onExpensePushButtonClicked();
    void _onDeletePushButtomClicked();
};

#endif // EXPENSETAB_H
