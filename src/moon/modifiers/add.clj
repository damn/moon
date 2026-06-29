(ns moon.modifiers.add
  (:require [moon.ops.add :as add]))

(defn f [mods other-mods]
  (merge-with add/f mods other-mods))
