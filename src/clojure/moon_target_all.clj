(ns clojure.moon-target-all
  (:require [moon.raycaster :as raycaster]))

(defn affected-targets [active-entities raycaster entity]
  (->> active-entities
       (filter #(:entity/species @%))
       (filter #(raycaster/line-of-sight? raycaster entity @%))
       (remove #(:entity/player? @%))))
