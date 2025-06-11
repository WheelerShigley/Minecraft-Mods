^LButton::
	While GetKeyState("LButton","P") {
		Send {LButton}
		Sleep 5
	}
Return

^RButton::
	While GetKeyState("RButton","P") {
		Send {RButton}
		Sleep 5
	}
Return