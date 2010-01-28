#!/usr/bin/env python
# -*- coding: utf-8 -*-

from distutils.core import setup

setup(
    name = "asbt",
    version = "0.9.0",
    author = "Alec Schueler",
    author_email = "johannalecschueler@googlemail.com",
    url = "", #XXX
    license = "New BSD License",
    data_files = [("man.1", ["asbt.1"])],
    scripts = ["asbt"],
    classifiers = [
        "Development Status :: Beta",
        "Intended Audience :: End Users/Desktop",
        "Intended Audience :: Developers",
        "Intended Audience :: System Administrators",
        "Enviroment :: Console",
        "Operating System :: POSIX",
        "Programming Language :: Python",
        "Topic :: Software Development :: Bug Tracking",
        ]
)
