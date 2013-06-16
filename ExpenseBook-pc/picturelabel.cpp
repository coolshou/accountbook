#include "picturelabel.h"

#include <QPixmap>
#include <QFileDialog>

PictureLabel::PictureLabel(QWidget *parent)
    :QLabel(parent)
{
    connect(this, SIGNAL(clicked()), SLOT(onClicked()));
}

QPixmap PictureLabel::getOriginPixmap() const
{
    return _originPicture;
}


void PictureLabel::mousePressEvent(QMouseEvent *event)
{
    emit clicked();
}



void PictureLabel::onClicked()
{
    QString picturePath = QFileDialog::getOpenFileName(this, "選擇照片", QDir::homePath(), "照片 (*.jpg)");

    if(picturePath.isEmpty())
        return;

    setOriginPixmap(picturePath);
}


void PictureLabel::setOriginPixmap(QPixmap picture)
{
    _originPicture = picture;
    QLabel::setPixmap(picture.scaled(400, 300, Qt::KeepAspectRatio));
}
