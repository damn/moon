(ns clojure.stats.add-mods
  (:require [clojure.mods.add :as add]))

(defn f [stats mods]
  (update stats :stats/modifiers add/f mods))
