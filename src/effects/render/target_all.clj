(ns effects.render.target-all
  (:require [moon.target-all :as target-all]))

(defn f
  [_
   {:keys [effect/source]}
   {:keys [ctx/active-entities
           ctx/colors
           ctx/raycaster]}]
  (let [source* @source]
    (for [target* (map deref (target-all/affected-targets active-entities raycaster source*))]
      [:draw/line
       (:body/position (:entity/body source*)) #_(start-point source* target*)
       (:body/position (:entity/body target*))

       (:colors/target-all-render colors)])))
