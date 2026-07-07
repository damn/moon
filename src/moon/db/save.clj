(ns moon.db.save
  (:require [clojure.recur-sort :refer [recur-sort]]
            [moon.property.type :refer [property->type]]
            [clojure.pprint :as pprint]))

(defn save!
  [{:keys [db/data db/file]}]
  (let [data (->> (vals data)
                  (sort-by property->type)
                  (map recur-sort)
                  doall)]
    (.start
     (Thread.
      (fn []
        (binding [*print-level* nil]
          (->> data
               pprint/pprint
               with-out-str
               (spit file))))))))
