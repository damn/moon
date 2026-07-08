(ns clojure.caves.k-to-adj-num
  (:require [clojure.caves.adj-num.default :as default]
            [clojure.caves.adj-num.thin :as thin]
            [clojure.caves.adj-num.wide :as wide]))

(def k->adj-num
  {:wide    wide/f
   :thin    thin/f    ; höhle mit breite 1 überall nur -> turn-ratio verringern besser
   :default default/f}) ; etwas breiter als 1 aber immernoch zu dünn für m ein game -> turn-ratio verringern besser
