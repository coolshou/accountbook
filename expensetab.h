#ifndef EXPENSETAB_H
#define EXPENSETAB_H

#include <QWidget>

class QSqlQueryModel;

namespace Ui
{
    class ExpenseTab;
}

class ExpenseTab : public QWidget
{
    Q_OBJECT
    
public:
    explicit ExpenseTab(QWidget *parent = 0);

    void setCategoryModel(QSqlQueryModel *categoryModel);

    ~ExpenseTab();
    
private:
    Ui::ExpenseTab *_ui;
};

#endif // EXPENSETAB_H
