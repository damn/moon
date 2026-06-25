(ns info.item
  (:require [clojure.string :as str]
            [moon.item.is-valid :as valid?]
            info.stats.modifiers))

(defn info-text [item _ctx]
  (assert (valid?/f item))
  (str/join "\n"
            (remove nil?
                    [(str "[PRETTY_NAME]" (:property/pretty-name item) "[]")
                     (str "[LIME]" (str/capitalize (name (:item/slot item))) "[]")
                     ; seq because they can be empty map ?
                     (when (seq (:stats/modifiers item))
                       (str "[CYAN]" (info.stats.modifiers/info (:stats/modifiers item) _ctx) "[]"))])))
