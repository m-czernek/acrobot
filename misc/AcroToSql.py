#!/usr/bin/env python3
#######################
# A simple, naive script to parse acronyms with their explanations
# into an SQL script. The expected file format is a list of acronyms separated by
# a double-comma ",,", where the first value is an acronym and the other  values
# are explanations. For example:
#
# acronym1,,explanation1,,explanation2
# acronym2,,explanation3,,explanation4
#
# Provide name of this file as the "input_file"

index_acronym = 0
index_explanation = 0
sql_filename = "import.sql"
email = "example@example.com"
input_file = "exampleAcroExpl"

def open_and_parse_file(filename):
    f = open(filename, "r")
    for line in f:
        explanation_list = line.strip().split(",,")
        write_into_file(explanation_list.pop(0), explanation_list)

def write_into_file(acronym, explanation_list):
  global index_acronym, index_explanation
  f = open(sql_filename,"a")
  f.write("INSERT INTO `acronym` VALUES ({},\'{}\' );\n".format(index_acronym, acronym))
  for explanation in explanation_list:
      f.write("INSERT INTO `explanation` VALUES ({}, '{}', \'{}\', true, {});\n".format(index_explanation, email, explanation, index_acronym))
      index_explanation += 1
  f.close()
  index_acronym += 1


open_and_parse_file(input_file)
