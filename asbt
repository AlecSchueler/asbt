#!/usr/bin/env python

from __future__ import with_statement

import optparse
import os
import sys

arch = os.popen("uname -m").read().strip()
osname = os.popen("uname -o").read().strip()
__version__ = "sbt, version 1.0 (%s-%s)" % (arch, osname)
__copyright__ = "Copyright (C) 2010  Alec Schueler"


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
                    self.parse_file(lines)
        else:
            self._set_version_1()
            with file(self.issue_file, "w") as F:
                F.write("version=%d\n" % self.version)

        self.sort_keys = {
            "ticket_number": lambda issue: issue[self.ticket_index],
            "urgency": lambda issue: issue[self.urgency_index],
            "type": lambda issue: issue[self.type_index],
            "state": lambda issue: issue[self.state_index]}

    def write(self, issue, urgency=3, type="bug", state="open"):
        if not self.current:
            ticket_number = "1"
        else:
            ticket_number = str(int(self.current[-1][0]) + 1)
        urgency = str(urgency)
        with file(self.issue_file, "a") as F:
            F.write(self.delimeter.join((ticket_number, urgency, state, type,
                issue)))
            F.write("\n")

    def report(self, sort_key="ticket_number", reverse=False):
        self.current.sort(key=self.sort_keys[sort_key])
        if reverse:
            self.current.reverse()
        for issue in self.current:
            print "%03d %s %-6s %-11s %s" % (int(issue[self.ticket_index]),
                                             issue[self.urgency_index],
                                             issue[self.state_index],
                                             issue[self.type_index],
                                             issue[self.issue_index])

    def update(self):
        with file(self.issue_file, "w") as F:
            F.write("version=%d\n" % self.version)
            for issue in self.current:
                F.write(self.delimeter.join(issue))
                F.write("\n")

    def close(self, ticket_number):
        self.current[ticket_number][self.state_index] = "closed"
        self.update()

    def _set_version_1(self):
        self.version = 1
        self.delimeter = "$"
        self.ticket_index = 0
        self.urgency_index = 1
        self.state_index = 2
        self.type_index = 3
        self.issue_index = 4
        self.parse_file = self._parse_file_v1

    def _parse_file_v1(self, lines):
        for line in lines:
            if line == "":
                continue
            line = line.split(self.delimeter)
            ticket_number = line[self.ticket_index]
            urgency = line[self.urgency_index]
            state = line[self.state_index]
            type = line[self.type_index]
            issue = self.delimeter.join(line[self.issue_index:])
            # if there's more than two fields, then the issue had a
            # literal delimeter; let's just join them again
            self.current.append([ticket_number, urgency, state, type, issue])

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
    parser = optparse.OptionParser(prog="sbt", version=\
        __version__ + "\n" + __copyright__)
    parser.add_option("-u", action="store", type="int", dest="urgency",
        default=3, help="urgency level (1-3)")
    parser.add_option("-t", action="store", type="string", dest="type",
        default="bug", help="bug type (bug / enhancement / proposal)")
    parser.add_option("-c", action="store", type="int", dest="ticket_number",
        default=None, help="close ticket (-c N to close ticket N)")
    parser.add_option("-l", action="store_true", dest="list",
        default=False, help="list open tickets")
    parser.add_option("-s", action="store", type="string", dest="key",
        default="ticket", help="sort key (ticket (number) / urgency / type "
            "/ state)")
    parser.add_option("-r", action="store_true", dest="reverse",
        default=False, help="reverse order of output")
    parser.add_option("-f", action="store", type="string", dest="file",
        default="./.sbt", help="read issues from FILE")

    options, arguments = parser.parse_args()

    if options.type[0] not in ("b", "e", "p"):
        parser.error("issue type must be one of: \"bug\", \"enchancement\" "
            "or \"proposal\"")

    if 0 > options.urgency > 4:
        parser.error("urgency level must be between 0 and 3")

    if options.list:
        if options.key[0] not in ("t", "s", "u"):
            parser.error("sort key must be one of: \"urgency\", \"type\" "
                "\"ticket\" (number) or \"state\"")

        keys = {"ty": "type", "ti": "ticket_number", "s": "state",
            "u": "urgency"}

        if options.key.startswith("t"):
            if len(options.key) < 2:
                parser.error("ambiguous sort key (t) - could be \"type\" or "
                    "\"ticket\"")
            options.key = keys[options.key[0:2]]
        else:
            options.key = keys[options.key[0]]

        return options, arguments, parser

    if options.ticket_number:
        return options, arguments, parser

    if options.type != "bug":
        if options.type[0] not in ("e", "b", "p"):
            parser.error("issue type must be one of: \"bug\", \"proposal\" or "
                "\"enhancement\"")
        else:
            types = {"b": "bug", "e": "enhancement", "p": "proposal"}
            options.type = types[options.type[0]]

    if not arguments:
        try:
            issue = sys.stdin.read().strip().replace("\n", " ")
        except KeyboardInterrupt:
            exit(1)
        if not issue:
            exit(1)
    else:
        issue = " ".join(arguments)

    return options, issue, parser


def main():
    options, issue, parser = handle_input()
    tracker = sbt_tracker(options.file)

    if options.list:
        tracker.report(sort_key=options.key, reverse=options.reverse)
        exit(0)

    if options.ticket_number:
        try:
            tracker.close(int(options.ticket_number) - 1)
        except IndexError:
            parser.error("ticket number %s not found" % options.ticket_number)
        exit(0)

    tracker.write(urgency=options.urgency, type=options.type, issue=issue)


if __name__ == "__main__":
    main()
