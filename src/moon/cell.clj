(ns moon.cell)

(defn add-field-data [cell faction distance eid]
  (assoc cell faction {:distance distance
                       :eid eid}))

(defn remove-field-data [cell faction]
  (assoc cell faction nil))

(defrecord RCell [position
                  middle
                  adjacent-cells
                  movement
                  entities
                  occupied
                  good
                  evil])

(defn create [position movement]
  {:pre [(#{:none :air :all} movement)]}
  (map->RCell
   {:position position
    :middle (mapv (partial + 0.5) position)
    :movement movement
    :entities #{}
    :occupied #{}}))
