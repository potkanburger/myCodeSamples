#ifndef TMATRIX_HH
#define TMATRIX_HH

#define IDENTITYMATRIX 1
#define RANDMATRIX 2
#define SMALLRANDMATRIX 3

#define X_AXIS 0
#define Y_AXIS 1
#define Z_AXIS 2

class TMatrix{
private:
	double mat_[3][3];

public:
	TMatrix();
	~TMatrix(){};
	TMatrix(const double& val);
	TMatrix(const int pValue);
	TMatrix(const double tmat[3][3]);
	TMatrix(const TMatrix& tmat);

	void edit(const double& val, const int& i, const int& j);
	void identity();
	void transpose();
	void createTransformationMat(int axis, double angleDeg);
	TMatrix newTransformationMat(int axis, double angleDeg);

	double get(int i, int j);
	void add(const TMatrix& tmat);
	void sub(const TMatrix& tmat);
	void mul(const TMatrix& tmat);

	double getDeterminant();

	void floorMat(); // all numbers to 10^-3 precision

	TMatrix operator+(const TMatrix& tmat);
	TMatrix operator-(const TMatrix& tmat);
	TMatrix operator*(const TMatrix& tmat);
	TMatrix operator=(const TMatrix& tmat);
	void printMat();
	void floorprintMat();
};

TMatrix::TMatrix(){
	for (int i = 0; i < 3; i++){
		for (int j = 0; j < 3; j++){
			mat_[i][j] = 0.0;
		}
	}
}


TMatrix::TMatrix(const double& val){
	for (int i = 0; i < 3; i++){
		for (int j = 0; j < 3; j++){
			mat_[i][j] = val;
		}
	}
}

TMatrix::TMatrix(const int pValue){
	switch (pValue)
	{

	case IDENTITYMATRIX:
		for (int i = 0; i < 3; i++){
			for (int j = 0; j < 3; j++){
				(i == j) ? mat_[i][j] = 1.0 : mat_[i][j] = 0.0;
			}
		}
		break;

	case RANDMATRIX:
		for (int i = 0; i < 3; i++){
			for (int j = 0; j < 3; j++){
				mat_[i][j] = (double) (std::rand() % 100);
			}
		}
		break;

	case SMALLRANDMATRIX:
		for (int i = 0; i < 3; i++){
			for (int j = 0; j < 3; j++){
				mat_[i][j] = (double)(std::rand() % 10);
			}
		}
		break;

	default:
		TMatrix();
		break;
	}
}

TMatrix::TMatrix(const double tmat[3][3]){
	for (int i = 0; i < 3; i++){
		for (int j = 0; j < 3; j++){
			mat_[i][j] = tmat[i][j];
		}
	}
}

TMatrix::TMatrix(const TMatrix& tmat){
	for (int i = 0; i < 3; i++){
		for (int j = 0; j < 3; j++){
			mat_[i][j] = tmat.mat_[i][j];
		}
	}
}

void TMatrix::edit(const double& val,const int& i, const int& j){
	mat_[i][j] = val;
}

double TMatrix::get(int i, int j){
	return mat_[i][j];
}

void TMatrix::identity(){
	for (int i = 0; i < 3; i++){
		for (int j = 0; j < 3; j++){
			(i == j) ? mat_[i][j] = 1.0 : mat_[i][j] = 0.0;
		}
	}
}

void TMatrix::transpose(){
	TMatrix tmp = TMatrix();
	for (int i = 0; i < 3; i++){
		for (int j = 0; j < 3; j++){
			tmp.mat_[i][j] = mat_[j][i];
		}
	}

	for (int i = 0; i < 3; i++){
		for (int j = 0; j < 3; j++){
			mat_[i][j] = tmp.mat_[i][j];
		}
	}
}

void TMatrix::floorMat(){
	for (int i = 0; i < 3; i++){
		for (int j = 0; j < 3; j++){
			mat_[i][j] = floor(mat_[i][j] * 10000) / 10000;
		}
	}
}

void TMatrix::createTransformationMat(int axis, double angle){
	double angle_ = angle*M_PI / 180;
	switch (axis)
	{
	case X_AXIS:
		for (int i = 0; i < 3; i++){
			for (int j = 0; j < 3; j++){
				mat_[i][j] = 0.0;
			}
		}
		mat_[0][0] = 1.0;
		mat_[1][1] = cos(angle_);
		mat_[1][2] = -sin(angle_);
		mat_[2][1] = sin(angle_);
		mat_[2][2] = cos(angle_);
		break;

	case Y_AXIS:
		for (int i = 0; i < 3; i++){
			for (int j = 0; j < 3; j++){
				mat_[i][j] = 0.0;
			}
		}
		mat_[0][0] = cos(angle_);
		mat_[0][2] = sin(angle_);
		mat_[1][1] = 1.0;
		mat_[2][0] = -sin(angle_);
		mat_[2][2] = cos(angle_);
		break;

	case Z_AXIS:
		for (int i = 0; i < 3; i++){
			for (int j = 0; j < 3; j++){
				mat_[i][j] = 0.0;
			}
		}
		mat_[0][0] = cos(angle_);
		mat_[0][1] = -sin(angle_);
		mat_[1][0] = sin(angle_);
		mat_[1][1] = cos(angle_);
		mat_[2][2] = 1.0;
		break;
	default:
		break;
	}
}

TMatrix newTransformationMat(int axis, double angleDeg){
	TMatrix tmp = TMatrix();
	tmp.createTransformationMat(axis, angleDeg);
	return tmp;
}

void TMatrix::add(const TMatrix& tmat){
	for (int i = 0; i < 3; i++){
		for (int j = 0; j < 3; j++){
			mat_[i][j] += tmat.mat_[i][j];
		}
	}
}

void TMatrix::sub(const TMatrix& tmat){
	for (int i = 0; i < 3; i++){
		for (int j = 0; j < 3; j++){
			mat_[i][j] -= tmat.mat_[i][j];
		}
	}
}

void TMatrix::mul(const TMatrix& tmat){
	TMatrix tmp;
	for (int i = 0; i < 3; i++){
		for (int j = 0; j < 3; j++){
			tmp.mat_[i][j] = mat_[i][0] * tmat.mat_[0][j] + mat_[i][1] * tmat.mat_[1][j] + mat_[i][2] * tmat.mat_[2][j];
		}
	}
	for (int i = 0; i < 3; i++){
		for (int j = 0; j < 3; j++){
			mat_[i][j] = tmp.mat_[i][j];
		}
	}
	
}


double TMatrix::getDeterminant(){
	double tmp = 0.0;
	for (int i = 0; i < 3; i++){
		tmp += mat_[(0 + i) % 3][0] * mat_[(1 + i) % 3][1] * mat_[(2 + i) % 3][2];
		tmp -= mat_[(2 + i) % 3][0] * mat_[(1 + i) % 3][1] * mat_[(0 + i) % 3][2];
	}
	return tmp;
}

TMatrix TMatrix::operator+(const TMatrix& tmat){
	TMatrix tmp = TMatrix(this->mat_);
	for (int i = 0; i < 3; i++){
		for (int j = 0; j < 3; j++){
			tmp.mat_[i][j] += tmat.mat_[i][j];
		}
	}
	return tmp;
}
TMatrix TMatrix::operator-(const TMatrix& tmat){
	TMatrix tmp = TMatrix(this->mat_);
	tmp.sub(tmat);
	return tmp;
}

TMatrix TMatrix::operator*(const TMatrix& tmat){
	TMatrix tmp = TMatrix(this->mat_);
	tmp.mul(tmat);
	return(tmp);
}


TMatrix TMatrix::operator=(const TMatrix& tmat){
	for (int i = 0; i < 3; i++){
		for (int j = 0; j < 3; j++){
			mat_[i][j] = tmat.mat_[i][j];
		}
	}
	return *this;
}


void TMatrix::printMat(){
	std::cout << std::endl;
	for (int i = 0; i < 3; i++){
		std::cout << "(" << mat_[i][0] << ", " << mat_[i][1] << ", " << mat_[i][2] << ")" << std::endl;
	}
	std::cout << std::endl;
}

void TMatrix::floorprintMat(){
	TMatrix prt = TMatrix(this->mat_);
	prt.floorMat();
	std::cout << std::endl;
	for (int i = 0; i < 3; i++){
		std::cout << "(" << prt.mat_[i][0] << ", " << prt.mat_[i][1] << ", " << prt.mat_[i][2] << ")" << std::endl;
	}
	std::cout << std::endl;
}

#endif