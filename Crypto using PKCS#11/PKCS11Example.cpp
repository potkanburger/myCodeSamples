// PKCS11Example.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
//#include "pkcs11test.h"
#include "hu_pkcs11_fun.h"




int main()
{
	PKCS11Test p11Test;

	cout << "Initialisation... " << endl;
	p11Test.initWIthSingleToken();
	cout << endl;
	/*

	This part is supposed to get the private key on the token to decrypt the data stored in the file.
	But the key is not on the token anymore...

	CK_BYTE dataT[256];
	CK_ULONG dataT_len = sizeof(dataT);
	CK_ULONG_PTR pdataT_len = &dataT_len;

	CK_BYTE dataDecrypted[256];
	CK_ULONG dataDecrypted_len = sizeof(dataDecrypted);
	CK_ULONG_PTR pdataDecrypted_len = &dataDecrypted_len;

	p11Test.getData(dataT, dataT_len);

	cout << "data decryption with finding key on token... " << endl;
	p11Test.findPrivAndDecrypt(dataT, dataT_len, dataDecrypted, pdataDecrypted_len);
	cout << endl;


	*/
	

	cout << "KeyPair generation... " << endl;
	p11Test.generateKeyPair();
	cout << endl;

	cout << "Logout..." << endl;
	p11Test.logout();
	cout << endl;

	cout << "Trying to get keys (public and private)... " << endl;
	p11Test.doTests();
	cout << endl;

	cout << "Login... " << endl;
	p11Test.login();
	cout << endl;

	cout << "Trying to get keys (public and private)... " << endl;
	p11Test.doTests();
	cout << endl;

	cout << "creating some data... " << endl;

	CK_BYTE data[20];
	CK_ULONG data_len = sizeof(data);
	for (int i = 0; i < (int)data_len; i++){
		data[i] = (CK_BYTE)(6 * i) % 255;
	}
	CK_BYTE res[256];
	CK_ULONG res_len = sizeof(res);
	
	cout << "data encryption... " << endl;
	p11Test.encrypt(data, data_len, res, &res_len);
	cout << endl;

	CK_BYTE data2[256];
	CK_ULONG data2_len = sizeof(data2);
	CK_ULONG_PTR pdata2_len = &data2_len;


	cout << "Logout..." << endl;
	p11Test.logout();
	cout << endl;

	cout << "data decryption...  " << endl;
	p11Test.decrypt(res, res_len, data2, pdata2_len);
	cout << endl;

	cout << "Login... " << endl;
	p11Test.login();
	cout << endl;

	cout << "data decryption...  " << endl;
	p11Test.decrypt(res, res_len, data2, pdata2_len);
	cout << endl;
	
	p11Test.testDecrypted(data, data_len, data2, data2_len);



	data_len = sizeof(data);
	for (int i = 0; i < (int)data_len; i++){
		data[i] = (CK_BYTE)(6 * i) % 255;
	}

	res_len = sizeof(res);
	data2_len = sizeof(data2);

	cout << "data encryption... " << endl;
	p11Test.encrypt(data, data_len, res, &res_len);
	cout << endl;

	p11Test.writeData(res, res_len);

	data2_len = sizeof(data2);

	CK_BYTE data3[256];
	CK_ULONG data3_len = sizeof(data3);
	CK_ULONG_PTR pdata3_len = &data3_len;

	p11Test.getData(data3, data3_len);

	cout << "data decryption with finding key on token... " << endl;
	p11Test.findPrivAndDecrypt(data3, data3_len, data2, pdata2_len);
	cout << endl;

	p11Test.testDecrypted(data, data_len, data2, data2_len);

	// Stop before end to keep cmd window open 
	getchar();

    return 0;
}

