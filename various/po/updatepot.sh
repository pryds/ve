#!/bin/bash

VERSION=$(grep "android:versionName" ../../AndroidManifest.xml | sed "s/.*android.versionName=\"\(.*\)\".*/\1/")

rm ve.pot
a2po export - --template ve1.pot

sed -e 's/^# Translations template for PROJECT.$/# Translations template for Vé./' -e 's/^# Copyright (C) 2014 ORGANIZATION$/# Copyright (C) 2014 Thomas Pryds/' -e 's/^# This file is distributed under the same license as the PROJECT project.$/# This file is distributed under the same license as the Vé project./' -e 's/^"Project-Id-Version: PROJECT VERSION\\n"$/"Project-Id-Version: Vé '"$VERSION"'\\n"/' -e 's_^"Report-Msgid-Bugs-To: EMAIL@ADDRESS\\n"$_"Report-Msgid-Bugs-To: https://github.com/pryds/ve/issues\\n"_' ve1.pot > ve.pot

rm ve1.pot

