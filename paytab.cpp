#include "paytab.h"
#include "ui_paytab.h"

PayTab::PayTab(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::PayTab)
{
    ui->setupUi(this);
}

PayTab::~PayTab()
{
    delete ui;
}
