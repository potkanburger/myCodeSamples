#ifndef TESTS_HH
#define TESTS_HH

#include "TMatrix.h"
#include "TVector.h"
#include "TRay.h"

void tests(){
	TVector tmp1 = TVector(5.0, 3.0, 6.0);
	TVector tmp2 = TVector(1.0, -3.0, 8.0);

	TVector hgh = tmp1;
	hgh.invertVector();
	hgh.printVect();

	TVector tmp = TVector(tmp1);
	tmp.add(tmp2);
	TVector test = tmp1;
	TVector test2 = TVector(tmp1);
	test2.add(tmp2);
	test2.add(tmp1);

	TVector test3 = tmp1 - tmp2 + tmp1;
	test.printVect();
	test.crossProduct(tmp2);



	//tmp.printVect();
	test.printVect();
	test2.printVect();
	test3.printVect();

	test3 = test*tmp2;

	test3.printVect();

	TMatrix mat1 = TMatrix(RANDMATRIX);
	//mat1.identity();
	mat1.floorprintMat();

	TMatrix mat2 = TMatrix(1.5);
	mat1.sub(mat2);
	mat1.floorprintMat();
	mat1.transpose();
	mat1.floorprintMat();
	mat1.transpose();
	mat1.floorprintMat();

	TMatrix mat3;
	mat3 = mat2 + mat2;
	mat3 = mat3 + TMatrix(IDENTITYMATRIX);
	mat3.floorprintMat();

	std::cout << mat3.getDeterminant() << std::endl;

	mat1 = TMatrix(SMALLRANDMATRIX);
	mat1.floorprintMat();

	std::cout << mat1.getDeterminant() << std::endl;

	mat3.mul(mat1);
	mat3.floorprintMat();


	TVector t4;
	t4.add(tmp1);
	t4.sub(tmp2);
	t4.printVect();
	mat3.floorprintMat();
	t4.rotate(mat3);
	t4.printVect();

	TMatrix rX;
	rX.createTransformationMat(X_AXIS, 180.0);
	rX.floorprintMat();

	/*t4.rotate(rX);
	t4.printVect();*/
	t4 = tmp1 + tmp2;
	t4.printVect();

	TVector result;
	result = t4.getRotation(rX);
	result.printVect();

	TVector resultb = t4;
	t4.rotate(rX);
	t4.printVect();

	resultb.rotate(X_AXIS, 180.0);
	resultb.printVect();
	resultb.normalise();
	resultb.printVect();
}


#endif