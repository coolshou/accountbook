#include "settingtab.h"
#include "ui_settingtab.h"

#include <QFileDialog>

SettingTab::SettingTab(QWidget *parent) :
    QWidget(parent),
    _ui(new Ui::SettingTab)
{
    _ui->setupUi(this);

    connect(_ui->changeDatabasePushButton, SIGNAL(clicked()), SLOT(onChangeDatabasePushButtonClicked()));
}

SettingTab::~SettingTab()
{
    delete _ui;
}

void SettingTab::onChangeDatabasePushButtonClicked()
{
    QString databasePath =
            QFileDialog::getSaveFileName(this, "選擇資料庫的位置和名稱", QDir::homePath() + "/accountbook.db");
    if(!databasePath.isEmpty())
        _ui->databasePathLineEdit->setText(databasePath);
}
