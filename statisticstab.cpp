#include "statisticstab.h"
#include "ui_statisticstab.h"

StatisticsTab::StatisticsTab(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::StatisticsTab)
{
    ui->setupUi(this);
}

StatisticsTab::~StatisticsTab()
{
    delete ui;
}
