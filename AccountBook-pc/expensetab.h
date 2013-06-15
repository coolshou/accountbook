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

protected:
    void keyPressEvent(QKeyEvent *keyEvent);

private:
    Ui::ExpenseTab *_ui;

    bool _editMode;
    QPixmap _currentPicture;

    void _clearExpense();

    void _onCapturePushButtonClicked();
    void _onExpensePushButtonClicked();
    void _onDeletePushButtomClicked();

    void _onExpenseTableViewClicked();
};

#endif // EXPENSETAB_H
