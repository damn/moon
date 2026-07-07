(ns clojure.property-image)

(defn f [{:keys [entity/image entity/animation]}]
  (or image
      (first (:animation/frames animation))))
