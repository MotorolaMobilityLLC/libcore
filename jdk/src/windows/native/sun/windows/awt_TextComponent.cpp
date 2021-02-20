/*
 * Copyright (c) 1996, 2012, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

#include "awt_Toolkit.h"
#include "awt_TextComponent.h"
#include "awt_TextArea.h"
#include "awt_TextField.h"
#include "awt_Canvas.h"

#include "jni.h"
#include "awt_Font.h"


/***********************************************************************/
// struct for _SetText() method
struct SetTextStruct {
    jobject textcomponent;
    jstring text;
};
// struct for _Select() method
struct SelectStruct {
    jobject textcomponent;
    jint start, end;
};
// struct for _EnableEditing() method
struct EnableEditingStruct {
    jobject textcomponent;
    jboolean on;
};
/************************************************************************
 * AwtTextComponent fields
 */

/************************************************************************
 * AwtTextComponent methods
 */

jmethodID AwtTextComponent::canAccessClipboardMID;

AwtTextComponent::AwtTextComponent() {
    m_synthetic = FALSE;
    m_lStartPos = -1;
    m_lEndPos   = -1;
    m_lLastPos  = -1;
    m_isLFonly        = FALSE;
    m_EOLchecked      = FALSE;
//    javaEventsMask = 0;    // accessibility support
}

LPCTSTR AwtTextComponent::GetClassName() {
    static BOOL richedLibraryLoaded = FALSE;
    if (!richedLibraryLoaded) {
        JDK_LoadSystemLibrary("RICHED20.DLL");
        richedLibraryLoaded = TRUE;
    }
    return RICHEDIT_CLASS;
}

/* Create a new AwtTextArea or AwtTextField object and window.   */
AwtTextComponent* AwtTextComponent::Create(jobject peer, jobject parent, BOOL isMultiline)
{
    JNIEnv *env = (JNIEnv *)JNU_GetEnv(jvm, JNI_VERSION_1_2);

    jobject target = NULL;
    AwtTextComponent* c = NULL;

    try {
        if (env->EnsureLocalCapacity(1) < 0) {
            return NULL;
        }

        PDATA pData;
        AwtCanvas* awtParent;
        JNI_CHECK_PEER_GOTO(parent, done);

        awtParent = (AwtCanvas*)pData;
        JNI_CHECK_NULL_GOTO(awtParent, "null awtParent", done);

        target = env->GetObjectField(peer, AwtObject::targetID);
        JNI_CHECK_NULL_GOTO(target, "null target", done);

        if(isMultiline){
            c = new AwtTextArea();
        }else{
            c = new AwtTextField();
        }

        {
            /* Adjust style for scrollbar visibility and word wrap  */
            DWORD scroll_style;

            if(isMultiline){

                 jint scrollbarVisibility =
                     env->GetIntField(target, AwtTextArea::scrollbarVisibilityID);

                 switch (scrollbarVisibility) {
                     case java_awt_TextArea_SCROLLBARS_NONE:
                         scroll_style = ES_AUTOVSCROLL;
                         break;
                     case java_awt_TextArea_SCROLLBARS_VERTICAL_ONLY:
                         scroll_style = WS_VSCROLL | ES_AUTOVSCROLL;
                         break;
                     case java_awt_TextArea_SCROLLBARS_HORIZONTAL_ONLY:
                         scroll_style = WS_HSCROLL | ES_AUTOHSCROLL | ES_AUTOVSCROLL;
                         break;
                     case java_awt_TextArea_SCROLLBARS_BOTH:
                         scroll_style = WS_VSCROLL | WS_HSCROLL |
                             ES_AUTOVSCROLL | ES_AUTOHSCROLL;
                         break;
                }
            }

          DWORD style = WS_CHILD | WS_CLIPSIBLINGS | ES_LEFT;

          /*
           * Specify ES_DISABLENOSCROLL - RichEdit control style to disable
           * scrollbars instead of hiding them when not needed.
           */
          style |= isMultiline ?  ES_MULTILINE | ES_WANTRETURN | scroll_style
              | ES_DISABLENOSCROLL : ES_AUTOHSCROLL;


          DWORD exStyle = WS_EX_CLIENTEDGE;
          if (GetRTL()) {
              exStyle |= WS_EX_RIGHT | WS_EX_LEFTSCROLLBAR;
              if (GetRTLReadingOrder())
                  exStyle |= WS_EX_RTLREADING;
          }


          jint x = env->GetIntField(target, AwtComponent::xID);
          jint y = env->GetIntField(target, AwtComponent::yID);
          jint width = env->GetIntField(target, AwtComponent::widthID);
          jint height = env->GetIntField(target, AwtComponent::heightID);

          c->CreateHWnd(env, L"", style, exStyle,
                        x, y, width, height,
                        awtParent->GetHWnd(),
                        reinterpret_cast<HMENU>(static_cast<INT_PTR>(
                awtParent->CreateControlID())),
                        ::GetSysColor(COLOR_WINDOWTEXT),
                        ::GetSysColor(COLOR_WINDOW),
                        peer);

          // Fix for 4753116.
          // If it is not win95 (we are using Richedit 2.0)
          // we set plain text mode, in which the control is
          // similar to a standard edit control:
          //  - The text in a plain text control can have only
          //    one format.
          //  - The user cannot paste rich text formats, such as RTF
          //    or embedded objects into a plain text control.
          //  - Rich text mode controls always have a default
          //    end-of-document marker or carriage return,
          //    to format paragraphs.
          // kdm@sparc.spb.su
          c->SendMessage(EM_SETTEXTMODE, TM_PLAINTEXT, 0);

          c->m_backgroundColorSet = TRUE;
          /* suppress inheriting parent's color. */
          c->UpdateBackground(env, target);
          c->SendMessage(EM_SETMARGINS, EC_LEFTMARGIN | EC_RIGHTMARGIN,
                         MAKELPARAM(1, 1));
          /*
           * Fix for BugTraq Id 4260109.
           * Set the text limit to the maximum.
           * Use EM_EXLIMITTEXT for RichEdit controls.
           * For some reason RichEdit 1.0 becomes read-only if the
           * specified limit is greater than 0x7FFFFFFD.
           */
          c->SendMessage(EM_EXLIMITTEXT, 0, 0x7FFFFFFD);

          /* Unregister RichEdit built-in drop target. */
          VERIFY(::RevokeDragDrop(c->GetHWnd()) != DRAGDROP_E_INVALIDHWND);

          /* To enforce CF_TEXT format for paste operations. */
          VERIFY(c->SendMessage(EM_SETOLECALLBACK, 0,
                                (LPARAM)&GetOleCallback()));

          c->SendMessage(EM_SETEVENTMASK, 0, ENM_CHANGE);
        }
    } catch (...) {
        env->DeleteLocalRef(target);
        throw;
    }

done:
    env->DeleteLocalRef(target);

    return c;
}

LRESULT
AwtTextComponent::WindowProc(UINT message, WPARAM wParam, LPARAM lParam) {

    switch (message) {
        case WM_PRINTCLIENT:
          {
            FORMATRANGE fr;
            HDC hPrinterDC = (HDC)wParam;
            int nHorizRes = ::GetDeviceCaps(hPrinterDC, HORZRES);
            int nVertRes = ::GetDeviceCaps(hPrinterDC, VERTRES);
            int nLogPixelsX = ::GetDeviceCaps(hPrinterDC, LOGPIXELSX);
            int nLogPixelsY = ::GetDeviceCaps(hPrinterDC, LOGPIXELSY);

            // Ensure the printer DC is in MM_TEXT mode.
            ::SetMapMode ( hPrinterDC, MM_TEXT );

            // Rendering to the same DC we are measuring.
            ::ZeroMemory(&fr, sizeof(fr));
            fr.hdc = fr.hdcTarget = hPrinterDC;
            // Set up the page.
            fr.rcPage.left     = fr.rcPage.top = 0;
            fr.rcPage.right    = (nHorizRes/nLogPixelsX) * 1440; // in twips
            fr.rcPage.bottom   = (nVertRes/nLogPixelsY) * 1440;
            fr.rc.left   = fr.rcPage.left;
            fr.rc.top    = fr.rcPage.top;
            fr.rc.right  = fr.rcPage.right;
            fr.rc.bottom = fr.rcPage.bottom;

            // start printing from the first visible line
            LRESULT nLine = SendMessage(EM_GETFIRSTVISIBLELINE, 0, 0);
            LONG startCh = static_cast<LONG>(SendMessage(EM_LINEINDEX,
                                                         (WPARAM)nLine, 0));
            fr.chrg.cpMin = startCh;
            fr.chrg.cpMax = -1;

            SendMessage(EM_FORMATRANGE, TRUE, (LPARAM)&fr);
          }

        break;
    }

    return AwtComponent::WindowProc(message, wParam, lParam);
}

LONG AwtTextComponent::EditGetCharFromPos(POINT& pt) {
    return static_cast<LONG>(SendMessage(EM_CHARFROMPOS, 0,
            reinterpret_cast<LPARAM>(&pt)));
}

/* Set a suitable font to IME against the component font. */
void AwtTextComponent::SetFont(AwtFont* font)
{
    DASSERT(font != NULL);
    if (font->GetAscent() < 0) {
        AwtFont::SetupAscent(font);
    }

    int index = font->GetInputHFontIndex();
    if (index < 0)
        /* In this case, user cannot get any suitable font for input. */
        index = 0;

    //im --- changed for over the spot composing
    m_hFont = font->GetHFont(index);
    SendMessage(WM_SETFONT, (WPARAM)m_hFont, MAKELPARAM(FALSE, 0));
    SendMessage(EM_SETMARGINS, EC_LEFTMARGIN | EC_RIGHTMARGIN,
                MAKELPARAM(1, 1));

    /*
     * WM_SETFONT reverts foreground color to the default for
     * rich edit controls. So we have to restore it manually.
     */
    SetColor(GetColor());
    VERIFY(::InvalidateRect(GetHWnd(), NULL, TRUE));
    //im --- end

}

int AwtTextComponent::RemoveCR(WCHAR *pStr)
{
    int i, nLen = 0;

    if (pStr) {
        /* check to see if there are any CR's */
        if (wcschr(pStr, L'\r') == NULL) {
            return static_cast<int>(wcslen(pStr));
        }

        for (i=0; pStr[i] != 0; i++) {
            if (m_isLFonly == TRUE) {
                if (pStr[i] == L'\r') {
                    continue;
                }
            } else {
                if (pStr[i] == L'\r' && pStr[i + 1] != L'\n') {
                    continue;
                }
            }
            pStr[nLen++] = pStr[i];
        }
        pStr[nLen] = 0;
    }
    return nLen;
}

MsgRouting
AwtTextComponent::WmNotify(UINT notifyCode)
{
    if (notifyCode == EN_CHANGE) {
      DoCallback("valueChanged", "()V");
    }
    return mrDoDefault;
}

BOOL AwtTextComponent::IsFocusingMouseMessage(MSG *pMsg)
{
    return pMsg->message == WM_LBUTTONDOWN || pMsg->message == WM_LBUTTONDBLCLK;
}

MsgRouting
AwtTextComponent::HandleEvent(MSG *msg, BOOL synthetic)
{
    MsgRouting returnVal;

    /*
     * Store the 'synthetic' parameter so that the WM_PASTE security check
     * happens only for synthetic events.
     */
    m_synthetic = synthetic;
    returnVal = AwtComponent::HandleEvent(msg, synthetic);
    m_synthetic = FALSE;
    return returnVal;
}

/*
 * If this Paste is occuring because of a synthetic Java event (e.g.,
 * a synthesized <CTRL>-V KeyEvent), then verify that the TextComponent
 * has permission to access the Clipboard before pasting. If permission
 * is denied, we should throw a SecurityException, but currently do not
 * because when we detect the security violation, we are in the Toolkit
 * thread, not the thread which dispatched the illegal event.
 */
MsgRouting
AwtTextComponent::WmPaste()
{
    if (m_synthetic) {
        JNIEnv *env = (JNIEnv *)JNU_GetEnv(jvm, JNI_VERSION_1_2);
        if (env->EnsureLocalCapacity(1) < 0) {
            return mrConsume;
        }
        jobject target = GetTarget(env);
        jboolean canAccessClipboard =
            env->CallBooleanMethod (target, AwtTextComponent::canAccessClipboardMID);
        env->DeleteLocalRef(target);
        return (canAccessClipboard) ? mrDoDefault : mrConsume;
    }
    else {
        return mrDoDefault;
    }
}

//im --- override to over the spot composition
void AwtTextComponent::SetCompositionWindow(RECT& rc)
{
    HWND hwnd = ImmGetHWnd();
    HIMC hIMC = ImmGetContext(hwnd);
    // rc is not used for text component.
    COMPOSITIONFORM cf = { CFS_FORCE_POSITION, {0,0}, {0,0,0,0} };
    GetCaretPos(&(cf.ptCurrentPos));
    // the proxy is the native focus owner and it contains the composition window
    // let's convert the position to a coordinate space relative to proxy
    ::MapWindowPoints(GetHWnd(), GetProxyFocusOwner(), (LPPOINT)&cf.ptCurrentPos, 1);
    ImmSetCompositionWindow(hIMC, &cf);

    LOGFONT lf;
    GetObject(m_hFont, sizeof(LOGFONT), &lf);
    ImmSetCompositionFont(hIMC, &lf);
    ImmReleaseContext(hwnd, hIMC);
}
//im --- end

LONG AwtTextComponent::getJavaSelPos(LONG orgPos)
{
    long wlen;
    long pos = 0;
    long cur = 0;
    LPTSTR wbuf;

    if ((wlen = GetTextLength()) == 0)
        return 0;
    wbuf = new TCHAR[wlen + 1];
    GetText(wbuf, wlen + 1);
    if (m_isLFonly == TRUE) {
        wlen = RemoveCR(wbuf);
    }

    while (cur < orgPos && pos++ < wlen) {
        if (wbuf[cur] == _T('\r') && wbuf[cur + 1] == _T('\n')) {
            cur++;
        }
        cur++;
    }
    delete[] wbuf;
    return pos;
}

LONG AwtTextComponent::getWin32SelPos(LONG orgPos)
{
    long wlen;
    long pos = 0;
    long cur = 0;
    LPTSTR wbuf;

    if ((wlen = GetTextLength()) == 0)
       return 0;
    wbuf = new TCHAR[wlen + 1];
    GetText(wbuf, wlen + 1);
    if (m_isLFonly == TRUE) {
        RemoveCR(wbuf);
    }

    while (cur < orgPos && pos < wlen) {
        if (wbuf[pos] == _T('\r') && wbuf[pos + 1] == _T('\n')) {
            pos++;
        }
        pos++;
        cur++;
    }
    delete[] wbuf;
    return pos;
}

void AwtTextComponent::CheckLineSeparator(WCHAR *pStr)
{
    if (pStr == NULL) {
        return;
    }

    if (GetTextLength() == 0) {
        m_EOLchecked = FALSE;
    }

    // check to see if there are any LF's
    if (m_EOLchecked == TRUE || wcschr(pStr, L'\n') == NULL) {
        return;
    }

    for (int i=0; pStr[i] != 0; i++) {
        if (pStr[i] == L'\n') {
            if (i > 0 && pStr[i-1] == L'\r') {
                m_isLFonly = FALSE;
            } else {
                m_isLFonly = TRUE;
            }
            m_EOLchecked = TRUE;
            return;
        }
    }
}

void AwtTextComponent::SetSelRange(LONG start, LONG end)
{
    SendMessage(EM_SETSEL,
                getWin32SelPos(start),
                getWin32SelPos(end));
    // it isn't necessary to wrap this in EM_HIDESELECTION or setting/clearing
    // ES_NOHIDESEL, as regular edit control honors EM_SCROLLCARET even when not in focus
}

jstring AwtTextComponent::_GetText(void *param)
{
    JNIEnv *env = (JNIEnv *)JNU_GetEnv(jvm, JNI_VERSION_1_2);

    jobject self = (jobject)param;

    AwtTextComponent *c = NULL;
    jstring result = NULL;

    PDATA pData;
    JNI_CHECK_PEER_GOTO(self, ret);

    c = (AwtTextComponent *)pData;
    if (::IsWindow(c->GetHWnd()))
    {
        int len = ::GetWindowTextLength(c->GetHWnd());
        if (len == 0) {
            /* Make java null string */
            jchar *jc = new jchar[0];
            result = env->NewString(jc, 0);
            delete [] jc;
        } else {
            WCHAR* buf = new WCHAR[len + 1];
            c->GetText(buf, len + 1);
            c->RemoveCR(buf);
            result = JNU_NewStringPlatform(env, buf);
            delete [] buf;
        }
    }
ret:
    env->DeleteGlobalRef(self);

    if (result != NULL)
    {
        jstring globalRef = (jstring)env->NewGlobalRef(result);
        env->DeleteLocalRef(result);
        return globalRef;
    }
    else
    {
        return NULL;
    }
}

void AwtTextComponent::_SetText(void *param)
{
    JNIEnv *env = (JNIEnv *)JNU_GetEnv(jvm, JNI_VERSION_1_2);

    SetTextStruct *sts = (SetTextStruct *)param;
    jobject self = sts->textcomponent;
    jstring text = sts->text;

    AwtTextComponent *c = NULL;

    PDATA pData;
    JNI_CHECK_PEER_GOTO(self, ret);
    c = (AwtTextComponent *)pData;
    if (::IsWindow(c->GetHWnd()))
    {
        int length = env->GetStringLength(text);
        WCHAR* buffer = new WCHAR[length + 1];
        env->GetStringRegion(text, 0, length, reinterpret_cast<jchar*>(buffer));
        buffer[length] = 0;
        c->CheckLineSeparator(buffer);
        c->RemoveCR(buffer);
        c->SetText(buffer);
        delete[] buffer;
    }
ret:
    env->DeleteGlobalRef(self);
    env->DeleteGlobalRef(text);

    delete sts;
}

jint AwtTextComponent::_GetSelectionStart(void *param)
{
    JNIEnv *env = (JNIEnv *)JNU_GetEnv(jvm, JNI_VERSION_1_2);

    jobject self = (jobject)param;

    jint result = 0;
    AwtTextComponent *c = NULL;

    PDATA pData;
    JNI_CHECK_PEER_GOTO(self, ret);
    c = (AwtTextComponent *)pData;
    if (::IsWindow(c->GetHWnd()))
    {
        long start;
        c->SendMessage(EM_GETSEL, (WPARAM)&start);
        result = c->getJavaSelPos(start);
    }
ret:
    env->DeleteGlobalRef(self);

    return result;
}

jint AwtTextComponent::_GetSelectionEnd(void *param)
{
    JNIEnv *env = (JNIEnv *)JNU_GetEnv(jvm, JNI_VERSION_1_2);

    jobject self = (jobject)param;

    jint result = 0;
    AwtTextComponent *c = NULL;

    PDATA pData;
    JNI_CHECK_PEER_GOTO(self, ret);
    c = (AwtTextComponent *)pData;
    if (::IsWindow(c->GetHWnd()))
    {
        long end;
        c->SendMessage(EM_GETSEL, 0, (LPARAM)&end);
        result = c->getJavaSelPos(end);
    }
ret:
    env->DeleteGlobalRef(self);

    return result;
}

void AwtTextComponent::_Select(void *param)
{
    JNIEnv *env = (JNIEnv *)JNU_GetEnv(jvm, JNI_VERSION_1_2);

    SelectStruct *ss = (SelectStruct *)param;
    jobject self = ss->textcomponent;
    jint start = ss->start;
    jint end = ss->end;

    AwtTextComponent *c = NULL;

    PDATA pData;
    JNI_CHECK_PEER_GOTO(self, ret);
    c = (AwtTextComponent *)pData;
    if (::IsWindow(c->GetHWnd()))
    {
        c->SetSelRange(start, end);
        c->SendMessage(EM_SCROLLCARET);
    }
ret:
    env->DeleteGlobalRef(self);

    delete ss;
}

void AwtTextComponent::_EnableEditing(void *param)
{
    JNIEnv *env = (JNIEnv *)JNU_GetEnv(jvm, JNI_VERSION_1_2);

    EnableEditingStruct *ees = (EnableEditingStruct *)param;
    jobject self = ees->textcomponent;
    jboolean on = ees->on;

    AwtTextComponent *c = NULL;

    PDATA pData;
    JNI_CHECK_PEER_GOTO(self, ret);
    c = (AwtTextComponent *)pData;
    if (::IsWindow(c->GetHWnd()))
    {
        c->SendMessage(EM_SETREADONLY, !on);
    }
ret:
    env->DeleteGlobalRef(self);

    delete ees;
}

/*
 * Disabled edit control has grayed foreground.
 * Disabled RichEdit 1.0 control has original foreground.
 * Thus we have to set grayed foreground manually.
 */
void AwtTextComponent::Enable(BOOL bEnable)
{
    AwtComponent::Enable(bEnable);
    SetColor(GetColor());
}


/*
 * WM_CTLCOLOR is not sent by rich edit controls.
 * Use EM_SETCHARFORMAT and EM_SETBKGNDCOLOR to set
 * respectively foreground and background color.
 */
void AwtTextComponent::SetColor(COLORREF c) {
    AwtComponent::SetColor(c);

    CHARFORMAT cf;
    memset(&cf, 0, sizeof(cf));
    cf.cbSize = sizeof(cf);
    cf.dwMask = CFM_COLOR;

    cf.crTextColor = ::IsWindowEnabled(GetHWnd()) ? GetColor() : ::GetSysColor(COLOR_3DSHADOW);

    /*
     * The documentation for EM_GETCHARFORMAT is not exactly
     * correct. It appears that wParam has the same meaning
     * as for EM_SETCHARFORMAT. Our task is to secure that
     * all the characters in the control have the required
     * formatting. That's why we use SCF_ALL.
     */
    VERIFY(SendMessage(EM_SETCHARFORMAT, SCF_ALL, (LPARAM)&cf));
    VERIFY(SendMessage(EM_SETCHARFORMAT, SCF_DEFAULT, (LPARAM)&cf));
}

/*
 * In responce to EM_SETBKGNDCOLOR rich edit changes
 * its bg color and repaints itself so we don't need
 * to force repaint.
 */
void AwtTextComponent::SetBackgroundColor(COLORREF c) {
    AwtComponent::SetBackgroundColor(c);
    SendMessage(EM_SETBKGNDCOLOR, (WPARAM)FALSE, (LPARAM)GetBackgroundColor());
}


/************************************************************************
 * WTextComponentPeer native methods
 */

extern "C" {

/*
 * Class:     sun_awt_windows_WTextComponentPeer
 * Method:    getText
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL
Java_sun_awt_windows_WTextComponentPeer_getText(JNIEnv *env, jobject self)
{
    TRY;

    jobject selfGlobalRef = env->NewGlobalRef(self);

    jstring globalRef = (jstring)AwtToolkit::GetInstance().SyncCall(
        (void*(*)(void*))AwtTextComponent::_GetText,
        (void *)selfGlobalRef);
    // selfGlobalRef is deleted in _GetText
    if (globalRef != NULL)
    {
        jstring localRef = (jstring)env->NewLocalRef(globalRef);
        env->DeleteGlobalRef(globalRef);
        return localRef;
    }
    else
    {
        return NULL;
    }

    CATCH_BAD_ALLOC_RET(NULL);
}

/*
 * Class:     sun_awt_windows_WTextComponentPeer
 * Method:    setText
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL
Java_sun_awt_windows_WTextComponentPeer_setText(JNIEnv *env, jobject self,
                                                jstring text)
{
    TRY;

    SetTextStruct *sts = new SetTextStruct;
    sts->textcomponent = env->NewGlobalRef(self);
    sts->text = (jstring)env->NewGlobalRef(text);

    AwtToolkit::GetInstance().SyncCall(AwtTextComponent::_SetText, sts);
    // global refs and sts are deleted in _SetText

    CATCH_BAD_ALLOC;
}

/*
 * Class:     sun_awt_windows_WTextComponentPeer
 * Method:    getSelectionStart
 * Signature: ()I
 */
JNIEXPORT jint JNICALL
Java_sun_awt_windows_WTextComponentPeer_getSelectionStart(JNIEnv *env,
                                                          jobject self)
{
    TRY;

    return static_cast<jint>(reinterpret_cast<INT_PTR>(AwtToolkit::GetInstance().SyncCall(
        (void *(*)(void *))AwtTextComponent::_GetSelectionStart,
        env->NewGlobalRef(self))));
    // global ref is deleted in _GetSelectionStart()

    CATCH_BAD_ALLOC_RET(0);
}

/*
 * Class:     sun_awt_windows_WTextComponentPeer
 * Method:    getSelectionEnd
 * Signature: ()I
 */
JNIEXPORT jint JNICALL
Java_sun_awt_windows_WTextComponentPeer_getSelectionEnd(JNIEnv *env,
                                                        jobject self)
{
    TRY;

    return static_cast<jint>(reinterpret_cast<INT_PTR>(AwtToolkit::GetInstance().SyncCall(
        (void *(*)(void *))AwtTextComponent::_GetSelectionEnd,
        env->NewGlobalRef(self))));
    // global ref is deleted in _GetSelectionEnd()

    CATCH_BAD_ALLOC_RET(0);
}

/*
 * Class:     sun_awt_windows_WTextComponentPeer
 * Method:    select
 * Signature: (II)V
 */
JNIEXPORT void JNICALL
Java_sun_awt_windows_WTextComponentPeer_select(JNIEnv *env, jobject self,
                                               jint start, jint end)
{
    TRY;

    SelectStruct *ss = new SelectStruct;
    ss->textcomponent = env->NewGlobalRef(self);
    ss->start = start;
    ss->end = end;

    AwtToolkit::GetInstance().SyncCall(AwtTextComponent::_Select, ss);
    // global ref and ss are deleted in _Select

    CATCH_BAD_ALLOC;
}

/*
 * Class:     sun_awt_windows_WTextComponentPeer
 * Method:    enableEditing
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL
Java_sun_awt_windows_WTextComponentPeer_enableEditing(JNIEnv *env,
                                                      jobject self,
                                                      jboolean on)
{
    TRY;

    EnableEditingStruct *ees = new EnableEditingStruct;
    ees->textcomponent = env->NewGlobalRef(self);
    ees->on = on;

    AwtToolkit::GetInstance().SyncCall(AwtTextComponent::_EnableEditing, ees);
    // global ref and ees are deleted in _EnableEditing()

    CATCH_BAD_ALLOC;
}

/*
 * Class:     sun_awt_windows_WTextComponentPeer
 * Method:    initIDs
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_sun_awt_windows_WTextComponentPeer_initIDs(JNIEnv *env, jclass cls)
{
    TRY;

    jclass textComponentClassID = env->FindClass("java/awt/TextComponent");
    AwtTextComponent::canAccessClipboardMID =
        env->GetMethodID(textComponentClassID,
        "canAccessClipboard", "()Z");
    env->DeleteLocalRef(textComponentClassID);

    DASSERT(AwtTextComponent::canAccessClipboardMID != NULL)

    CATCH_BAD_ALLOC;
}


AwtTextComponent::OleCallback AwtTextComponent::sm_oleCallback;

/************************************************************************
 * Inner class OleCallback definition.
 */

AwtTextComponent::OleCallback::OleCallback() {
    m_refs = 0;
    AddRef();
}

STDMETHODIMP
AwtTextComponent::OleCallback::QueryInterface(REFIID riid, LPVOID * ppvObj) {
     if (::IsEqualIID(riid, IID_IUnknown) ||::IsEqualIID(riid, IID_IRichEditOleCallback)  ) {
         *ppvObj = static_cast<IRichEditOleCallback*>(this);
         AddRef();
         return S_OK;
     }
     *ppvObj = NULL;
     return E_NOINTERFACE;
}


STDMETHODIMP_(ULONG)
AwtTextComponent::OleCallback::AddRef() {
    return ++m_refs;
}

STDMETHODIMP_(ULONG)
AwtTextComponent::OleCallback::Release() {
    return (ULONG)--m_refs;
}

STDMETHODIMP
AwtTextComponent::OleCallback::GetNewStorage(LPSTORAGE FAR * ppstg) {
    return E_NOTIMPL;
}

STDMETHODIMP
AwtTextComponent::OleCallback::GetInPlaceContext(LPOLEINPLACEFRAME FAR * ppipframe,
                                                 LPOLEINPLACEUIWINDOW FAR* ppipuiDoc,
                                                 LPOLEINPLACEFRAMEINFO pipfinfo)
{
    return E_NOTIMPL;
}

STDMETHODIMP
AwtTextComponent::OleCallback::ShowContainerUI(BOOL fShow) {
    return E_NOTIMPL;
}

STDMETHODIMP
AwtTextComponent::OleCallback::QueryInsertObject(LPCLSID pclsid,
                                                 LPSTORAGE pstg,
                                                 LONG cp) {
    return S_OK;
}

STDMETHODIMP
AwtTextComponent::OleCallback::DeleteObject(LPOLEOBJECT poleobj) {
    return S_OK;
}

STDMETHODIMP
AwtTextComponent::OleCallback::QueryAcceptData(LPDATAOBJECT pdataobj,
                                               CLIPFORMAT *pcfFormat,
                                               DWORD reco,
                                               BOOL fReally,
                                               HGLOBAL hMetaPict) {
    if (reco == RECO_PASTE) {
        // If CF_TEXT format is available edit controls will select it,
        // otherwise if it is CF_UNICODETEXT is available it will be
        // selected, otherwise if CF_OEMTEXT is available it will be selected.
        if (::IsClipboardFormatAvailable(CF_TEXT)) {
            *pcfFormat = CF_TEXT;
        } else if (::IsClipboardFormatAvailable(CF_UNICODETEXT)) {
            *pcfFormat = CF_UNICODETEXT;
        } else if (::IsClipboardFormatAvailable(CF_OEMTEXT)) {
            *pcfFormat = CF_OEMTEXT;
        } else {
            // Don't allow rich edit to paste clipboard data
            // in other formats.
            *pcfFormat = CF_TEXT;
        }
    }

    return S_OK;
}

STDMETHODIMP
AwtTextComponent::OleCallback::ContextSensitiveHelp(BOOL fEnterMode) {
    return S_OK;
}

STDMETHODIMP
AwtTextComponent::OleCallback::GetClipboardData(CHARRANGE *pchrg,
                                                DWORD reco,
                                                LPDATAOBJECT *ppdataobj) {
    return E_NOTIMPL;
}

STDMETHODIMP
AwtTextComponent::OleCallback::GetDragDropEffect(BOOL fDrag,
                                                 DWORD grfKeyState,
                                                 LPDWORD pdwEffect) {

    return E_NOTIMPL;
}


STDMETHODIMP
AwtTextComponent::OleCallback::GetContextMenu(WORD seltype,
                                              LPOLEOBJECT lpoleobj,
                                              CHARRANGE FAR * lpchrg,
                                              HMENU FAR * lphmenu) {
    return E_NOTIMPL;
}



//
// Accessibility support
//

/* To be fully implemented in a future release
 *
 * Class:     sun_awt_windows_WTextComponentPeer
 * Method:    getIndexAtPoint
 * Signature: (II)I
 *
JNIEXPORT jlong JNICALL
Java_sun_awt_windows_WTextComponentPeer_filterEvents(JNIEnv *env, jobject self, jlong mask)
{
    TRY;

    PDATA pData;
    JNI_CHECK_PEER_RETURN_NULL(self);
    AwtTextComponent* c = (AwtTextComponent*)pData;

    jlong oldMask = c->javaEventsMask;
    c->javaEventsMask = mask;

    return oldMask;

    CATCH_BAD_ALLOC_RET(0);
}
*/

// [[[FIXME]]] need to switch to rich edit field; look for EN_SELCHANGE event instead
/*
 * Handle WmKeyDown to catch keystrokes which may move the caret,
 * and fire events as appropriate when that happens, if they are wanted
 *
 * Note: mouse clicks come through WmKeyDown as well (do they??!?!)
 *
MsgRouting AwtTextComponent::WmKeyDown(UINT wkey, UINT repCnt,
                                   UINT flags, BOOL system) {

    printf("AwtTextComponent::WmKeyDown called\r\n");


    // NOTE: WmKeyDown won't be processed 'till well after we return
    //       so we need to modify the values based on the keystroke
    //
    static long oldStart = -1;
    static long oldEnd = -1;

    // most keystrokes can move the caret
    // so we'll simply check to see if the caret has moved!
    if (javaEventsMask & (jlong) java_awt_TextComponent_textSelectionMask) {
        long start;
        long end;
        SendMessage(EM_GETSEL, (WPARAM)&start, (LPARAM)&end);
        if (start != oldStart || end != oldEnd) {

            printf("  -> calling TextComponent.selectionValuesChanged()\r\n");
            printf("  -> old = (%d, %d); new = (%d, %d)\r\n",
                    oldStart, oldEnd, start, end);

            DoCallback("selectionValuesChanged", "(II)V", start, end); // let Java-side track details...
            oldStart = start;
            oldEnd = end;
        }
    }

    return AwtComponent::WmKeyDown(wkey, repCnt, flags, system);
}
*/

/* To be fully implemented in a future release
 *
 * Class:     sun_awt_windows_WTextComponentPeer
 * Method:    getIndexAtPoint
 * Signature: (II)I
 *
JNIEXPORT jint JNICALL
Java_sun_awt_windows_WTextComponentPeer_getIndexAtPoint(JNIEnv *env, jobject self, jint x, jint y)
{
    TRY;

    PDATA pData;
//    JNI_CHECK_PEER_RETURN_VAL(self, -1);   [[[FIXME]]] Peter Korn -> should return -1 here
    JNI_CHECK_PEER_RETURN_NULL(self);
    AwtTextComponent* c = (AwtTextComponent*)pData;
    int indicies = c->SendMessage(EM_CHARFROMPOS, (WPARAM) 0, (LPARAM) MAKELPARAM(x, y));
    int index = LOWORD(indicies);   // index into the line the (x,y) coord is on
    int lineIndex = c->SendMessage(EM_LINEINDEX, HIWORD(indicies));  // index of start of line
    return (index + lineIndex);

    CATCH_BAD_ALLOC_RET(-1);
}
*/

/* To be fully implemented in a future release
 *
 * Class:     sun_awt_windows_WTextComponentPeer
 * Method:    getCharacterBounds
 * Signature: (I)Ljava/awt/Rectangle;
 *
JNIEXPORT jobject JNICALL
Java_sun_awt_windows_WTextComponentPeer_getCharacterBounds(JNIEnv *env, jobject self, jint i)
{

    //  loop through lines with EM_LINELENGTH?  e.g.:
    //     line = 0; ttl = 0;   // index is passed in as 'i' above
    //     while (ttl < index) {
    //        ttl += SendMessage(EM_LINELENGTH, line++);
    //     }
    //     line-- (decrement back again)
    //  alternately, we could use EM_LINEINDEX to the same effect; perhaps slightly cleaner:
    //     computedIndex = 0; line = 0;
    //     while (computedIndex < index) {
    //        computedIndex = SendMessage(EM_LINEINDEX, 1 + line++);
    //     }
    //     line--;

    // EM_POSFROMCHAR  - convert char index into a Point
    // wParam = (LPPOINT) lpPoint;        // address of structure
                                                  // receiving character position
    // lParam = (LPARAM) wCharIndex;      // zero-based index of character
    //
    // still need to turn the above into a Rect somehow...
    // (use font metrics on font info for letter to get height?  use
    // getLineHeight type of message?).

    // WM_GETFONT - get the font struct for the window control
    // wParam = lParam = 0
    // returns an HFONT
    //  -or-
    // GetTextMetrics(hDC) to get the text info for the font selected
    // into the hDC of the control (tmHeight is what we want in the
    // TEXTMETRIC struct).
    // also GetCharWidth32() with the char at the index in question to get
    // the width of that char
    // *** Can't use GetTextMetrics/GetCharWidth32, as we don't have an hDC!! ***

    TRY;

    PDATA pData;
    JNI_CHECK_PEER_RETURN_NULL(self);
    AwtComponent* c = (AwtComponent*)pData;
/*
    int line = 0;
    int lineIndex = 0;
    while (lineIndex < i) {
        lineIndex = c->SendMessage(EM_LINEINDEX, 1 + line++);
    }
    line--;     // line is now the line which contains our character at position 'i'
    int offsetIndex = i - lineIndex;    // offsetIndex is now distance in on the line
* /
    POINT p;

    c->SendMessage(EM_POSFROMCHAR, (WPARAM) &p, (LPARAM) i);    // x coord is meaningful; y may not be

    // need to calculate charWidth, charHeight, and set p.y to something meangful

    jint charWidth;
    jint charHeight;

/*
    HFONT font = c->SendMessage(WM_GETFONT);
    if (GetCharWidth32(c->hdc, i, i, &charWidth) != 0) {        // [[[FIXME]]] need to get hDC!

        JNIEnv *env = (JNIEnv *)JNU_GetEnv(jvm, JNI_VERSION_1_2);
        jobject rect = JNU_NewObjectByName(env, "java/awt/Rectangle", "(IIII)V",
                                           (jint) p.x, (jint) p.y, charWidth, charHeight);

        return rect;
    }
* /
    return (jobject) 0;

    CATCH_BAD_ALLOC_RET(0);
}
*/

} /* extern "C" */
