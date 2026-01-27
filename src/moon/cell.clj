(ns moon.cell)

(defn blocked? [{:keys [movement]} z-order]
  (case movement
    :none true
    :air (case z-order
           :z-order/flying false
           :z-order/ground true)
    :all false))

(defn blocks-vision? [{:keys [movement]}]
  (= movement :none))

(defn occupied-by-other? [{:keys [occupied]} eid]
  (some #(not= % eid) occupied))

(defn nearest-entity [this faction]
  (-> this faction :eid))

(defn nearest-entity-distance [this faction]
  (-> this faction :distance))

(defn pf-blocked? [this]
  (blocked? this :z-order/ground))

(defrecord FieldData [distance eid])

(defn add-field-data [cell faction distance eid]
  (assoc cell faction (->FieldData distance eid)))

(defn remove-field-data [cell faction]
  (assoc cell faction nil)) ; don't dissoc - will lose the Cell record type

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
