#!/bin/bash

rm ve.pot
a2po export
mv -v template.pot ve.pot

