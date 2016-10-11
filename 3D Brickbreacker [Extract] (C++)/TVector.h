#ifndef TVECTOR_HH
#define TVECTOR_HH
class TVector{
private:
	double x_, y_, z_;

public:
	TVector();
	~TVector(){};
	TVector(double x, double y, double z);
	TVector(const TVector& tvect);

	double getX();
	double getY();
	double getZ();

	void setX(double c);
	void setY(double c);
	void setZ(double c);

	void add(const TVector& tvect);
	void sub(const TVector& tvect);
	void crossProduct(const TVector& tvect);
	TVector getCrossProduct(const TVector& tvect);
	double dotProduct(const TVector& tvect);
	double getMagnitude();
	void normalise();
	void invertVector();
	
	TVector homotheticTransformation(const double& k);

	TVector getRotation(TMatrix rot);
	TVector getRotation(int axis, double angle);
	void rotate(TMatrix rot);
	void rotate(int axis, double angle);

	TVector operator+(const TVector& tvect);
	TVector operator-(const TVector& tvect);
	TVector operator*(const TVector& tvect);
	TVector operator=(const TVector& tvect);

	void floorVect();
	void printVect();
};

TVector::TVector(){
	x_ = 0.0;
	y_ = 0.0;
	z_ = 0.0;
}

TVector::TVector(double a, double b, double c){
	x_ = a;
	y_ = b;
	z_ = c;
}

TVector::TVector(const TVector& tvect){
	x_ = tvect.x_;
	y_ = tvect.y_;
	z_ = tvect.z_;
}


double TVector::getX(){
	return x_;
}

double TVector::getY(){
	return y_;
}

double TVector::getZ(){
	return z_;
}


void TVector::setX(double c){
	x_ = c;
}

void TVector::setY(double c){
	y_ = c;
}

void TVector::setZ(double c){
	z_ = c;
}
void TVector::add(const TVector& tvect){
	x_ = x_ + tvect.x_;
	y_ = y_ + tvect.y_;
	z_ = z_ + tvect.z_;
}

void TVector::sub(const TVector& tvect){
	x_ = x_ - tvect.x_;
	y_ = y_ - tvect.y_;
	z_ = z_ - tvect.z_;
}

void TVector::crossProduct(const TVector& tvect){
	x_ = y_*tvect.z_ - z_*tvect.y_;
	y_ = z_*tvect.x_  - x_*tvect.z_;
	z_ = x_*tvect.y_ - y_*tvect.x_;
}

TVector TVector::getCrossProduct(const TVector& tvect){
	TVector tt = TVector(y_*tvect.z_ - z_*tvect.y_, z_*tvect.x_ - x_*tvect.z_, x_*tvect.y_ - y_*tvect.x_);
	return tt;
}

double TVector::dotProduct(const TVector& tvect){
	double tmp = x_*tvect.x_ + y_*tvect.y_ + z_*tvect.z_;
	return tmp;
}

double TVector::getMagnitude(){
	return sqrt(x_*x_ + y_*y_ + z_*z_);
}

void TVector::normalise(){
	double norm = getMagnitude();
	if (norm != 0){
		x_ /= norm;
		y_ /= norm;
		z_ /= norm;
	}
	floorVect();
}

void TVector::invertVector(){
	x_ = -x_;
	y_ = -y_;
	z_ = -z_;
}

TVector TVector::homotheticTransformation(const double& k){
	TVector hT = TVector(this->x_*k, this->y_*k, this->z_*k);
	return hT;
}

TVector TVector::getRotation(TMatrix rotMat){
	TVector tmp = TVector();
	tmp.x_ = x_*rotMat.get(0, 0) + y_*rotMat.get(0, 1) + z_*rotMat.get(0, 2);
	tmp.y_ = x_*rotMat.get(1, 0) + y_*rotMat.get(1, 1) + z_*rotMat.get(1, 2);
	tmp.z_ = x_*rotMat.get(2, 0) + y_*rotMat.get(2, 1) + z_*rotMat.get(2, 2);
	return tmp;
}

TVector TVector::getRotation(int axis, double angle){
	TMatrix tmat;
	TVector tmpV = TVector(this->x_, this->y_, this->z_);
	tmat.createTransformationMat(axis, angle);
	tmpV.rotate(tmat);
	return tmpV;
}


void TVector::rotate(TMatrix rotMat){
	TVector tmp = TVector(this->x_, this->y_, this->z_);
	//tmp.getRotation(rotMat);
	tmp.x_ = x_*rotMat.get(0, 0) + y_*rotMat.get(0, 1) + z_*rotMat.get(0, 2);
	tmp.y_ = x_*rotMat.get(1, 0) + y_*rotMat.get(1, 1) + z_*rotMat.get(1, 2);
	tmp.z_ = x_*rotMat.get(2, 0) + y_*rotMat.get(2, 1) + z_*rotMat.get(2, 2);

	x_ = tmp.x_;
	y_ = tmp.y_;
	z_ = tmp.z_;
}

void TVector::rotate(int axis, double angle){
	TMatrix tmat;
	TVector tmpV = TVector(this->x_, this->y_, this->z_);
	tmat.createTransformationMat(axis, angle);
	tmpV.rotate(tmat);
	x_ = tmpV.x_;
	y_ = tmpV.y_;
	z_ = tmpV.z_;
}


TVector TVector::operator+(const TVector& tvect){
	TVector tmp = TVector(this->x_, this->y_, this->z_);
	tmp.x_ = tmp.x_ + tvect.x_;
	tmp.y_ = tmp.y_ + tvect.y_;
	tmp.z_ = tmp.z_ + tvect.z_;
	return tmp;
}

TVector TVector::operator-(const TVector& tvect){
	TVector a =  TVector(x_ - tvect.x_, y_ - tvect.y_, z_ - tvect.z_);
	return a;
}

TVector TVector::operator*(const TVector& tvect){
	TVector tmp = TVector(y_*tvect.z_ - z_*tvect.y_, z_*tvect.x_ - x_*tvect.z_, x_*tvect.y_ - y_*tvect.x_);
	return tmp;
}

TVector TVector::operator=(const TVector& tvect){
	x_ = tvect.x_;
	y_ = tvect.y_;
	z_ = tvect.z_;
	return *this;

}

void TVector::floorVect(){
	x_ = floor(x_ * 1000) / 1000;
	y_ = floor(y_ * 1000) / 1000;
	z_ = floor(z_ * 1000) / 1000;
}

void TVector::printVect(){
	floorVect();
	std::cout << "(" << x_ << ", " << y_ << ", " << z_ << ")" << std::endl;
}


#endif