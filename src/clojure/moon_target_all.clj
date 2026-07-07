(ns clojure.moon-target-all
  (:require [clojure.line-of-sight :as line-of-sight?]))

(defn affected-targets [active-entities raycaster entity]
  (->> active-entities
       (filter #(:entity/species @%))
       (filter #(line-of-sight?/f raycaster entity @%))
       (remove #(:entity/player? @%))))
