(ns moon.ui.scroll-pane-window
  (:require [moon.ui.scroll-pane-cell :as scroll-pane-cell]
            [moon.ui :as ui]))

(defn create
  [{:keys [skin viewport-height rows]}]
  (ui/actor
   {:type :ui/window
    :skin skin
    :title "Choose"
    :modal? true
    :close-button? true
    :center? true
    :close-on-escape? true
    :rows [[(scroll-pane-cell/create skin viewport-height rows)]]
    :pack? true}))
