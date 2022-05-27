import json
import sys

if len(sys.argv) < 2:
    raise ValueError("Please provide path to json file")

json_file = sys.argv[1]
print("Calculating cycles for " + json_file)
file = open(json_file)
data = json.load(file)

energy = 0
cycles = 0
nb_of_entries = 0

for entry in data:
    nb_of_entries += 1
    meta = entry['Meta']
    energy += meta['EnergyConsumed']
    cycles += meta['TotalCycles']

mean_energy = energy / nb_of_entries
mean_cycles = cycles / nb_of_entries
print("Found " + str(nb_of_entries) + " runs")
print("Mean energy used: " + str(mean_energy))
print("Mean cycles used: " + str(mean_cycles))
