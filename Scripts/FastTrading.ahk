^*t::
Sleep 500
Loop {
	if GetKeyState("t", "P") {
		Send {Blind}{LCtrl up}
		Send {Blind}{LShift up}

		return
	}
	if !GetKeyState("LCtrl") {
		Send {LCtrl down}
	}
	if !GetKeyState("LShift") {
		Send {LShift down}
	}

	Send {LAlt down}
	Sleep 2
	Send {Blind}{LAlt up}
	Sleep 2

	Send {Space down}
	Sleep 2
	Send {Blind}{Space up}
	Sleep 2
}