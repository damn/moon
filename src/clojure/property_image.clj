(ns clojure.property-image)

; moon.oroperty

(defn f [{:keys [entity/image entity/animation]}]
  (or image
      (first (:animation/frames animation))))
