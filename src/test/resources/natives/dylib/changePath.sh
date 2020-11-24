#!/bin/bash
	install_name_tool -change /usr/local/lib/libSystemTranform.dylib  @loader_path/libSystemTransform.dylib "libHCNetSDK.dylib"
	install_name_tool -change @loader_path/libPlayCtrl.dylib  @loader_path/libPlayCtrl.dylib "libHCNetSDK.dylib"
    install_name_tool -change /usr/local/lib/libNPQos.dylib  @loader_path/libNPQos.dylib "libHCNetSDK.dylib"

