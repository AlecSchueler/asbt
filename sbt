#!/usr/bin/env python

from __future__ import with_statement

import optparse
import os
import sys


class tracker:

    def __init__(self):
        sort_keys = {
            "ticket_number": lambda issue: issue[0],
            "urgency": lambda issue: issue[1],
            "type": lambda issue: issue[2]}
        self.current = []

        if os.path.isfile("./.sbt"):
            with file("./.sbt", "r") as F:
                for line in F.read().split("\n"):
                    line = line.split("$")
                    ticket_number = line[0]
                    urgency = line[1]
                    type = line[2]
                    issue = "$".join(line[3:])
                    # if there's more than two fields, then the issue had a
                    # literal $; let's just join them again
                    self.current.append((ticket_number, urgency, type, issue))

    def write(self, issue, urgency=3, type="bug"):
        if not self.current:
            ticket_number = 1
        else:
            ticket_numer = int(self.current[-1][0]) + 1
        with file("./.sbt", "a") as F:
            F.write("%d$%d$%s$%s\n" % (ticket_number, urgency, type, issue))

    def read(self, sort_key="ticket_numer"):
        self.current.sort(key=sort_key)
        for issue in self.current:
            print "%03d %d %-10s %s" % (issue[0], issue[1], issue[2], issue[3])

    def close(self, ticket_number):
        pass


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

if __name__ == "__main__":
    main()
