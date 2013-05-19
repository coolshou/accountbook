#include "expensetab.h"
#include "ui_expensetab.h"

#include <QDebug>
#include <QSqlQueryModel>

ExpenseTab::ExpenseTab(QWidget *parent)
    :QWidget(parent), _ui(new Ui::ExpenseTab)
{
    _ui->setupUi(this);
}

void ExpenseTab::setCategoryModel(QSqlQueryModel *categoryModel)
{
    _ui->categoryListView->setModel(categoryModel);
}

ExpenseTab::~ExpenseTab()
{
    delete _ui;
}
