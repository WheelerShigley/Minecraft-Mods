package me.wheelershigley.unlimited_anvil;

public enum AnvilMode {
    Disabled,  //Invalid Inputs
    Enchant,   //valid primary-input with EnchantedBook
    Combine,   //both inputs are of the same type
    Repair,    //secondary input is a valid repair item of the first input
    RenameOnly //only primary input with new name
}
