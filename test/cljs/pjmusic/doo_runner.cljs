(ns pjmusic.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [pjmusic.core-test]))

(doo-tests 'pjmusic.core-test)

