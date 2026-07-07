(ns clojure.item
  (:require [clojure.string :as str]
            [clojure.info :refer [info]]
            [clojure.item-is-valid :as valid?]))

(defn info-text [item _ctx]
  (assert (valid?/f item))
  (str/join "\n"
            (remove nil?
                    [(str "[PRETTY_NAME]" (:property/pretty-name item) "[]")
                     (str "[LIME]" (str/capitalize (name (:item/slot item))) "[]")
                     ; seq because they can be empty map ?
                     (when (seq (:stats/modifiers item))
                       (str "[CYAN]" ((:stats/modifiers (:k->fn info)) (:stats/modifiers item) _ctx) "[]"))])))
