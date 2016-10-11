#pragma once
#include "stdafx.h"
#include <list>
#include <cassert>
#include <iostream>
#include <fstream>
#include "pkcs11stub.h"

using namespace std;

#define PKCS11_DLL "softhsm2.dll"

#define MAX_READER_NAME_LENGTH  256
#define TESTING_TOKEN_LABEL		 "MyToken 1"
typedef unsigned long ULONG;
//typedef unsigned int DWORD;
typedef char TCHAR;
typedef void* CK_VOID_PTR;

#define TS_ASSERT(x) assert(x)
#define TS_FAIL(x) { cout << x; assert(false); }
//#define _T(x) x


typedef struct _ITEM_DATA_PKCS11READER {
	ULONG               slotID;
	ULONG               hSession;
	char                readerName[MAX_READER_NAME_LENGTH];
	bool                bCardPresent;
	char                cardATR[MAX_READER_NAME_LENGTH];

	_ITEM_DATA_PKCS11READER() {
		clear();
	}

	void clear() {
		slotID = 0;
		hSession = NULL;
		memset(readerName, 0, sizeof(readerName));
		bCardPresent = false;
		memset(cardATR, 0, sizeof(cardATR));
	}
} ITEM_DATA_PKCS11READER;

typedef list<ITEM_DATA_PKCS11READER> lp11r;

CK_SESSION_HANDLE		mhSession;
CK_RV rv;

CK_OBJECT_HANDLE testKey1, testKey2;

CK_MECHANISM mechanism_cd;

CK_BYTE_PTR pModulus, pExponent;
CK_ULONG ulModulus, ulExponent;


class PKCS11Test {
	CPKCS11Stub m_pkcs11Mngr;


	int getTestTokenSession(CK_SLOT_ID* pSlotID, CK_SLOT_INFO* pSlotInfo, CK_TOKEN_INFO* pTokenInfo) {
		int             status = 0;
		CK_SLOT_ID      pkcs11Slots[100];
		DWORD           pkcs11SlotsCount = 100;
		ITEM_DATA_PKCS11READER  readerInfo;
		m_pkcs11Mngr.Init(PKCS11_DLL);

		TS_ASSERT(m_pkcs11Mngr.C_GetSlotList(FALSE, pkcs11Slots, &pkcs11SlotsCount) == CKR_OK);
		TS_ASSERT(pkcs11SlotsCount > 0);
		bool	bTestTokenFound = FALSE;
		for (DWORD i = 0; i < pkcs11SlotsCount; i++) {
			TS_ASSERT(m_pkcs11Mngr.C_GetSlotInfo(pkcs11Slots[i], pSlotInfo) == CKR_OK);
			TS_ASSERT(m_pkcs11Mngr.C_GetTokenInfo(pkcs11Slots[i], pTokenInfo) == CKR_OK);

			// IF MORE THAN 3 SPACES DETECTED, THAN CUT READER NAME
			TCHAR* pos = 0;
			if ((pos = strstr((TCHAR*)pTokenInfo->label, _T("   "))) != NULL) {
				memset(pTokenInfo->label + (pos - (TCHAR*)pTokenInfo->label) * sizeof(TCHAR), 0, sizeof(TCHAR));
			}

			// Do only when testing token was found
			if (strcmp((const char*)pTokenInfo->label, TESTING_TOKEN_LABEL) == 0) {
				bTestTokenFound = true;
				*pSlotID = pkcs11Slots[i];
				break;
			}
		}

		if (!bTestTokenFound){
			status = CKR_TOKEN_NOT_PRESENT;
		}
		else{
			status = CKR_OK;
		}

		return status;
	}

	int getFirstTokenSession(CK_SLOT_ID* pSlotID, CK_SLOT_INFO* pSlotInfo, CK_TOKEN_INFO* pTokenInfo) {
		int             status = 0;
		CK_SLOT_ID      pkcs11Slots[100];
		DWORD           pkcs11SlotsCount = 100;
		ITEM_DATA_PKCS11READER  readerInfo;
		m_pkcs11Mngr.Init(PKCS11_DLL);

		TS_ASSERT(m_pkcs11Mngr.C_GetSlotList(FALSE, pkcs11Slots, &pkcs11SlotsCount) == CKR_OK);
		TS_ASSERT(pkcs11SlotsCount > 0);
		TS_ASSERT(m_pkcs11Mngr.C_GetSlotInfo(pkcs11Slots[0], pSlotInfo) == CKR_OK);
		TS_ASSERT(m_pkcs11Mngr.C_GetTokenInfo(pkcs11Slots[0], pTokenInfo) == CKR_OK);

		// IF MORE THAN 3 SPACES DETECTED, THAN CUT READER NAME
		TCHAR* pos = 0;
		if ((pos = strstr((TCHAR*)pTokenInfo->label, _T("   "))) != NULL) {
			memset(pTokenInfo->label + (pos - (TCHAR*)pTokenInfo->label) * sizeof(TCHAR), 0, sizeof(TCHAR));
		}
		*pSlotID = pkcs11Slots[0];

		return CKR_OK;
	}


public:

	void initWIthSingleToken(){
		m_pkcs11Mngr.Init(PKCS11_DLL);
		CK_SLOT_ID				slotId = CK_UNAVAILABLE_INFORMATION;
		CK_SLOT_INFO			slotInfo;
		CK_TOKEN_INFO			tokenInfo;
		CK_BYTE					tokenName[sizeof(TESTING_TOKEN_LABEL)] = TESTING_TOKEN_LABEL;


		TS_ASSERT(getFirstTokenSession(&slotId, &slotInfo, &tokenInfo) == CKR_OK);
		TS_ASSERT(m_pkcs11Mngr.C_OpenSession(slotId, CKF_SERIAL_SESSION, NULL_PTR, NULL_PTR, &mhSession) == CKR_OK);

		rv = m_pkcs11Mngr.C_Login(mhSession, CKU_USER, (CK_CHAR*)"1234", 4);
		if (rv != 0){
			cout << rv << endl;
		}

		mechanism_cd = { CKM_RSA_PKCS, NULL_PTR, 0 };

	}

	void logout(){
		rv = m_pkcs11Mngr.C_Logout(mhSession);
		if (rv != 0){
			cout << rv << endl;
		}
		else{
			cout << "Success" << endl;
		}
	}

	void login(){
		rv = m_pkcs11Mngr.C_Login(mhSession, CKU_USER, (CK_CHAR*)"1234", 4);
		if (rv != 0){
			cout << rv << endl;
		}
		else{
			cout << "Success" << endl;
		}
	}

	void generateKeyPair(){

		boolean tmp = false;

		static CK_ULONG  ck_bits = 2048;
		static CK_BBOOL  ck_true = TRUE;
		static CK_BBOOL  ck_false = FALSE;
		static CK_BYTE   pub_exp[] = { 0x01, 0x00, 0x01 };
        
		CK_MECHANISM mechanism = { CKM_RSA_PKCS_KEY_PAIR_GEN, NULL_PTR, 0 };

		CK_ATTRIBUTE publicKeyTemplate[] = {
			{ CKA_MODULUS_BITS, &ck_bits, sizeof(ck_bits) },
			{ CKA_ENCRYPT, &ck_true, sizeof(ck_true) },
			{ CKA_VERIFY, &ck_true, sizeof(ck_true) },
			{ CKA_PUBLIC_EXPONENT, &pub_exp, sizeof(pub_exp) },
			{ CKA_EXTRACTABLE, &ck_true, sizeof(ck_true) }
		};

		CK_ATTRIBUTE privateKeyTemplate[] = {
			{ CKA_DECRYPT, &ck_true, sizeof(ck_true)},
			{ CKA_SENSITIVE, &ck_true, sizeof(ck_true) },
			{ CKA_TOKEN, &ck_false, sizeof(ck_false) }
		};

		CK_ULONG             ulPublicKeyAttributeCount = 5; //Number of attributes set for the publicKeyTemplate
		CK_ULONG             ulPrivateKeyAttributeCount = 3; //Number of attributes set for the privateKeyTemplate


		CK_OBJECT_HANDLE	 hPublicKey;
		CK_OBJECT_HANDLE	 hPrivateKey;
		rv = m_pkcs11Mngr.C_GenerateKeyPair(mhSession, &mechanism, publicKeyTemplate, ulPublicKeyAttributeCount, privateKeyTemplate, ulPrivateKeyAttributeCount, &hPublicKey, &hPrivateKey);
		std::cout << "object handle public key: " << hPublicKey << std::endl;
		std::cout << "object handle private key: " << hPrivateKey << std::endl;



		CK_ATTRIBUTE tmplate[] = {
			{ CKA_MODULUS, NULL_PTR, 0 },
			{ CKA_PUBLIC_EXPONENT, NULL_PTR, 0 }
		};

		CK_BYTE_PTR pModulus;
		CK_BYTE_PTR pExponent;

		rv = m_pkcs11Mngr.C_GetAttributeValue(mhSession, hPrivateKey, tmplate, 2);
		if (rv != 0){
			cout << rv << endl;
			tmp = true;
		}
		

		pModulus = (CK_BYTE_PTR)malloc(tmplate[0].ulValueLen);
		tmplate[0].pValue = pModulus;
		pExponent = (CK_BYTE_PTR)malloc(tmplate[1].ulValueLen);
		tmplate[1].pValue = pExponent;

		rv = m_pkcs11Mngr.C_GetAttributeValue(mhSession, hPrivateKey, tmplate, 2);
		if (rv != 0){
			cout << rv << endl;
			tmp = true;
		}

		ulModulus = tmplate[0].ulValueLen;
		ulExponent = tmplate[1].ulValueLen;
		
		for (int i = 0; i < ulModulus; i++){
			cout << hex << (int) pModulus[i] << " ";
		}
		cout << endl;
		cout << endl;

		for (int i = 0; i < ulExponent; i++){
			cout << hex << (int)pExponent[i] << " ";
		}
		cout << endl;
		

		testKey1 = hPublicKey;
		testKey2 = hPrivateKey;


		if (!tmp){
			cout << "success" << endl;
		}
		else{
			cout << "failure" << endl;
		}

	};

	void doTests(){

		boolean tmp = false;

		CK_ATTRIBUTE tmplate[] = {
			{ CKA_MODULUS, NULL_PTR, 0 },
			{ CKA_PUBLIC_EXPONENT, NULL_PTR, 0 }
		};

		rv = m_pkcs11Mngr.C_GetAttributeValue(mhSession, testKey1, tmplate, 2);
		if (rv != 0){
			cout << rv << endl;
			tmp = true;
		}

		pModulus = (CK_BYTE_PTR)malloc(tmplate[0].ulValueLen);
		tmplate[0].pValue = pModulus;
		pExponent = (CK_BYTE_PTR)malloc(tmplate[1].ulValueLen);
		tmplate[1].pValue = pExponent;

		rv = m_pkcs11Mngr.C_GetAttributeValue(mhSession, testKey1, tmplate, 2);
		if (rv != 0){
			cout << rv << endl;
			tmp = true;
		}

		if (!tmp){
			cout << "success" << endl;
		}
		else{
			cout << "failure" << endl;
		}

	}


	void encrypt(CK_BYTE_PTR data, CK_ULONG data_len, CK_BYTE_PTR res, CK_ULONG_PTR res_len){
		boolean tmp = false;

		rv = m_pkcs11Mngr.C_EncryptInit(mhSession, &mechanism_cd, testKey1);
		if (rv != 0){
			cout << rv << endl;
			tmp = true;
		}
		rv = m_pkcs11Mngr.C_Encrypt(mhSession, data, data_len, res, res_len);
		if (rv != 0){
			cout << hex << (int)rv << endl;
			tmp = true;
		}
		
		if (!tmp){
			cout << "success" << endl;
		}
		else{
			cout << "failure" << endl;
		}
	}




	void decrypt(CK_BYTE_PTR encData, CK_ULONG ulEncData, CK_BYTE_PTR res, CK_ULONG_PTR res_len){

		boolean tmp = false;

		rv = m_pkcs11Mngr.C_DecryptInit(mhSession, &mechanism_cd, testKey2);
		if (rv != 0){
			cout << rv << endl;
			tmp = true;
		}

		rv = m_pkcs11Mngr.C_Decrypt(mhSession, encData, ulEncData, res, res_len);
		if (rv != 0){
			cout << rv << endl;
			tmp = true;
		}

		if (!tmp){
			cout << "success" << endl;
		}
		else{
			cout << "failure" << endl;
		}

	}

	void testDecrypted(CK_BYTE_PTR data, CK_ULONG data_len, CK_BYTE_PTR data2, CK_ULONG data2_len){

		boolean decError = false;
		if (data2_len == data_len){
			for (int i = 0; i < data_len; i++){
				if (data[i] != data2[i]){
					decError = true;
					break;
				}
			}
		}
		else{
			decError = true;
		}

		if (decError){
			cout << "An error as occured" << endl;
		}
		else{
			cout << "Encrypt/Decrypt successful" << endl;
		}

	}

	void findPrivAndDecrypt(CK_BYTE_PTR encData, CK_ULONG ulEncData, CK_BYTE_PTR res, CK_ULONG_PTR res_len){
		CK_OBJECT_CLASS keyClass = CKO_PRIVATE_KEY;
		CK_ATTRIBUTE tmplate[] = {
			{ CKA_CLASS, &keyClass, sizeof(keyClass) }
		};

		CK_ULONG objectCount;
		CK_OBJECT_HANDLE object;

		rv = m_pkcs11Mngr.C_FindObjectsInit(mhSession, tmplate, 1);
		if (rv != 0){
			cout << "Find Obj init:" << hex << rv << endl;
		}

		rv = m_pkcs11Mngr.C_FindObjects(mhSession, &object, 1, &objectCount);
		if (rv != 0){
			cout << "Find Obj:" << hex << rv << endl;
		}

		boolean tmp = false;

		rv = m_pkcs11Mngr.C_DecryptInit(mhSession, &mechanism_cd, object);
		if (rv != 0){
			cout << hex << rv << endl;
			tmp = true;
		}

		rv = m_pkcs11Mngr.C_Decrypt(mhSession, encData, ulEncData, res, res_len);
		if (rv != 0){
			cout << hex << rv << endl;
			tmp = true;
		}

		if (!tmp){
			cout << "success" << endl;
		}
		else{
			cout << "failure" << endl;
		}
	}

	void writeData(CK_BYTE_PTR data, CK_ULONG len){
		ofstream filePubKey("storePublicKey", ios::out | ios::trunc | ios::binary);

		if (filePubKey){

			for(int i = 0; i < len; i++){
				filePubKey << (int) data[i] << " ";
			}
			
			filePubKey.close();
		}
	}

	void getData(CK_BYTE_PTR data, CK_ULONG len){
		ifstream filePubKey("storePublicKey", ios::in | ios::binary);

		int tmp;
		if (filePubKey){
			for (int i = 0; i < len; i++){
				filePubKey >> tmp;
				data[i] = (CK_BYTE) tmp;
			}
			filePubKey.close();
		}
	}

	
};

