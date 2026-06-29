(ns moon.modifiers.remove
  (:require [moon.ops.remove :as remove]))

(defn f [mods other-mods]
  (merge-with remove/f mods other-mods))
