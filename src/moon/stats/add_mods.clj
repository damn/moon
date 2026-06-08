(ns moon.stats.add-mods
  (:require [moon.modifiers :as modifiers]))

(defn f [stats mods]
  (update stats :stats/modifiers modifiers/add mods))
