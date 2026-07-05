(ns moon.caves.k-to-adj-num
  (:require [moon.caves.adj-num.default :as default]
            [moon.caves.adj-num.thin :as thin]
            [moon.caves.adj-num.wide :as wide]))

(def k->adj-num
  {:wide    wide/f
   :thin    thin/f    ; höhle mit breite 1 überall nur -> turn-ratio verringern besser
   :default default/f}) ; etwas breiter als 1 aber immernoch zu dünn für m ein game -> turn-ratio verringern besser
