(ns game.info
  (:require [clojure.core.sort-by-k-order :refer [sort-by-k-order]]
            [clojure.string :as str]
            [game.constants :refer [k->colors k-order]]
            [game.info-fns :as info-fns]
            info.stats.modifiers))

(defmulti text (fn [object ctx]
                 (cond (:item/slot object)
                       :info/item
                       :else
                       :info/entity)))

(defn- remove-newlines [s]
  (let [new-s (-> s
                  (str/replace "\n\n" "\n")
                  (str/replace #"^\n" "")
                  str/trim-newline)]
    (if (= (count new-s) (count s))
      s
      (remove-newlines new-s))))

(defmethod text :info/entity [entity ctx]
  (let [component-info (fn [[k v]]
                         (let [s (if-let [info-fn (info-fns/mapping k)]
                                   (do
                                    (str #_k #_" - " (info-fn v ctx))))]
                           (if-let [color (k->colors k)]
                             (str "[" color "]" s "[]")
                             s)))]
    (->> entity
         (sort-by-k-order k-order)
         (keep (fn [{k 0 v 1 :as component}]
                 (str (try (component-info component)
                           (catch Throwable t
                             (str "*info-error* " k))) ; TODO this try/catch FIXME design error
                      (when (map? v)
                        (str "\n" (text v ctx))))))
         (str/join "\n")
         remove-newlines)))

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

(defmethod text :info/item [item _ctx]
  (assert (valid-item? item))
  (str/join "\n"
            (remove nil?
                    [(str "[PRETTY_NAME]" (:property/pretty-name item) "[]")
                     (str "[LIME]" (str/capitalize (name (:item/slot item))) "[]")
                     ; seq because they can be empty map ?
                     (when (seq (:stats/modifiers item))
                       (str "[CYAN]" (info.stats.modifiers/info (:stats/modifiers item) _ctx) "[]"))])))
