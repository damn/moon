(ns moon.stats.add-mods
  (:require [moon.modifiers.add :as add]))

(defn f [stats mods]
  (update stats :stats/modifiers add/f mods))
