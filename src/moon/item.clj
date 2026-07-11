(ns moon.item
  (:require [clojure.string :as str]
            [moon.mods :as mods]))

(defn valid? [item]
  (let [keyset (set (keys item))]
    (or (= #{:property/id
             :property/pretty-name
             :entity/image
             :item/slot
             :stats/modifiers} keyset)
        (= #{:property/id
             :property/pretty-name
             :entity/image
             :item/slot} keyset))))

(defn stackable? [item-a item-b]
  (and (:count item-a)
       (:count item-b)
       (= (:property/id item-a) (:property/id item-b))))

(defn info-text [item]
  (assert (valid? item))
  (str/join "\n"
            (remove nil?
                    [(str "[PRETTY_NAME]" (:property/pretty-name item) "[]")
                     (str "[LIME]" (str/capitalize (name (:item/slot item))) "[]")
                     ; seq because they can be empty map ?
                     (when (seq (:stats/modifiers item))
                       (str "[CYAN]" (mods/format-text (:stats/modifiers item)) "[]"))])))
