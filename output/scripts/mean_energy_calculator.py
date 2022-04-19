import json
import sys

if len(sys.argv) < 2:
    raise ValueError("Please provide path to json file")

json_file = sys.argv[1]
print("Calculating mean for " + json_file)
file = open(json_file)
data = json.load(file)

mean = 0
nb_of_entries = 0

for entry in data:
    nb_of_entries += 1
    meta = entry['Meta']
    mean += meta['EnergyConsumed']

mean = mean / nb_of_entries
print("Found " + str(nb_of_entries) + " runs")
print("Mean energy used: " + str(mean))
