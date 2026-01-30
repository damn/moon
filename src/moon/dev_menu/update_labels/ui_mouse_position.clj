(ns moon.dev-menu.update-labels.ui-mouse-position)

(def item
  {:label "GUI"
   :update-fn (fn [{:keys [ctx/ui-mouse-position]}]
                (mapv int ui-mouse-position))})
