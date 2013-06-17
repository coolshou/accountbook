#include "mainwindow.h"
#include "ui_mainwindow.h"
#include "globals.h"
#include "expensesqlmodel.h"
#include "expenselistitemdelegate.h"

#include <QKeyEvent>

#include <QSettings>
#include <QDir>
#include <QMessageBox>
#include <QFileDialog>
#include <QRect>

//SQL
#include <QtSQl>

//工具
#include <QIntValidator>
#include <QTimer>
#include <QDebug>

MainWindow::MainWindow(QWidget *parent)
    :QWidget(parent), _ui(new Ui::MainWindow), _currentMode(NormalMode)
{
    _ui->setupUi(this);
    _ui->expenseWidget->hide();

    QIntValidator *validator = new QIntValidator(this);
    validator->setBottom(0);
    _ui->spendLineEdit->setValidator(validator);
    _ui->dateDateEdit->setDisplayFormat(ExpenseBook::dateFormat());
    _ui->dateDateEdit->setCalendarPopup(true);

    connect(_ui->addExpensePushButton, SIGNAL(clicked()), this, SLOT(_onAddExpenseButtonClicked()));
    connect(_ui->expenseListView, SIGNAL(clicked(QModelIndex)), this, SLOT(_onExpenseListItemSelected(QModelIndex)));
    connect(_ui->changeDatabasePathPushButton, SIGNAL(clicked()), this, SLOT(_onChangeDatabasePathButtonClicked()));
    connect(_ui->savePushButton, SIGNAL(clicked()), this, SLOT(_onSaveButtonClicked()));
    connect(_ui->deletePushButton, SIGNAL(clicked()), SLOT(_onDeleteButtonClicked()));
}

MainWindow::~MainWindow()
{
    delete _ui;
}

void MainWindow::showEvent(QShowEvent *)
{
    QTimer::singleShot(1000, this, SLOT(_prepareDatabase()));
}

void MainWindow::keyPressEvent(QKeyEvent *keyEvent)
{
    switch(keyEvent->key())
    {
    case Qt::Key_Escape: _returnNormalModel(); break;
    }
}

void MainWindow::_prepareDatabase()
{
    QString databasePath = _getDatabasePathFromSettings();
    if(databasePath.isEmpty())
    {
        databasePath = _getDatabasePathFromFileDialog();
        while(databasePath.isEmpty())
        {
            QMessageBox::about(NULL, "注意！", "請指定資料庫的位置和名稱喔");
            databasePath = _getDatabasePathFromFileDialog();
        }

        //設定資料庫位置
        QSettings settings;
        settings.setValue(ExpenseBook::databasePathSetting(), databasePath);
    }

    _ui->databasePathLabel->setText(databasePath);

    QSqlDatabase database = QSqlDatabase::addDatabase("QSQLITE");
    database.setDatabaseName(databasePath);
    if(!database.open())
        qDebug() << database.lastError();

    //其實應該要判斷有那些 table
    if(database.tables().isEmpty())
        _createDatabaseTables();

    _setExpenseListView();
}

void MainWindow::_onAddExpenseButtonClicked()
{
    _ui->pictureLabel->setOriginPixmap(QPixmap(":/images/empty_picture.jpg"));
    _ui->spendLineEdit->clear();
    _ui->dateDateEdit->setDate(QDate::currentDate());
    _ui->categoryComboBox->setModel(_getCategorySqlModel());
    _ui->noteTextEdit->clear();

    _ui->deletePushButton->setDisabled(true);

    _currentMode = AddExpenseMode;
    _showExpenseWidget();
}

void MainWindow::_onExpenseListItemSelected(QModelIndex index)
{
    ExpenseSqlModel *expenseSqlModel = qobject_cast<ExpenseSqlModel*>(_ui->expenseListView->model());

    _currentExpenseId = expenseSqlModel->data(index, ExpenseBook::IdRole).toLongLong();

    QPixmap picture = expenseSqlModel->data(index, ExpenseBook::PictureRole).value<QPixmap>();
    qlonglong spend = expenseSqlModel->data(index, ExpenseBook::SpendRole).toLongLong();

    QString dateString = expenseSqlModel->data(index, ExpenseBook::DateRole).toString();
    QString category = expenseSqlModel->data(index, ExpenseBook::CategoryRole).toString();
    QString note = expenseSqlModel->data(index, ExpenseBook::NoteRole).toString();

    _ui->pictureLabel->setOriginPixmap(picture);
    _ui->spendLineEdit->setText(QString::number(spend));
    _ui->dateDateEdit->setDate(QDate::fromString(dateString, ExpenseBook::dateFormat()));

    QSqlQueryModel *categorySqlModel = _getCategorySqlModel();
    _ui->categoryComboBox->setModel(categorySqlModel);

    for(int i=0; categorySqlModel->rowCount(); i++)
    {
        QModelIndex currentIndex = categorySqlModel->index(i, 0);
        if(categorySqlModel->data(currentIndex).toString() == category)
        {
            _ui->categoryComboBox->setCurrentIndex(i);
            break;
        }
    }

    _ui->noteTextEdit->setText(note);

    _ui->deletePushButton->setDisabled(false);

    _showExpenseWidget();
    _currentMode = EditExpenseMode;
}

void MainWindow::_onChangeDatabasePathButtonClicked()
{
    QString databasePath = _getDatabasePathFromFileDialog();
    if(databasePath.isEmpty())
        return;

    QSqlDatabase database = QSqlDatabase::database();
    database.setDatabaseName(databasePath);
    if(!database.open())
        qDebug() << database.lastError();

    //其實應該要判斷有那些 table
    if(database.tables().isEmpty())
        _createDatabaseTables();

    _setExpenseListView();
}

void MainWindow::_onSaveButtonClicked()
{
    if(_currentMode == NormalMode)
        qDebug() << "不可能是 NormalMode";

    QByteArray pictureBytes;
    QBuffer buffer(&pictureBytes);
    buffer.open(QIODevice::WriteOnly);
    _ui->pictureLabel->getOriginPixmap().save(&buffer, "JPG");

    qlonglong spend = _ui->spendLineEdit->text().toLongLong();
    QString dateString = _ui->dateDateEdit->text().trimmed();

    ExpenseSqlModel expenseSqlModel(this);
    qlonglong categoryId = expenseSqlModel.getCategoryIdfromCategory(_ui->categoryComboBox->currentText());
    if(categoryId == -1)
        qDebug() << "沒有這個分類" << _ui->categoryComboBox->currentText();

    QString note = _ui->noteTextEdit->toPlainText();
    QSqlQuery query;

    if(_currentMode == AddExpenseMode)
    {
        query.prepare(ExpenseBook::insertExpenseString());
    }
    else if(_currentMode == EditExpenseMode)
    {
        query.prepare(ExpenseBook::editExpenseString());
        query.bindValue(":_id", _currentExpenseId);
    }
    else
    {
        qDebug() << "沒有這個 currentModel" << _currentMode;
        return;
    }

    query.bindValue(":picture_bytes", pictureBytes);
    query.bindValue(":spend", spend);
    query.bindValue(":date_string", dateString);
    query.bindValue(":category_id", categoryId);
    query.bindValue(":note", note);

    if(!query.exec())
        qDebug() << query.lastError();

    _setExpenseListView();

    _hideExpenseWidget();
}

void MainWindow::_onDeleteButtonClicked()
{
    QSqlQuery query(ExpenseBook::deleteExpenseString());
    query.addBindValue(_currentExpenseId);

    if(!query.exec())
        qDebug() << query.lastError();

    _setExpenseListView();

    _hideExpenseWidget();
}

void MainWindow::_hideExpenseWidget()
{
    _ui->expenseWidget->hide();
}

void MainWindow::_showExpenseWidget()
{
    _ui->expenseWidget->show();

    /*
    QRect expenseWidgetRect = _ui->expenseWidget->geometry();
    QRect expenseListViewRect = _ui->expenseListView->geometry();

    QPropertyAnimation *expenseWidgetAnimation = new QPropertyAnimation(_ui->expenseWidget, "geometry");
    expenseWidgetAnimation->setDuration(300);
    expenseWidgetAnimation->setStartValue(QRect(QPoint(expenseListViewRect.topRight()), QSize(0, expenseWidgetRect.height())));
    expenseWidgetAnimation->setEndValue(expenseWidgetRect);

    QPropertyAnimation *expenseListViewAnimation = new QPropertyAnimation(_ui->expenseListView, "geometry");
    expenseListViewAnimation->setDuration(300);
    expenseListViewAnimation->setStartValue(expenseListViewRect);
    expenseListViewAnimation->setEndValue(QRect(expenseListViewRect.topLeft(), QSize(350, expenseListViewRect.height())));

    //expenseWidgetAnimation->start();
    expenseListViewAnimation->start();*/
}

QString MainWindow::_getDatabasePathFromSettings() const
{
    QSettings settings;
    if(settings.contains(ExpenseBook::databasePathSetting()))
        return settings.value(ExpenseBook::databasePathSetting()).toString();
    return QString();
}

QString MainWindow::_getDatabasePathFromFileDialog() const
{
    QFileDialog dialog;
    dialog.setAcceptMode(QFileDialog::AcceptSave);
    dialog.setFileMode(QFileDialog::AnyFile);
    dialog.setOption(QFileDialog::DontConfirmOverwrite);

    dialog.selectFile("accountbook.db");
    dialog.setWindowTitle("選擇資料庫的位置和名稱");

    QString databasePath;
    if(dialog.exec())
        databasePath = dialog.selectedFiles()[0];
    return databasePath;
}

void MainWindow::_returnNormalModel()
{
    _hideExpenseWidget();
    _ui->expenseListView->clearSelection();

    _currentMode = NormalMode;
}

void MainWindow::_createDatabaseTables()
{
    QSqlQuery query;
    if(!query.exec(ExpenseBook::createExpenseTableString()))
        qDebug() << query.lastError();

    if(!query.exec(ExpenseBook::createCategoryTableString()))
        qDebug() << query.lastError();

    //設定 default category
    if(!query.exec("insert into categories(category) values('未分類');"))
        qDebug() << query.lastError();
    if(!query.exec("insert into categories(category) values('飲食');"))
        qDebug() << query.lastError();
    if(!query.exec("insert into categories(category) values('衣物');"))
        qDebug() << query.lastError();
    if(!query.exec("insert into categories(category) values('住宿');"))
        qDebug() << query.lastError();
    if(!query.exec("insert into categories(category) values('行動');"))
        qDebug() << query.lastError();
    if(!query.exec("insert into categories(category) values('教育');"))
        qDebug() << query.lastError();
    if(!query.exec("insert into categories(category) values('娛樂');"))
        qDebug() << query.lastError();
}

void MainWindow::_setExpenseListView()
{
    ExpenseSqlModel *expenseModel = new ExpenseSqlModel(this);
    _ui->expenseListView->setModel(expenseModel);
    _ui->expenseListView->setItemDelegate(new ExpenseListItemDelegate(this));
}

QSqlQueryModel *MainWindow::_getCategorySqlModel()
{
    QSqlQueryModel *categorySqlModel = new QSqlQueryModel(this);
    categorySqlModel->setQuery("SELECT category FROM categories");
    return categorySqlModel;
}
