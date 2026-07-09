(ns clojure.update-labels.gui)

(def item
  {:label "GUI"
   :update-fn (fn [{:keys [ctx/ui-mouse-position]}]
                (mapv int ui-mouse-position))})
