#!/usr/bin/env python

from __future__ import with_statement

import optparse
import os
import sys


class sbt_tracker:

    def __init__(self, issue_file="./.sbt"):
        self.issue_file = os.path.expanduser(issue_file)
        self.current = []

        if os.path.isfile(self.issue_file):
            with file(self.issue_file, "r") as F:
                lines = F.read().split("\n")
                self._parse_meta(lines.pop(0))
                if self.version == 1:
                    self._set_version_1()
                    self._parse_file(lines)
        else:
            self._set_version_1()
            with file(self.issue_file, "w") as F:
                F.write("version=%s\n" % self.version)

        sort_keys = {
            "ticket_number": lambda issue: issue[self_ticket_index],
            "urgency": lambda issue: issue[self_urgency_index],
            "type": lambda issue: issue[self_type_index]}

    def write(self, issue, urgency=3, type="bug"):
        if not self.current:
            ticket_number = 1
        else:
            ticket_numer = int(self.current[-1][0]) + 1
        with file(self.issue_file, "a") as F:
            F.write(self.delimeter.join((ticket_number, urgency, type, issue)))
            F.write("\n")

    def read(self, sort_key="ticket_numer"):
        self.current.sort(key=self.sort_keys[sort_key])
        for issue in self.current:
            print "%03d %d %-10s %s" % (issue[self.ticket_index],
                                        issue[self.urgency_index],
                                        issue[self.type_index],
                                        issue[self.issue_index])

    def close(self, ticket_number):
        pass

    def _set_version_1(self):
        self.version = 1
        self.delimeter = "$"
        self.ticket_index = 0
        self.urgency_index = 1
        self.type_index = 2
        self.issue_index = 3
        self._parse_file = self._parse_file_v1

    def _parse_file_v1(self, lines):
        for line in lines:
            line = line.split(self.delimeter)
            ticket_number = line[self.ticket_index]
            urgency = line[self_urgency_index]
            type = line[self.type_index]
            issue = self.delimeter.join(line[self.issue_index:])
            # if there's more than two fields, then the issue had a
            # literal delimeter; let's just join them again
            self.current.append((ticket_number, urgency, type, issue))

    def _parse_meta(self, meta_line):
        if not "version=" in meta_line:
            raise ValueError
        else:
            version_start = meta_line.index("version=") + len("version=")
            version = meta_line[version_start]
            self.version = int(version)
        if self.version == 1:
            self.delimeter = "$"


def handle_input():
    parser = optparse.OptionParser()
    parser.add_option("-u", action="store", type="int", dest="urgency",
        default=3, help="urgency level (1-3)")
    parser.add_option("-t", action="store", type="string", dest="type",
        default="bug", help="bug type (bug / enhancement / proposal")
    parser.add_option("-c", action="store", type="int", dest="ticket_number",
        default=False, help="close ticket (-c N to close ticket N)")
    parser.add_option("-l", action="store_true", dest="list",
        default=False, help="list open tickets")
    parser.add_option("-f", action="store", type="string", dest="file",
        default="./.sbt", help="read issues from FILE")

    options, arguments = parser.parse_args()
    if options.list:
        return options, arguments

    if not arguments:
        issue = sys.stdin.read()
    else:
        issue = " ".join(arguments)

    return options, issue


def main():
    options, issue = handle_input()
    tracker = sbt_tracker(options.file)

if __name__ == "__main__":
    main()
