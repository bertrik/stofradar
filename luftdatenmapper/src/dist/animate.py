#!/usr/bin/env python3

import argparse
import os
import re
import subprocess

from pathlib import Path

def build_cmd_list(dirpath):
    paths = sorted(Path(dirpath).iterdir(), key=os.path.getmtime)
    cmdlist = [];
    for p in paths:
        if re.match("[0-9]{4}\.png", p.name):
            cmdlist.append(f"file {p.name}")
            cmdlist.append("duration 0.08")
    return cmdlist


def main():
    """ main entry point """

    parser = argparse.ArgumentParser()
    parser.add_argument("-d", "--dir", help="The directory with input files", default=".")
    parser.add_argument("-o", "--output", help="The output file", default="out.mp4")
    args = parser.parse_args()

    # build the command file
    cmd_list = build_cmd_list(args.dir)
    cmdfile_name = "cmdfile.txt"
    with open(os.path.join(args.dir, cmdfile_name), "w") as cmdfile:
        cmdfile.write('\n'.join(cmd_list))
    
    # invoke ffmpeg
    params = ["ffmpeg", "-y", "-f", "concat", "-i", cmdfile_name, args.output]
    subprocess.run(params, cwd = args.dir)
    
if __name__ == "__main__":
    main()

