(ns moon.dev-menu.update-labels.world-mouse-position)

(def item
  {:label "World"
   :update-fn (fn [{:keys [ctx/world-mouse-position]}]
                (mapv int world-mouse-position))})
