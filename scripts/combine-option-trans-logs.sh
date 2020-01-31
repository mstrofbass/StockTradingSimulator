#!/bin/bash

cd /var/data/stock-data/logs
> option-trans.log

echo "strategy,symbol,expirationDate,strike,optionType,buyDate,buyYear,qty,buyPrice,cost,sellDate,sellYear,sellPrice,proceeds,profit,realPrice" >> option-trans.log

cat option-trans.log.0 >> option-trans.log && cat option-trans.log.1 >> option-trans.log
