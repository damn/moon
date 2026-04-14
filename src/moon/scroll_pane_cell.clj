(ns moon.scroll-pane-cell
  (:require [moon.actor :as actor]
            [moon.ui :as ui]))

(defn create [skin viewport-height rows]
  (let [table (ui/create
               {:type :ui/table
                :table/cell-defaults {:pad 5}
                :table/rows rows} )]
    {:actor (ui/create {:type :ui/scroll-pane
                        :actor table
                        :skin skin})
     :width  (+ (actor/width table) 50)
     :height (min (- viewport-height 50)
                  (actor/height table))}))
