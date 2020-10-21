#!/usr/bin/env bash

set -e
set -o pipefail

srcDir=src/main/resources/solidity
abiDir=$srcDir/build/
javaDir=src/main/java

for file in `ls $srcDir/*.sol`; do
  target=$(basename $file .sol)

  echo "Compiling Solidity file ${target}.sol"

  solc --bin --abi --optimize --overwrite \
          --allow-paths "$(pwd)" \
          $file -o $abiDir
  echo "Complete"

  echo "Generating contract bindings"
  web3j solidity generate \
      $abiDir/$target.bin \
      $abiDir/$target.abi \
      -p com.hubwiz.demo.contracts \
      -o $javaDir > /dev/null
  echo "Complete"

done
