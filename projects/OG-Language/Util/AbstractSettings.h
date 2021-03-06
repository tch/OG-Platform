/**
 * Copyright (C) 2010 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */

#ifndef __inc_og_language_util_abstractsettings_h
#define __inc_og_language_util_abstractsettings_h

// Runtime configuration options

#include "Mutex.h"
#include "Unicode.h"

struct _setting {
	TCHAR * pszKey;
	TCHAR * pszValue;
	struct _setting *pNext;
};

class CAbstractSettingProvider {
private:
	static CMutex s_oMutex;
	bool m_bCalculated;
	TCHAR *m_pszValue;
protected:
	virtual TCHAR *CalculateString () = 0;
public:
	CAbstractSettingProvider ();
	~CAbstractSettingProvider ();
	const TCHAR *GetString ();
};

class CAbstractSettings {
private:
#ifdef _WIN32
	HKEY m_hkeyLocal;
	HKEY m_hkeyGlobal;
#endif
	struct _setting *m_pCache;
protected:
	const TCHAR *CacheGet (const TCHAR *pszKey);
	const TCHAR *CachePut (const TCHAR *pszKey, const TCHAR *pszValue);
#ifdef _WIN32
	PCTSTR RegistryGet (HKEY hKey, PCTSTR pszKey);
#endif
	const TCHAR *Get (const TCHAR *pszKey);
	const TCHAR *Get (const TCHAR *pszKey, const TCHAR *pszDefault);
	const TCHAR *Get (const TCHAR *pszKey, CAbstractSettingProvider *poDefault);
	int Get (const TCHAR *pszKey, int nDefault);
public:
	CAbstractSettings ();
	~CAbstractSettings ();
	static bool GetSettingsLocation (TCHAR *pszBuffer, size_t cbBufferLen);
	virtual const TCHAR *GetLogConfiguration () = 0;
};

#endif /* ifndef __inc_og_language_util_abstractsettings_h */
