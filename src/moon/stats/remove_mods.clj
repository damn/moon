(ns moon.stats.remove-mods
  (:require [moon.modifiers :as modifiers]))

(defn f [stats mods]
  (update stats :stats/modifiers modifiers/remove mods))
