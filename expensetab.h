#ifndef EXPENSETAB_H
#define EXPENSETAB_H

#include <QWidget>

namespace Ui {
class ExpenseTab;
}

class ExpenseTab : public QWidget
{
    Q_OBJECT
    
public:
    explicit ExpenseTab(QWidget *parent = 0);
    ~ExpenseTab();
    
private:
    Ui::ExpenseTab *ui;
};

#endif // EXPENSETAB_H
