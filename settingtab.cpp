#include "settingtab.h"
#include "ui_settingtab.h"

SettingTab::SettingTab(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::SettingTab)
{
    ui->setupUi(this);
}

SettingTab::~SettingTab()
{
    delete ui;
}
