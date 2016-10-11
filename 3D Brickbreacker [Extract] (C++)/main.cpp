#define _USE_MATH_DEFINES
#include <stdio.h>
#include <stdlib.h>
#include <Windows.h>
#include <iostream>
#include <math.h>

#include "TMatrix.h"
#include "TVector.h"
#include "TRay.h"
#include "Tests.h"

#include "GL\glut.h"

static GLfloat spin = 0.0;
const float pos[4] = { 0.1, 0.1, 1.0, 1.0 };

TVector eye = TVector(0.0, -0.2, 0.5);
TVector teye = TVector(0.0, 0.0, 0.5);
TVector center = TVector();
TVector up = TVector();
TVector dang = TVector();
TMatrix matrot = newTransformationMat(Z_AXIS, 10.0);
TMatrix batrot = newTransformationMat(Z_AXIS, 7.0);
TMatrix invbatrot = newTransformationMat(Z_AXIS, -7.0);
TMatrix invmatrot = newTransformationMat(Z_AXIS, -10.0);
TVector point = TVector(0.8, 0.8, 0.0);
TVector pointB = point - TVector(0.0, 0.0, 0.1);
TVector groundDif = TVector(0.0, 0.0, 0.05);

TVector batOffset = TVector(0.1, 0.1, -0.05);
TVector batWidth = TVector(0.02, 0.02, 0.0);

TVector batPosition = point;
TVector batPosition2;

TVector batRay1, batRay2;

TVector balls[3];
TVector ballDirections[3];
double ballSize = 0.02;

bool play = false;

TVector prev = TVector();
TVector prevB = TVector();
TVector tmpNormal = TVector();

static GLfloat ballColor[4] = { 1.0, 1.0, 1.0, 1.0 };
static GLfloat groundColor[4] = { 1.0, 0.6, 0.2, 1.0 };
static GLfloat batColor[4] = { 1.0, 0.6, 0.8, 1.0 };

TVector bat1Up[2][4];
TVector bat1Down[2][4];

TVector bat2Up[2][4];
TVector bat2Down[2][4];

TVector bat3Up[2][4];
TVector bat3Down[2][4];


void mglVertex3d(TVector v){
	glVertex3d(v.getX(), v.getY(), v.getZ());
}

double getAngleZrot(TVector vec){
	return asin(vec.getY() / vec.getMagnitude());
}


bool inInterval(double value, double bornA, double bornB){
	double min = min(bornA, bornB);
	double max = max(bornA, bornB);

	return (value >= min && value <= max);
}

void fillBat(TVector (&bat)[2][4], TVector (&batD)[2][4], TVector pos1, TVector pos2){
	bat[0][0] = pos1;
	bat[1][0] = pos2;
	for (int i = 0; i < 2; i++){
		for (int j = 1; j < 4; j++){
			bat[i][j] = bat[i][j - 1].getRotation(batrot);
		}
	}

	for (int i = 0; i < 2; i++){
		for (int j = 0; j < 4; j++){
			batD[i][j] = bat[i][j];
			batD[i][j].setZ(0.0);
		}
	}
}

void drawBat(TVector bat1Up[2][4], TVector bat1Down[2][4]){
	//Up side of bat

	TVector calc1 = TVector();
	TVector calc2 = TVector();

	glBegin(GL_POLYGON);

	calc1 = bat1Up[1][0];
	calc1.sub(bat1Up[0][0]);
	calc2 = bat1Up[0][0]; 
	calc2.sub(bat1Up[0][3]);


	
	calc1.normalise();
	calc2.normalise();
	tmpNormal = calc1.getCrossProduct(calc2);
	tmpNormal.normalise();
	glNormal3d(tmpNormal.getX(), tmpNormal.getY(), tmpNormal.getZ());

	mglVertex3d(bat1Up[0][0]);
	mglVertex3d(bat1Up[0][1]);
	mglVertex3d(bat1Up[1][1]);
	mglVertex3d(bat1Up[1][0]);
	glEnd();

	glBegin(GL_POLYGON);
	mglVertex3d(bat1Up[0][2]);
	mglVertex3d(bat1Up[0][3]);
	mglVertex3d(bat1Up[1][3]);
	mglVertex3d(bat1Up[1][2]);
	glEnd();

	glBegin(GL_POLYGON);
	mglVertex3d(bat1Up[0][1]);
	mglVertex3d(bat1Up[0][2]);
	mglVertex3d(bat1Up[1][2]);
	mglVertex3d(bat1Up[1][1]);
	glEnd();


	//down side of bat
	glBegin(GL_POLYGON);
	calc1 = bat1Down[0][1] - bat1Down[0][0];
	calc2 = bat1Down[0][0] - bat1Down[0][1];
	calc1.normalise();
	calc2.normalise();
	tmpNormal = calc1.getCrossProduct(calc2);
	tmpNormal.normalise();
	glNormal3d(tmpNormal.getX(), tmpNormal.getY(), tmpNormal.getZ());

	mglVertex3d(bat1Down[0][0]);
	mglVertex3d(bat1Down[0][1]);
	mglVertex3d(bat1Down[1][1]);
	mglVertex3d(bat1Down[1][0]);
	glEnd();

	glBegin(GL_POLYGON);
	mglVertex3d(bat1Down[0][2]);
	mglVertex3d(bat1Down[0][3]);
	mglVertex3d(bat1Down[1][3]);
	mglVertex3d(bat1Down[1][2]);
	glEnd();

	glBegin(GL_POLYGON);
	mglVertex3d(bat1Down[0][1]);
	mglVertex3d(bat1Down[0][2]);
	mglVertex3d(bat1Down[1][2]);
	mglVertex3d(bat1Down[1][1]);
	glEnd();



	// side "out" of the bat 

	TVector tmpCenter = bat1Up[0][2] + bat1Up[0][1];
	tmpCenter = tmpCenter.homotheticTransformation(0.5);
	tmpNormal = tmpCenter - center;
	tmpNormal.normalise();
	glNormal3d(tmpNormal.getX(), tmpNormal.getY(), tmpNormal.getZ());


	glBegin(GL_POLYGON);

	mglVertex3d(bat1Down[0][0]);
	mglVertex3d(bat1Down[0][1]);
	mglVertex3d(bat1Up[0][1]);
	mglVertex3d(bat1Up[0][0]);

	glEnd();



	glBegin(GL_POLYGON);

	mglVertex3d(bat1Down[0][1]);
	mglVertex3d(bat1Down[0][2]);
	mglVertex3d(bat1Up[0][2]);
	mglVertex3d(bat1Up[0][1]);

	glEnd();


	glBegin(GL_POLYGON);
	mglVertex3d(bat1Down[0][2]);
	mglVertex3d(bat1Down[0][3]);
	mglVertex3d(bat1Up[0][3]);
	mglVertex3d(bat1Up[0][2]);

	glEnd();

	tmpNormal = center - tmpCenter;
	tmpNormal.normalise();
	glNormal3d(tmpNormal.getX(), tmpNormal.getY(), tmpNormal.getZ());



	glBegin(GL_POLYGON);

	mglVertex3d(bat1Down[1][0]);
	mglVertex3d(bat1Down[1][1]);
	mglVertex3d(bat1Up[1][1]);
	mglVertex3d(bat1Up[1][0]);

	glEnd();



	glBegin(GL_POLYGON);

	mglVertex3d(bat1Down[1][1]);
	mglVertex3d(bat1Down[1][2]);
	mglVertex3d(bat1Up[1][2]);
	mglVertex3d(bat1Up[1][1]);

	glEnd();

	glBegin(GL_POLYGON);
	mglVertex3d(bat1Down[1][2]);
	mglVertex3d(bat1Down[1][3]);
	mglVertex3d(bat1Up[1][3]);
	mglVertex3d(bat1Up[1][2]);

	glEnd();

	calc1 = bat1Up[1][0] - bat1Up[0][0];
	calc2 = bat1Down[1][0] - bat1Up[1][0];
	calc1.normalise();
	calc2.normalise();
	tmpNormal = calc1.getCrossProduct(calc2);
	tmpNormal.normalise();
	glNormal3d(tmpNormal.getX(), tmpNormal.getY(), tmpNormal.getZ());


	glBegin(GL_POLYGON);
	mglVertex3d(bat1Up[0][0]);
	mglVertex3d(bat1Up[1][0]);
	mglVertex3d(bat1Down[1][0]);
	mglVertex3d(bat1Down[0][0]);

	glEnd();

	calc1 = bat1Up[1][3] - bat1Up[0][3];
	calc2 = bat1Down[1][3] - bat1Up[1][3];
	calc1.normalise();
	calc2.normalise();
	tmpNormal = calc1.getCrossProduct(calc2);
	tmpNormal.normalise();
	glNormal3d(tmpNormal.getX(), tmpNormal.getY(), tmpNormal.getZ());

	glBegin(GL_POLYGON);
	mglVertex3d(bat1Up[0][3]);
	mglVertex3d(bat1Up[1][3]);
	mglVertex3d(bat1Down[1][3]);
	mglVertex3d(bat1Down[0][3]);

	glEnd();
}

void spinBatsRight(void){

	batPosition.rotate(matrot);
	batPosition2.rotate(matrot);
	if (!play){
		play = true;
	}
	glutPostRedisplay();
	glutIdleFunc(NULL);
}

void spinBatsLeft(void){

	batPosition.rotate(invmatrot);
	batPosition2.rotate(invmatrot);
	if (!play){
		play = true;
	}
	glutPostRedisplay();
	glutIdleFunc(NULL);
}

void nuit(void){
	glDisable(GL_LIGHT0);
	
	glutPostRedisplay();
	glutIdleFunc(NULL);
}

void jour(void){
	glEnable(GL_LIGHT0);
	glutPostRedisplay();
	glutIdleFunc(NULL);
}

void elevationCtrl(void){

	teye.rotate(X_AXIS, 2.0);
	up.rotate(X_AXIS, 2.0);

	glutPostRedisplay();
	glutIdleFunc(NULL);
}

void moveDatCircleBack(void){
	if (teye.getZ() < 21.0){
		teye.add(TVector(0.0, 0.0, 0.1));
		std::cout << teye.getZ() << std::endl;
	}
		
	glutPostRedisplay();
	glutIdleFunc(NULL);
}

void moveDatCircle(void){
	teye.rotate(Z_AXIS, 5.0);
	up.rotate(Z_AXIS, 5.0);

	glutPostRedisplay();
	glutIdleFunc(NULL);
}


void playPause(void){
	play = !play;
	glutPostRedisplay();
	glutIdleFunc(NULL);
}

void initialisation(void){
	glEnable(GL_DEPTH_TEST);
	glPolygonMode(GL_FRONT, GL_FILL);
	glEnable(GL_SMOOTH);
	glEnable(GL_LIGHTING);
	
	glEnable(GL_LIGHT0);
	glLightfv(GL_LIGHT0, GL_POSITION, pos);

	// Applies matrix operations to the projection matrix stack
	glMatrixMode(GL_PROJECTION);
	// Replaces the current matrix with the identity matrix
	glLoadIdentity();
	// Sets up a perspective projection matrix
	gluPerspective(40.0, 1.0, 0.1, 10.0);
	
	up = TVector(0.0,1.0,0.0);

	batPosition.sub(batOffset);
	batPosition2 = batPosition - batWidth;
	TVector tmp = batPosition2.getRotation(batrot) + batPosition2.getRotation(batrot).getRotation(batrot);
	tmp = tmp.homotheticTransformation(0.5);
	tmp.setZ(0.0);

	batRay1 = batPosition;
	batRay2 = batPosition - batWidth;
	double ratio = tmp.getMagnitude() / (tmp.getMagnitude() + ballSize);
	tmp = tmp.homotheticTransformation(ratio);
	for (int i = 0; i < 3; i++){
		if (i == 0){
			balls[i] = tmp;
		}
		else{
			balls[i] = tmp.getRotation(Z_AXIS, i*120.0);
		}
		ballDirections[i] = center - balls[i];
		ballDirections[i].normalise();
		ballDirections[i] = ballDirections[i].homotheticTransformation(0.01);
	}
	teye.add(TVector(0.0, 0.0, 4.0));
	teye.rotate(X_AXIS, 60.0);
	up.rotate(X_AXIS, 60.0);

}

// Reshapes the window
void reshape(int w, int h)
{
	// Sets the viewport
	glViewport(0, 0, (GLsizei)w, (GLsizei)h);
	// Applies matrix operations to the projection matrix stack
	glMatrixMode(GL_PROJECTION);
	// Replaces the current matrix with the identity matrix
	glLoadIdentity();
	// Sets up a perspective projection matrix
	gluPerspective(40.0, (GLfloat)w / (GLfloat)h, 0.1, 1000.0);
	std::cout << glGetError() << std::endl;
	std::cout << "Perspective" << std::endl;
}

void draw(void)
{
	glMatrixMode(GL_MODELVIEW);

	// Clears the color buffer bit and depth buffer bit
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	//teye.add(center);

	glLightfv(GL_LIGHT0, GL_POSITION, pos);
	glLoadIdentity();
	gluLookAt(teye.getX(), teye.getY(), teye.getZ(), center.getX(), center.getY(), center.getZ(), up.getX(), up.getY(), up.getZ());

	
	glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, ballColor);
	glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, ballColor);
	glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 10.0f);
	if (play){
		std::cout << "--------------------------" << std::endl;
	}
	//glutSolidTeapot(0.2);
	GLfloat ballColor2[4] = { 0.0, 0.0, 0.0, 0.0 };
	batColor[1] = 0.6;
	TVector calcD;
	for (int i = 0; i < 3; i++){
		if (play){
			balls[i].add(ballDirections[i]);
			 calcD = balls[i] - center;
			
			TVector calcB = batPosition;
			TVector calcC = batPosition - batWidth;
			calcB.setZ(0.0);
			calcC.setZ(0.0);
			if (calcD.getMagnitude() >= point.getMagnitude()){
				balls[i].sub(ballDirections[i]);
				ballDirections[i].invertVector();
				balls[i].add(ballDirections[i]);
			}

			
			if (calcD.getMagnitude() <= batRay1.getMagnitude() && calcD.getMagnitude() >= batRay2.getMagnitude()){
				if (!(!(calcD.getX() >= 0) != !(calcB.getX() >= 0)) && inInterval(getAngleZrot(calcD), getAngleZrot(calcB), getAngleZrot(calcB.getRotation(batrot).getRotation(batrot).getRotation(batrot)))){
					batColor[1] = 0.0;
					balls[i].sub(ballDirections[i]);
					ballDirections[i].invertVector();
					balls[i].add(ballDirections[i]);
				}
			}

		}
		
		glPushMatrix();
			glTranslated(0.0, 0.0, 0.02);
			glTranslated(balls[i].getX(), balls[i].getY(), balls[i].getZ());
			glutSolidSphere(ballSize, 36, 36);
		glPopMatrix();
	}

	glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, groundColor);
	glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, groundColor);
	glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 10.0f);


	for (int i = 0; i < 36; i++){
		tmpNormal = point - center;
		glNormal3d(tmpNormal.getX(), tmpNormal.getY(), tmpNormal.getZ());
		prev = point;
		prevB = point - groundDif;
		point.rotate(Z_AXIS, 10.0);
		pointB = point - groundDif;
		glBegin(GL_POLYGON);
		mglVertex3d(prevB);
		mglVertex3d(prev);
		mglVertex3d(point);
		mglVertex3d(pointB);
		glEnd();
	}

	glNormal3d(0.0, 0.0, 1.0);
	glBegin(GL_POLYGON);
	for (int i = 0; i < 36; i++){
		point.rotate(Z_AXIS, 10.0);
		mglVertex3d(point);
	}
	glEnd();

	glBegin(GL_POLYGON);
	for (int i = 0; i < 36; i++){
		pointB.rotate(Z_AXIS, 10.0);
		mglVertex3d(pointB);
	}
	glEnd();

	
	const GLfloat cst = 10.0f;
	glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, batColor);
	glMaterialfv(GL_FRONT, GL_SPECULAR, batColor);
	glMaterialfv(GL_FRONT, GL_SHININESS, &cst);

	
	fillBat(bat1Up, bat1Down, batPosition, batPosition2);
	fillBat(bat2Up, bat2Down, batPosition.getRotation(Z_AXIS, 120.0), batPosition2.getRotation(Z_AXIS, 120.0));
	fillBat(bat3Up, bat3Down, batPosition.getRotation(Z_AXIS, 240.0), batPosition2.getRotation(Z_AXIS, 240.0));


	drawBat(bat1Up, bat1Down);
	drawBat(bat2Up, bat2Down);
	drawBat(bat3Up, bat3Down);
	
	// Forces execution of OpenGL functions in finite time
	glFlush();
	glutSwapBuffers();
	
	
	Sleep(15);
	glutPostRedisplay();
}

// Function that reads input from the keyboard
void Keyboard(unsigned char key, int A, int B)
{
	switch (key)
	{
		// Start rotation
	case '1':
		// Sets the global idle callback
		glutIdleFunc(moveDatCircle);
		break;
		// Stop rotation
	case '2':
		// Sets the global idle callback
		glutIdleFunc(moveDatCircleBack);
		break;

	case '3':
		// Sets the global idle callback
		glutIdleFunc(elevationCtrl);
		break;

	case '4':
		// Sets the global idle callback
		glutIdleFunc(nuit);
		break;
		// Escapes from the program by pressing 'Esc'

	case '5':
		// Sets the global idle callback
		glutIdleFunc(jour);
		break;
		// Escapes from the program by pressing 'Esc'

	case 'Q':
	case 'q':
		glutIdleFunc(spinBatsLeft);
		break;

	case 'D':
	case 'd':
		glutIdleFunc(spinBatsRight);
		break;

	case 'a':
	case 'A':
		glutIdleFunc(playPause);
		break;
	case 27:
		// Terminates the program
		exit(0);
		break;
	default:
		break;
	}
}

int main(int argc, char **argv){
	
	// Initialise GLUT library
	glutInit(&argc, argv);

	// Initialise the display mode
	glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB | GLUT_DEPTH);
	// Initialise the window position
	glutInitWindowPosition(0, 0);
	// Initialise the window size
	glutInitWindowSize(700, 700);

	// Creates a window on the screen
	glutCreateWindow("Agusti will be jealous");

	initialisation();
	
	// Function that reads input from the keyboard
	glutKeyboardFunc(Keyboard);

	// The display function is called each time there is a display callback
	glutDisplayFunc(draw);

	// The reshape function reshapes the scene
	glutReshapeFunc(reshape);

	// Causes the program to enter an event-processing loop
	glutMainLoop();

	return -1;
}