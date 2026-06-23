(ns info.entity
  (:require [clojure.sort-by-k-order :refer [sort-by-k-order]]
            [clojure.string :as str]
            [string.remove-newlines :refer [remove-newlines]]))

(defn info-text
  [entity {:keys [ctx/info] :as ctx}]
  (let [{:keys [k->fn
                k-order
                k->colors]} info
        component-info (fn [[k v]]
                         (let [s (if-let [info-fn (k->fn k)]
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
                        (str "\n" (info-text v ctx))))))
         (str/join "\n")
         remove-newlines)))
