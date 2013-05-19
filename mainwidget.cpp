#include "mainwidget.h"
#include "ui_mainwidget.h"
#include "globals.h"
#include "strings.h"
#include "core.h"
#include "settingtab.h"

#include <QSettings>
#include <QMessageBox>

MainWidget::MainWidget(QWidget *parent)
    :QWidget(parent), _ui(new Ui::MainWidget), _core(new Core(parent))
{
    _ui->setupUi(this);

    connect(_core, &Core::databasePathChanged, _ui->settingTab, &SettingTab::setDatabasePath);
    connect(_ui->settingTab, &SettingTab::databasePathChanged, _core, &Core::setDatabasePath);

    _core->loadSettings();

    //檢查資料庫路徑
    if(_core->getDatabasePath() == "")
    {
        QMessageBox::about(NULL, AccountBook::welcomeMessageTitle(), AccountBook::welcomeMessageContent());
        _ui->tabWidget->setCurrentIndex(AccountBook::SETTING_TAB);
        _ui->settingTab->onChangeDatabasePushButtonClicked();
    }

    //讀取資料庫
    _core->loadDatabase();

}

MainWidget::~MainWidget()
{
    delete _ui;
}
