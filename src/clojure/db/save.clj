(ns clojure.db.save
  (:require [clojure.pprint :as pprint]
            [clojure.recur-sort :refer [recur-sort]]
            [clojure.type :refer [property->type]]))

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
