(ns clojure.update-labels.world)

(def item
  {:label "World"
   :update-fn (fn [{:keys [ctx/world-mouse-position]}]
                (mapv int world-mouse-position))})
