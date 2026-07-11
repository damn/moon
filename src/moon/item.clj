(ns moon.item
  (:require [clojure.string :as str]
            [moon.mods :as mods]
            [clojure.item-is-valid :as valid?]))

(defn info-text [item]
  (assert (valid?/f item))
  (str/join "\n"
            (remove nil?
                    [(str "[PRETTY_NAME]" (:property/pretty-name item) "[]")
                     (str "[LIME]" (str/capitalize (name (:item/slot item))) "[]")
                     ; seq because they can be empty map ?
                     (when (seq (:stats/modifiers item))
                       (str "[CYAN]" (mods/format-text (:stats/modifiers item)) "[]"))])))
