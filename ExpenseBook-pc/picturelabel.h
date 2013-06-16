#ifndef PICTURELABEL_H
#define PICTURELABEL_H

#include <QLabel>
#include <QPixmap>

class PictureLabel :public QLabel
{
    Q_OBJECT

public:

    explicit PictureLabel(QWidget *parent = 0);
    QPixmap getOriginPixmap() const;
    
protected:

    void mousePressEvent(QMouseEvent *event);

signals:
    
    void clicked();

public slots:
    
    void onClicked();
    void setOriginPixmap(QPixmap picture);

private:

    QPixmap _originPicture;

    void setPixmap(const QPixmap &);
};

#endif // PICTURELABEL_H
