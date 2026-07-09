(ns clojure.effect-render
  (:require [clojure.body.end-point :refer [end-point]]
            [clojure.body.in-range :refer [in-range?]]
            [clojure.moon-target-all :as target-all]
            [clojure.start-point :refer [start-point]]))

(defmulti f
  (fn [[k _v] _effect-ctx _ctx]
    k))

(defmethod f :default
  [_ _effect-ctx _ctx]
  nil)

(defmethod f :effects/target-all
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

(defmethod f :effects/target-entity
  [[_ {:keys [maxrange]}]
   {:keys [effect/source effect/target]}
   {:keys [ctx/colors]}]
  (when target
    (let [body        (:entity/body @source)
          target-body (:entity/body @target)]
      [[:draw/line
        (start-point body target-body)
        (end-point body target-body maxrange)
        (if (in-range? body target-body maxrange)
          (:colors/target-entity-in-range colors)
          (:colors/target-entity-not-in-range colors))]])))
