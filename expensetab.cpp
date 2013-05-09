#include "expensetab.h"
#include "ui_expensetab.h"

ExpenseTab::ExpenseTab(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::ExpenseTab)
{
    ui->setupUi(this);
}

ExpenseTab::~ExpenseTab()
{
    delete ui;
}
