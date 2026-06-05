(ns info.item
  (:require [clojure.string :as str]
            [game.info :as info]
            info.stats.modifiers))

(defn- valid-item? [item]
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

(defmethod info/text :info/item [item _ctx]
  (assert (valid-item? item))
  (str/join "\n"
            (remove nil?
                    [(str "[PRETTY_NAME]" (:property/pretty-name item) "[]")
                     (str "[LIME]" (str/capitalize (name (:item/slot item))) "[]")
                     ; seq because they can be empty map ?
                     (when (seq (:stats/modifiers item))
                       (str "[CYAN]" (info.stats.modifiers/info (:stats/modifiers item) _ctx) "[]"))])))
