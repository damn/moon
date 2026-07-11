(ns moon.cell)

(defrecord R [position
              middle
              adjacent-cells
              movement
              entities
              occupied
              good
              evil])

(defn blocks-vision? [{:keys [movement]}]
  (= movement :none))

(defn blocked? [{:keys [movement]} z-order]
  (case movement
    :none true
    :air (case z-order
           :z-order/flying false
           :z-order/ground true)
    :all false))
