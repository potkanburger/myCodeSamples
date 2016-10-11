#ifndef TRAY_HH
#define TRAY_HH

class TRay{
private:
	TVector point_;
	TVector direction_;
public:
	TRay();
	~TRay();


};

TRay::TRay(){
	point_ = TVector();
	direction_ = TVector();
}

#endif