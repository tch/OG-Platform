/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */

#ifndef __inc_og_language_util_thread_h
#define __inc_og_language_util_thread_h

// Threads using either Win32 or APR

#ifndef _WIN32
#include <apr-1/apr_thread_proc.h>
#include "MemoryPool.h"
#include "Semaphore.h"
#include "Error.h"
#endif

#include "Atomic.h"

class IRunnable {
public:
	IRunnable () { }
	virtual ~IRunnable () { }
	virtual void Run () = 0;
};

class CThread : public IRunnable {
private:
	CAtomicInt m_oRefCount;
#ifdef _WIN32
	HANDLE m_hThread;
	DWORD m_dwThreadId;
	static DWORD WINAPI StartProc (void *pObject);
#else
	apr_thread_t *m_pThread;
	CSemaphore m_oTerminate;
	CMemoryPool m_oPool;
	static void *APR_THREAD_FUNC StartProc (apr_thread_t *handle, void *pObject);
#endif
public:
	CThread () : IRunnable () {
		m_oRefCount.Set (1);
#ifdef _WIN32
		m_hThread = NULL;
#else
		m_pThread = NULL;
#endif
	}
	~CThread () {
		assert (m_oRefCount.Get () == 0);
#ifdef _WIN32
		if (m_hThread) CloseHandle (m_hThread);
#else
		if (m_pThread) apr_thread_detach (m_pThread);
#endif
	}
	void Retain () {
		m_oRefCount.IncrementAndGet ();
	}
	static void Release (CThread *poThread) {
		if (poThread->m_oRefCount.DecrementAndGet () == 0) {
			delete poThread;
		}
	}
	bool Start () { // NOT RE-ENTRANT
#ifdef _WIN32
		assert (!m_hThread);
		Retain ();
		m_hThread = CreateThread (NULL, 0, StartProc, this, 0, &m_dwThreadId);
		if (!m_hThread) {
			Release (this);
			return false;
		}
#else
		assert (!m_pThread);
		apr_threadattr_t *pAttr;
		if (!PosixLastError (apr_threadattr_create (&pAttr, m_oPool))) return false;
		Retain ();
		if (!PosixLastError (apr_thread_create (&m_pThread, pAttr, StartProc, this, m_oPool))) {
			Release (this);
			return false;
		}
#endif
		return true;
	}
	int GetThreadId () {
#ifdef _WIN32
		return m_dwThreadId;
#else
		// TODO
		return 0;
#endif
	}
	bool Wait (unsigned long timeout = 0xFFFFFFFF) { // NOT RE-ENTRANT
#ifdef _WIN32
		switch (WaitForSingleObject (m_hThread, timeout)) {
		case WAIT_ABANDONED :
			SetLastError (ERROR_ABANDONED_WAIT_0);
			return false;
		case WAIT_OBJECT_0 :
			return true;
		case WAIT_TIMEOUT :
			SetLastError (ERROR_TIMEOUT);
			return false;
		default :
			return false;
		}
#else
		if (m_pThread) {
			if (m_oTerminate.Wait (timeout)) {
				// Leave the semaphore signalled
				m_oTerminate.Signal ();
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
#endif
	}
	static bool WaitAndRelease (CThread *pThread, unsigned long timeout = 0xFFFFFFFF) { // NOT RE-ENTRANT
		bool result = pThread->Wait (timeout);
		Release (pThread);
		return result;
	}
};

#endif /* ifndef __inc_og_language_util_thread_h */