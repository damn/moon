(ns clojure.property-types)

(defn property-types [{:keys [db/schemas]}]
  (filter #(= "properties" (namespace %)) (keys schemas)))
