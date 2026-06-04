(ns moon.db.get-raw)

(defn get-raw [{:keys [db/data]} property-id]
  (assert (contains? data property-id))
  (get data property-id) )
